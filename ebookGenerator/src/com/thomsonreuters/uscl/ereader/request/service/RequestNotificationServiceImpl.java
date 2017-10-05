package com.thomsonreuters.uscl.ereader.request.service;

import java.util.Collection;
import java.util.HashSet;

import javax.mail.internet.InternetAddress;

import com.thomsonreuters.uscl.ereader.JobParameterKey;
import com.thomsonreuters.uscl.ereader.core.service.CoreService;
import com.thomsonreuters.uscl.ereader.orchestrate.core.service.NotificationService;
import com.thomsonreuters.uscl.ereader.request.domain.XppBundleArchive;
import com.thomsonreuters.uscl.ereader.util.EmailNotification;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.beans.factory.annotation.Required;

public class RequestNotificationServiceImpl implements NotificationService {
    private CoreService coreService;

    @Override
    public void sendNotification(
        final ExecutionContext jobExecutionContext,
        final JobParameters jobParams,
        String bodyMessage,
        final long jobInstanceId,
        final long jobExecutionId) {
        final String subject;
        final String failedJobInfo;
        final XppBundleArchive xppBundleArchive =
            (XppBundleArchive) jobExecutionContext.get(JobParameterKey.KEY_XPP_BUNDLE);
        final String jobEnvironment = jobParams.getString(JobParameterKey.ENVIRONMENT_NAME);

        if (xppBundleArchive != null) {
            failedJobInfo = "eBook Request Failure:  "
                + jobEnvironment
                + "\nMessage ID: "
                + xppBundleArchive.getMessageId()
                + "\nMaterial Number: "
                + xppBundleArchive.getMaterialNumber()
                + "\nJob Instance ID: "
                + jobInstanceId
                + "\nJob Execution ID: "
                + jobExecutionId;
        } else {
            failedJobInfo = "eBook Request Failure:  " + jobParams.getString(JobParameterKey.KEY_REQUEST_XML);
        }
        bodyMessage = failedJobInfo + "  \n" + bodyMessage;
        subject = failedJobInfo;

        final Collection<InternetAddress> emailRecipients =
            coreService.createEmailRecipients(new HashSet<InternetAddress>());
        EmailNotification.send(emailRecipients, subject, bodyMessage);
    }

    @Required
    public void setCoreService(final CoreService service) {
        coreService = service;
    }
}
