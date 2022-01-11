package com.thomsonreuters.uscl.ereader.notification.service;

import javax.annotation.Resource;

import com.thomsonreuters.uscl.ereader.JobExecutionKey;
import com.thomsonreuters.uscl.ereader.common.notification.service.EmailBuilder;
import com.thomsonreuters.uscl.ereader.core.book.domain.BookDefinition;
import com.thomsonreuters.uscl.ereader.notification.step.SendEmailNotificationStep;
import com.thomsonreuters.uscl.ereader.stats.domain.PublishingStats;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.StringUtils;

import java.util.Collection;
import java.util.Map;

public abstract class AbstractEmailBuilder implements EmailBuilder {
    private static final String PAGES_IN_WRONG_ORDER_WARNING = "\n\nWARNING: some pages are in different order in main section and in footnotes section\n";
    private static final String CANADIAN_DIGEST_MISSING_WARNING = "\n\nWARNING: some documents metadata have Canadian Digest section with missing elements:\n";
    private static final String CANADIAN_TOPIC_CODE_MISSING_WARNING = "\n\nWARNING: some documents metadata have Canadian Topic Code section with missing elements:\n";
    private static final String DOCUMENT = "\tDocument ";
    private static final String PAGE = "\t\tpage ";
    @Resource(name = "sendNotificationTask")
    protected SendEmailNotificationStep step;

    /* (non-Javadoc)
     * @see com.thomsonreuters.uscl.ereader.common.notification.service.EmailBuilder#getSubject()
     */
    @Override
    public String getSubject() {
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
    public String getBody() {
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

        sb.append(getPagesInfo(bookDefinition));
        sb.append(getInlineTocInfo(bookDefinition));

        sb.append(getPagebreaksInWrongOrderWarning());
        sb.append(getCanadianDigestMissingWarning());
        sb.append(getCanadianTopicCodeMissingWarning());

        return sb.toString();
    }

    private String getPagebreaksInWrongOrderWarning() {
        if (step.hasJobExecutionPropertyPagebreaksInWrongOrder()) {
            Map<String, Collection<String>> pagebreaksInWrongOrder = step.getJobExecutionPropertyPagebreaksInWrongOrder();
            final StringBuilder sb = new StringBuilder(PAGES_IN_WRONG_ORDER_WARNING);
            pagebreaksInWrongOrder.forEach((docId, pagebreaks) -> {
                sb.append(DOCUMENT).append(docId).append(":\n");
                pagebreaks.forEach(pagebreak -> {
                    sb.append(PAGE).append(pagebreak).append("\n");
                });
                sb.append("\n");
            });
            return sb.toString();
        }
        return StringUtils.EMPTY;
    }

    private String getCanadianDigestMissingWarning() {
        if (CollectionUtils.isNotEmpty(step.getJobExecutionPropertyCanadianDigestMissing())) {
            return CANADIAN_DIGEST_MISSING_WARNING + String.join("\n", step.getJobExecutionPropertyCanadianDigestMissing());
        }
        return StringUtils.EMPTY;
    }

    private String getCanadianTopicCodeMissingWarning() {
        if (CollectionUtils.isNotEmpty(step.getJobExecutionPropertyCanadianTopicCodeMissing())) {
            return CANADIAN_TOPIC_CODE_MISSING_WARNING + String.join("\n", step.getJobExecutionPropertyCanadianTopicCodeMissing());
        }
        return StringUtils.EMPTY;
    }

    /**
     * Returns additional subject part
     */
    protected abstract String getAdditionalSubjectPart();

    /**
     * Returns additional body part
     */
    protected abstract String getAdditionalBodyPart();

    private String getVersionInfo(final boolean isCurrent) {
        final PublishingStats stats = isCurrent ? step.getCurrentsStats() : step.getPreviousStats();
        if (stats == null) {
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

    private String getPagesInfo(final BookDefinition bookDefinition) {
        final boolean pageNumbersActuallyAdded = step.getJobExecutionPropertyBoolean(JobExecutionKey.WITH_PAGE_NUMBERS);

        if (bookDefinition.isPrintPageNumbers() && !pageNumbersActuallyAdded) {
            return "\n\nWARNING: printPageNumbers option is set in eBook Definition but source data files are missing pagebreaks";
        }
        return "";
    }

    private String getInlineTocInfo(final BookDefinition bookDefinition) {
        final boolean inlineTocActuallyAdded = step.getJobExecutionPropertyBoolean(JobExecutionKey.WITH_INLINE_TOC);

        if (bookDefinition.isInlineTocIncluded() && !inlineTocActuallyAdded) {
            return "\n\nWARNING: inlineToc option is set in eBook Definition but source TOC xml is missing inlineToc related attributes";
        }
        return "";
    }
}
