package com.thomsonreuters.uscl.ereader.notification.service;

import javax.annotation.Resource;

import com.thomsonreuters.uscl.ereader.common.notification.service.EmailBuilder;
import com.thomsonreuters.uscl.ereader.core.book.domain.BookDefinition;
import com.thomsonreuters.uscl.ereader.notification.step.SendEmailNotificationStep;
import com.thomsonreuters.uscl.ereader.stats.domain.PublishingStats;

public abstract class AbstractEmailBuilder implements EmailBuilder
{
    @Resource(name = "sendNotificationTask")
    protected SendEmailNotificationStep step;

    /* (non-Javadoc)
     * @see com.thomsonreuters.uscl.ereader.common.notification.service.EmailBuilder#getSubject()
     */
    @Override
    public String getSubject()
    {
        final BookDefinition bookDefinition = step.getBookDefinition();
        final StringBuilder sb = new StringBuilder();
        sb.append("eBook Publishing Successful - " + bookDefinition.getFullyQualifiedTitleId());
        sb.append(getAdditionalSubjectPart());
        return sb.toString();
    }

    /* (non-Javadoc)
     * @see com.thomsonreuters.uscl.ereader.common.notification.service.EmailBuilder#getBody()
     */
    @Override
    public String getBody()
    {
        final BookDefinition bookDefinition = step.getBookDefinition();
        final String fullyQualifiedTitleId = bookDefinition.getFullyQualifiedTitleId();
        final String proviewDisplayName = bookDefinition.getProviewDisplayName();

        final StringBuilder sb = new StringBuilder();
        sb.append("eBook Publishing Successful - " + fullyQualifiedTitleId);
        sb.append("\t\nProview Display Name: " + proviewDisplayName);
        sb.append("\t\nTitle ID: " + fullyQualifiedTitleId);
        sb.append("\t\nEnvironment: " + step.getEnvironment());
        sb.append("\t\nJob Instance ID: " + step.getJobInstanceId());
        sb.append("\t\nJob Execution ID: " + step.getJobExecutionId());

        sb.append(getVersionInfo(true));
        sb.append(getVersionInfo(false));

        sb.append(getAdditionalBodyPart());
        return sb.toString();
    }

    /**
     * Returns additional subject part
     */
    protected abstract String getAdditionalSubjectPart();

    /**
     * Returns additional body part
     */
    protected abstract String getAdditionalBodyPart();

    private String getVersionInfo(final boolean isCurrent)
    {
        final PublishingStats stats = isCurrent ? step.getCurrentsStats() : step.getPreviousStats();
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
}
