package com.thomsonreuters.uscl.ereader.format.step;

import com.thomsonreuters.uscl.ereader.context.CommonTestContextConfiguration;
import com.thomsonreuters.uscl.ereader.core.service.JsoupService;
import com.thomsonreuters.uscl.ereader.format.service.jsoup.JsoupTransformation;
import com.thomsonreuters.uscl.ereader.format.service.jsoup.LegalTopicBlockGeneration;
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
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.File;
import java.net.URISyntaxException;
import java.util.Arrays;

import static com.thomsonreuters.uscl.ereader.StepTestUtil.givenJobInstanceId;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {JsoupConversionsIntegrationTest.Config.class, StepIntegrationTestRunner.Config.class})
@ActiveProfiles("IntegrationTests")
public class JsoupConversionsIntegrationTest {
    private static final Long JOB_1 = 1L;
    private static final String DOC_1 = "Id7201723682311ea83aee46e6decbb85";
    private static final String DOC_2 = "Id7210175682311ea83aee46e6decbb85";
    private static final String CLASSIFNUM_1 = "classifnum1";
    private static final String CLASSIFICATION_1 = "classification1";
    private static final String CLASSIFNUM_2 = "classifnum2";
    private static final String CLASSIFICATION_2 = "classification2";
    private static final long CD_ID = 1L;
    private static final long CD_ID_2 = 2L;

    @Autowired
    private JsoupTransformationsStep step;

    @Autowired
    private StepIntegrationTestRunner runner;

    private File resourceDir;

    @Before
    public void setUp() throws URISyntaxException {
        runner.setUp(step);
        resourceDir = new File(JsoupConversionsIntegrationTest.class.getResource("resourceJsoupConversions").toURI());
    }

    @Test
    public void shouldCreateLegalTopic() throws Exception {
        givenJobInstanceId(step.getChunkContext(), JOB_1);
        runner.test(step, new File(resourceDir, "createLegalTopic"));
    }

    @Configuration
    @Profile("IntegrationTests")
    @Import(CommonTestContextConfiguration.class)
    public static class Config {
        @Bean
        public JsoupTransformationsStep jsoupTransformationsStep() {
            return new JsoupTransformationsStep();
        }

        @Bean
        @Order(1)
        public JsoupTransformation legalTopicBlockGeneration() {
            return new LegalTopicBlockGeneration();
        }

        @Bean
        public CanadianDigestService canadianDigestService() {
            CanadianDigestDao canadianDigestDaoMock = Mockito.mock(CanadianDigestDao.class);
            CanadianDigest canadianDigest1 = getCanadianDigest(DOC_1, CLASSIFNUM_1, CLASSIFICATION_1, CD_ID);
            CanadianDigest canadianDigest2 = getCanadianDigest(DOC_2, CLASSIFNUM_2, CLASSIFICATION_2, CD_ID_2);
            Mockito.when(canadianDigestDaoMock.findAllByJobInstanceId(JOB_1)).thenReturn(Arrays.asList(canadianDigest1, canadianDigest2));
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

        @Bean
        public JsoupService jsoupService() {
            return new JsoupService();
        }
    }
}
