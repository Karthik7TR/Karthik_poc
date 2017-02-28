package com.thomsonreuters.uscl.ereader.xpp.common;

import java.util.Collection;

import javax.annotation.Resource;
import javax.mail.internet.InternetAddress;

import com.thomsonreuters.uscl.ereader.common.notification.service.EmailService;
import com.thomsonreuters.uscl.ereader.common.notification.service.SendFailureNotificationStrategy;
import com.thomsonreuters.uscl.ereader.common.notification.service.StepFailureNotificationService;
import com.thomsonreuters.uscl.ereader.common.notification.step.FailureNotificationType;
import com.thomsonreuters.uscl.ereader.common.step.BookStep;
import com.thomsonreuters.uscl.ereader.core.book.domain.BookDefinition;
import com.thomsonreuters.uscl.ereader.core.service.CoreService;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.jetbrains.annotations.NotNull;

@SendFailureNotificationStrategy(FailureNotificationType.XPP)
public class XppStepFailureNotificationServiceImpl implements StepFailureNotificationService<BookStep>
{
    @Resource(name = "coreService")
    private CoreService coreService;
    @Resource(name = "emailService")
    private EmailService emailService;

    @Override
    public void sendFailureNotification(final BookStep step, final Exception e)
    {
        final String username = step.getUserName();
        final Collection<InternetAddress> emailRecipients = coreService.getEmailRecipientsByUsername(username);
        emailService.send(emailRecipients, getSubject(step), getBody(step, e));
    }

    @NotNull
    private String getSubject(final BookStep step)
    {
        final String environment = step.getEnvironment();
        final BookDefinition bookDefinition = step.getBookDefinition();
        final String fullyQualifiedTitleId = bookDefinition.getFullyQualifiedTitleId();
        final String proviewDisplayName = bookDefinition.getProviewDisplayName();
        return String.format(
            "eBook Publishing Failure: %s  %s  %s  %s  %s",
            environment,
            fullyQualifiedTitleId,
            proviewDisplayName,
            step.getJobInstanceId(),
            step.getJobExecutionId());
    }

    @NotNull
    private String getBody(final BookStep step, final Exception e)
    {
        return String
            .format("%s%nError Message : %s%n%s", getSubject(step), e.getMessage(), ExceptionUtils.getStackTrace(e));
    }
}
