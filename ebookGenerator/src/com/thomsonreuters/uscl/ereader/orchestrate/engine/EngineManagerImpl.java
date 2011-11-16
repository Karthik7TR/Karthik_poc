package com.thomsonreuters.uscl.ereader.orchestrate.engine;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;

import javax.annotation.Resource;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.configuration.JobRegistry;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.JobOperator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

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

	/**
	 * Immediately run a job at the specified thread execution priority.
	 * @param jobName the name of the job as defined in Spring bean definition file(s)
	 * @param threadPriority 1..10, but mapped to LOW (1..4), NORMAL (5), HIGH (6..10).
	 * @return the job execution object
	 * @throws Exception on unable to find job name, or launching the job
	 */
	public JobExecution runJob(String jobName, Integer threadPriority) throws Exception {
		int priority = (threadPriority != null) ? threadPriority.intValue() : Thread.NORM_PRIORITY;
		if ((priority < Thread.MIN_PRIORITY) || (priority > Thread.MAX_PRIORITY)) {
			throw new IllegalArgumentException("Thread priority must be in range 1..10");
		}
		JobLauncher prioritizedJobLauncher = getJobLauncher(priority);
		// Lookup job object from set of defined jobs 
		Job job = jobRegistry.getJob(jobName);
		
		// Create the async task executor and have it run at the desired thread priority
//		SimpleAsyncTaskExecutor taskExecutor = new SimpleAsyncTaskExecutor("ER_");
//		taskExecutor.setThreadPriority(priority);
		
		// Create the job launcher with the execution executor
//		SimpleJobLauncher jobLauncher = new SimpleJobLauncher();
//		jobLauncher.setTaskExecutor(taskExecutor);
		
		// Launch the job  TODO: what about job parameters here ...
		JobParameters jobParameters = new JobParameters();
		JobExecution jobExecution = prioritizedJobLauncher.run(job, jobParameters);
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
}
