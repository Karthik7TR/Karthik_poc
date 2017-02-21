package com.thomsonreuters.uscl.ereader.common.step;

import java.io.File;
import java.util.Date;

import com.thomsonreuters.uscl.ereader.JobExecutionKey;
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

public abstract class BookStepImpl extends BaseStepImpl
    implements BookStep, OutageAwareStep, SendNotificationStep, PublishingStatusUpdateStep
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
    public String getBookVersion()
    {
        return getJobParameterString(JobParameterKey.BOOK_VERSION_SUBMITTED);
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

    @Override
    @NotNull
    public File getWorkDirectory()
    {
        return new File(getJobExecutionPropertyString(JobExecutionKey.WORK_DIRECTORY));
    }

    @Override
    @NotNull
    public File getAssembleDirectory()
    {
        return new File(getWorkDirectory(), "Assemble");
    }

    @Override
    @NotNull
    public File getAssembleTitleDirectory()
    {
        final BookDefinition bookDefinition = getBookDefinition();
        return new File(getAssembleDirectory(), bookDefinition.getTitleId());
    }

    @Override
    @NotNull
    public File getAssembleAssetsDirectory()
    {
        return new File(getAssembleTitleDirectory(), "assets");
    }

    @Override
    @NotNull
    public File getAssembleDocumentsDirectory()
    {
        return new File(getAssembleTitleDirectory(), "documents");
    }

    @Override
    @NotNull
    public File getAssembleSplitTitleDirectory(final String splitTitleId)
    {
        return new File(getAssembleDirectory(), splitTitleId);
    }

    @Override
    @NotNull
    public File getAssembledBookFile()
    {
        final BookDefinition bookDefinition = getBookDefinition();
        return new File(getWorkDirectory(), bookDefinition.getTitleId() + JobExecutionKey.BOOK_FILE_TYPE_SUFFIX);
    }

    @Override
    @NotNull
    public File getAssembledSplitTitleFile(final String splitTitleId)
    {
        return new File(getWorkDirectory(), splitTitleId + JobExecutionKey.BOOK_FILE_TYPE_SUFFIX);
    }

    @Override
    @NotNull
    public File getTitleXml()
    {
        return new File(getAssembleTitleDirectory(), "title.xml");
    }
}
