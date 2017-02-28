package com.thomsonreuters.uscl.ereader.xpp.notification;

import java.util.Collection;

import javax.annotation.Resource;
import javax.mail.internet.InternetAddress;

import com.thomsonreuters.uscl.ereader.common.notification.service.EmailService;
import com.thomsonreuters.uscl.ereader.common.notification.step.FailureNotificationType;
import com.thomsonreuters.uscl.ereader.common.notification.step.SendFailureNotificationPolicy;
import com.thomsonreuters.uscl.ereader.common.publishingstatus.step.SavePublishingStatusPolicy;
import com.thomsonreuters.uscl.ereader.common.step.BookStepImpl;
import com.thomsonreuters.uscl.ereader.core.book.domain.BookDefinition;
import com.thomsonreuters.uscl.ereader.core.service.CoreService;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.springframework.batch.core.ExitStatus;

@SendFailureNotificationPolicy(FailureNotificationType.XPP)
@SavePublishingStatusPolicy
public class SendEmailNotification extends BookStepImpl
{
    private static final Logger LOG = LogManager.getLogger(SendEmailNotification.class);

    @Resource(name = "emailService")
    private EmailService emailService;
    @Resource(name = "coreService")
    private CoreService coreService;

    @Override
    public ExitStatus executeStep() throws Exception
    {
        final Collection<InternetAddress> recipients = getRecipients();
        LOG.debug("recipients: " + recipients);

        emailService.send(recipients, getEmailSubject(), getEmailBody());
        return ExitStatus.COMPLETED;
    }

    @NotNull
    private Collection<InternetAddress> getRecipients()
    {
        final String userName = getUserName();
        return coreService.getEmailRecipientsByUsername(userName);
    }

    @NotNull
    private String getEmailSubject()
    {
        final BookDefinition bookDefinition = getBookDefinition();
        return String.format("eBook Shell XPP job - %s", bookDefinition.getFullyQualifiedTitleId());
    }

    @NotNull
    private String getEmailBody()
    {
        final BookDefinition bookDefinition = getBookDefinition();
        final String fullyQualifiedTitleId = bookDefinition.getFullyQualifiedTitleId();
        final String proviewDisplayName = bookDefinition.getProviewDisplayName();

        final StringBuilder sb = new StringBuilder();
        sb.append("eBook Publishing Successful - " + fullyQualifiedTitleId);
        sb.append("\t\nProview Display Name: " + proviewDisplayName);
        sb.append("\t\nTitle ID: " + fullyQualifiedTitleId);
        sb.append("\t\nEnvironment: " + getEnvironment());
        sb.append("\t\nJob Instance ID: " + getJobInstanceId());
        sb.append("\t\nJob Execution ID: " + getJobExecutionId());
        return sb.toString();
    }
}
