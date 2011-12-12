/*
 * Copyright 2011: Thomson Reuters Global Resources. All Rights Reserved.
 * Proprietary and Confidential information of TRGR. Disclosure, Use or
 * Reproduction without the written authorization of TRGR is prohibited
 */
package com.thomsonreuters.uscl.ereader.orchestrate.engine.queue;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.scheduling.annotation.Scheduled;

import com.thomsonreuters.uscl.ereader.orchestrate.core.JobRunRequest;
import com.thomsonreuters.uscl.ereader.orchestrate.engine.service.EngineService;
import com.thomsonreuters.uscl.ereader.orchestrate.engine.throttle.Throttle;

/**
 * A regularly scheduled task to check the batch job run request queue(s) for new job run request messages.
 * If a message is present, and we can run the job because less than
 * the maximum number of concurrent batch jobs is running (not throttled) then the specified job will be run.
 */
public class JobRunQueuePoller {
	private static final Logger log = Logger.getLogger(JobRunQueuePoller.class);

	private Throttle throttle;
	private EngineService engineService;
	private JobQueueManager jobQueueManager;
	
	@Scheduled(fixedRate=15000)
	public void run() {
		try {
			// If the engine will accept the start of another job and is not at the max concurrent jobs upper limit
			if (!throttle.isAtMaximum()) {
				// then look to see if there is a job run request sitting on the high priority queue
				JobRunRequest jobRunRequest = jobQueueManager.getHighPriorityJobRunRequest();
				// if not, then check the normal priority queue
				if (jobRunRequest == null) {
					jobRunRequest = jobQueueManager.getNormalPriorityJobRunRequest();
				}
				// if there was a job to run, then launch it
				if (jobRunRequest != null) {
					engineService.runJob(jobRunRequest);
				}
			}
		} catch (Exception e) {
			log.error(String.format("Failed to fetch job run request from launch input queue.\n\t%s", e.getMessage()));
		}
	}
	@Required
	public void setThrottle(Throttle throttle) {
		this.throttle = throttle;
	}
	@Required
	public void setEngineService(EngineService engineService) {
		this.engineService = engineService;
	}
	@Required
	public void setJobQueueManager(JobQueueManager jobQueueManager) {
		this.jobQueueManager = jobQueueManager;
	}
}
