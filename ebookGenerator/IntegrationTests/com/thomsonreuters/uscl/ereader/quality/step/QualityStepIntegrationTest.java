package com.thomsonreuters.uscl.ereader.quality.step;

import static java.util.Collections.singleton;

import static com.thomsonreuters.uscl.ereader.JobParameterKey.QUALITY_REPORTS;
import static com.thomsonreuters.uscl.ereader.common.filesystem.FileContentMatcher.hasSameContentAs;
import static com.thomsonreuters.uscl.ereader.quality.step.QualityStep.DIVXML;
import static com.thomsonreuters.uscl.ereader.quality.step.QualityStep.HTML;
import static com.thomsonreuters.uscl.ereader.quality.step.QualityStep.TRANSFORMED_EXTENSION;
import static com.thomsonreuters.uscl.ereader.quality.step.QualityStep.StreamType.DIVXML_FOOTNOTE;
import static com.thomsonreuters.uscl.ereader.quality.step.QualityStep.StreamType.DIVXML_MAIN;
import static com.thomsonreuters.uscl.ereader.quality.step.QualityStep.StreamType.HTML_FOOTNOTE;
import static com.thomsonreuters.uscl.ereader.quality.step.QualityStep.StreamType.HTML_MAIN;
import static com.thomsonreuters.uscl.ereader.xpp.transformation.service.XppFormatFileSystemDir.QUALITY_DIR;
import static org.apache.commons.io.FileUtils.copyDirectory;
import static org.apache.commons.io.FileUtils.forceMkdir;
import static org.hamcrest.Matchers.containsString;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyMap;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpMethod.POST;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.thomsonreuters.uscl.ereader.JobParameterKey;
import com.thomsonreuters.uscl.ereader.common.xslt.TransformerBuilderFactory;
import com.thomsonreuters.uscl.ereader.common.xslt.XslTransformationService;
import com.thomsonreuters.uscl.ereader.context.CommonTestContextConfiguration;
import com.thomsonreuters.uscl.ereader.core.service.EmailUtil;
import com.thomsonreuters.uscl.ereader.quality.domain.response.JsonResponse;
import com.thomsonreuters.uscl.ereader.quality.helper.FtpManager;
import com.thomsonreuters.uscl.ereader.quality.helper.QualityUtil;
import com.thomsonreuters.uscl.ereader.quality.service.ComparisonService;
import com.thomsonreuters.uscl.ereader.quality.service.ComparisonServiceImpl;
import com.thomsonreuters.uscl.ereader.quality.service.ReportService;
import com.thomsonreuters.uscl.ereader.quality.service.ReportServiceImpl;
import com.thomsonreuters.uscl.ereader.quality.transformer.IdentityTransformer;
import com.thomsonreuters.uscl.ereader.quality.transformer.IdentityTransformerImpl;
import com.thomsonreuters.uscl.ereader.xpp.transformation.service.XppFormatFileSystem;
import com.thomsonreuters.uscl.ereader.xpp.transformation.service.XppGatherFileSystem;
import lombok.SneakyThrows;
import org.apache.commons.io.FileUtils;
import org.apache.commons.net.ftp.FTPClient;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Answers;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.client.RestTemplate;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = QualityStepIntegrationTest.Config.class)
@ActiveProfiles("IntegrationTests")
@TestPropertySource("/WEB-INF/spring/properties/default-spring.properties")
public final class QualityStepIntegrationTest {
    private static final String EMAIL = "test@thomsonreuters.com";
    private static final String MATERIAL_NUMBER = "12345";
    private static final String FILE_NAME = "0-SLHK_Front_vol_1.";

    @InjectMocks
    @Autowired
    private QualityStep sut;
    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private ChunkContext chunkContext;
    @Mock
    private ExecutionContext jobExecutionContext;
    @Autowired
    private XppFormatFileSystem fileSystem;
    @Autowired
    private FtpManager ftpManager;
    @Autowired
    private EmailUtil emailUtil;
    @Autowired
    private XppGatherFileSystem gatherFileSystem;
    @Autowired
    private RestTemplate restTemplate;
    @Value("${xpp.quality.webservice}")
    private String dtApiUrl;

    private File expectedTransformedMainDivXml;
    private File expectedTransformedFootnoteDivXml;
    private File expectedTransformedMainHtml;
    private File expectedTransformedFootnoteHtml;
    private File expectedReport;

    private Map<String, List<File>> resultMap;

    @Captor
    private ArgumentCaptor<String> filesCaptor;

    @Before
    public void setUp() throws IOException, URISyntaxException, AddressException {
        org.mockito.MockitoAnnotations.initMocks(this);

        when(chunkContext.getStepContext()
            .getStepExecution()
            .getJobParameters()
            .getString(JobParameterKey.USER_NAME)).thenReturn(EMAIL);

        when(chunkContext.getStepContext()
            .getStepExecution()
            .getJobExecution()
            .getExecutionContext()).thenReturn(jobExecutionContext);

        doAnswer(invocationOnMock -> resultMap = (Map<String, List<File>>) invocationOnMock.getArguments()[1])
            .when(jobExecutionContext)
            .put(eq(QUALITY_REPORTS), anyMap());

        final JsonResponse jsonResponse = loadResponse();

        doAnswer(invocationOnMock -> {
            final File reportsDir = new File(QualityStepIntegrationTest.class.getResource("data/reports")
                .toURI());
            final File actualReportsDir =
                new File(fileSystem.getDirectory(sut, QUALITY_DIR, MATERIAL_NUMBER), "reports");
            copyDirectory(reportsDir, actualReportsDir);
            return actualReportsDir.listFiles()[0];
        }).when(ftpManager)
            .downloadFile(anyString(), anyString());
        doNothing().when(ftpManager)
            .uploadFile(anyString());

        doReturn(ResponseEntity.ok(jsonResponse)).when(restTemplate)
            .exchange(eq(dtApiUrl), eq(POST), any(), eq(JsonResponse.class));

        doReturn(singleton(new InternetAddress(EMAIL))).when(emailUtil)
            .getEmailRecipientsByUsername(anyString());

        final File originalDir = fileSystem.getFormatDirectory(sut);
        forceMkdir(originalDir);
        final File sourceDir = new File(QualityStepIntegrationTest.class.getResource("data/source")
            .toURI());
        copyDirectory(sourceDir, originalDir);
        final File gatherDir = gatherFileSystem.getXppBundlesDirectory(sut);
        forceMkdir(gatherDir);
        final File bundlesDir = new File(QualityStepIntegrationTest.class.getResource("data/bundles")
            .toURI());
        copyDirectory(bundlesDir, gatherDir);
    }

    @Test
    @SneakyThrows
    public void shouldCompareAndGetReports() {
        //given
        expectedTransformedMainDivXml =
            new File(QualityStepIntegrationTest.class.getResource("data/expected/expected.main.DIVXML.transformed")
                .toURI());
        expectedTransformedMainHtml =
            new File(QualityStepIntegrationTest.class.getResource("data/expected/expected.main.HTML.transformed")
                .toURI());
        expectedTransformedFootnoteDivXml =
            new File(QualityStepIntegrationTest.class.getResource("data/expected/expected.footnote.DIVXML.transformed")
                .toURI());
        expectedTransformedFootnoteHtml =
            new File(QualityStepIntegrationTest.class.getResource("data/expected/expected.footnote.HTML.transformed")
                .toURI());
        expectedReport = new File(QualityStepIntegrationTest.class.getResource("data/reports/report.html")
            .toURI());
        //when
        sut.executeTransformation();
        //then
        final File actualTransformedMainDivXml = fileSystem.getFile(
            sut,
            QUALITY_DIR,
            MATERIAL_NUMBER,
            FILE_NAME + DIVXML_MAIN + "." + DIVXML + "." + MATERIAL_NUMBER + TRANSFORMED_EXTENSION);
        final File actualTransformedMainHtml = fileSystem.getFile(
            sut,
            QUALITY_DIR,
            MATERIAL_NUMBER,
            FILE_NAME + HTML_MAIN + "." + HTML + "." + MATERIAL_NUMBER + TRANSFORMED_EXTENSION);
        final File actualTransformedFootnoteDivXml = fileSystem.getFile(
            sut,
            QUALITY_DIR,
            MATERIAL_NUMBER,
            FILE_NAME + DIVXML_FOOTNOTE + "." + DIVXML + "." + MATERIAL_NUMBER + TRANSFORMED_EXTENSION);
        final File actualTransformedFootnoteHtml = fileSystem.getFile(
            sut,
            QUALITY_DIR,
            MATERIAL_NUMBER,
            FILE_NAME + HTML_FOOTNOTE + "." + HTML + "." + MATERIAL_NUMBER + TRANSFORMED_EXTENSION);
        final File actualReport = resultMap.get(MATERIAL_NUMBER)
            .get(0);
        assertThat(expectedTransformedMainDivXml, hasSameContentAs(actualTransformedMainDivXml));
        assertThat(expectedTransformedMainHtml, hasSameContentAs(actualTransformedMainHtml));
        assertThat(expectedTransformedFootnoteDivXml, hasSameContentAs(actualTransformedFootnoteDivXml));
        assertThat(expectedTransformedFootnoteHtml, hasSameContentAs(actualTransformedFootnoteHtml));
        assertThat(expectedReport, hasSameContentAs(actualReport));
        verify(jobExecutionContext).put(eq(QUALITY_REPORTS), anyMap());
        verify(ftpManager, atLeastOnce()).uploadFile(filesCaptor.capture());
        filesCaptor.getAllValues().forEach(value -> assertThat(value, containsString("vol_1")));
    }

    @SneakyThrows
    private JsonResponse loadResponse() {
        final File responseFile = new File(QualityStepIntegrationTest.class.getResource("data/response.json")
            .toURI());
        final String responseString = FileUtils.readFileToString(responseFile);
        return new ObjectMapper().readValue(responseString, JsonResponse.class);
    }

    @Configuration
    @Profile("IntegrationTests")
    @Import(CommonTestContextConfiguration.class)
    public static class Config {
        @Bean
        public QualityStep qualityStep() {
            return new QualityStep();
        }

        @Bean
        public FtpManager ftpManager() {
            return Mockito.mock(FtpManager.class);
        }

        @Bean
        public FTPClient ftpClient() {
            return Mockito.mock(FTPClient.class);
        }

        @Bean
        public QualityUtil qualityUtil(@Value("${xpp.quality.ftp.storage}") final String ftpStoragePath) {
            return new QualityUtil(ftpStoragePath);
        }

        @Bean
        public RestTemplate restTemplate() {
            return Mockito.mock(RestTemplate.class);
        }

        @Bean
        public EmailUtil emailUtil() {
            return Mockito.mock(EmailUtil.class);
        }

        @Bean
        @Autowired
        public ComparisonService comparisonService(
            final FtpManager ftpManager,
            final QualityUtil qualityUtil,
            final RestTemplate restTemplate,
            @Value("${xpp.quality.webservice}") final String dtApiUrl) {
            return new ComparisonServiceImpl(ftpManager, qualityUtil, restTemplate, dtApiUrl);
        }

        @Bean
        @Autowired
        public ReportService reportService(final FtpManager ftpManager, final QualityUtil qualityUtil) {
            return new ReportServiceImpl(ftpManager, qualityUtil);
        }

        @Bean
        @Autowired
        public IdentityTransformer identityTransformer(
            @Value("${xpp.quality.identity}") final File identityXsl,
            @Qualifier("xslTransformationService") final XslTransformationService transformationService,
            @Qualifier("transformerBuilderFactory") final TransformerBuilderFactory transformerBuilderFactory) {
            return new IdentityTransformerImpl(identityXsl, transformationService, transformerBuilderFactory);
        }
    }
}
