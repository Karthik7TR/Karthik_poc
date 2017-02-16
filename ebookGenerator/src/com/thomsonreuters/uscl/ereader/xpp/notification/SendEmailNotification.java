package com.thomsonreuters.uscl.ereader.xpp.notification;

import java.util.Collection;

import javax.mail.internet.InternetAddress;

import com.thomsonreuters.uscl.ereader.JobParameterKey;
import com.thomsonreuters.uscl.ereader.StatsUpdateTypeEnum;
import com.thomsonreuters.uscl.ereader.core.book.domain.BookDefinition;
import com.thomsonreuters.uscl.ereader.orchestrate.core.service.NotificationService;
import com.thomsonreuters.uscl.ereader.orchestrate.core.tasklet.AbstractSbTasklet;
import com.thomsonreuters.uscl.ereader.stats.PublishingStatus;
import com.thomsonreuters.uscl.ereader.stats.domain.PublishingStats;
import com.thomsonreuters.uscl.ereader.stats.service.PublishingStatsService;
import com.thomsonreuters.uscl.ereader.util.EmailNotification;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.beans.factory.annotation.Required;

public class SendEmailNotification extends AbstractSbTasklet
{
    private static final Logger LOG = LogManager.getLogger(SendEmailNotification.class);
    private PublishingStatsService publishingStatsService;

    @Override
    public ExitStatus executeStep(final StepContribution contribution, final ChunkContext chunkContext) throws Exception
    {
        PublishingStatus publishStatus = PublishingStatus.COMPLETED;
        try
        {
            final Collection<InternetAddress> recipients = getRecipients(chunkContext);
            final String subject = getSubject(chunkContext);
            final String body = getBody(chunkContext);

            LOG.debug("recipients: " + recipients);
            EmailNotification.send(recipients, subject, body);
        }
        catch (final Exception e)
        {
            publishStatus = PublishingStatus.FAILED;
            LOG.error("Failed to send Email notification to the user ", e);
            throw e;
        }
        finally
        {
            updatePublishingStatus(chunkContext, publishStatus);
        }
        return ExitStatus.COMPLETED;
    }

    private void updatePublishingStatus(final ChunkContext chunkContext, final PublishingStatus publishStatus)
    {
        final PublishingStats jobstats = new PublishingStats();
        jobstats.setJobInstanceId(getJobInstance(chunkContext).getId());
        jobstats.setPublishStatus("sendEmailNotification : " + publishStatus);
        publishingStatsService.updatePublishingStats(jobstats, StatsUpdateTypeEnum.GENERAL);
    }

    Collection<InternetAddress> getRecipients(final ChunkContext chunkContext)
    {
        final JobParameters jobParams = getJobParameters(chunkContext);
        return coreService.getEmailRecipientsByUsername(jobParams.getString(JobParameterKey.USER_NAME));
    }

    String getSubject(final ChunkContext chunkContext)
    {
        final BookDefinition bookDefinition =
            (BookDefinition) getJobExecutionContext(chunkContext).get(JobParameterKey.EBOOK_DEFINITON);
        final StringBuilder sb = new StringBuilder();
        sb.append("eBook Shell XPP job - " + bookDefinition.getFullyQualifiedTitleId());
        return sb.toString();
    }

    String getBody(final ChunkContext chunkContext)
    {
        final BookDefinition bookDefinition =
            (BookDefinition) getJobExecutionContext(chunkContext).get(JobParameterKey.EBOOK_DEFINITON);
        final JobParameters jobParams = getJobParameters(chunkContext);
        final StepExecution stepExecution = chunkContext.getStepContext().getStepExecution();
        final long jobInstanceId = stepExecution.getJobExecution().getJobInstance().getId();
        final long jobExecutionId = stepExecution.getJobExecutionId();

        final String fullyQualifiedTitleId = bookDefinition.getFullyQualifiedTitleId();
        final String proviewDisplayName = bookDefinition.getProviewDisplayName();

        final StringBuilder sb = new StringBuilder();
        sb.append("eBook Publishing Successful - " + fullyQualifiedTitleId);
        sb.append("\t\nProview Display Name: " + proviewDisplayName);
        sb.append("\t\nTitle ID: " + fullyQualifiedTitleId);
        sb.append("\t\nEnvironment: " + jobParams.getString(JobParameterKey.ENVIRONMENT_NAME));
        sb.append("\t\nJob Instance ID: " + jobInstanceId);
        sb.append("\t\nJob Execution ID: " + jobExecutionId);
        return sb.toString();
    }

    @Override
    @Required
    public void setNotificationService(final NotificationService service)
    {
        notificationService = service;
    }

    @Required
    public void setPublishingStatsService(final PublishingStatsService publishingStatsService)
    {
        this.publishingStatsService = publishingStatsService;
    }
}
