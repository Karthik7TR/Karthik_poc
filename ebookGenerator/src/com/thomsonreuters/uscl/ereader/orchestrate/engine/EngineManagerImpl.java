package com.thomsonreuters.uscl.ereader.orchestrate.engine;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameter;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.configuration.JobRegistry;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.JobOperator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.thomsonreuters.codes.security.authentication.LdapUserInfo;

@Component
public class EngineManagerImpl implements EngineManager {
	
	@Autowired
	private JobRegistry jobRegistry;
	@Autowired
	private JobOperator jobOperator;
	@Resource(name="lowThreadPriorityJobLauncher")
	private JobLauncher lowThreadPriorityJobLauncher;
	@Resource(name="normalThreadPriorityJobLauncher")
	private JobLauncher normalThreadPriorityJobLauncher;
	@Resource(name="highThreadPriorityJobLauncher")
	private JobLauncher highThreadPriorityJobLauncher;
	
	public JobExecution runJob(String jobName) throws Exception {
		return runJob(jobName, null);
	}
	
	public JobExecution runJob(String jobName, Integer threadPriority) throws Exception {
		return runJob(jobName, new JobParameters(), threadPriority);
	}

	/**
	 * Immediately run a job at the specified thread execution priority.
	 * @param jobName the name of the job as defined in Spring bean definition file(s)
	 * @param threadPriority 1..10, but mapped to LOW (1..4), NORMAL (5), HIGH (6..10).
	 * @return the job execution object
	 * @throws Exception on unable to find job name, or launching the job
	 */
	public JobExecution runJob(String jobName, JobParameters jobParameters, Integer threadPriority) throws Exception {
		int priority = (threadPriority != null) ? threadPriority.intValue() : Thread.NORM_PRIORITY;
		if ((priority < Thread.MIN_PRIORITY) || (priority > Thread.MAX_PRIORITY)) {
			throw new IllegalArgumentException("Thread priority must be in range 1..10");
		}
		JobLauncher prioritizedJobLauncher = getJobLauncher(priority);
		// Lookup job object from set of defined jobs 
		Job job = jobRegistry.getJob(jobName);
		
		// Add in 
		JobParameters launchJobParameters = setUpStandardJobParameters(jobParameters);
		// Launch the job with a MIN|NORMAL|MAX thread priority
		JobExecution jobExecution = prioritizedJobLauncher.run(job, launchJobParameters);
		return jobExecution;
	}
	
	/**
	 * Add the standard set of job parameters to the launch configuration.
	 * @param jobParameters an existing set of parameters, perhaps specific to the job being run.
	 * @return a superset of the provided jobParameters.
	 */
	private static JobParameters setUpStandardJobParameters(JobParameters jobParameters) {
		Map<String,JobParameter> jobParamMap = jobParameters.getParameters();
		
		// Who is running the job?  Add the username of the currently authenticated user to the set of job launch parameters.
		LdapUserInfo authenticatedUser = LdapUserInfo.getAuthenticatedUser();
		String userName = (authenticatedUser != null) ? authenticatedUser.getUsername() : null;
		String userEmail = (authenticatedUser != null) ? authenticatedUser.getEmail() : null;
		// What host is the job running on?
		String hostName = null;
		try {
			InetAddress host = InetAddress.getLocalHost();
			hostName = host.getHostName();
		} catch (UnknownHostException uhe) {
			hostName = null;
		}
		// Store the values in the job parameters map
		jobParamMap.put(EngineConstants.JOB_PARAM_USER_NAME, new JobParameter(userName));
		jobParamMap.put(EngineConstants.JOB_PARAM_USER_EMAIL, new JobParameter(userEmail));
		jobParamMap.put(EngineConstants.JOB_PARAM_HOST_NAME, new JobParameter(hostName));
		return new JobParameters(jobParamMap);
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
	
	private JobLauncher getJobLauncher(int priority) {
		if (priority < Thread.NORM_PRIORITY) {
			return lowThreadPriorityJobLauncher;
		} else if (priority > Thread.NORM_PRIORITY) {
			return highThreadPriorityJobLauncher;
		} else {
			return normalThreadPriorityJobLauncher;
		}
	}
}
