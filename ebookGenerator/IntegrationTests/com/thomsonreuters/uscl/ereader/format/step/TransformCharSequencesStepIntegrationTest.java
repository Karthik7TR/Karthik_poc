package com.thomsonreuters.uscl.ereader.format.step;

import com.thomsonreuters.uscl.ereader.context.CommonTestContextConfiguration;
import com.thomsonreuters.uscl.ereader.core.service.JsoupService;
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

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {TransformCharSequencesStepIntegrationTest.Config.class, StepIntegrationTestRunner.Config.class})
@ActiveProfiles("IntegrationTests")
public class TransformCharSequencesStepIntegrationTest {
    private static final String RESOURCE_DIR_NAME = "resourceTransformChars";

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
        public JsoupService jsoupService() {
            return new JsoupService();
        }
    }
}
