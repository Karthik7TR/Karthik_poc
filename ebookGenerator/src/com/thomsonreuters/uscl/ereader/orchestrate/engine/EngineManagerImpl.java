package com.thomsonreuters.uscl.ereader.orchestrate.engine;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
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
import com.thomsonreuters.uscl.ereader.orchestrate.core.engine.EngineConstants;
import com.thomsonreuters.uscl.ereader.orchestrate.engine.service.EngineService;

@Component
public class EngineManagerImpl implements EngineManager {
	
	@Autowired
	private JobRegistry jobRegistry;
	@Autowired
	private JobOperator jobOperator;
	@Autowired
	private EngineService engineService;
	@Resource(name="lowThreadPriorityJobLauncher")
	private JobLauncher lowThreadPriorityJobLauncher;
	@Resource(name="normalThreadPriorityJobLauncher")
	private JobLauncher normalThreadPriorityJobLauncher;
	@Resource(name="highThreadPriorityJobLauncher")
	private JobLauncher highThreadPriorityJobLauncher;
	
	@Override
	public JobExecution runJob(String jobName) throws Exception {
		return runJob(jobName, Thread.NORM_PRIORITY);
	}
	@Override	
	public JobExecution runJob(String jobName, Integer threadPriority) throws Exception {
		return runJob(jobName, threadPriority, new JobParameters());
	}
	
	/**
	 * Immediately run a job at the specified thread execution priority.
	 * @param jobName the name of the job as defined in Spring bean definition file(s)
	 * @param threadPriority 1..10, but mapped to LOW (1..4), NORMAL (5), HIGH (6..10).
	 * @return the job execution object
	 * @throws Exception on unable to find job name, or launching the job
	 */
	@Override
	public JobExecution runJob(String jobName, Integer threadPriority, JobParameters userJobParameters) throws Exception {
		
		// Lookup job object from set of defined collection of jobs 
		Job job = jobRegistry.getJob(jobName);
		if (job == null) {
			throw new IllegalArgumentException("Job name: " + jobName + " was not found!");
		}
		
		// Get the launcher with the correctly prioritized thread priority for this job
		JobLauncher prioritizedJobLauncher = getJobLauncher(threadPriority);
		
		// Load the pre-defined set of job parameters for this specific job from a database table
		JobParameters databaseJobParameters = engineService.loadJobParameters(jobName);
		
		// Combine and add in the well-known set of launch parameters
		JobParameters combinedJobParameters = createLaunchJobParameters(userJobParameters, databaseJobParameters);
		
		// Launch the job with a MIN|NORMAL|MAX thread priority
		JobExecution jobExecution = prioritizedJobLauncher.run(job, combinedJobParameters);
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
	
	private JobLauncher getJobLauncher(int priority) {
		if (priority < Thread.NORM_PRIORITY) {
			return lowThreadPriorityJobLauncher;
		} else if (priority > Thread.NORM_PRIORITY) {
			return highThreadPriorityJobLauncher;
		} else {
			return normalThreadPriorityJobLauncher;
		}
	}
	
	/**
	 * Load job launch parameters from a database table.
	 * @param jobName job to load launch parameters for, lookup key in table
	 * @return a map of the job parameters
	 */
	
//	private Map<String,JobParameter> loadJobParameters(String jobName) {
//		Map<String, JobParameter> params = engineService.loadJobParameters(jobName);
//	}
	
	/**
	 * Combine user and database loaded job params and ddd the standard well-known set of job parameters to the job launch configuration.
	 * @return a superset of the provided jobParameters including the well-known set.
	 */
	private static JobParameters createLaunchJobParameters(JobParameters userJobParams, JobParameters databaseJobParams) {
		
		// Combine the user and database provided job parameters
		Map<String,JobParameter> jobParamMap = new HashMap<String,JobParameter>(userJobParams.getParameters());
		jobParamMap.putAll(databaseJobParams.getParameters());
		
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
		// Add the "well-known"  ken/values into the job parameters map
		jobParamMap.put(EngineConstants.JOB_PARAM_USER_NAME, new JobParameter(userName));
		jobParamMap.put(EngineConstants.JOB_PARAM_USER_EMAIL, new JobParameter(userEmail));
		jobParamMap.put(EngineConstants.JOB_PARAM_HOST_NAME, new JobParameter(hostName));
		return new JobParameters(jobParamMap);
	}
}
