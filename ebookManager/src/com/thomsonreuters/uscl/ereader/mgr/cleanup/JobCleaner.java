package com.thomsonreuters.uscl.ereader.mgr.cleanup;

import com.thomsonreuters.uscl.ereader.mgr.web.service.ManagerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class JobCleaner {
    private final ManagerService managerService;
    private final int cleanJobsGreaterThanThisManyDaysOld;
    private final int cleanPlannedOutagesGreaterThanThisManyDaysOld;
    private final int numberLastMajorVersionKept;
    private final int daysBeforeDocMetadataDelete;
    private final int cleanCwbFilesGreaterThanThisManyDaysOld;

    @Autowired
    public JobCleaner(
            final ManagerService managerService,
            @Value("${cleanup.jobs.older.than.this.many.days.old}")final int cleanJobsGreaterThanThisManyDaysOld,
            @Value("${clean.planned.outages.greater.than.day.old}")final int cleanPlannedOutagesGreaterThanThisManyDaysOld,
            @Value("${number.last.major.version.kept}")final int numberLastMajorVersionKept,
            @Value("${days.before.docmetadata.delete}")final int daysBeforeDocMetadataDelete,
            @Value("${cleanup.cwb.files.older.than.this.many.days.old}")final int cleanCwbFilesGreaterThanThisManyDaysOld) {
        this.managerService = managerService;
        this.cleanJobsGreaterThanThisManyDaysOld = cleanJobsGreaterThanThisManyDaysOld;
        this.cleanPlannedOutagesGreaterThanThisManyDaysOld = cleanPlannedOutagesGreaterThanThisManyDaysOld;
        this.numberLastMajorVersionKept = numberLastMajorVersionKept;
        this.daysBeforeDocMetadataDelete = daysBeforeDocMetadataDelete;
        this.cleanCwbFilesGreaterThanThisManyDaysOld = cleanCwbFilesGreaterThanThisManyDaysOld;
    }

    @Scheduled(fixedRate = 24 * 60 * 60 * 1000)
    public void cleanupOldData() {
        managerService.cleanupOldSpringBatchDatabaseRecords(cleanJobsGreaterThanThisManyDaysOld);
        managerService
            .cleanupOldFilesystemFiles(cleanJobsGreaterThanThisManyDaysOld, cleanCwbFilesGreaterThanThisManyDaysOld);
        managerService.cleanupOldPlannedOutages(cleanPlannedOutagesGreaterThanThisManyDaysOld);
        managerService.cleanupOldTransientMetadata(numberLastMajorVersionKept, daysBeforeDocMetadataDelete);
    }
}
