package com.thomsonreuters.uscl.ereader.xpp.notification;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

import javax.annotation.Resource;
import javax.mail.internet.InternetAddress;

import com.thomsonreuters.uscl.ereader.common.notification.entity.NotificationEmail;
import com.thomsonreuters.uscl.ereader.common.notification.service.EmailService;
import com.thomsonreuters.uscl.ereader.common.notification.step.FailureNotificationType;
import com.thomsonreuters.uscl.ereader.common.notification.step.SendFailureNotificationPolicy;
import com.thomsonreuters.uscl.ereader.common.publishingstatus.step.SavePublishingStatusPolicy;
import com.thomsonreuters.uscl.ereader.common.step.BookStepImpl;
import com.thomsonreuters.uscl.ereader.core.book.domain.BookDefinition;
import com.thomsonreuters.uscl.ereader.core.service.CoreService;
import com.thomsonreuters.uscl.ereader.request.domain.PrintComponent;
import com.thomsonreuters.uscl.ereader.stats.domain.PublishingStats;
import com.thomsonreuters.uscl.ereader.stats.service.PublishingStatsService;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.springframework.batch.core.ExitStatus;

@SendFailureNotificationPolicy(FailureNotificationType.XPP)
@SavePublishingStatusPolicy
public class SendEmailNotification extends BookStepImpl {
    private static final Logger LOG = LogManager.getLogger(SendEmailNotification.class);
    private final boolean IS_HTML_BODY_CONTENT = true;

    @Resource(name = "emailService")
    private EmailService emailService;
    @Resource(name = "coreService")
    private CoreService coreService;
    @Resource(name = "publishingStatsService")
    private PublishingStatsService publishingStatsService;

    @Override
    public ExitStatus executeStep() throws Exception {
        final Collection<InternetAddress> recipients = getRecipients();
        LOG.debug("recipients: " + recipients);

        emailService.send(new NotificationEmail(recipients, getEmailSubject(), getEmailBody(), IS_HTML_BODY_CONTENT));

        return ExitStatus.COMPLETED;
    }

    @NotNull
    private Collection<InternetAddress> getRecipients() {
        final String userName = getUserName();
        return coreService.getEmailRecipientsByUsername(userName);
    }

    @NotNull
    private String getEmailSubject() {
        final BookDefinition bookDefinition = getBookDefinition();
        return String.format("eBook XPP Publishing Successful - %s", bookDefinition.getFullyQualifiedTitleId());
    }

    private PublishingStats getCurrentVersionPublishingStats() {
        return publishingStatsService.findPublishingStatsByJobId(getJobInstanceId());
    }

    private PublishingStats getPreviousVersionPublishingStats() {
        return publishingStatsService.getPreviousPublishingStatsForSameBook(getJobInstanceId());
    }

    @NotNull
    private String getPrintComponentsTable(final BookDefinition bookDefinition) {
        final StringBuilder tempBuilder = new StringBuilder();
        final Set<PrintComponent> printComponents = bookDefinition.getPrintComponents();
        final List<PrintComponent> sortedPrintComponentList = new ArrayList<>(printComponents);

        final Comparator<PrintComponent> comparator = new Comparator<PrintComponent>() {
            @Override
            public int compare(final PrintComponent left, final PrintComponent right) {
                return left.getComponentOrder() - right.getComponentOrder();
            }
        };
        Collections.sort(sortedPrintComponentList, comparator);
        tempBuilder.append("<br><table border = '1'><th colspan = '2'>Print Components</th>");
        tempBuilder.append("<tr><td>Material Number</td><td>SAP Description</td></tr>");
        for (final PrintComponent element : sortedPrintComponentList) {
            tempBuilder.append("<tr><td>" + element.getMaterialNumber() + "</td>");
            tempBuilder.append("<td>" + element.getComponentName() + "</td></tr>");
        }
        tempBuilder.append("</table>");
        return tempBuilder.toString();
    }

    private String getVersionDifferencesTable(final PublishingStats currentVersionPublishingStats, final PublishingStats previousVersionsPublishingStats) {
        final StringBuilder tempBuilder = new StringBuilder();
        final String tempPreviousVersionDocCount;
            if (previousVersionsPublishingStats.getAssembleDocCount() == null) {
                tempPreviousVersionDocCount = "—";
            } else {
                tempPreviousVersionDocCount = previousVersionsPublishingStats.getAssembleDocCount().toString();
            }
        tempBuilder.append("<br><br><table border = '1'><tr><td></td><td>Current version</td><td>Previous version</td>");
        tempBuilder.append("<tr><td>Job Instance ID</td><td>" + getJobInstanceId() + "</td><td>" + previousVersionsPublishingStats.getJobInstanceId() + "</td></tr>");
        tempBuilder.append("<tr><td>Doc Count</td><td>" + currentVersionPublishingStats.getAssembleDocCount() + "</td><td>" + tempPreviousVersionDocCount + "</td></tr>");
        tempBuilder.append("<tr><td>Book Size</td><td>" + currentVersionPublishingStats.getBookSizeHumanReadable() + "</td><td>" + previousVersionsPublishingStats.getBookSizeHumanReadable() + "</td></tr>");
        tempBuilder.append("</table>");
        return tempBuilder.toString();
    }

    @NotNull
    private String getEmailBody() {
        final BookDefinition bookDefinition = getBookDefinition();
        final String fullyQualifiedTitleId = bookDefinition.getFullyQualifiedTitleId();
        final String proviewDisplayName = bookDefinition.getProviewDisplayName();
        final PublishingStats currentVersionPublishingStats = getCurrentVersionPublishingStats();
        final PublishingStats previousVersionsPublishingStats = getPreviousVersionPublishingStats();

        final StringBuilder sb = new StringBuilder();
        sb.append("eBook Publishing Successful - " + fullyQualifiedTitleId);
        sb.append("<br>Proview Display Name: " + proviewDisplayName);
        sb.append("<br>Title ID: " + fullyQualifiedTitleId);
        sb.append("<br>Environment: " + getEnvironment());
        sb.append("<br>Job Execution ID: " + getJobExecutionId());
        sb.append(getVersionDifferencesTable(currentVersionPublishingStats, previousVersionsPublishingStats));
        sb.append(getPrintComponentsTable(bookDefinition));

        return sb.toString();
    }
}
