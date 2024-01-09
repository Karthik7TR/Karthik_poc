package com.thomsonreuters.uscl.ereader.request.service;

import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;

import javax.mail.internet.InternetAddress;

import com.thomsonreuters.uscl.ereader.JobParameterKey;
import com.thomsonreuters.uscl.ereader.common.notification.entity.NotificationEmail;
import com.thomsonreuters.uscl.ereader.common.notification.service.EmailService;
import com.thomsonreuters.uscl.ereader.core.service.EmailUtil;
import com.thomsonreuters.uscl.ereader.orchestrate.core.service.NotificationService;
import com.thomsonreuters.uscl.ereader.request.domain.XppBundleArchive;
import lombok.Setter;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.item.ExecutionContext;

@Setter
public class RequestNotificationServiceImpl implements NotificationService {
    private EmailUtil emailUtil;
    private EmailService emailService;

    @Override
    public void sendNotification(
        final ExecutionContext jobExecutionContext,
        final JobParameters jobParams,
        final String bodyMessage,
        final long jobInstanceId,
        final long jobExecutionId) {
        final XppBundleArchive xppBundleArchive =
            (XppBundleArchive) jobExecutionContext.get(JobParameterKey.KEY_XPP_BUNDLE);
        final String jobEnvironment = jobParams.getString(JobParameterKey.ENVIRONMENT_NAME);

        final String subject = Objects.nonNull(xppBundleArchive)
            ? String.format(
                "eBook Request Failure:  %s%n"
                    + "Message ID: %s%n"
                    + "Material Number: %s%n"
                    + "Job Instance ID: %s%n"
                    + "Job Execution ID: %s",
                jobEnvironment,
                xppBundleArchive.getMessageId(),
                xppBundleArchive.getMaterialNumber(),
                jobInstanceId,
                jobExecutionId)
            : String.format("eBook Request Failure:  %s", jobParams.getString(JobParameterKey.KEY_REQUEST_XML));
        final String body = String.format("%s  %n%s", subject, bodyMessage);

        final Collection<InternetAddress> emailRecipients =
            emailUtil.createEmailRecipients(new HashSet<InternetAddress>());
        emailService.send(new NotificationEmail(emailRecipients, subject, body));
    }
}
