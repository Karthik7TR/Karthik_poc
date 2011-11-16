package com.thomsonreuters.uscl.ereader.orchestrate.dashboard.web.scheduler;

import java.util.Date;
import java.util.Map;

import org.apache.log4j.Logger;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SimpleTrigger;
import org.quartz.impl.StdSchedulerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Required;

/**
 * Initialization and shutdown of the Quartz job scheduler.
 * This is a Spring bean that is loaded at app context load time.
 *
 */
public class QuartzManager implements InitializingBean {
	
	private static Logger log = Logger.getLogger(QuartzManager.class);
	/** The Quartz scheduler engine, responsible for running the job at a regular interval. */
	private Scheduler scheduler;
	
	/** A job task that removes the oldest rows from the batch metadata tables. */
	private CleanupTask cleanupTask;
	private int cleanupTaskRunIntervalHours;
	

	public void afterPropertiesSet() {
		startScheduler();
		startCleanupJob();
	}
	
	private void startScheduler() {
	   	try {
	        this.scheduler = StdSchedulerFactory.getDefaultScheduler();
	        scheduler.start();
    	} catch (SchedulerException e) {
    		log.error("Could not start the Quartz job scheduler!", e);
    	}
	}
	
	@SuppressWarnings("unchecked")
	private void startCleanupJob() {
		// Define the job container
		JobDetail cleanupJob = new JobDetail();
		cleanupJob.setName(CleanupJob.class.getName());
		cleanupJob.setJobClass(CleanupJob.class);
		
		// Define the tasks the job will run of the following schedule
		Map<String,Object> dataMap = cleanupJob.getJobDataMap();
		dataMap.put(CleanupTask.class.getName(), this.cleanupTask);

		// Configure the scheduler - how often the job will run
		SimpleTrigger trigger = new SimpleTrigger();
    	trigger.setName("cleanupTrigger");
    	// Have the job run the first time 60 seconds after the application boots
    	trigger.setStartTime(new Date(System.currentTimeMillis() + 60000));
    	trigger.setRepeatCount(SimpleTrigger.REPEAT_INDEFINITELY);
    	trigger.setRepeatInterval(cleanupTaskRunIntervalHours*60*60*1000);  // every n hours (milliseconds)
    	try {
	        scheduler.scheduleJob(cleanupJob, trigger);
    	} catch (SchedulerException e) {
    		log.error("Could not start the Quartz cleanup job", e);
    	}
	}
	
	/**
	 * Shutdown the scheduler.
	 * Invoked from a web listener when then container shuts down.  See web.xml for the listener definition.
	 * @throws SchedulerException if the scheduler failed to shut down properly, unrecoverable.
	 */
	public void shutdownQuartzScheduler() throws SchedulerException {
		scheduler.shutdown();
	}
	@Required
	public void setCleanupTask(CleanupTask task) {
		this.cleanupTask = task;
	}
	@Required
	public void setCleanupTaskRunIntervalHours(int hours) {
		this.cleanupTaskRunIntervalHours = hours;
	}
}
