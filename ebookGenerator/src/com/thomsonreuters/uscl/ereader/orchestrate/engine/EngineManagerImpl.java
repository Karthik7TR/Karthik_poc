package com.thomsonreuters.uscl.ereader.orchestrate.engine;

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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.thomsonreuters.uscl.ereader.orchestrate.core.JobRunRequest;
import com.thomsonreuters.uscl.ereader.orchestrate.core.engine.EngineConstants;
import com.thomsonreuters.uscl.ereader.orchestrate.engine.service.EngineService;

@Component
public class EngineManagerImpl implements EngineManager {
	private static final Logger log = Logger.getLogger(EngineManagerImpl.class);
	@Autowired
	private JobRegistry jobRegistry;
	@Autowired
	private JobOperator jobOperator;
	@Autowired
	private EngineService engineService;
	@Autowired
	private JobLauncher jobLauncher;
	
	/**
	 * Immediately run a job at the specified thread execution priority.
	 * @param jobName the name of the job as defined in Spring bean definition file(s)
	 * @param threadPriority 1..10, but mapped to LOW (1..4), NORMAL (5), HIGH (6..10).
	 * @return the job execution object
	 * @throws Exception on unable to find job name, or launching the job
	 */
	@Override
	public JobExecution runJob(JobRunRequest request) throws Exception {
		log.debug(String.format("Starting job: %s", request));

		// Lookup job object from set of defined collection of jobs 
		Job job = jobRegistry.getJob(request.getJobName());
		if (job == null) {
			throw new IllegalArgumentException("Job name: " + request.getJobName() + " was not found!");
		}
		
		// Load the pre-defined set of job parameters for this specific job from a database table
		JobParameters databaseJobParameters = engineService.loadJobParameters(request.getJobName());
		
		// Combine and add in the well-known set of launch parameters
		JobParameters combinedJobParameters = createCombinedJobParameters(request, databaseJobParameters);
		
		// Launch the job with the specified set of JobParameters
		JobExecution jobExecution = jobLauncher.run(job, combinedJobParameters);
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
	
	/**
	 * Combine user and database loaded job params and the standard "well-known" set of job parameters to the job launch configuration.
	 * @return a superset of the provided jobParameters including the well-known set.
	 */
	private static JobParameters createCombinedJobParameters(JobRunRequest runRequest, JobParameters databaseJobParams) {
		
		// Combine the user and database provided job parameters
		Map<String,JobParameter> jobParamMap = new HashMap<String,JobParameter>(databaseJobParams.getParameters());
		
		// What host is the job running on?
		String hostName = null;
		try {
			InetAddress host = InetAddress.getLocalHost();
			hostName = host.getHostName();
		} catch (UnknownHostException uhe) {
			hostName = null;
		}
		// Add the "well-known"  ken/values into the job parameters map
		jobParamMap.put(EngineConstants.JOB_PARAM_BOOK_CODE, new JobParameter(runRequest.getBookCode()));
		jobParamMap.put(EngineConstants.JOB_PARAM_BOOK_TITLE, new JobParameter(runRequest.getBookTitle()));
		jobParamMap.put(EngineConstants.JOB_PARAM_BOOK_VERSION, new JobParameter(runRequest.getBookVersion()));
		jobParamMap.put(EngineConstants.JOB_PARAM_USER_NAME, new JobParameter(runRequest.getUserName()));
		jobParamMap.put(EngineConstants.JOB_PARAM_USER_EMAIL, new JobParameter(runRequest.getUserEmail()));
		jobParamMap.put(EngineConstants.JOB_PARAM_HOST_NAME, new JobParameter(hostName));
		jobParamMap.put(EngineConstants.JOB_PARAM_SERIAL_NUMBER, new JobParameter(System.currentTimeMillis()));
		return new JobParameters(jobParamMap);
	}
}
