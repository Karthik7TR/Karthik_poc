package com.thomsonreuters.uscl.ereader.orchestrate.engine.scheduler;

import java.util.Date;
import java.util.Map;

import org.apache.log4j.Logger;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SimpleTrigger;
import org.quartz.impl.StdSchedulerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Initialization and shutdown of the Quartz job scheduler.
 * This is a Spring bean that is loaded at app context load time.
 *
 */
@Component("quartzManager")
public class QuartzManager implements InitializingBean {
	
	private static Logger log = Logger.getLogger(QuartzManager.class);
	private static final int QUEUE_POLLING_INTERVAL = 15000; // milliseconds
	
	/** The Quartz scheduler engine, responsible for running the job at a regular interval. */
	private Scheduler scheduler;
	
	@Autowired
	private BatchJobQueuePollingTask queuePollingTask;
	

	/**
	 * Automatic startup of the Quartz Scheduler once Spring has finished setting up this bean.
	 */
	public void afterPropertiesSet() {
		startScheduler();
		startQueuePolling();
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
	private void startQueuePolling() {
		// Define the job container
		JobDetail jobDetail = new JobDetail();
		jobDetail.setName(BatchJobQueuePollingJob.class.getName());
		jobDetail.setJobClass(BatchJobQueuePollingJob.class);
		
		// Define the tasks the job will run of the following schedule
		Map<String,Object> dataMap = jobDetail.getJobDataMap();
		dataMap.put(BatchJobQueuePollingTask.class.getName(), this.queuePollingTask);

		// Configure the scheduler - how often the job will run
		SimpleTrigger trigger = new SimpleTrigger();
    	trigger.setName("queuePollingTrigger");
    	// When to fire the trigger the very first time
    	trigger.setStartTime(new Date(System.currentTimeMillis() + QUEUE_POLLING_INTERVAL));
    	trigger.setRepeatCount(SimpleTrigger.REPEAT_INDEFINITELY);
    	trigger.setRepeatInterval(QUEUE_POLLING_INTERVAL);
    	try {
	        scheduler.scheduleJob(jobDetail, trigger);
    	} catch (SchedulerException e) {
    		log.error("Could not start the Quartz queue polling job", e);
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
}
