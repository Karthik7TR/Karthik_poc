package com.thomsonreuters.uscl.ereader.orchestrate.engine.scheduler;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.thomsonreuters.uscl.ereader.orchestrate.core.JobControlRequest;
import com.thomsonreuters.uscl.ereader.orchestrate.engine.EngineManager;
import com.thomsonreuters.uscl.ereader.orchestrate.engine.queue.JobQueueManager;
import com.thomsonreuters.uscl.ereader.orchestrate.engine.throttle.Throttle;

/**
 * A Quartz scheduler job task to check batch job request queue(s) for new job run request messages.
 * If a message is present, and we can run the job because less than
 * the maximum number of concurrent batch jobs is running (not throttled) then launch the batch job.
 */
@Component
public class BatchJobQueuePollingTask {
	private static final Logger log = Logger.getLogger(BatchJobQueuePollingTask.class);

	@Autowired
	private Throttle throttle;
	@Autowired
	private EngineManager engineManager;
	@Autowired
	private JobQueueManager jobQueueManager;
	
	public void run() {
		try {
			// If the engine will accept the start of another job and is not at the max concurrent jobs limit
			if (!throttle.isAtMaximum()) {
				// then look to see if there is a job run request sitting on the high priority queue
				JobControlRequest jobRequest = jobQueueManager.getHighPriorityJobRunRequest();
				// if not, then check the normal priority queue
				if (jobRequest == null) {
					jobRequest = jobQueueManager.getNormalPriorityJobRunRequest();
				}
				// if there was a job to run, then launch it
				if (jobRequest != null) {
					engineManager.runJob(jobRequest.getJobName(), jobRequest.getThreadPriority());
				}
			}
		} catch (Exception e) {
			log.error("Failed to fetch or run batch job", e);
		}
	}
}
