package com.thomsonreuters.uscl.ereader.format.step;

import com.thomsonreuters.uscl.ereader.context.CommonTestContextConfiguration;
import com.thomsonreuters.uscl.ereader.core.book.domain.BookDefinition;
import com.thomsonreuters.uscl.ereader.format.service.jsoup.AddHiddenFootnotesAndReferences;
import com.thomsonreuters.uscl.ereader.format.service.jsoup.ExternalLinksTransformation;
import com.thomsonreuters.uscl.ereader.format.service.jsoup.InnerLinksTransformation;
import com.thomsonreuters.uscl.ereader.format.service.jsoup.JsoupTransformation;
import com.thomsonreuters.uscl.ereader.format.service.jsoup.LegalTopicBlockGeneration;
import com.thomsonreuters.uscl.ereader.format.service.jsoup.RemoveGapsBetweenChaptersService;
import com.thomsonreuters.uscl.ereader.format.service.jsoup.TableStylesAddition;
import com.thomsonreuters.uscl.ereader.gather.metadata.dao.CanadianDigestDao;
import com.thomsonreuters.uscl.ereader.gather.metadata.domain.CanadianDigest;
import com.thomsonreuters.uscl.ereader.gather.metadata.service.CanadianDigestService;
import com.thomsonreuters.uscl.ereader.gather.metadata.service.CanadianDigestServiceImpl;
import org.jetbrains.annotations.NotNull;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.net.URISyntaxException;
import java.util.Arrays;

import static com.thomsonreuters.uscl.ereader.JobExecutionKey.WITH_PAGE_NUMBERS;
import static com.thomsonreuters.uscl.ereader.StepTestUtil.givenBook;
import static com.thomsonreuters.uscl.ereader.StepTestUtil.givenJobInstanceId;
import static org.mockito.Mockito.when;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {JsoupConversionsIntegrationTest.Config.class, StepIntegrationTestRunner.Config.class})
@ActiveProfiles("IntegrationTests")
public class JsoupConversionsIntegrationTest {
    private static final Long JOB_1 = 1L;
    private static final String TITLE_ID = "uscl/an/title";
    private static final String CW_TITLE_ID = "cw/eg/thorburn_en";
    private static final String DOC_1 = "Id7201723682311ea83aee46e6decbb85";
    private static final String DOC_2 = "Id7210175682311ea83aee46e6decbb85";
    private static final String CLASSIFNUM_1 = "classifnum1";
    private static final String CLASSIFICATION_1 = "classification1";
    private static final String CLASSIFNUM_2 = "classifnum2";
    private static final String CLASSIFICATION_2 = "classification2";
    private static final String CLASSIFNUM_3 = "classifnum3";
    private static final String CLASSIFICATION_4 = "classification4";
    private static final long CD_ID = 1L;
    private static final long CD_ID_2 = 2L;

    @Autowired
    private JsoupTransformationsStep step;

    @Autowired
    private StepIntegrationTestRunner runner;

    @Before
    public void setUp() throws URISyntaxException {
        runner.setUp(step, "resourceJsoupConversions", true);
    }

    @Test
    public void shouldCreateLegalTopic() throws Exception {
        givenJobInstanceId(step.getChunkContext(), JOB_1);
        runner.test(step, "createLegalTopic");
    }

    @Test
    public void shouldRemoveGapsBetweenChapters() throws Exception {
        BookDefinition bookDefinition = new BookDefinition();
        bookDefinition.setPrintPageNumbers(true);
        bookDefinition.setFullyQualifiedTitleId(TITLE_ID);
        givenBook(step.getChunkContext(), bookDefinition);
        runner.test(step, "removeGapsBetweenChapters");
    }

    @Test
    public void shouldAddHiddenReferencesAndFootnotes() throws Exception {
        when(step.getJobExecutionProperty(WITH_PAGE_NUMBERS)).thenReturn(true);
        runner.test(step, "addHiddenFootnotesAndReferences");
    }

    @Test
    public void shouldAddStylesToCWTables() throws Exception {
        setUpCwBookDefinition();
        runner.test(step, "addStylesToCWTables");
    }

    @Test
    public void shouldTransformExternalLinks() throws Exception {
        givenJobInstanceId(step.getChunkContext(), JOB_1);
        setUpCwBookDefinition();
        runner.test(step, "transformExternalLinks");
    }

    @Test
    public void shouldFixLinkSerialNumber() throws Exception {
        givenJobInstanceId(step.getChunkContext(), JOB_1);
        setUpCwBookDefinition();
        runner.test(step, "fixLinkSerialNumber");
    }

    @Test
    public void shouldTransformInnerLinks() throws Exception {
        runner.test(step, "transformInnerLinks");
    }

    @Test
    public void shouldTransformInnerLinksMissingLink() throws Exception {
        runner.test(step, "transformInnerLinksMissingLink");
    }

    private void setUpCwBookDefinition() {
        BookDefinition bookDefinition = new BookDefinition();
        bookDefinition.setFullyQualifiedTitleId(CW_TITLE_ID);
        givenBook(step.getChunkContext(), bookDefinition);
    }

    @Configuration
    @Profile("IntegrationTests")
    @Import(CommonTestContextConfiguration.class)
    public static class Config {
        @Value("${westlaw.url}")
        private String westlawUrl;
        @Value("${westlaw.canada.url}")
        private String westlawCanadaUrl;

        @Bean
        public JsoupTransformationsStep jsoupTransformationsStep() {
            return new JsoupTransformationsStep();
        }

        @Bean
        public JsoupTransformation legalTopicBlockGeneration() {
            return new LegalTopicBlockGeneration();
        }

        @Bean
        public JsoupTransformation tableStylesAddition() {
            return new TableStylesAddition();
        }

        @Bean
        public JsoupTransformation removeGapsBetweenChapters() {
            return new RemoveGapsBetweenChaptersService();
        }

        @Bean
        public JsoupTransformation addHiddenFootnotesAndReferences() {
            return new AddHiddenFootnotesAndReferences();
        }

        @Bean
        public JsoupTransformation innerLinksTransformation() {
            return new InnerLinksTransformation();
        }

        @Bean
        public ExternalLinksTransformation externalLinksTransformation() {
            ExternalLinksTransformation externalLinksTransformation = new ExternalLinksTransformation();
            externalLinksTransformation.setWestlawUrl(westlawUrl);
            externalLinksTransformation.setWestlawCanadaUrl(westlawCanadaUrl);
            return externalLinksTransformation;
        }

        @Bean
        public CanadianDigestService canadianDigestService() {
            CanadianDigestDao canadianDigestDaoMock = Mockito.mock(CanadianDigestDao.class);
            CanadianDigest canadianDigest1 = getCanadianDigest(DOC_1, CLASSIFNUM_1, CLASSIFICATION_1, CD_ID);
            CanadianDigest canadianDigest2 = getCanadianDigest(DOC_2, CLASSIFNUM_2, CLASSIFICATION_2, CD_ID_2);
            CanadianDigest canadianDigest3 = getCanadianDigest(DOC_2, CLASSIFNUM_3, null, CD_ID_2);
            CanadianDigest canadianDigest4 = getCanadianDigest(DOC_2, " ", CLASSIFICATION_4, CD_ID_2);
            Mockito.when(canadianDigestDaoMock.findAllByJobInstanceId(JOB_1))
                    .thenReturn(Arrays.asList(canadianDigest1, canadianDigest2, canadianDigest3, canadianDigest4));
            return new CanadianDigestServiceImpl(canadianDigestDaoMock);
        }

        @NotNull
        private CanadianDigest getCanadianDigest(final String docGuid, final String classifnum, final String classification, final long id) {
            CanadianDigest canadianDigest = new CanadianDigest();
            canadianDigest.setDocUuid(docGuid);
            canadianDigest.setJobInstanceId(JOB_1);
            canadianDigest.setClassifnum(classifnum);
            canadianDigest.setClassification(classification);
            canadianDigest.setId(id);
            return canadianDigest;
        }
    }
}
