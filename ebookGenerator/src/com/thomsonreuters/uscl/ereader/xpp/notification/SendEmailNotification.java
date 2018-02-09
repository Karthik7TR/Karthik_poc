package com.thomsonreuters.uscl.ereader.xpp.notification;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.annotation.Resource;
import javax.mail.internet.InternetAddress;

import com.thomsonreuters.uscl.ereader.JobParameterKey;
import com.thomsonreuters.uscl.ereader.common.notification.entity.NotificationEmail;
import com.thomsonreuters.uscl.ereader.common.notification.service.EmailService;
import com.thomsonreuters.uscl.ereader.common.notification.step.FailureNotificationType;
import com.thomsonreuters.uscl.ereader.common.notification.step.SendFailureNotificationPolicy;
import com.thomsonreuters.uscl.ereader.common.publishingstatus.step.SavePublishingStatusPolicy;
import com.thomsonreuters.uscl.ereader.common.step.BookStepImpl;
import com.thomsonreuters.uscl.ereader.core.book.domain.BookDefinition;
import com.thomsonreuters.uscl.ereader.core.service.CoreService;
import com.thomsonreuters.uscl.ereader.request.domain.PrintComponent;
import com.thomsonreuters.uscl.ereader.smoketest.service.SmokeTestServiceImpl;
import com.thomsonreuters.uscl.ereader.stats.domain.PublishingStats;
import com.thomsonreuters.uscl.ereader.stats.service.PublishingStatsService;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.commons.text.StringEscapeUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.springframework.batch.core.ExitStatus;

@SendFailureNotificationPolicy(FailureNotificationType.XPP)
@SavePublishingStatusPolicy
public class SendEmailNotification extends BookStepImpl {
    private static final Logger LOG = LogManager.getLogger(SendEmailNotification.class);
    private static final String LONG_DASH = "—";
    private static final boolean IS_HTML_BODY_CONTENT = true;
    public static final String PRINT_COMPONENTS_SPLITTER = "<tr><td colspan=\"3\" style=\"text-align: center;\">- - - - - - - - - - - - - -</td></tr>";

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

    @NotNull
    private String getPrintComponentsTable(final BookDefinition bookDefinition) {
        final StringBuilder tempBuilder = new StringBuilder();
        final List<PrintComponent> sortedPrintComponentList = bookDefinition.getPrintComponents().stream()
            .sorted(Comparator.comparing(PrintComponent::getComponentOrder))
            .collect(Collectors.toList());

        tempBuilder.append("<br><table border = '1'><th colspan = '3'>Print Components</th>");
        tempBuilder.append("<tr><td>Material Number</td><td>SAP Description</td><td></td></tr>");
        for (final PrintComponent element : sortedPrintComponentList) {
            if (NumberUtils.isNumber(element.getMaterialNumber())) {
                tempBuilder.append("<tr><td>");
                tempBuilder.append(element.getMaterialNumber());
                tempBuilder.append("</td><td>");
                tempBuilder.append(element.getComponentName());
                tempBuilder.append("</td><td>");
                tempBuilder.append("<a href=\"");
                tempBuilder.append(getDownloadLink(getJobInstanceId(), element));
                tempBuilder.append("\">download</a></td></tr>");
            } else {
                tempBuilder.append(PRINT_COMPONENTS_SPLITTER);
            }
        }
        tempBuilder.append("</table>");
        return tempBuilder.toString();
    }

    private String getVersionDifferencesTable() {
        final StringBuilder tempBuilder = new StringBuilder();
        final PublishingStats currentVersionPublishingStats =
            publishingStatsService.findPublishingStatsByJobId(getJobInstanceId());
        final Optional<PublishingStats> previousVersionsPublishingStats =
            Optional.ofNullable(publishingStatsService.getPreviousPublishingStatsForSameBook(getJobInstanceId()));

        final String previousJobInstanceId = previousVersionsPublishingStats.map(PublishingStats::getJobInstanceId)
            .map(Object::toString)
            .orElse(LONG_DASH);
        final String previousDocCount = previousVersionsPublishingStats.map(PublishingStats::getAssembleDocCount)
            .map(Object::toString)
            .orElse(LONG_DASH);
        final String previousHumanReadableSize = previousVersionsPublishingStats
            .map(PublishingStats::getBookSizeHumanReadable).map(Object::toString).orElse(LONG_DASH);
        tempBuilder
            .append("<br><br><table border = '1'><tr><td></td><td>Current version</td><td>Previous version</td>");
        tempBuilder.append(
            "<tr><td>Job Instance ID</td><td>");
        tempBuilder.append(getJobInstanceId());
        tempBuilder.append("</td><td>");
        tempBuilder.append(previousJobInstanceId);
        tempBuilder.append("</td></tr>");
        tempBuilder.append("<tr><td>Doc Count</td><td>");
        tempBuilder.append(currentVersionPublishingStats.getAssembleDocCount());
        tempBuilder.append("</td><td>");
        tempBuilder.append(previousDocCount);
        tempBuilder.append("</td></tr>");
        tempBuilder.append("<tr><td>Book Size</td><td>");
        tempBuilder.append(currentVersionPublishingStats.getBookSizeHumanReadable());
        tempBuilder.append("</td><td>");
        tempBuilder.append(previousHumanReadableSize);
        tempBuilder.append("</td></tr></table>");
        return tempBuilder.toString();
    }

    @NotNull
    private String getEmailBody() {
        final BookDefinition bookDefinition = getBookDefinition();
        final String fullyQualifiedTitleId = bookDefinition.getFullyQualifiedTitleId();
        final String proviewDisplayName = bookDefinition.getProviewDisplayName();

        final StringBuilder sb = new StringBuilder();
        sb.append("eBook Publishing Successful - ");
        sb.append(fullyQualifiedTitleId);
        sb.append("<br>Proview Display Name: ");
        sb.append(proviewDisplayName);
        sb.append("<br>Title ID: ");
        sb.append(fullyQualifiedTitleId);
        sb.append("<br>Environment: ");
        sb.append(getEnvironment());
        sb.append("<br>Job Execution ID: ");
        sb.append(getJobExecutionId());
        sb.append(getVersionDifferencesTable());
        sb.append(getPrintComponentsTable(bookDefinition));

        return sb.toString();
    }

    @NotNull
    private String getDownloadLink(final long jobInstanceId, final PrintComponent element) {
        final String host = getJobParameterString(JobParameterKey.HOST_NAME);
        final String port = "workstation".equals(getEnvironment()) ? "8080" : SmokeTestServiceImpl.PORT_9002;
        final String componentName = encodeComponentNameForURL(element.getComponentName());

        final String archiveName = String.join("_", componentName, element.getBookDefinition().getTitleId(), element.getMaterialNumber());

        return String.format("http://%s:%s/ebookGenerator/pdfs/%s/%s/%s.zip", host, port, jobInstanceId, element.getMaterialNumber(), archiveName);
    }

    private String encodeComponentNameForURL(final String componentName) {
        try {
            final String componentNameUnescapedHtml = StringEscapeUtils.unescapeHtml4(componentName);
            return URLEncoder.encode(componentNameUnescapedHtml.replaceAll("[^a-zA-Z0-9 ]", StringUtils.EMPTY), "UTF-8");
        } catch (final UnsupportedEncodingException e) {
            LOG.error("Unexpected exception.", e);
            return StringUtils.EMPTY;
        }
    }
}
