package com.thomsonreuters.uscl.ereader.common.step;

import java.util.Date;

import com.thomsonreuters.uscl.ereader.JobParameterKey;
import com.thomsonreuters.uscl.ereader.core.book.domain.BookDefinition;
import com.thomsonreuters.uscl.ereader.core.book.model.Version;
import org.jetbrains.annotations.NotNull;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.util.Assert;

public abstract class BookStepImpl extends BaseStepImpl implements BookStep
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

    @Override
    @NotNull
    public BookDefinition getBookDefinition()
    {
        final BookDefinition book = (BookDefinition) getJobExecutionContext().get(JobParameterKey.EBOOK_DEFINITON);
        Assert.notNull(book);
        return book;
    }

    @Override
    @NotNull
    public Long getBookDefinitionId()
    {
        return getJobParameterLong(JobParameterKey.BOOK_DEFINITION_ID);
    }

    @Override
    @NotNull
    public String getBookVersionString()
    {
        return getJobParameterString(JobParameterKey.BOOK_VERSION_SUBMITTED);
    }

    @Override
    @NotNull
    public Version getBookVersion()
    {
        return new Version(Version.VERSION_PREFIX + getBookVersionString());
    }

    @Override
    @NotNull
    public String getUserName()
    {
        return getJobParameterString(JobParameterKey.USER_NAME);
    }

    @Override
    @NotNull
    public String getHostName()
    {
        return getJobParameterString(JobParameterKey.HOST_NAME);
    }

    @Override
    @NotNull
    public String getEnvironment()
    {
        return getJobParameterString(JobParameterKey.ENVIRONMENT_NAME);
    }

    @Override
    @NotNull
    public Date getSubmitTimestamp()
    {
        return getJobParameterDate(JobParameterKey.TIMESTAMP);
    }
}
