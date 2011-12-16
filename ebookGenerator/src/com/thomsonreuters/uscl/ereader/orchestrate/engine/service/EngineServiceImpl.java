/*
 * Copyright 2011: Thomson Reuters Global Resources. All Rights Reserved.
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

import org.apache.log4j.Logger;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameter;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.configuration.JobRegistry;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.JobOperator;
import org.springframework.beans.factory.annotation.Required;

import com.thomsonreuters.uscl.ereader.JobParameterKey;
import com.thomsonreuters.uscl.ereader.orchestrate.core.BookDefinition;
import com.thomsonreuters.uscl.ereader.orchestrate.core.JobRunRequest;

public class EngineServiceImpl implements EngineService {
	private static Logger log = Logger.getLogger(EngineServiceImpl.class);
	private JobRegistry jobRegistry;
	private JobOperator jobOperator;
	private JobLauncher jobLauncher;

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
		log.debug(String.format("Starting job: %s", jobName));

		// Lookup job object from set of defined collection of jobs 
		Job job = jobRegistry.getJob(jobName);
		if (job == null) {
			throw new IllegalArgumentException("Job definition: " + jobName + " was not found!");
		}
		
		// Launch the job with the specified set of JobParameters
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
	public Long restartJob(long jobExecutionId) throws Exception {
		Long restartedJobExecutionId = jobOperator.restart(jobExecutionId);
		return restartedJobExecutionId;
	}
	
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
	public JobParameters createBookDefinitionJobParameters(BookDefinition bookDefinition) {
		Map<String, JobParameter> paramMap = new HashMap<String,JobParameter>();
		paramMap.put(JobParameterKey.BOOK_TITLE_ID, new JobParameter(bookDefinition.getTitleId()));
		paramMap.put(JobParameterKey.BOOK_MAJOR_VERSION, new JobParameter(bookDefinition.getPrimaryKey().getMajorVersion()));
		paramMap.put(JobParameterKey.BOOK_NAME, new JobParameter(bookDefinition.getName()));
// TODO: there are a lot more to add here once the BookDefinition is completely defined
		return new JobParameters(paramMap);
	}
		
	@Override
	public JobParameters createDynamicJobParameters(JobRunRequest runRequest) {
		Map<String,JobParameter> jobParamMap = new HashMap<String,JobParameter>();
		// What host is the job running on?
		String hostName = null;
		try {
			InetAddress host = InetAddress.getLocalHost();
			hostName = host.getHostName();
		} catch (UnknownHostException uhe) {
			hostName = null;
		}
		
		// Add the dyanamic key/value pairs into the job parameters map
		jobParamMap.put(JobParameterKey.USER_NAME, new JobParameter(runRequest.getUserName()));
		jobParamMap.put(JobParameterKey.USER_EMAIL, new JobParameter(runRequest.getUserEmail()));
		jobParamMap.put(JobParameterKey.HOST_NAME, new JobParameter(hostName));
		jobParamMap.put(JobParameterKey.JOB_TIMESTAMP, new JobParameter(System.currentTimeMillis()));
		return new JobParameters(jobParamMap);
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
}
