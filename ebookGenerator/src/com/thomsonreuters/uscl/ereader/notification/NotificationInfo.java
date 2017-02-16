package com.thomsonreuters.uscl.ereader.notification;

import com.thomsonreuters.uscl.ereader.JobParameterKey;
import com.thomsonreuters.uscl.ereader.core.book.domain.BookDefinition;
import com.thomsonreuters.uscl.ereader.stats.domain.PublishingStats;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.item.ExecutionContext;

public class NotificationInfo
{
    @NotNull
    private ExecutionContext jobExecutionContext;
    @NotNull
    private JobParameters jobParams;
    private long jobInstanceId;
    private long jobExecutionId;
    @NotNull
    private PublishingStats currentPublishingStats;
    @Nullable
    private PublishingStats previousPublishingStats;

    public NotificationInfo(
        @NotNull final ExecutionContext jobExecutionContext,
        @NotNull final JobParameters jobParams,
        final long jobInstanceId,
        final long jobExecutionId,
        @NotNull final PublishingStats currentPublishingStats,
        @Nullable final PublishingStats previousPublishingStats)
    {
        this.jobExecutionContext = jobExecutionContext;
        this.jobParams = jobParams;
        this.jobInstanceId = jobInstanceId;
        this.jobExecutionId = jobExecutionId;
        this.currentPublishingStats = currentPublishingStats;
        this.previousPublishingStats = previousPublishingStats;
    }

    @NotNull
    public ExecutionContext getJobExecutionContext()
    {
        return jobExecutionContext;
    }

    @Nullable
    public String getUserName()
    {
        return jobParams.getString(JobParameterKey.USER_NAME);
    }

    @Nullable
    public String getEnvironment()
    {
        return jobParams.getString(JobParameterKey.ENVIRONMENT_NAME);
    }

    @NotNull
    public BookDefinition getBookDefinition()
    {
        final BookDefinition bookDefinition = (BookDefinition) jobExecutionContext.get(JobParameterKey.EBOOK_DEFINITON);
        if (bookDefinition == null)
        {
            throw new RuntimeException("Book definition was not found in job execution context");
        }
        return bookDefinition;
    }

    public long getJobExecutionId()
    {
        return jobExecutionId;
    }

    public long getJobInstanceId()
    {
        return jobInstanceId;
    }

    @Nullable
    public Integer getTocNodeCount()
    {
        return currentPublishingStats.getGatherTocNodeCount();
    }

    @Nullable
    public Integer getThresholdValue()
    {
        return getBookDefinition().getDocumentTypeCodes().getThresholdValue();
    }

    public boolean isBigToc()
    {
        final Integer tocNodeCount = getTocNodeCount();
        final Integer thresholdValue = getThresholdValue();
        if (tocNodeCount == null || thresholdValue == null)
        {
            throw new RuntimeException(
                "Gather TOC Node Count = "
                    + tocNodeCount
                    + " and cannot be compared with was Threshold Value = "
                    + thresholdValue);
        }
        return tocNodeCount >= thresholdValue;
    }

    @NotNull
    public PublishingStats getCurrentPublishingStats()
    {
        return currentPublishingStats;
    }

    @Nullable
    public PublishingStats getPreviousPublishingStats()
    {
        return previousPublishingStats;
    }

}
