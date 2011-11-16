package com.thomsonreuters.uscl.ereader.orchestrate.engine.scheduler;

import java.util.Map;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.stereotype.Component;

/**
 * A _Quartz_ job (not a Spring batch job) that runs the queue polling task at a regular interval.
 */
@Component
public class BatchJobQueuePollingJob implements Job {
	//private static Logger log = Logger.getLogger(QueuePollingJob.class);
	
	@Override
	@SuppressWarnings("unchecked")
	public void execute(JobExecutionContext context) throws JobExecutionException {
		Map<String,Object> dataMap = context.getJobDetail().getJobDataMap();
		BatchJobQueuePollingTask task = (BatchJobQueuePollingTask) dataMap.get(BatchJobQueuePollingTask.class.getName());
		task.run();
	}
}
