package com.thomsonreuters.uscl.ereader.format.step;

import com.thomsonreuters.uscl.ereader.JobExecutionKey;
import com.thomsonreuters.uscl.ereader.context.CommonTestContextConfiguration;
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
import java.util.List;

import static com.thomsonreuters.uscl.ereader.StepTestUtil.givenJobInstanceId;
import static com.thomsonreuters.uscl.ereader.StepTestUtil.whenJobExecutionPropertyBoolean;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {AddThesaurusToDocumentsStepIntegrationTest.Config.class, StepIntegrationTestRunner.Config.class})
@ActiveProfiles("IntegrationTests")
public class AddThesaurusToDocumentsStepIntegrationTest {
    private static final String DOC_1 = "doc1";
    private static final String DOC_2 = "doc2";
    private static final long JOB_1 = 1L;

    @Autowired
    private AddThesaurusToDocumentsStep step;

    @Autowired
    private StepIntegrationTestRunner runner;

    @Before
    public void setUp() throws URISyntaxException {
        runner.setUp(step, "resourceAddThesaurusToDocuments");
    }

    @Test
    public void shouldAddThesaurusToDocuments() throws Exception {
        givenJobInstanceId(step.getChunkContext(), JOB_1);
        whenJobExecutionPropertyBoolean(step.getJobExecutionContext(), JobExecutionKey.WITH_THESAURUS, Boolean.TRUE);
        runner.test(step, "addThesaurus");
    }

    @Configuration
    @Profile("IntegrationTests")
    @Import(CommonTestContextConfiguration.class)
    public static class Config {
        @Bean
        public AddThesaurusToDocumentsStep buildThesaurusStep() {
            return new AddThesaurusToDocumentsStep();
        }

        @Bean
        public CanadianTopicCodeService canadianTopicCodeService() {
            CanadianTopicCodeService topicCodeService = mock(CanadianTopicCodeService.class);
            when(topicCodeService.findCanadianTopicCodesForDocument(JOB_1, DOC_1)).thenReturn(getTopicCodes1());
            when(topicCodeService.findCanadianTopicCodesForDocument(JOB_1, DOC_2)).thenReturn(getTopicCodes2());
            return topicCodeService;
        }

        private List<CanadianTopicCode> getTopicCodes1() {
            return Arrays.asList(
                    getTopicCode("IPY.V.1", DOC_1),
                    getTopicCode("IPY.V", DOC_1),
                    getTopicCode("IPY", DOC_1)
            );
        }

        private List<CanadianTopicCode> getTopicCodes2() {
            return Arrays.asList(
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
