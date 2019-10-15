package com.thomsonreuters.uscl.ereader.format.step;

import java.io.File;
import java.net.URISyntaxException;

import com.thomsonreuters.uscl.ereader.context.CommonTestContextConfiguration;
import com.thomsonreuters.uscl.ereader.core.service.JsoupService;
import com.thomsonreuters.uscl.ereader.format.service.TransformDoubleHyphensIntoEmDashesService;
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

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {ProcessAnnotationsStepIntegrationTest.Config.class, StepIntegrationTestRunner.Config.class})
@ActiveProfiles("IntegrationTests")
public class ProcessAnnotationsStepIntegrationTest {
    private static final String RESOURCE_DIR_NAME = "resourceProcessAnnotations";

    @Autowired
    private ProcessAnnotationsStep processAnnotationsStep;

    @Autowired
    private StepIntegrationTestRunner runner;

    private File resourceDir;

    @Before
    public void setUp() throws URISyntaxException {
        runner.setUp(processAnnotationsStep);
        resourceDir = new File(InlineTocStepIntegrationTest.class.getResource(RESOURCE_DIR_NAME).toURI());
    }

    @Test
    @SneakyThrows
    public void shouldRemoveDoubleHyphensOnlyInAnnotations() {
        runner.test(processAnnotationsStep, resourceDir);
    }

    @Configuration
    @Profile("IntegrationTests")
    @Import(CommonTestContextConfiguration.class)
    public static class Config {
        @Bean
        public ProcessAnnotationsStep processAnnotationsStep() {
            return new ProcessAnnotationsStep();
        }

        @Bean
        public TransformDoubleHyphensIntoEmDashesService removeDoubleHyphensService() {
            return new TransformDoubleHyphensIntoEmDashesService();
        }

        @Bean
        public JsoupService jsoupService() {
            return new JsoupService();
        }
    }
}
