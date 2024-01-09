package com.thomsonreuters.uscl.ereader.mgr.dao;

import java.util.Date;

import com.thomsonreuters.uscl.ereader.core.book.domain.BookDefinition;
import org.jetbrains.annotations.NotNull;
import org.springframework.batch.core.JobExecution;

public interface ManagerDao {
    /**
     * Returns the job execution of a running job, and the job parameter values as specified.
     * Used to determine if a user is attempting to launch a book that is already running.
     * @param book the book to compare against the corresponding job parameter
     * @return the jobExecutionId that matches the criteria, or null if not found.
     */
    JobExecution findRunningJobExecution(@NotNull BookDefinition book);

    /**
     * Archive step data to JOB_HISTORY table and delete all old Spring Batch job data before the specified date.
     * @param jobsBefore job data before this date will be removed.
     * @return the number of job step executions that were archived/deleted.
     */
    int archiveAndDeleteSpringBatchJobRecordsBefore(Date deleteBefore);

    /**
     * Delete old planned_outage records prior to the specified date
     * @param deleteBefore delete records before this date.
     */
    void deletePlannedOutagesBefore(Date deleteBefore);

    /**
     * Delete old document_metadata and image_metadata records
     * @param numberLastMajorVersionKept delete records of all but last numberLastMajorVersionKept major versions.
     * @param daysBeforeDocMetadataDelete delete records prior to current date minus daysBeforeDocMetadataDelete.
     */
    void deleteTransientMetadata(int numberLastMajorVersionKept, int daysBeforeDocMetadataDelete);
}
