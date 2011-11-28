package com.thomsonreuters.uscl.ereader.orchestrate.engine.scheduler;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.thomsonreuters.uscl.ereader.orchestrate.core.JobRunRequest;
import com.thomsonreuters.uscl.ereader.orchestrate.engine.EngineManager;
import com.thomsonreuters.uscl.ereader.orchestrate.engine.queue.JobQueueManager;
import com.thomsonreuters.uscl.ereader.orchestrate.engine.throttle.Throttle;

/**
 * A regularly scheduled task to check the batch job run request queue(s) for new job run request messages.
 * If a message is present, and we can run the job because less than
 * the maximum number of concurrent batch jobs is running (not throttled) then the specified job will be run.
 */
@Component
public class JobRunQueuePoller {
	private static final Logger log = Logger.getLogger(JobRunQueuePoller.class);

	@Autowired
	private Throttle throttle;
	@Autowired
	private EngineManager engineManager;
	@Autowired
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
					engineManager.runJob(jobRunRequest);
				}
			}
		} catch (Exception e) {
			log.error("Failed to fetch or run batch job", e);
		}
	}
}
