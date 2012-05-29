/*
 * Copyright 2011: Thomson Reuters Global Resources. All Rights Reserved.
 * Proprietary and Confidential information of TRGR. Disclosure, Use or
 * Reproduction without the written authorization of TRGR is prohibited
 */
package com.thomsonreuters.uscl.ereader.orchestrate.engine.queue;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.dbcp.BasicDataSource;
import org.apache.log4j.Logger;
import org.springframework.batch.core.JobParameter;
import org.springframework.batch.core.JobParameters;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import com.thomsonreuters.uscl.ereader.core.job.domain.JobRequest;
import com.thomsonreuters.uscl.ereader.core.job.service.JobRequestService;
import com.thomsonreuters.uscl.ereader.core.outage.domain.PlannedOutage;
import com.thomsonreuters.uscl.ereader.core.outage.service.OutageService;
import com.thomsonreuters.uscl.ereader.orchestrate.engine.domain.PlannedOutageContainer;
import com.thomsonreuters.uscl.ereader.orchestrate.engine.service.EngineService;
import com.thomsonreuters.uscl.ereader.orchestrate.engine.service.JobStartupThrottleService;
import com.thomsonreuters.uscl.ereader.util.EmailNotification;

/**
 * A regularly scheduled task to check the batch job run request queue(s) for
 * new job run request messages. If a message is present, and we can run the job
 * because less than the maximum number of concurrent batch jobs is running (not
 * throttled) then the specified job will be run.
 */
public class JobRunQueuePoller {
	private static final Logger log = Logger.getLogger(JobRunQueuePoller.class);

	private EngineService engineService;
	private JobStartupThrottleService jobStartupThrottleService;
	private JobRequestService jobRequestService;
	private OutageService outageService;
	private ThreadPoolTaskExecutor springBatchTaskExecutor;
	@Resource(name = "dataSource")
	private BasicDataSource basicDataSource;
	private PlannedOutageContainer plannedOutages;

	@Scheduled(fixedDelay = 15000)
	public void pollJobQueue() {
//		log.debug("----- CHECKING FOR A JOB TO RUN ------");	
		JobRequest jobRequest = null;
		try {
			/**
			 * Do not consume a JOB_REQUEST record if:
			 * 1) A planned outage is active, no jobs started until outage is over
			 * 2) The current number of concurrent job threads equals the configured pool size in the ThreadPoolTaskExecutor.
			 * 3) The application step specific rules prevent it from running.
			 */
			PlannedOutage outage = processPlannedOutages();
			if (outage == null) {
				// Core pool size is the task executor pool size, effectively the maximum number of concurrent jobs.
				// This is dynamic and can be changed through the administrative UI.
				int coreThreadPoolSize = springBatchTaskExecutor.getCorePoolSize();
				int activeThreads = springBatchTaskExecutor.getActiveCount();
				if (activeThreads < coreThreadPoolSize) {
					if (jobStartupThrottleService.checkIfnewJobCanbeLaunched()) {
						jobRequest = jobRequestService.getNextJobToExecute();
						if (jobRequest != null) {
							// Create the dynamic set of launch parameters, things like
							// user name, user email, and a unique serial number
							JobParameters dynamicJobParameters = engineService
												.createDynamicJobParameters(jobRequest);
							// Put the dynamic job parameters in the map.
							Map<String, JobParameter> allJobParametersMap = new HashMap<String, JobParameter>(
															dynamicJobParameters.getParameters());
							JobParameters allJobParameters = new JobParameters(allJobParametersMap);
							// Start the job that builds the ebook
							log.debug("Starting Job: " + jobRequest);
							engineService.runJob(JobRequest.JOB_NAME_CREATE_EBOOK, allJobParameters);
							if (log.isDebugEnabled()) {
								log.debug(String.format("THREAD POOL: activeCount=%d,poolSize=%d,corePoolSize=%d,maxPoolSize=%d",
										springBatchTaskExecutor.getActiveCount(), springBatchTaskExecutor.getPoolSize(),
										springBatchTaskExecutor.getCorePoolSize(), springBatchTaskExecutor.getMaxPoolSize()));
								log.debug(String.format("DB CONN POOL: numActive=%d,numIdle=%d,maxActive=%d,maxWait=%d",
										basicDataSource.getNumActive(), basicDataSource.getNumIdle(),
										basicDataSource.getMaxActive(), basicDataSource.getMaxWait()));
							}
						} else {
//							log.debug("No job request was found in the job queue.");
						}
					} else {
						log.debug("Application step throttling is not allowing any new jobs to run.");
					}
				} else {
					log.debug(String.format("There are %d active job threads running, the maximum allowed is %d.  No new jobs will be started until the active is less than the maximum.",
											activeThreads, coreThreadPoolSize));
				}
			} else {
				log.debug(String.format("A planned outage is in effect until %s, no jobs will be started until the outage is over.",
										outage.getEndTime().toString()));
			}
		} catch (Exception e) {
			log.error("Failed run job request: " + jobRequest, e);
		}
	}
	
	
	/**
	 * 
	 * @return the outage object if we are currently in the middle of an outage.
	 */
	private PlannedOutage processPlannedOutages() {
		Date timeNow = new Date();
		PlannedOutage outage = plannedOutages.findOutage(timeNow);
		if (outage != null) {
			// If not already sent, send email indicating an outage has started
			if (!outage.isNotificationEmailSent()) {
				outage.setNotificationEmailSent(true);
				outageService.savePlannedOutage(outage);
				String subject = String.format("Start of eBook generator outage on host %s", getHostName());
				sendOutageEmail(subject, outage);
			}
		}

		// Check for any outages that have now passed, and remove them from the collection
		PlannedOutage expiredOutage = plannedOutages.findExpiredOutage(timeNow);
		if (expiredOutage != null) {
			// If not already sent, send email indicating that the outage is over
			if (!expiredOutage.isAllClearEmailSent()) {
				expiredOutage.setAllClearEmailSent(true);
				outageService.savePlannedOutage(expiredOutage);
				String subject = String.format("End of eBook generator outage on host %s", getHostName());
				sendOutageEmail(subject, expiredOutage);
			}
		}
		return outage;
	}
	
	private void sendOutageEmail(String subject, PlannedOutage outage) {
// TODO: Get email recipients
String to = "tom.hall@thomsonreuters.com";  // TESTING
		// Make the subject line also be the first line of the body, because it is easier to read in Outlook preview pane
		String body = subject + "\n\n";
		body += outage.toString();
		EmailNotification.send(to, subject, body);
	}
	
	private String getHostName() {
		try {
			InetAddress localHost = InetAddress.getLocalHost();
			return localHost.getHostName();
		} catch (UnknownHostException e) {
			return "<unknown>";
		}
	}

	@Required
	public void setEngineService(EngineService engineService) {
		this.engineService = engineService;
	}
	@Required
	public void setJobStartupThrottleService(
			JobStartupThrottleService jobStartupThrottleService) {
		this.jobStartupThrottleService = jobStartupThrottleService;
	}
	@Required
	public void setOutageService(OutageService service) {
		this.outageService = service;
	}
	@Required
	public void setJobRequestService(JobRequestService jobRequestService) {
		this.jobRequestService = jobRequestService;
	}
	@Required
	public void setSpringBatchTaskExecutor(ThreadPoolTaskExecutor taskExecutor) {
		this.springBatchTaskExecutor = taskExecutor;
	}
	@Required
	public void setPlannedOutages(PlannedOutageContainer container) {
		this.plannedOutages = container;
	}
}
