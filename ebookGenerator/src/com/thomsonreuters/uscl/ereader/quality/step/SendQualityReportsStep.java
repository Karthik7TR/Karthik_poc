package com.thomsonreuters.uscl.ereader.quality.step;

import static com.thomsonreuters.uscl.ereader.JobParameterKey.QUALITY_REPORTS;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;

import com.thomsonreuters.uscl.ereader.JobParameterKey;
import com.thomsonreuters.uscl.ereader.common.EBookApps;
import com.thomsonreuters.uscl.ereader.common.notification.step.FailureNotificationType;
import com.thomsonreuters.uscl.ereader.common.notification.step.SendFailureNotificationPolicy;
import com.thomsonreuters.uscl.ereader.common.publishingstatus.step.SavePublishingStatusPolicy;
import com.thomsonreuters.uscl.ereader.common.step.BookStepImpl;
import com.thomsonreuters.uscl.ereader.core.quality.domain.QualityReportRecipient;
import com.thomsonreuters.uscl.ereader.core.quality.service.QualityReportsRecipientService;
import com.thomsonreuters.uscl.ereader.core.service.EmailUtil;
import com.thomsonreuters.uscl.ereader.quality.domain.email.QualityReportEmail;
import com.thomsonreuters.uscl.ereader.quality.service.QualityEmailService;
import org.springframework.batch.core.ExitStatus;
import org.springframework.beans.factory.annotation.Autowired;

@SendFailureNotificationPolicy(FailureNotificationType.XPP)
@SavePublishingStatusPolicy
public class SendQualityReportsStep extends BookStepImpl {
    @Autowired
    private EmailUtil emailUtil;
    @Autowired
    private QualityReportsRecipientService qualityReportRecipientService;
    @Autowired
    private QualityEmailService qualityEmailService;

    @Override
    public ExitStatus executeStep() throws Exception {
        final Map<String, List<File>> reports = (Map<String, List<File>>) getJobExecutionContext().get(QUALITY_REPORTS);
        final List<InternetAddress> recipients = Stream.concat(getPublishers(), getQualityReportsRecipients())
            .collect(Collectors.toList());

        final QualityReportEmail reportEmail = QualityReportEmail.builder()
            .environment(getEnvironment())
            .jobId(getJobInstanceId())
            .host(getJobParameterString(JobParameterKey.HOST_NAME))
            .port(EBookApps.GENERATOR.getEnv(getEnvironment()).port())
            .titleId(getBookDefinition().getFullyQualifiedTitleId())
            .reports(reports)
            .recipients(recipients)
            .build();
        qualityEmailService.sendReportEmail(reportEmail);
        return ExitStatus.COMPLETED;
    }

    private Stream<InternetAddress> getPublishers() {
        return emailUtil.getEmailRecipientsByUsername(getUserName())
            .stream();
    }

    private Stream<InternetAddress> getQualityReportsRecipients() {
        return qualityReportRecipientService.getAll()
            .stream()
            .map(QualityReportRecipient::getEmail)
            .map(email -> {
                try {
                    return new InternetAddress(email);
                } catch (final AddressException e) {
                    throw new RuntimeException(e);
                }
            });
    }
}
