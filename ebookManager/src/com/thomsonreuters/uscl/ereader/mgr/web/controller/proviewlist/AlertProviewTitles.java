package com.thomsonreuters.uscl.ereader.mgr.web.controller.proviewlist;

import com.thomsonreuters.uscl.ereader.common.notification.service.EmailService;
import com.thomsonreuters.uscl.ereader.core.CoreConstants;
import com.thomsonreuters.uscl.ereader.core.service.EmailUtil;
import com.thomsonreuters.uscl.ereader.deliver.exception.ProviewException;
import com.thomsonreuters.uscl.ereader.deliver.service.ProviewTitleInfo;
import com.thomsonreuters.uscl.ereader.proviewaudit.domain.ProviewAudit;
import com.thomsonreuters.uscl.ereader.proviewaudit.service.ProviewAuditService;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
@Slf4j
public class AlertProviewTitles {

    private final ProviewTitlesProvider proviewTitlesProvider;
    private final ProviewAuditService proviewAuditService;
    private final EmailUtil emailUtil;
    private final EmailService emailService;

    @Value("${root.work.directory}")
    private File rootWorkDirectory;

    @Value("${alert.proview.email.list}")
    private String alertEmails;

    @Autowired
    @Qualifier("environmentName")
    private String environmentName;

    @Autowired
    public AlertProviewTitles(final ProviewTitlesProvider proviewTitlesProvider,
                              final ProviewAuditService proviewAuditService,
                              final EmailUtil emailUtil,
                              final EmailService emailService) {
        this.proviewTitlesProvider = proviewTitlesProvider;
        this.proviewAuditService = proviewAuditService;
        this.emailUtil = emailUtil;
        this.emailService = emailService;
    }

    @Scheduled(cron = "${alert.proview.job.cron}")
    public void alertTitlesInReviewStageForMoreThan24hrs() throws ProviewException {
        log.info("Alert Job for Proview Titles in Review Stage for more than 24 hrs started");
        //Map<String, ProviewTitleContainer> allProviewTitleInfo = proviewTitlesProvider.provideAll(false);
        //Review & Final XML
        List<ProviewTitleInfo> allLatestProviewTitleInfo =  proviewTitlesProvider.provideAllLatest();

        List<ProviewAudit> proviewAuditJobSubmitterNameList =  proviewAuditService.findJobSubmitterNameForAllTitlesLatestVersion();

        //update Job Submitter Name for each title
        allLatestProviewTitleInfo.forEach(titleInfo -> {
            ProviewAudit proviewAudit = proviewAuditJobSubmitterNameList.stream().filter(submitter ->
                    submitter.getTitleId().equals(titleInfo.getTitleId()) &&
                            submitter.getBookVersion().equals(titleInfo.getVersion())).findFirst().orElse(null);
            if (proviewAudit != null) {
                titleInfo.setJobSubmitterName(proviewAudit.getUsername());
            }
        });

        //Review & Promote in PROVIEW_AUDIT
        List<ProviewAudit>  proviewAuditReviewBooksList =  proviewAuditService.findBooksInReviewStageForMoreThan24Hrs();

        List<ProviewTitleInfo> allLatestProviewTitleInfoAlert = new ArrayList<ProviewTitleInfo>();
        //Filter List for Titles in Review status and more than 24 hrs
        allLatestProviewTitleInfo.forEach(titleInfo -> {
            ProviewAudit proviewAudit = proviewAuditReviewBooksList.stream().filter(audit ->
                            audit.getTitleId().equals(titleInfo.getTitleId()) &&
                            titleInfo.getStatus().equalsIgnoreCase("Review") &&
                            audit.getBookVersion().equals(titleInfo.getVersion())).findFirst().orElse(null);
            if (proviewAudit != null) {
                allLatestProviewTitleInfoAlert.add(titleInfo);
            }
        });

        //If List has data, generate Excel workbook
        if ( allLatestProviewTitleInfoAlert != null  && allLatestProviewTitleInfoAlert.size() > 0) {
            try {
                final ProviewListExcelReviewBooksAlertExportService excelExportService = new ProviewListExcelReviewBooksAlertExportService();
                final Workbook wb = excelExportService.createExcelDocument(allLatestProviewTitleInfoAlert);
                //Save file
                // like "/apps/eBookBuilder/prod/data" or "/apps/eBookBuilder/workstation/data"
                final File environmentDir = new File(rootWorkDirectory, environmentName);
                final File dataDir = new File(environmentDir, CoreConstants.DATA_DIR);
                final File alertReportFile = new File(dataDir,"AlertReportReviewBooks.xls");

                FileOutputStream out = new FileOutputStream(alertReportFile);
                wb.write(out);
                out.close();

                //Send email with attachment
                final String subject =
                        "eBook user Notification for Proview titles in Review Stage for more than 24 hours";
                final String emailBody = "Attached is the file which has Proview Titles in Review stage for more than 24 hours";
                final Collection<InternetAddress> emailRecipients = emailUtil.getEmailRecipientsByUsername("286076");
                List<String> alertEmailsList = Stream.of(alertEmails.split(",", -1))
                        .collect(Collectors.toList());

                alertEmailsList.stream().forEach((email) -> {
                    try {
                        emailRecipients.add(new InternetAddress(email));
                    } catch (AddressException e) {
                        log.error("Invalid user preference email address - ignored: " + email, e);
                    }
                });

                log.debug("Notification email recipients : " + emailRecipients);
                log.debug("Notification email subject : " + subject);
                log.debug("Notification email body : " + emailBody);

                final List<String> filenames = new ArrayList<>();
                filenames.add(alertReportFile.getAbsolutePath());
                emailService.sendWithAttachment(emailRecipients, subject, emailBody, filenames);

                //Delete the temp report file
                alertReportFile.delete();
                log.info("Alert Job for Proview Titles in Review Stage for more than 24 hrs email sent");

            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
        }
        log.info("Alert Job for Proview Titles in Review Stage for more than 24 hrs completed");

    }

}