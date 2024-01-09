package com.thomsonreuters.uscl.ereader.xpp.common;

import java.util.Collection;

import javax.annotation.Resource;
import javax.mail.internet.InternetAddress;

import com.thomsonreuters.uscl.ereader.common.notification.entity.NotificationEmail;
import com.thomsonreuters.uscl.ereader.common.notification.service.EmailService;
import com.thomsonreuters.uscl.ereader.common.notification.service.SendFailureNotificationStrategy;
import com.thomsonreuters.uscl.ereader.common.notification.service.StepFailureNotificationService;
import com.thomsonreuters.uscl.ereader.common.notification.step.FailureNotificationType;
import com.thomsonreuters.uscl.ereader.common.step.BookStep;
import com.thomsonreuters.uscl.ereader.core.book.domain.BookDefinition;
import com.thomsonreuters.uscl.ereader.core.service.EmailUtil;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;

@SendFailureNotificationStrategy(FailureNotificationType.XPP)
public class XppStepFailureNotificationServiceImpl implements StepFailureNotificationService<BookStep> {
    @Autowired
    private EmailUtil emailUtil;
    @Resource(name = "emailService")
    private EmailService emailService;

    @Override
    public void sendFailureNotification(final BookStep step, final Exception e) {
        final String username = step.getUserName();
        final Collection<InternetAddress> emailRecipients = emailUtil.getEmailRecipientsByUsername(username);
        emailService.send(new NotificationEmail(emailRecipients, getSubject(step), getBody(step, e)));
    }

    @NotNull
    private String getSubject(final BookStep step) {
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
    private String getBody(final BookStep step, final Exception e) {
        return String
            .format("%s%nError Message : %s%n%s", getSubject(step), e.getMessage(), ExceptionUtils.getStackTrace(e));
    }
}
