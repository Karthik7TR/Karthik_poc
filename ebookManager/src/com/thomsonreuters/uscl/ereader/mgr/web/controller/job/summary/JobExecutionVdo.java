package com.thomsonreuters.uscl.ereader.mgr.web.controller.job.summary;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.thomsonreuters.uscl.ereader.core.book.domain.EbookAudit;
import com.thomsonreuters.uscl.ereader.core.job.domain.JobSummary;
import com.thomsonreuters.uscl.ereader.mgr.web.UserUtils;
import com.thomsonreuters.uscl.ereader.stats.domain.PublishingStats;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameter;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.item.ExecutionContext;

/**
 * A View Data Object (VDO) wrapper around a Spring Batch JobExecution object (Decorator/VDO patterns).
 * Exists to provide convenience methods to expose complex calculated data and values which would otherwise be very
 * messy to calculate directly within the JSP.
 */
public class JobExecutionVdo
{
    //private static final Logger log = LogManager.getLogger(JobExecutionVdo.class);
    private static final Comparator<StepExecution> STEP_START_TIME_COMPARATOR = new StepStartTimeComparator();
    /** Comparator to sort lists of properties into ascending key order */
    private static final Comparator<Map.Entry<String, ?>> MAP_ENTRY_KEY_COMPARATOR = new MapEntryKeyComparator();

    private JobExecution jobExecution;
    private EbookAudit bookInfo;
    private PublishingStats stats;

    public JobExecutionVdo()
    {
        super();
    }

    /**
     * @param jobExecution the Spring Batch job execution object.
     * May be null, but no null checks are made in the convenience methods that use it so clients must first check
     * that the jobExecution property is not null.
     * @param bookInfo book data needed for presentation that is associated with this instance.
     */
    public JobExecutionVdo(final JobExecution jobExecution, final EbookAudit bookInfo, final PublishingStats stats)
    {
        this.jobExecution = jobExecution;
        this.bookInfo = bookInfo;
        this.stats = stats;
    }

    public JobExecution getJobExecution()
    {
        return jobExecution;
    }

    public EbookAudit getBookInfo()
    {
        return bookInfo;
    }

    public PublishingStats getPublishingStats()
    {
        return stats;
    }

    /**
     * Get the job execution steps in descending start time order.
     */
    public List<StepExecution> getSteps()
    {
        final Collection<StepExecution> stepColl = jobExecution.getStepExecutions();
        final List<StepExecution> stepList = new ArrayList<>(stepColl);
        Collections.sort(stepList, STEP_START_TIME_COMPARATOR);
        return stepList;
    }

    /**
     * Returns true if the Spring Batch job is restartable per Spring Batch rules.
     * Used to determine if the job "Restart" button should be displayed.
     */
    public boolean isJobRestartable()
    {
        final BatchStatus batchStatus = jobExecution.getStatus();
        return ((BatchStatus.STOPPED == batchStatus) || (BatchStatus.FAILED == batchStatus));
    }

    /**
     * Returns true if the Spring Batch job is stoppable per Spring Batch rules.
     * Used to determine if the job "Stop" button should be displayed.
     */
    public boolean isJobStoppable()
    {
        return (BatchStatus.STARTED == jobExecution.getStatus());
    }

    /**
     * Returns true if the user is a SUPERUSER or the user who launched the job in the first place
     * which means that they can stop or restart it.
     */
    public boolean isUserAllowedToStopAndRestartJob()
    {
        final PublishingStats stats = getPublishingStats();
        final String submittedBy = (stats != null) ? stats.getJobSubmitterName() : null;
        return UserUtils.isUserAuthorizedToStopOrRestartBatchJob(submittedBy);
    }

    public String getDuration()
    {
        String duration = null;
        if (jobExecution != null)
        {
            duration = JobSummary.getExecutionDuration(
                JobSummary.getExecutionDuration(jobExecution.getStartTime(), jobExecution.getEndTime()));
        }
        return duration;
    }

    /**
     * Creates a sorted list of job launch parameter map entries fetched from the current JobExecution.
     * This list of key/value pairs is then presented on the JSP.
     * @return a sorted list of job parameter map entries, possibly empty, never null
     */
    public List<Map.Entry<String, ?>> getJobParameterMapEntryList()
    {
        if (jobExecution != null)
        {
            final JobParameters jobParameters = jobExecution.getJobParameters();
            final Map<String, JobParameter> jobParameterMap = jobParameters.getParameters();
            final Set entrySet = jobParameterMap.entrySet();
            return createMapEntryList(entrySet);
        }
        return Collections.EMPTY_LIST;
    }

    /**
     * Creates a sorted list of job execution context map entries from the current JobExecution.
     * 	 * This list of key/value pairs is then presented on the JSP.
     * @return a sorted list of job execution context map entries, possibly empty, never null
     */
    public List<Map.Entry<String, ?>> getJobExecutionContextMapEntryList()
    {
        if (jobExecution != null)
        {
            final ExecutionContext execContext = jobExecution.getExecutionContext();
            final Set entrySet = execContext.entrySet();
            return createMapEntryList(entrySet);
        }
        return Collections.EMPTY_LIST;
    }

    /**
     * Create a ascending key sorted list of map entries from the specified map.
     * @param map the map whose entries will be extracted into a list sorted by key in ascending order.
     * @return a list of map entries sorted by key.
     */
    private List<Map.Entry<String, ?>> createMapEntryList(final Set<Map.Entry<String, ?>> entrySet)
    {
        final List<Map.Entry<String, ?>> mapEntryList = new ArrayList<>();
        final Iterator<Map.Entry<String, ?>> entryIterator = entrySet.iterator();
        while (entryIterator.hasNext())
        {
            mapEntryList.add(entryIterator.next());
        }
        Collections.sort(mapEntryList, MAP_ENTRY_KEY_COMPARATOR);
        return mapEntryList;
    }
}
