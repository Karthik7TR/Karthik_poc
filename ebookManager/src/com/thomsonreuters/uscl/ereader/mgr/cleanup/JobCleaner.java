package com.thomsonreuters.uscl.ereader.mgr.cleanup;

import com.thomsonreuters.uscl.ereader.mgr.web.service.ManagerService;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.scheduling.annotation.Scheduled;

public class JobCleaner
{
    private ManagerService managerService;
    private int cleanJobsGreaterThanThisManyDaysOld;
    private int cleanPlannedOutagesGreaterThanThisManyDaysOld;
    private int numberLastMajorVersionKept;
    private int daysBeforeDocMetadataDelete;
    private int cleanCwbFilesGreaterThanThisManyDaysOld;

    @Scheduled(fixedRate = 24 * 60 * 60 * 1000)
    public void cleanupOldData()
    {
        managerService.cleanupOldSpringBatchDatabaseRecords(cleanJobsGreaterThanThisManyDaysOld);
        managerService
            .cleanupOldFilesystemFiles(cleanJobsGreaterThanThisManyDaysOld, cleanCwbFilesGreaterThanThisManyDaysOld);
        managerService.cleanupOldPlannedOutages(cleanPlannedOutagesGreaterThanThisManyDaysOld);
        managerService.cleanupOldTransientMetadata(numberLastMajorVersionKept, daysBeforeDocMetadataDelete);
    }

    @Required
    public void setManagerService(final ManagerService service)
    {
        managerService = service;
    }

    @Required
    public void setCleanJobsGreaterThanThisManyDaysOld(final int daysBack)
    {
        cleanJobsGreaterThanThisManyDaysOld = daysBack;
    }

    @Required
    public void setCleanPlannedOutagesGreaterThanThisManyDaysOld(final int daysBack)
    {
        cleanPlannedOutagesGreaterThanThisManyDaysOld = daysBack;
    }

    @Required
    public void setNumberLastMajorVersionKept(final int numberLastMajorVersionKept)
    {
        this.numberLastMajorVersionKept = numberLastMajorVersionKept;
    }

    @Required
    public void setDaysBeforeDocMetadataDelete(final int daysBeforeDocMetadataDelete)
    {
        this.daysBeforeDocMetadataDelete = daysBeforeDocMetadataDelete;
    }

    @Required
    public void setCleanCwbFilesGreaterThanThisManyDaysOld(final int daysBack)
    {
        cleanCwbFilesGreaterThanThisManyDaysOld = daysBack;
    }
}
