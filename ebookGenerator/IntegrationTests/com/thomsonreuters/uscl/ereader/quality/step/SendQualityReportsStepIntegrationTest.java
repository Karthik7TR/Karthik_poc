package com.thomsonreuters.uscl.ereader.quality.step;

import static com.ninja_squad.dbsetup.Operations.deleteAllFrom;
import static com.ninja_squad.dbsetup.Operations.insertInto;
import static com.ninja_squad.dbsetup.Operations.sequenceOf;
import static com.thomsonreuters.uscl.ereader.JobParameterKey.EBOOK_DEFINITON;
import static com.thomsonreuters.uscl.ereader.JobParameterKey.ENVIRONMENT_NAME;
import static com.thomsonreuters.uscl.ereader.JobParameterKey.HOST_NAME;
import static com.thomsonreuters.uscl.ereader.JobParameterKey.QUALITY_REPORTS;
import static com.thomsonreuters.uscl.ereader.JobParameterKey.USER_NAME;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import java.io.File;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.mail.internet.InternetAddress;
import javax.sql.DataSource;

import com.ninja_squad.dbsetup.DbSetup;
import com.ninja_squad.dbsetup.destination.DataSourceDestination;
import com.ninja_squad.dbsetup.operation.Operation;
import com.thomsonreuters.uscl.ereader.common.notification.entity.NotificationEmail;
import com.thomsonreuters.uscl.ereader.common.notification.service.EmailService;
import com.thomsonreuters.uscl.ereader.config.db.AbstractDatabaseIntegrationTestConfig;
import com.thomsonreuters.uscl.ereader.context.CommonTestContextConfiguration;
import com.thomsonreuters.uscl.ereader.core.book.domain.BookDefinition;
import com.thomsonreuters.uscl.ereader.core.quality.dao.QualityReportRecipientDao;
import com.thomsonreuters.uscl.ereader.core.quality.domain.QualityReportRecipient;
import com.thomsonreuters.uscl.ereader.core.quality.service.QualityReportRecipientServiceImpl;
import com.thomsonreuters.uscl.ereader.core.quality.service.QualityReportsRecipientService;
import com.thomsonreuters.uscl.ereader.core.service.EmailUtil;
import com.thomsonreuters.uscl.ereader.core.service.EmailUtilImpl;
import com.thomsonreuters.uscl.ereader.quality.service.QualityEmailService;
import com.thomsonreuters.uscl.ereader.quality.service.QualityEmailServiceImpl;
import com.thomsonreuters.uscl.ereader.quality.service.ReportFileHandlingService;
import com.thomsonreuters.uscl.ereader.quality.service.ReportFileHandlingServiceImpl;
import com.thomsonreuters.uscl.ereader.userpreference.dao.UserPreferenceDao;
import com.thomsonreuters.uscl.ereader.userpreference.domain.UserPreference;
import com.thomsonreuters.uscl.ereader.userpreference.service.UserPreferenceEmailService;
import com.thomsonreuters.uscl.ereader.userpreference.service.UserPreferenceEmailServiceImpl;
import com.thomsonreuters.uscl.ereader.userpreference.service.UserPreferenceService;
import com.thomsonreuters.uscl.ereader.userpreference.service.UserPreferenceServiceImpl;
import lombok.SneakyThrows;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Answers;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = SendQualityReportsStepIntegrationTest.Config.class)
@ActiveProfiles("IntegrationTests")
@TestPropertySource("/WEB-INF/spring/properties/default-spring.properties")
public final class SendQualityReportsStepIntegrationTest {
    private static final String EXPECTED_EMAIL_BODY =
        "eBook Text quality test results - test_title"
            + "<br>Job Execution ID: 1</br>"
            + "<br>Environment: IntegrationTestEnv</br>"
            + "<br>"
                + "<table border = '1'>"
                    + "<th colspan = '3'>Text quality results</th>"
                    + "<tr>"
                        + "<td>File name</td>"
                        + "<td>Matching Percentage</td>"
                        + "<td></td>"
                    + "</tr>"
                    + "<tr><td>0-NYPJIH_Front_vol_1.main.DIVXML.42065619.transformed</td><td>99.77058%</td><td><a href=\"http://IntegrationTestHost:9002/ebookGenerator/qualityreport/1/1111111/0-TEST_Front_vol_1.main.DIVXML.1111111.html\">DeltaText report</a></td></tr>"
                    + "<tr><td>1-NYPJIH_I.footnote.DIVXML.42065619.transformed</td><td>94.09722%</td><td><a href=\"http://IntegrationTestHost:9002/ebookGenerator/qualityreport/1/1111111/1-TEST_I.footnote.DIVXML.1111111.html\">DeltaText report</a></td></tr>"
                    + "<tr><td>1-NYPJIH_I.main.DIVXML.42065619.transformed</td><td>97.23866%</td><td><a href=\"http://IntegrationTestHost:9002/ebookGenerator/qualityreport/1/1111111/1-TEST_I.main.DIVXML.1111111.html\">DeltaText report</a></td></tr>"
                    + "<tr><td>10002-volume_1_Summary_and_Detailed_Table_of_Contents.main.DIVXML.42065619.transformed</td><td>85.65436%</td><td><a href=\"http://IntegrationTestHost:9002/ebookGenerator/qualityreport/1/1111111/10002-volume_1_Summary_and_Detailed_Table_of_Contents.main.DIVXML.1111111.html\">DeltaText report</a></td></tr>"
                    + "<tr><td>1001-NYPJIH_Volume_1_Index.main.DIVXML.42065619.transformed</td><td>99.50631%</td><td><a href=\"http://IntegrationTestHost:9002/ebookGenerator/qualityreport/1/1111111/1001-TEST_Volume_1_Index.main.DIVXML.1111111.html\">DeltaText report</a></td></tr>"
                + "</table>"
            + "</br>";

    @Autowired
    @InjectMocks
    private SendQualityReportsStep sut;

    @Autowired
    private EmailService emailService;
    @Autowired
    private DataSource jpaDataSource;

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private ChunkContext chunkContext;
    @Mock
    private BookDefinition bookDefinition;
    @Captor
    private ArgumentCaptor<NotificationEmail> emailCaptor;

    @Before
    public void onTestSetUp() {
        final Operation deleteAllQualityReportsRecipients = deleteAllFrom("QUALITY_REPORTS_RECIPIENT");
        final Operation deleteAllUserPreference = deleteAllFrom("USER_PREFERENCE");
        final Operation insertQualityReportsRecipients = insertInto("QUALITY_REPORTS_RECIPIENT")
            .columns("EMAIL")
            .values("testUser1@tr.com")
            .values("testUser2@tr.com")
            .values("testUser3@tr.com")
            .build();
        final Operation insertUserPreferences = insertInto("USER_PREFERENCE")
            .columns("USER_NAME", "EMAIL_LIST", "LAST_UPDATED")
            .values("testUser", "testUser4@tr.com,testUser5@tr.com", new Date())
            .build();
        final Operation initialOperations = sequenceOf(deleteAllQualityReportsRecipients, deleteAllUserPreference,
            insertQualityReportsRecipients, insertUserPreferences);
        new DbSetup(new DataSourceDestination(jpaDataSource), initialOperations).launch();

        org.mockito.MockitoAnnotations.initMocks(this);
        given(chunkContext.getStepContext().getStepExecution().getJobExecution().getExecutionContext().get(QUALITY_REPORTS))
            .willReturn(getTestReports());
        given(chunkContext.getStepContext().getStepExecution().getJobParameters().getString(ENVIRONMENT_NAME))
            .willReturn("IntegrationTestEnv");
        given(chunkContext.getStepContext().getStepExecution().getJobParameters().getString(HOST_NAME))
            .willReturn("IntegrationTestHost");
        given(chunkContext.getStepContext().getStepExecution().getJobExecution().getJobInstance().getId())
            .willReturn(1L);
        given(chunkContext.getStepContext().getStepExecution().getJobExecution().getExecutionContext().get(EBOOK_DEFINITON))
            .willReturn(bookDefinition);
        given(chunkContext.getStepContext().getStepExecution().getJobParameters().getString(USER_NAME))
            .willReturn("testUserWithoutEmails");
        given(bookDefinition.getFullyQualifiedTitleId()).willReturn("test_title");
    }

    private Map<String, List<File>> getTestReports() {
        final File[] files = Paths.get("IntegrationTests", "com", "thomsonreuters", "uscl", "ereader", "quality", "step", "reports")
            .toFile()
            .listFiles();
        return Collections.singletonMap("1111111", Arrays.asList(files));
    }

    @Test
    @SneakyThrows
    public void shouldSendAnEmailTo6Addresses() {
        //given
        given(chunkContext.getStepContext().getStepExecution().getJobParameters().getString(USER_NAME))
            .willReturn("testUser");
        //when
        sut.executeStep();

        //then
        then(emailService).should().send(emailCaptor.capture());
        assertThat(emailCaptor.getValue().getRecipients(), hasSize(6));
        assertThat(emailCaptor.getValue().getRecipients(), containsInAnyOrder(
            new InternetAddress("testUser1@tr.com"), new InternetAddress("testUser2@tr.com"),
            new InternetAddress("testUser3@tr.com"), new InternetAddress("testUser4@tr.com"),
            new InternetAddress("testUser5@tr.com"), new InternetAddress("west.ebookGenerationSupport@thomsonreuters.com")));
        assertEquals(emailCaptor.getValue().getSubject(), "Text quality report: 1");
        assertEquals(emailCaptor.getValue().getBody(), EXPECTED_EMAIL_BODY);
        assertTrue(emailCaptor.getValue().isBodyContentHtmlType());
    }

    @Configuration
    @Profile("IntegrationTests")
    @Import(CommonTestContextConfiguration.class)
    @EnableJpaRepositories(basePackageClasses = {QualityReportRecipientDao.class, UserPreferenceDao.class})
    @EnableTransactionManagement
    public static class Config extends AbstractDatabaseIntegrationTestConfig {
        public Config() {
            super(sessionFactory -> sessionFactory.setAnnotatedClasses(QualityReportRecipient.class, UserPreference.class));
        }

        @Bean
        public SendQualityReportsStep sendQualityReportsStep() {
            return new SendQualityReportsStep();
        }

        @Bean
        public EmailUtil emailUtil() {
            return new EmailUtilImpl();
        }

        @Bean
        public UserPreferenceService userPreferenceService(final UserPreferenceDao userPreferenceDao) {
            return new UserPreferenceServiceImpl(userPreferenceDao, userPreferenceEmailService());
        }

        @Bean
        public UserPreferenceEmailService userPreferenceEmailService() {
            return new UserPreferenceEmailServiceImpl();
        }

        @Bean
        public QualityReportsRecipientService qualityReportRecipientService(final QualityReportRecipientDao dao) {
            return new QualityReportRecipientServiceImpl(dao);
        }

        @Bean
        public QualityEmailService qualityEmailService(final EmailService emailService) {
            return new QualityEmailServiceImpl(emailService, reportFileHandlingService());
        }

        @Bean
        public ReportFileHandlingService reportFileHandlingService() {
            return new ReportFileHandlingServiceImpl();
        }
    }
}
