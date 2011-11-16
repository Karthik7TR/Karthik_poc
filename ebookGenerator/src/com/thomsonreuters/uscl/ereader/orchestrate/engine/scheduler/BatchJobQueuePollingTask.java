package com.thomsonreuters.uscl.ereader.orchestrate.engine.scheduler;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.thomsonreuters.uscl.ereader.orchestrate.core.JobRunRequest;
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
			if (!throttle.isAtMaximum()) {
				JobRunRequest jobRunRequest = jobQueueManager.getHighPriorityJobRunRequest();
if (jobRunRequest != null) log.debug("Queue high priority job: " + jobRunRequest);	// DEBUG
				if (jobRunRequest == null) {
					jobRunRequest = jobQueueManager.getNormalPriorityJobRunRequest();
if (jobRunRequest != null) log.debug("Queue normal priority job: " + jobRunRequest);  // DEBUG					
				}
				if (jobRunRequest != null) {
					engineManager.runJob(jobRunRequest.getJobName(), jobRunRequest.getThreadPriority());
				}
			}
		} catch (Exception e) {
			log.error("Failed to fetch or run batch job", e);
		}
	}
}
