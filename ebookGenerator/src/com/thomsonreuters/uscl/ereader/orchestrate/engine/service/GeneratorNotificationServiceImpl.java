package com.thomsonreuters.uscl.ereader.orchestrate.engine.service;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.mail.internet.InternetAddress;

import com.thomsonreuters.uscl.ereader.JobParameterKey;
import com.thomsonreuters.uscl.ereader.common.notification.service.EmailService;
import com.thomsonreuters.uscl.ereader.core.book.domain.BookDefinition;
import com.thomsonreuters.uscl.ereader.core.service.EmailUtil;
import com.thomsonreuters.uscl.ereader.orchestrate.core.service.NotificationService;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.item.ExecutionContext;

@Slf4j
@Setter
public class GeneratorNotificationServiceImpl implements NotificationService {
    private static final String EMAIL_BODY_FORMAT = "eBook Publishing Failure - %s\nProview Display Name: %s\nEnvironment: %s\nJob Instance ID: %s\nJob Execution ID: %s\n%s";
    private EmailService emailService;
    private EmailUtil emailUtil;

    @Override
    public void sendNotification(
        final ExecutionContext jobExecutionContext,
        final JobParameters jobParams,
        final String bodyMessage,
        final long jobInstanceId,
        final long jobExecutionId) {
        final List<String> fileList = new ArrayList<>();
        final BookDefinition bookDefinition = (BookDefinition) jobExecutionContext.get(JobParameterKey.EBOOK_DEFINITON);
        final String jobEnvironment = jobParams.getString(JobParameterKey.ENVIRONMENT_NAME);

        // Determine the recipient of the email, use user preference value(s), otherwise use the group as the default
        final String username = jobParams.getString(JobParameterKey.USER_NAME);
        final Collection<InternetAddress> emailRecipients = emailUtil.getEmailRecipientsByUsername(username);

        final String subject = String.format(
            "eBook Publishing Failure:  %s  %s  %s  %s  %s",
            jobEnvironment,
            bookDefinition.getFullyQualifiedTitleId(),
            bookDefinition.getProviewDisplayName(),
            jobInstanceId,
            jobExecutionId);

        final String body = String.format(EMAIL_BODY_FORMAT,
                bookDefinition.getFullyQualifiedTitleId(),
                bookDefinition.getProviewDisplayName(),
                jobEnvironment,
                jobInstanceId,
                jobExecutionId,
                bodyMessage);

        getImageMissingGuidsFileFromContextPath(jobExecutionContext, fileList);
        getImageMissingGuidsFileFromGatherDocsDir(jobExecutionContext, fileList);

        if (!fileList.isEmpty()) {
            emailService.sendWithAttachment(emailRecipients, subject, body, fileList);
        } else {
            emailService.send(emailRecipients, subject, body);
        }
    }

    private void getImageMissingGuidsFileFromContextPath(
        final ExecutionContext jobExecutionContext,
        final List<String> fileList) {
        try {
            final String imgGuidsFile = jobExecutionContext.getString(JobParameterKey.IMAGE_MISSING_GUIDS_FILE);

            if (getFileSize(imgGuidsFile) > 0) {
                fileList.add(imgGuidsFile);
            }
        } catch (final Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    private void getImageMissingGuidsFileFromGatherDocsDir(
        final ExecutionContext jobExecutionContext,
        final List<String> fileList) {
        try {
            final String gatherDir = jobExecutionContext.getString(JobParameterKey.GATHER_DOCS_DIR);

            final String missingGuidsFile =
                StringUtils.substringBeforeLast(gatherDir, System.getProperty("file.separator"))
                    + "_doc_missing_guids.txt";

            if (getFileSize(missingGuidsFile) > 0) {
                fileList.add(missingGuidsFile);
            }
        } catch (final Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    /**
     * @param filename
     * @return a long value of file length
     */
    private long getFileSize(final String filename) {
        final File file = new File(filename);
        if (!file.exists() || !file.isFile()) {
            return -1;
        }
        return file.length();
    }
}
