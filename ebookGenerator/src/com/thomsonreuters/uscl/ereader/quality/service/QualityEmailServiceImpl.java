package com.thomsonreuters.uscl.ereader.quality.service;

import com.thomsonreuters.uscl.ereader.common.notification.entity.NotificationEmail;
import com.thomsonreuters.uscl.ereader.common.notification.service.EmailService;
import com.thomsonreuters.uscl.ereader.quality.domain.email.QualityReportEmail;
import com.thomsonreuters.uscl.ereader.quality.service.ReportFileHandlingService.ReportFileParameter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("qualityEmailService")
public class QualityEmailServiceImpl implements QualityEmailService {
    private static final String TABLE_ROW_TEMPLATE = "<tr><td>%s</td><td>%s</td><td>%s</td></tr>";
    private static final String EMAIL_TEMPLATE =
        "eBook Text quality test results - %s"
            + "<br>Job Execution ID: %s</br>"
            + "<br>Environment: %s</br>"
            + "<br>"
                + "<table border = '1'>"
                    + "<th colspan = '3'>Text quality results</th>"
                    + "<tr>"
                        + "<td>File name</td>"
                        + "<td>Matching Percentage</td>"
                        + "<td></td>"
                    + "</tr>"
                    + "%s"
                + "</table>"
            + "</br>";

    private final EmailService emailService;
    private final ReportFileHandlingService reportFileHandlingService;

    @Autowired
    public QualityEmailServiceImpl(final EmailService emailService, final ReportFileHandlingService reportFileHandlingService) {
        this.emailService = emailService;
        this.reportFileHandlingService = reportFileHandlingService;
    }

    @Override
    public void sendReportEmail(final QualityReportEmail email) {
        final String body = String.format(EMAIL_TEMPLATE, email.getTitleId(), email.getJobId(),
            email.getEnvironment(), getReportsTable(email));
        emailService.send(new NotificationEmail(email.getRecipients(), String.format("Text quality report: %s", email.getJobId()), body, true));
    }

    private String getReportsTable(final QualityReportEmail email) {
        final StringBuilder tableBuilder = new StringBuilder();
        email.getReports().forEach((material, files) -> {
            files.forEach(file -> {
                tableBuilder.append(String.format(TABLE_ROW_TEMPLATE,
                    reportFileHandlingService.extractParameter(file, ReportFileParameter.FILE_NAME),
                    reportFileHandlingService.extractParameter(file, ReportFileParameter.PERCENTAGE),
                    getReportFileLink(email.getHost(), email.getPort(), email.getJobId(), material, file.getName())));
            });
        });
        return tableBuilder.toString();
    }

    private String getReportFileLink(final String host, final String port, final long jobId, final String material, final String fileName) {
        final String link =  String.format("http://%s:%s/ebookGenerator/qualityreport/%s/%s/%s", host, port, jobId, material, fileName);
        return String.format("<a href=\"%s\">DeltaText report</a>", link);
    }
}
