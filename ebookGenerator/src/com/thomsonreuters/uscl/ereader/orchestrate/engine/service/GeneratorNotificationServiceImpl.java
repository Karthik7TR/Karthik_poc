package com.thomsonreuters.uscl.ereader.orchestrate.engine.service;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.mail.internet.InternetAddress;

import com.thomsonreuters.uscl.ereader.JobParameterKey;
import com.thomsonreuters.uscl.ereader.core.book.domain.BookDefinition;
import com.thomsonreuters.uscl.ereader.core.service.CoreService;
import com.thomsonreuters.uscl.ereader.orchestrate.core.service.NotificationService;
import com.thomsonreuters.uscl.ereader.util.EmailNotification;
import org.apache.commons.lang3.StringUtils;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.beans.factory.annotation.Required;

public class GeneratorNotificationServiceImpl implements NotificationService
{
    private CoreService coreService;

    @Override
    public void sendNotification(
        final ExecutionContext jobExecutionContext,
        final JobParameters jobParams,
        String bodyMessage,
        final long jobInstanceId,
        final long jobExecutionId)
    {
        final List<String> fileList = new ArrayList<>();
        final String subject;
        final String failedJobInfo;
        final BookDefinition bookDefinition = (BookDefinition) jobExecutionContext.get(JobParameterKey.EBOOK_DEFINITON);
        final String jobEnvironment = jobParams.getString(JobParameterKey.ENVIRONMENT_NAME);

        // Determine the recipient of the email, use user preference value(s), otherwise use the group as the default
        final String username = jobParams.getString(JobParameterKey.USER_NAME);
        final Collection<InternetAddress> emailRecipients = coreService.getEmailRecipientsByUsername(username);

        failedJobInfo = "eBook Publishing Failure:  "
            + jobEnvironment
            + "  "
            + bookDefinition.getFullyQualifiedTitleId()
            + "  "
            + bookDefinition.getProviewDisplayName()
            + "  "
            + jobInstanceId
            + "  "
            + jobExecutionId;
        bodyMessage = failedJobInfo + "  \n" + bodyMessage;
        subject = failedJobInfo;

        final String imgGuidsFile = jobExecutionContext.getString(JobParameterKey.IMAGE_MISSING_GUIDS_FILE);

        if (getFileSize(imgGuidsFile) > 0)
        {
            fileList.add(imgGuidsFile);
        }

        final String gatherDir = jobExecutionContext.getString(JobParameterKey.GATHER_DOCS_DIR);

        final String missingGuidsFile =
            StringUtils.substringBeforeLast(gatherDir, System.getProperty("file.separator")) + "_doc_missing_guids.txt";

        if (getFileSize(missingGuidsFile) > 0)
        {
            fileList.add(missingGuidsFile);
        }

        if (fileList.size() > 0)
        {
            EmailNotification.sendWithAttachment(emailRecipients, subject, bodyMessage.toString(), fileList);
        }
        else
        {
            EmailNotification.send(emailRecipients, subject, bodyMessage.toString());
        }
    }

    /**
     * @param filename
     * @return a long value of file length
     */
    private long getFileSize(final String filename)
    {
        final File file = new File(filename);
        if (!file.exists() || !file.isFile())
        {
            return -1;
        }
        return file.length();
    }

    @Required
    public void setCoreService(final CoreService service)
    {
        coreService = service;
    }
}
