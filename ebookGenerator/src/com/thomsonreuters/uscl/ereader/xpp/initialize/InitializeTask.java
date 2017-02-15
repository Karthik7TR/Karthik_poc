package com.thomsonreuters.uscl.ereader.xpp.initialize;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.jms.IllegalStateException;

import com.thomsonreuters.uscl.ereader.JobExecutionKey;
import com.thomsonreuters.uscl.ereader.JobParameterKey;
import com.thomsonreuters.uscl.ereader.core.CoreConstants;
import com.thomsonreuters.uscl.ereader.core.book.domain.BookDefinition;
import com.thomsonreuters.uscl.ereader.core.book.domain.EbookAudit;
import com.thomsonreuters.uscl.ereader.core.book.service.BookDefinitionService;
import com.thomsonreuters.uscl.ereader.core.book.service.EBookAuditService;
import com.thomsonreuters.uscl.ereader.orchestrate.core.tasklet.AbstractSbTasklet;
import com.thomsonreuters.uscl.ereader.stats.PublishingStatus;
import com.thomsonreuters.uscl.ereader.stats.domain.PublishingStats;
import com.thomsonreuters.uscl.ereader.stats.service.PublishingStatsService;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobInstance;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.beans.factory.annotation.Required;

/**
 * Perform job setup for creating an ebook and place data into the JobExecutionContext for use in later steps. This
 * includes various file system path calculations based on the JobParameters used to run the job.
 */
public class InitializeTask extends AbstractSbTasklet
{
    private static final Logger LOG = LogManager.getLogger(InitializeTask.class);

    private File rootWorkDirectory; // "/nas/ebookbuilder/data"
    private String environmentName;
    private PublishingStatsService publishingStatsService;
    private EBookAuditService eBookAuditService;
    private BookDefinitionService bookDefnService;

    @Override
    public ExitStatus executeStep(final StepContribution contribution, final ChunkContext chunkContext) throws Exception
    {
        final JobExecution jobExecution = chunkContext.getStepContext().getStepExecution().getJobExecution();
        final ExecutionContext jobExecutionContext = jobExecution.getExecutionContext();
        final JobInstance jobInstance = jobExecution.getJobInstance();
        final JobParameters jobParams = jobExecution.getJobParameters();
        PublishingStatus publishStatus = PublishingStatus.COMPLETED;
        try
        {
            final BookDefinition bookDefinition = getBookDefinition(jobExecutionContext, jobParams);
            createWorkDir(jobExecutionContext, jobInstance, bookDefinition);

            LOG.debug("Proview Domain URL: " + System.getProperty("proview.domain"));
        }
        catch (final Exception e)
        {
            publishStatus = PublishingStatus.FAILED;
            throw e;
        }
        finally
        {
            createPublishingStats(jobInstance, jobParams, publishStatus);
        }
        return ExitStatus.COMPLETED;
    }

    private BookDefinition getBookDefinition(final ExecutionContext jobExecutionContext, final JobParameters jobParams)
    {
        final BookDefinition bookDefinition =
            bookDefnService.findBookDefinitionByEbookDefId(jobParams.getLong(JobParameterKey.BOOK_DEFINITION_ID));
        jobExecutionContext.put(JobExecutionKey.EBOOK_DEFINITION, bookDefinition);
        LOG.debug("titleId (Fully Qualified): " + bookDefinition.getTitleId());
        LOG.debug("hostname: " + jobParams.getString(JobParameterKey.HOST_NAME));
        return bookDefinition;
    }

    /**
     * Create the work directory for the ebook and create the physical directory in the filesystem
     * "yyyyMMdd/titleId/jobInstanceId". Sample: "/apps/eBookBuilder/prod/data/20120131/FRCP/356"
     *
     * @param jobExecutionContext
     * @param jobInstance
     * @param bookDefinition
     */
    private void createWorkDir(
        final ExecutionContext jobExecutionContext,
        final JobInstance jobInstance,
        final BookDefinition bookDefinition) throws IllegalStateException
    {
        final String dateStr = new SimpleDateFormat(CoreConstants.DIR_DATE_FORMAT).format(new Date());
        final String dynamicPath = String.format(
            "%s/%s/%s/%s/%d",
            environmentName,
            CoreConstants.DATA_DIR,
            dateStr,
            bookDefinition.getTitleId(),
            jobInstance.getId());
        final File workDirectory = new File(rootWorkDirectory, dynamicPath);
        workDirectory.mkdirs();
        if (!workDirectory.exists())
        {
            throw new IllegalStateException(
                "Expected work directory was not created in the filesystem: " + workDirectory.getAbsolutePath());
        }
        jobExecutionContext.putString(JobExecutionKey.WORK_DIRECTORY, workDirectory.getAbsolutePath());
        LOG.debug("workDirectory: " + workDirectory.getAbsolutePath());
    }

    private void createPublishingStats(
        final JobInstance jobInstance,
        final JobParameters jobParams,
        final PublishingStatus publishStatus)
    {
        final PublishingStats pubStats = new PublishingStats();
        final Date rightNow = new Date();
        final Long ebookDefId = jobParams.getLong(JobParameterKey.BOOK_DEFINITION_ID);
        pubStats.setEbookDefId(ebookDefId);
        final Long auditId = eBookAuditService.findEbookAuditByEbookDefId(ebookDefId);
        final EbookAudit audit = new EbookAudit();
        audit.setAuditId(auditId);
        pubStats.setAudit(audit);
        pubStats.setBookVersionSubmitted(jobParams.getString(JobParameterKey.BOOK_VERSION_SUBMITTED));
        pubStats.setJobHostName(jobParams.getString(JobParameterKey.HOST_NAME));
        pubStats.setJobInstanceId(Long.valueOf(jobInstance.getId().toString()));
        pubStats.setJobSubmitterName(jobParams.getString(JobParameterKey.USER_NAME));
        pubStats.setJobSubmitTimestamp(jobParams.getDate(JobParameterKey.TIMESTAMP));
        pubStats.setPublishStatus("initializeXPPJob : " + publishStatus);
        pubStats.setPublishStartTimestamp(rightNow);
        pubStats.setLastUpdated(rightNow);
        publishingStatsService.savePublishingStats(pubStats);
    }

    @Required
    public void setRootWorkDirectory(final File rootDir)
    {
        rootWorkDirectory = rootDir;
    }

    @Required
    public void setEnvironmentName(final String envName)
    {
        environmentName = envName;
    }

    @Required
    public void setPublishingStatsService(final PublishingStatsService publishingStatsService)
    {
        this.publishingStatsService = publishingStatsService;
    }

    @Required
    public void setEbookAuditService(final EBookAuditService eBookAuditService)
    {
        this.eBookAuditService = eBookAuditService;
    }

    @Required
    public void setBookDefnService(final BookDefinitionService bookDefnService)
    {
        this.bookDefnService = bookDefnService;
    }
}
