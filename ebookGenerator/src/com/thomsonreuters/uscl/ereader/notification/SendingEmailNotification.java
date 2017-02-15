package com.thomsonreuters.uscl.ereader.notification;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import javax.mail.internet.InternetAddress;

import com.thomsonreuters.uscl.ereader.JobExecutionKey;
import com.thomsonreuters.uscl.ereader.StatsUpdateTypeEnum;
import com.thomsonreuters.uscl.ereader.core.book.domain.BookDefinition;
import com.thomsonreuters.uscl.ereader.format.service.AutoSplitGuidsService;
import com.thomsonreuters.uscl.ereader.gather.metadata.service.DocMetadataService;
import com.thomsonreuters.uscl.ereader.orchestrate.core.tasklet.AbstractSbTasklet;
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
import org.springframework.batch.item.ExecutionContext;
import org.springframework.beans.factory.annotation.Required;

/**
 * @author ravi.nandikolla@thomsonreuters.com c139353
 *
 */
public class SendingEmailNotification extends AbstractSbTasklet
{
    private static final Logger log = LogManager.getLogger(SendingEmailNotification.class);
    private PublishingStatsService publishingStatsService;
    private AutoSplitGuidsService autoSplitGuidsService;
    private DocMetadataService docMetadataService;

    @Override
    public ExitStatus executeStep(final StepContribution contribution, final ChunkContext chunkContext) throws Exception
    {
        String publishStatus = "Completed";
        try
        {
            final NotificationInfo info = getNotificationInfo(chunkContext);

            final Collection<InternetAddress> recipients = coreService.getEmailRecipientsByUsername(info.getUserName());
            log.debug("Sending job completion notification to: " + recipients);
            final String subject = getSubject(info);
            final String body = getBody(info);
            EmailNotification.send(recipients, subject, body);
        }
        catch (final Exception e)
        {
            publishStatus = "Failed";
            log.error("Failed to send Email notification to the user ", e);
            throw e;
        }
        finally
        {
            final PublishingStats jobstats = new PublishingStats();
            jobstats.setJobInstanceId(getJobInstance(chunkContext).getId());
            jobstats.setPublishStatus("sendEmailNotification : " + publishStatus);
            publishingStatsService.updatePublishingStats(jobstats, StatsUpdateTypeEnum.GENERAL);
        }
        return ExitStatus.COMPLETED;
    }

    NotificationInfo getNotificationInfo(final ChunkContext chunkContext)
    {
        final ExecutionContext jobExecutionContext = getJobExecutionContext(chunkContext);
        final JobParameters jobParams = getJobParameters(chunkContext);

        final StepExecution stepExecution = chunkContext.getStepContext().getStepExecution();
        final long jobInstanceId = stepExecution.getJobExecution().getJobInstance().getId();
        final long jobExecutionId = stepExecution.getJobExecutionId();
        final PublishingStats publishingStats = publishingStatsService.findPublishingStatsByJobId(jobInstanceId);
        if (publishingStats == null)
        {
            throw new RuntimeException("publishingStats not found for jobInstanceId=" + jobInstanceId);
        }
        final PublishingStats previousStats =
            publishingStatsService.getPreviousPublishingStatsForSameBook(jobInstanceId);
        return new NotificationInfo(
            jobExecutionContext,
            jobParams,
            jobInstanceId,
            jobExecutionId,
            publishingStats,
            previousStats);
    }

    String getSubject(final NotificationInfo info)
    {
        final BookDefinition bookDefinition = info.getBookDefinition();
        final StringBuilder sb = new StringBuilder();
        sb.append("eBook Publishing Successful - " + bookDefinition.getFullyQualifiedTitleId());
        if (bookDefinition.isSplitBook())
        {
            sb.append(" (Split Book)");
        }
        else if (info.isBigToc())
        {
            sb.append(" THRESHOLD WARNING");
        }
        return sb.toString();
    }

    String getBody(final NotificationInfo info)
    {
        final BookDefinition bookDefinition = info.getBookDefinition();
        final String fullyQualifiedTitleId = bookDefinition.getFullyQualifiedTitleId();
        final String proviewDisplayName = bookDefinition.getProviewDisplayName();

        final StringBuilder sb = new StringBuilder();
        sb.append("eBook Publishing Successful - " + fullyQualifiedTitleId);
        sb.append("\t\nProview Display Name: " + proviewDisplayName);
        sb.append("\t\nTitle ID: " + fullyQualifiedTitleId);
        sb.append("\t\nEnvironment: " + info.getEnvironment());
        sb.append("\t\nJob Instance ID: " + info.getJobInstanceId());
        sb.append("\t\nJob Execution ID: " + info.getJobExecutionId());

        sb.append(getVersionInfo(true, info));
        sb.append(getVersionInfo(false, info));

        if (bookDefinition.isSplitBook())
        {
            sb.append(getBookPartsAndTitle(info));
        }
        else if (info.isBigToc())
        {
            sb.append(getMetricsInfo(info));
        }
        return sb.toString();
    }

    String getVersionInfo(final boolean isCurrent, final NotificationInfo info)
    {
        final PublishingStats stats = isCurrent ? info.getCurrentPublishingStats() : info.getPreviousPublishingStats();
        if (stats == null)
        {
            return "\t\n\t\nNo Previous Version.";
        }
        final Long bookSize = stats.getBookSize();
        final Integer docRetrievedCount = stats.getGatherDocRetrievedCount();
        final StringBuilder sb = new StringBuilder();
        sb.append("\t\n\t\n");
        sb.append(isCurrent ? "Current Version:" : "Previous Version:");
        sb.append("\t\nJob Instance ID: " + stats.getJobInstanceId());
        sb.append("\t\nGather Doc Retrieved Count: " + (docRetrievedCount == null ? "-" : docRetrievedCount));
        sb.append("\t\nBook Size: " + (bookSize == null ? "-" : bookSize));
        return sb.toString();
    }

    String getBookPartsAndTitle(final NotificationInfo info)
    {
        final BookDefinition bookDefinition = info.getBookDefinition();
        final String fullyQualifiedTitleId = bookDefinition.getFullyQualifiedTitleId();
        final String proviewDisplayName = bookDefinition.getProviewDisplayName();
        final List<String> splitTitles = docMetadataService.findDistinctSplitTitlesByJobId(info.getJobInstanceId());

        final StringBuilder sb = new StringBuilder();
        sb.append("\t\n\t\nPlease find the below information regarding the split titles");
        sb.append("\t\nProview display name : " + proviewDisplayName);
        sb.append("\t\nFully Qualified Title : " + fullyQualifiedTitleId);
        sb.append("\t\nTotal parts : " + splitTitles.size());
        sb.append("\t\nSplit Title Id's :");
        for (final String splitTitleId : splitTitles)
        {
            sb.append("\t\n" + splitTitleId);
        }
        return sb.toString();
    }

    String getMetricsInfo(final NotificationInfo info)
    {
        final int totalSplitParts = getTotalSplitParts(info);
        final Map<String, String> splitGuidTextMap = autoSplitGuidsService.getSplitGuidTextMap();

        final StringBuilder sb = new StringBuilder();
        sb.append("\t\n\t\n**WARNING**: The book exceeds threshold value " + info.getThresholdValue());
        sb.append("\t\nTotal node count is " + info.getTocNodeCount());
        sb.append("\t\nPlease find the below system suggested information");
        sb.append("\t\nTotal split parts : " + totalSplitParts);
        sb.append("\t\nTOC/NORT guids :");
        for (final Map.Entry<String, String> entry : splitGuidTextMap.entrySet())
        {
            final String uuid = entry.getKey();
            final String name = entry.getValue();
            sb.append("\t\n" + uuid + "  :  " + name);
        }
        return sb.toString();
    }

    int getTotalSplitParts(final NotificationInfo info)
    {
        final BookDefinition bookDefinition = info.getBookDefinition();
        final String tocXmlFile =
            getRequiredStringProperty(info.getJobExecutionContext(), JobExecutionKey.GATHER_TOC_FILE);
        final Integer tocNodeCount = info.getTocNodeCount();
        final Long jobInstanceId = info.getJobInstanceId();

        try (InputStream tocInputSteam = new FileInputStream(tocXmlFile))
        {
            return autoSplitGuidsService
                .getAutoSplitNodes(tocInputSteam, bookDefinition, tocNodeCount, jobInstanceId, true).size() + 1;
        }
        catch (final IOException e)
        {
            throw new RuntimeException("Cannot read file " + tocXmlFile, e);
        }
    }

    @Required
    public void setDocMetadataService(final DocMetadataService docMetadataService)
    {
        this.docMetadataService = docMetadataService;
    }

    @Required
    public void setAutoSplitGuidsService(final AutoSplitGuidsService autoSplitGuidsService)
    {
        this.autoSplitGuidsService = autoSplitGuidsService;
    }

    @Required
    public void setPublishingStatsService(final PublishingStatsService publishingStatsService)
    {
        this.publishingStatsService = publishingStatsService;
    }
}
