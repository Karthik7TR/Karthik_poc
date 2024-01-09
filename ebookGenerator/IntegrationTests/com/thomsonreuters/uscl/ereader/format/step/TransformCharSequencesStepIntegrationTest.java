package com.thomsonreuters.uscl.ereader.format.step;

import com.thomsonreuters.uscl.ereader.JobExecutionKey;
import com.thomsonreuters.uscl.ereader.context.CommonTestContextConfiguration;
import com.thomsonreuters.uscl.ereader.format.service.DuplicatedPagebreaksResolver;
import com.thomsonreuters.uscl.ereader.format.service.InnerDocumentAnchorsMarker;
import com.thomsonreuters.uscl.ereader.format.service.InnerDocumentAnchorsMarkerImpl;
import com.thomsonreuters.uscl.ereader.format.service.TransformCharSequencesService;
import lombok.SneakyThrows;
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

import static com.thomsonreuters.uscl.ereader.StepTestUtil.whenJobExecutionPropertyBoolean;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {TransformCharSequencesStepIntegrationTest.Config.class, StepIntegrationTestRunner.Config.class})
@ActiveProfiles("IntegrationTests")
public class TransformCharSequencesStepIntegrationTest {
    private static final String RESOURCE_DIR_NAME = "resourceTransformChars";
    private static final String CW_TITLE_ID = "cw/eg/title";

    @Autowired
    private TransformCharSequencesStep transformCharSequencesStep;

    @Autowired
    private StepIntegrationTestRunner runner;

    @Before
    public void setUp() throws URISyntaxException {
        runner.setUp(transformCharSequencesStep, RESOURCE_DIR_NAME);
    }

    @Test
    @SneakyThrows
    public void shouldRemoveDoubleHyphensOnlyInAnnotations() {
        runner.test(transformCharSequencesStep, "test");
    }

    @Test
    @SneakyThrows
    public void shouldAddCarswellContainerToCiteQueryInCwBooks() {
        transformCharSequencesStep.getBookDefinition().setFullyQualifiedTitleId(CW_TITLE_ID);
        runner.test(transformCharSequencesStep, "testCw");
    }

    @Test
    @SneakyThrows
    public void shouldRemovePagebreaksFromFootnoteRef() {
        transformCharSequencesStep.getBookDefinition().setPrintPageNumbers(true);
        runner.test(transformCharSequencesStep, "testRemoveDuplicatedPagebreaks");
    }

    @Test
    @SneakyThrows
    public void shouldMovePagebreaksAfterFootnoteReferences() {
        whenJobExecutionPropertyBoolean(transformCharSequencesStep.getJobExecutionContext(), JobExecutionKey.PAGE_VOLUMES_SET, Boolean.TRUE);
        transformCharSequencesStep.getBookDefinition().setPrintPageNumbers(true);
        runner.test(transformCharSequencesStep, "testMovePbsAfterFtnReferences");
    }

    @Test
    @SneakyThrows
    public void shouldAddInnerAnchors() {
        runner.test(transformCharSequencesStep, "testInnerAnchors");
    }

    @Configuration
    @Profile("IntegrationTests")
    @Import(CommonTestContextConfiguration.class)
    public static class Config {
        @Bean
        public TransformCharSequencesStep transformCharSequencesStep() {
            return new TransformCharSequencesStep();
        }

        @Bean
        public TransformCharSequencesService removeDoubleHyphensService() {
            return new TransformCharSequencesService();
        }

        @Bean
        public DuplicatedPagebreaksResolver duplicatedPagebreaksResolver() {
            return new DuplicatedPagebreaksResolver();
        }

        @Bean
        public InnerDocumentAnchorsMarker innerDocumentAnchorsMarker() {
            return new InnerDocumentAnchorsMarkerImpl();
        }
    }
}
