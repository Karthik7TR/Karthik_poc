package com.thomsonreuters.uscl.ereader.mgr.web.service;

import java.net.InetSocketAddress;

import com.thomsonreuters.uscl.ereader.core.book.domain.BookDefinition;
import com.thomsonreuters.uscl.ereader.core.job.domain.JobThrottleConfig;
import com.thomsonreuters.uscl.ereader.core.job.domain.MiscConfig;
import com.thomsonreuters.uscl.ereader.core.job.domain.SimpleRestServiceResponse;
import com.thomsonreuters.uscl.ereader.core.outage.domain.PlannedOutage;
import org.jetbrains.annotations.NotNull;
import org.springframework.batch.core.JobExecution;

public interface ManagerService
{
    /**
     * Returns true if there is any currently running batch job or a job queued to run
     * (i.e. a row present in the JOB_REQUEST table).
     */
    boolean isAnyJobsStartedOrQueued();

    /**
     * Returns the job execution for a job that is running with the specified id.
     * @param book book definition
     * @return the job execution of the matching job, or null if none found
     */
    JobExecution findRunningJob(@NotNull BookDefinition book);

    /**
     *
     * @param socketAddr Container for the hostname and port number for the push REST operation that will accept
     *  the new app config.
     * @param webAppContextName the name of the web application, like "ebookGenerator"
     * @return the response object indicating success or failure
     */
    SimpleRestServiceResponse pushMiscConfiguration(
        MiscConfig config,
        String webAppContextName,
        InetSocketAddress socketAddr);

    SimpleRestServiceResponse pushJobThrottleConfiguration(
        JobThrottleConfig config,
        InetSocketAddress socketAddr);

    /**
     * Push out to generator applications the notification that there will be a planned
     * application resource availability disruption for some time period.
     * This means that new generator jobs will not be started or restarted during an outage period.
     * @param outage the properties of the outage event
     * @param socketAddr the host:port of the generator app that needs to receive notification
     */
    SimpleRestServiceResponse pushPlannedOutage(PlannedOutage outage, InetSocketAddress socketAddr);

    /**
     * Delete old database table and filesystem job data.
     * @param daysBack jobs more than this many days old will be deleted.
     */
    void cleanupOldSpringBatchDatabaseRecords(int daysBack);

    /**
     * Delete all the old files that were created while generating an ebook that are older than the specified
     * number of days.
     * @param daysBack
     * @param cwbFilesDaysBack
     */
    void cleanupOldFilesystemFiles(int daysBack, int cwbFilesDaysBack);

    /**
     * Delete old records in PLANNED_OUTAGE table.
     * @param daysBack records with an end_time more than this many days old will be deleted.
     */
    void cleanupOldPlannedOutages(int daysBack);

    /**
     * Delete old records in DOCUMENT_METADATA and IMAGE_METADATA tables
     * older than numberLastMajorVersionKept days, but keeping numberLastMajorVersionKept versions.
     * @param numberLastMajorVersionKept keep numberLastMajorVersionKept last successful major versions.
     * @param daysBeforeDocMetadataDelete the number of days to subtract from current date before deleting data
     */
    void cleanupOldTransientMetadata(int numberLastMajorVersionKept, int daysBeforeDocMetadataDelete);
}
