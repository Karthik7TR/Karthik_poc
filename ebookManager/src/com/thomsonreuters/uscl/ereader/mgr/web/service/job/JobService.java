package com.thomsonreuters.uscl.ereader.mgr.web.service.job;

import java.util.List;

import com.thomsonreuters.uscl.ereader.core.job.domain.JobFilter;
import com.thomsonreuters.uscl.ereader.core.job.domain.JobSort;
import com.thomsonreuters.uscl.ereader.core.job.domain.JobSummary;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobInstance;
import org.springframework.batch.core.StepExecution;

/**
 * Service methods for accessing Spring Batch job instance and execution data from the Spring Batch table schema.
 */
public interface JobService {
    /**
     * Find a job execution by its primary key.
     * @param jobExecutionId primary key
     * @return the found execution, or null if not found
     */
    JobExecution findJobExecution(long jobExecutionId);

    /**
     * Find a list of Spring Batch job executions from a list of their primary keys.
     * @param jobExecutionIds the primary key of the job execution we want
     * @return the found job execution, possibly empty, never null
     */
    List<JobExecution> findJobExecutions(List<Long> jobExecutionIds);

    /**
     * Find the Spring Batch job execution primary keys that match the provided filter criteria, and are sorted per the JobSort object constraints.
     * @param filter the search criteria
     * @param sort specifies the column to sort on and the sort direction, ascending or descending.
     * @return a list of job execution id's primary keys, possibly empty, but never null
     */
    List<Long> findJobExecutions(JobFilter filter, JobSort sort);

    /**
     * Returns all the Spring Batch job executions for a specific job instance.
     */
    List<JobExecution> findJobExecutions(JobInstance jobInstance);

    /**
     * Find and return a Spring Batch job instance by its primary key.
     * @param jobInstanceId the primary key of the instance
     * @return the instance found, or null if not found
     */
    JobInstance findJobInstance(long jobInstanceId);

    /**
     * Fetch the model for the table columns shown on the Job Summary page.
     * @param jobExecutionIds primay key(s) for the job executions we are interested in.
     * @return a list of JobSummary object ordered in same way as the list of jobExecutionIds provided
     */
    List<JobSummary> findJobSummary(List<Long> jobExecutionIds);

    StepExecution findStepExecution(long jobExecutionId, long stepExecutionId);

    /**
     * Returns the total number of currently executing jobs, i.e. jobs that have a batch status of BatchStatus.STARTED|STARTING.
     * @return the number of currently executing jobs as known by the job repository.
     */
    int getStartedJobCount();
}
