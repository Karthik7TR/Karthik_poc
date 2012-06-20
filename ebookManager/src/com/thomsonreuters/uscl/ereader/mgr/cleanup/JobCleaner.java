/*
 * Copyright 2011: Thomson Reuters Global Resources. All Rights Reserved.
 * Proprietary and Confidential information of TRGR. Disclosure, Use or
 * Reproduction without the written authorization of TRGR is prohibited
 */
package com.thomsonreuters.uscl.ereader.mgr.cleanup;

import org.springframework.beans.factory.annotation.Required;
import org.springframework.scheduling.annotation.Scheduled;

import com.thomsonreuters.uscl.ereader.mgr.web.service.ManagerService;

public class JobCleaner {
	private ManagerService managerService;
	private int cleanJobsGreaterThanThisManyDaysOld;
	private int cleanPlannedOutagesGreaterThanThisManyDaysOld;
	private int numberLastMajorVersionKept;
	private int daysBeforeDocMetadataDelete;
	
	@Scheduled(fixedRate = 24*60*60*1000)
	public void cleanupOldData() {
		managerService.cleanupOldSpringBatchDatabaseRecords(cleanJobsGreaterThanThisManyDaysOld);
		managerService.cleanupOldFilesystemFiles(cleanJobsGreaterThanThisManyDaysOld);
		managerService.cleanupOldPlannedOutages(cleanPlannedOutagesGreaterThanThisManyDaysOld);
		managerService.cleanupOldTransientMetadata(numberLastMajorVersionKept, daysBeforeDocMetadataDelete);
	}
	
	@Required
	public void setManagerService(ManagerService service) {
		this.managerService = service;
	}
	@Required
	public void setCleanJobsGreaterThanThisManyDaysOld(int daysBack) {
		this.cleanJobsGreaterThanThisManyDaysOld = daysBack;
	}
	@Required
	public void setCleanPlannedOutagesGreaterThanThisManyDaysOld(int daysBack) {
		this.cleanPlannedOutagesGreaterThanThisManyDaysOld = daysBack;
	}
	@Required
	public void setNumberLastMajorVersionKept(int numberLastMajorVersionKept) {
		this.numberLastMajorVersionKept = numberLastMajorVersionKept;
	}

	public void setDaysBeforeDocMetadataDelete(int daysBeforeDocMetadataDelete) {
		this.daysBeforeDocMetadataDelete = daysBeforeDocMetadataDelete;
	}
}
