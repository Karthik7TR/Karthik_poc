package com.thomsonreuters.uscl.ereader.format.step;

import com.thomsonreuters.uscl.ereader.JobExecutionKey;
import com.thomsonreuters.uscl.ereader.context.CommonTestContextConfiguration;
import com.thomsonreuters.uscl.ereader.core.service.JsoupService;
import com.thomsonreuters.uscl.ereader.gather.metadata.domain.CanadianTopicCode;
import com.thomsonreuters.uscl.ereader.gather.metadata.service.CanadianTopicCodeService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static com.thomsonreuters.uscl.ereader.StepTestUtil.givenJobInstanceId;
import static org.mockito.Mockito.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {BuildThesaurusStepIntegrationTest.Config.class, StepIntegrationTestRunner.Config.class})
@ActiveProfiles("IntegrationTests")
public final class BuildThesaurusStepIntegrationTest {
    private static final String DOC_1 = "doc1";
    private static final String DOC_2 = "doc2";
    private static final long JOB_1 = 1L;
    private static final long JOB_2 = 0L;

    @Autowired
    private BuildThesaurusStep step;

    @Autowired
    private StepIntegrationTestRunner runner;

    @Before
    public void setUp() throws URISyntaxException {
        runner.setUp(step, "resourceBuildThesaurus");
    }

    @Test
    public void shouldBuildThesaurusXml() throws Exception {
        givenJobInstanceId(step.getChunkContext(), JOB_1);
        runner.testWithExpectedOnly(step, "buildTestWithThesaurus");
        verify(step.getJobExecutionContext()).put(JobExecutionKey.WITH_THESAURUS, Boolean.TRUE);
    }

    @Test
    public void shouldNotBuildThesaurusXml() throws Exception {
        givenJobInstanceId(step.getChunkContext(), JOB_2);
        runner.test(step);
        verify(step.getJobExecutionContext(), never()).put(JobExecutionKey.WITH_THESAURUS, Boolean.TRUE);
    }

    @Configuration
    @Profile("IntegrationTests")
    @Import(CommonTestContextConfiguration.class)
    public static class Config {
        @Bean
        public JsoupService jsoupService() {
            return new JsoupService();
        }

        @Bean
        public BuildThesaurusStep buildThesaurusStep() {
            return new BuildThesaurusStep();
        }

        @Bean
        public CanadianTopicCodeService canadianTopicCodeService() {
            CanadianTopicCodeService topicCodeService = mock(CanadianTopicCodeService.class);
            when(topicCodeService.findAllCanadianTopicCodesForTheBook(JOB_1)).thenReturn(getTopicCodes());
            when(topicCodeService.findAllCanadianTopicCodesForTheBook(JOB_2)).thenReturn(Collections.emptyList());
            return topicCodeService;
        }

        private List<CanadianTopicCode> getTopicCodes() {
            return Arrays.asList(
                    getTopicCode("IPY.V.1", DOC_1),
                    getTopicCode("IPY.V", DOC_1),
                    getTopicCode("IPY", DOC_1),
                    getTopicCode("IPY.V.2", DOC_2),
                    getTopicCode("IPY.V", DOC_2),
                    getTopicCode("IPY", DOC_2)
                    );
        }

        private CanadianTopicCode getTopicCode(final String key, final String docId) {
            CanadianTopicCode topicCode = new CanadianTopicCode();
            topicCode.setJobInstanceId(1L);
            topicCode.setTopicKey(key);
            topicCode.setDocUuid(docId);
            return topicCode;
        }
    }
}
