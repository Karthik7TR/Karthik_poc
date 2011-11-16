package com.thomsonreuters.uscl.ereader.orchestrate.dashboard.web.scheduler;

import java.util.Map;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

/**
 * Runs the ipamonWeb regularly scheduled tasks.
 */
public class CleanupJob implements Job {
	//private static Logger log = Logger.getLogger(CleanerJob.class);
	
	@Override
	@SuppressWarnings("unchecked")
	public void execute(JobExecutionContext context) throws JobExecutionException {
		Map<String,Object> dataMap = context.getJobDetail().getJobDataMap();
		CleanupTask cleanupTask = (CleanupTask) dataMap.get(CleanupTask.class.getName());
		cleanupTask.deleteOldJobs();
	}
}
