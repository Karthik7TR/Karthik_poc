package com.thomsonreuters.uscl.ereader.orchestrate.dashboard.web.scheduler;

import java.util.Calendar;
import java.util.Date;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;

import com.thomsonreuters.uscl.ereader.orchestrate.dashboard.web.service.DashboardService;

/**
 * A Quartz job task to remove the oldest database records.
 * This task is executed from the Job class.
 */
public class CleanupTask {
	private static final Logger log = Logger.getLogger(CleanupTask.class);
	private DashboardService service;
	private int deleteJobsDaysOlderThan;
	
	public void deleteOldJobs() {
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DATE, -deleteJobsDaysOlderThan);
		Date removeBeforeDate = cal.getTime();
		log.info("SB Job cleanup task has started, deleting job metadata older than: " + removeBeforeDate);
		service.jobCleaner(removeBeforeDate);
		log.info("SB Job cleanup task has completed.");
	}
	
	@Required
	public void setDashboardService(DashboardService service) {
		this.service = service;
	}
	@Required
	public void setDeleteJobsDaysOlderThan(int days) {
		this.deleteJobsDaysOlderThan = days;
	}
}
