package com.thomsonreuters.uscl.ereader.notification.step;

import java.util.Collection;

import javax.annotation.Resource;
import javax.mail.internet.InternetAddress;

import com.thomsonreuters.uscl.ereader.common.notification.service.EmailBuilder;
import com.thomsonreuters.uscl.ereader.common.notification.service.EmailBuilderFactory;
import com.thomsonreuters.uscl.ereader.common.notification.service.EmailService;
import com.thomsonreuters.uscl.ereader.common.publishingstatus.step.SavePublishingStatusPolicy;
import com.thomsonreuters.uscl.ereader.common.step.BookStepImpl;
import com.thomsonreuters.uscl.ereader.core.service.CoreService;
import com.thomsonreuters.uscl.ereader.stats.domain.PublishingStats;
import com.thomsonreuters.uscl.ereader.stats.service.PublishingStatsService;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.jetbrains.annotations.Nullable;
import org.springframework.batch.core.ExitStatus;
import org.springframework.util.Assert;

@SavePublishingStatusPolicy
public class SendEmailNotificationStepImpl extends BookStepImpl implements SendEmailNotificationStep
{
    private static final Logger LOG = LogManager.getLogger(SendEmailNotificationStepImpl.class);

    @Resource(name = "emailService")
    private EmailService emailService;
    @Resource(name = "coreService")
    private CoreService coreService;
    @Resource(name = "publishingStatsService")
    private PublishingStatsService publishingStatsService;
    @Resource(name = "sendNotificationEmailBuilderFactory")
    private EmailBuilderFactory emailBuilderFactory;

    private PublishingStats currentsStats;
    private PublishingStats previousStats;

    @Override
    public ExitStatus executeStep() throws Exception
    {
        final Collection<InternetAddress> recipients = coreService.getEmailRecipientsByUsername(getUserName());
        LOG.debug("Sending job completion notification to: " + recipients);

        getPublishingStats();
        final EmailBuilder emailBuilder = emailBuilderFactory.create();

        emailService.send(recipients, emailBuilder.getSubject(), emailBuilder.getBody());
        return ExitStatus.COMPLETED;
    }

    private void getPublishingStats()
    {
        currentsStats = publishingStatsService.findPublishingStatsByJobId(getJobInstanceId());
        Assert.notNull(currentsStats, "publishingStats not found for jobInstanceId=" + getJobInstanceId());
        previousStats = publishingStatsService.getPreviousPublishingStatsForSameBook(getJobInstanceId());
    }

    @Override
    @Nullable
    public Integer getTocNodeCount()
    {
        return currentsStats.getGatherTocNodeCount();
    }

    @Override
    @Nullable
    public Integer getThresholdValue()
    {
        return getBookDefinition().getDocumentTypeCodes().getThresholdValue();
    }

    @Override
    public PublishingStats getCurrentsStats()
    {
        return currentsStats;
    }

    @Override
    public PublishingStats getPreviousStats()
    {
        return previousStats;
    }
}
