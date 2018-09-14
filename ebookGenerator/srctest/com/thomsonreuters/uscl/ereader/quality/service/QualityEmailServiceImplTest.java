package com.thomsonreuters.uscl.ereader.quality.service;

import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import java.io.File;
import java.util.Collections;

import javax.mail.internet.InternetAddress;

import com.thomsonreuters.uscl.ereader.common.notification.entity.NotificationEmail;
import com.thomsonreuters.uscl.ereader.common.notification.service.EmailService;
import com.thomsonreuters.uscl.ereader.quality.domain.email.QualityReportEmail;
import com.thomsonreuters.uscl.ereader.quality.service.ReportFileHandlingService.ReportFileParameter;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public final class QualityEmailServiceImplTest {
    private static final String EXPECTED_EMAIL_BODY =
        "eBook Text quality test results - titleId"
            + "<br>Job Execution ID: 1</br>"
            + "<br>Environment: test</br>"
            + "<br>"
                + "<table border = '1'>"
                    + "<th colspan = '3'>Text quality results</th>"
                    + "<tr>"
                        + "<td>File name</td>"
                        + "<td>Matching Percentage</td>"
                        + "<td></td>"
                    + "</tr>"
                    + "<tr><td>mock_file_name</td><td>146.00%</td><td><a href=\"http://testenv:8888/ebookGenerator/qualityreport/1/1111111/reportFileName\">DeltaText report</a></td></tr>"
                + "</table>"
            + "</br>";

    @InjectMocks
    private QualityEmailServiceImpl sut;
    @Mock
    private EmailService emailService;
    @Mock
    private ReportFileHandlingService reportFileHandlingService;

    @Mock
    private QualityReportEmail qualityReportEmail;
    @Mock
    private File mockReportFile;
    @Mock
    private InternetAddress recipientEmail;
    @Captor
    private ArgumentCaptor<NotificationEmail> notificationEmailCaptor;

    @Test
    public void shouldSendReportEmail() {
        //given
        given(qualityReportEmail.getTitleId()).willReturn("titleId");
        given(qualityReportEmail.getJobId()).willReturn(1L);
        given(qualityReportEmail.getEnvironment()).willReturn("test");
        given(qualityReportEmail.getReports()).willReturn(Collections.singletonMap("1111111", Collections.singletonList(mockReportFile)));
        given(qualityReportEmail.getHost()).willReturn("testenv");
        given(qualityReportEmail.getPort()).willReturn("8888");
        given(qualityReportEmail.getHost()).willReturn("testenv");
        given(qualityReportEmail.getRecipients()).willReturn(Collections.singletonList(recipientEmail));
        given(mockReportFile.getName()).willReturn("reportFileName");

        given(reportFileHandlingService.extractParameter(mockReportFile, ReportFileParameter.FILE_NAME)).willReturn("mock_file_name");
        given(reportFileHandlingService.extractParameter(mockReportFile, ReportFileParameter.PERCENTAGE)).willReturn("146.00%");

        //when
        sut.sendReportEmail(qualityReportEmail);

        //then
        verify(emailService).send(notificationEmailCaptor.capture());
        assertThat(notificationEmailCaptor.getValue().getRecipients(), hasSize(1));
        assertThat(notificationEmailCaptor.getValue().getRecipients(), contains(recipientEmail));
        assertEquals(notificationEmailCaptor.getValue().getSubject(), "Text quality report: 1");
        assertEquals(notificationEmailCaptor.getValue().getBody(), EXPECTED_EMAIL_BODY);
        assertTrue(notificationEmailCaptor.getValue().isBodyContentHtmlType());
    }
}
