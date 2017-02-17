package com.thomsonreuters.uscl.ereader.common.step;

import java.util.Date;

import com.thomsonreuters.uscl.ereader.JobParameterKey;
import com.thomsonreuters.uscl.ereader.common.notification.step.SendNotificationStep;
import com.thomsonreuters.uscl.ereader.common.outage.step.OutageAwareStep;
import com.thomsonreuters.uscl.ereader.common.publishingstatus.step.PublishingStatusUpdateStep;
import com.thomsonreuters.uscl.ereader.core.book.domain.BookDefinition;
import org.jetbrains.annotations.NotNull;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.util.Assert;

public abstract class BookStep extends BaseStep
    implements OutageAwareStep, SendNotificationStep, PublishingStatusUpdateStep
{
    /**
     * Used as an anchor for aspects.
     */
    @Override
    public RepeatStatus execute(final StepContribution stepContribution, final ChunkContext chunkContext)
        throws Exception
    {
        return super.execute(stepContribution, chunkContext);
    }

    /**
     * Should return {@code true} for initial step and {@code false} otherwise. Default value is
     * {@code false}.
     */
    public boolean isInitialStep()
    {
        return false;
    }

    @NotNull
    public BookDefinition getBookDefinition()
    {
        final BookDefinition book = (BookDefinition) getJobExecutionContext().get(JobParameterKey.EBOOK_DEFINITON);
        Assert.notNull(book);
        return book;
    }

    @NotNull
    public Long getBookDefinitionId()
    {
        return getJobParameterLong(JobParameterKey.BOOK_DEFINITION_ID);
    }

    @NotNull
    public String getBookVersion()
    {
        return getJobParameterString(JobParameterKey.BOOK_VERSION_SUBMITTED);
    }

    @NotNull
    public String getUserName()
    {
        return getJobParameterString(JobParameterKey.USER_NAME);
    }

    @NotNull
    public String getHostName()
    {
        return getJobParameterString(JobParameterKey.HOST_NAME);
    }

    @NotNull
    public String getEnvironment()
    {
        return getJobParameterString(JobParameterKey.ENVIRONMENT_NAME);
    }

    @NotNull
    public Date getSubmitTimestamp()
    {
        return getJobParameterDate(JobParameterKey.TIMESTAMP);
    }
}
