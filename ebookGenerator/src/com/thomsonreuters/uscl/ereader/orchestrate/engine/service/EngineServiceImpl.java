/*
 * Copyright 2016: Thomson Reuters Global Resources. All Rights Reserved.
 * Proprietary and Confidential information of TRGR. Disclosure, Use or
 * Reproduction without the written authorization of TRGR is prohibited
 */

package com.thomsonreuters.uscl.ereader.orchestrate.engine.service;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;

 import org.apache.log4j.LogManager; import org.apache.log4j.Logger;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameter;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.configuration.JobRegistry;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.JobOperator;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import com.thomsonreuters.uscl.ereader.JobParameterKey;
import com.thomsonreuters.uscl.ereader.core.job.domain.JobRequest;
import com.thomsonreuters.uscl.ereader.core.job.domain.JobThrottleConfig;
import com.thomsonreuters.uscl.ereader.core.service.JobThrottleConfigSyncService;
import com.thomsonreuters.uscl.ereader.core.service.MiscConfigSyncService;


public class EngineServiceImpl implements EngineService, JobThrottleConfigSyncService {
	private static Logger log = LogManager.getLogger(EngineServiceImpl.class);
	private String environmentName;  // like "ci" or "test"
	private JobRegistry jobRegistry;
	private JobOperator jobOperator;
	private JobLauncher jobLauncher;
	private ThreadPoolTaskExecutor springBatchTaskExecutor;
	private String imageService;
	private String dbServiceName;
	private JobStartupThrottleService jobStartupThrottleService;
	private MiscConfigSyncService miscConfigSyncService;

	@Override
	public void syncJobThrottleConfig(JobThrottleConfig config) throws Exception {
		
		// Reset the Spring Batch task executor with the core thread pool size read from the database as part of the job throttle config.
		setTaskExecutorCoreThreadPoolSize(config.getCoreThreadPoolSize());
		jobStartupThrottleService.setJobThrottleConfig(config);
		
		log.debug("Successfully synchronized: " + config);
	}

	/**
	 * Immediately run a job as defined in the specified JobRunRequest.
	 * JobParameters for the job are loaded from a database table as keyed by the book identifier.
	 * The launch set of JobParameters also includes a set of pre-defined "well-known" parameters, things
	 * like the username of person who started the job, the host on which the job is running, and others.
	 * See the Job Parameters section of the Job Execution Details page of the dashboard web app.
	 * to see the complete list for any single JobExecution.
	 * @throws Exception on unable to find job name, or in launching the job
	 */
	@Override
	public JobExecution runJob(String jobName, JobParameters jobParameters) throws Exception {

		// Lookup job object from set of defined collection of jobs 
		Job job = jobRegistry.getJob(jobName);
		if (job == null) {
			throw new IllegalArgumentException("Job definition: " + jobName + " was not found!");
		}
		
		// Launch the job with the specified set of JobParameters
		log.debug("Launch Job Parameters: " + jobParameters);
		JobExecution jobExecution = jobLauncher.run(job, jobParameters);
		return jobExecution;
	}
	
	/**
	 * Resume a stopped job.  Required that it already be in a STOPPED or FAILED status, but makes no attempt to 
	 * verify this before attempting to restart it.
	 * @param jobExecutionId of the job to be resumed
	 * @return the job execution ID of the restarted job
	 * @throws Exception on restart errors
	 */
	@Override
	public Long restartJob(long jobExecutionId) throws Exception {
		Long restartedJobExecutionId = jobOperator.restart(jobExecutionId);
		return restartedJobExecutionId;
	}
	@Override
	public void stopJob(long jobExecutionId) throws Exception {
		jobOperator.stop(jobExecutionId);
	}
	
	public static String getStackTrace(Throwable aThrowable) {
		Writer writer = new StringWriter();
		PrintWriter printWriter = new PrintWriter(writer);
		aThrowable.printStackTrace(printWriter);
		return writer.toString();
	}
		
	@Override
	public JobParameters createDynamicJobParameters(JobRequest jobRequest) {
		Map<String,JobParameter> jobParamMap = new HashMap<String,JobParameter>();
		String hostName = null;	// The host this job running on
		try {
			InetAddress host = InetAddress.getLocalHost();
			hostName = host.getHostName();
		} catch (UnknownHostException uhe) {
			hostName = null;
		}
		
		// Add misc metadata, dynamic key/value pairs into the job parameters map
		jobParamMap.put(JobParameterKey.USER_NAME, new JobParameter(jobRequest.getSubmittedBy()));
		jobParamMap.put(JobParameterKey.BOOK_DEFINITION_ID, new JobParameter(jobRequest.getBookDefinition().getEbookDefinitionId()));
		jobParamMap.put(JobParameterKey.BOOK_VERSION_SUBMITTED, new JobParameter(jobRequest.getBookVersion()));	
		jobParamMap.put(JobParameterKey.HOST_NAME, new JobParameter(hostName));
		jobParamMap.put(JobParameterKey.ENVIRONMENT_NAME, new JobParameter(environmentName));
		jobParamMap.put(JobParameterKey.PROVIEW_HOST_NAME, new JobParameter(miscConfigSyncService.getProviewHost().getHostName()));
		jobParamMap.put(JobParameterKey.TIMESTAMP, new JobParameter(jobRequest.getSubmittedAt()));  // When the job entered the JOB_REQUEST run queue
		jobParamMap.put(JobParameterKey.IMAGESVC_DOMAIN_NAME, new JobParameter(imageService));
		jobParamMap.put(JobParameterKey.NOVUS_ENV, new JobParameter(miscConfigSyncService.getNovusEnvironment().toString()));	
		jobParamMap.put(JobParameterKey.DATABASE_SERVICE_NAME, new JobParameter(dbServiceName));
		return new JobParameters(jobParamMap);
	}
	
	@Override
	public void setTaskExecutorCoreThreadPoolSize(int coreThreadPoolSize) {
		springBatchTaskExecutor.setCorePoolSize(coreThreadPoolSize);
	}

	@Required
	public void setEnvironmentName(String envName) {
		this.environmentName = envName;
	}
	@Required
	public void setJobRegistry(JobRegistry jobRegistry) {
		this.jobRegistry = jobRegistry;
	}
	@Required
	public void setJobOperator(JobOperator jobOperator) {
		this.jobOperator = jobOperator;
	}
	@Required
	public void setJobLauncher(JobLauncher jobLauncher) {
		this.jobLauncher = jobLauncher;
	}
	@Required	
	public void setImageService(String imageService) {
		this.imageService = imageService;
	}

	@Required	
	public void setDbServiceName(String dbServiceName) {
		this.dbServiceName = dbServiceName;
	}
	@Required
	public void setTaskExecutor(ThreadPoolTaskExecutor taskExecutor) {
		this.springBatchTaskExecutor = taskExecutor;
	}
	@Required
	public void setJobStartupThrottleService(JobStartupThrottleService service) {
		this.jobStartupThrottleService = service;
	}
	@Required
	public void setMiscConfigSyncService(MiscConfigSyncService service) {
		this.miscConfigSyncService = service;
	}
}
