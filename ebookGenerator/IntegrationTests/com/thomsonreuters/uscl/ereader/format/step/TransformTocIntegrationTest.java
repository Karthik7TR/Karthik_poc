package com.thomsonreuters.uscl.ereader.format.step;

import com.thomsonreuters.uscl.ereader.context.CommonTestContextConfiguration;
import com.thomsonreuters.uscl.ereader.format.service.TocHeadersSubstitutionService;
import com.thomsonreuters.uscl.ereader.format.service.TransformTocService;
import com.thomsonreuters.uscl.ereader.format.service.TransformTocServiceImpl;
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
@ContextConfiguration(classes = {TransformTocIntegrationTest.Config.class, StepIntegrationTestRunner.Config.class})
@ActiveProfiles("IntegrationTests")
public class TransformTocIntegrationTest {
    private static final String RESOURCE_DIR_NAME = "resourceTransformToc";

    @Autowired
    private TransformToc transformToc;

    @Autowired
    private StepIntegrationTestRunner runner;

    @Before
    public void setUp() throws URISyntaxException {
        runner.setUp(transformToc, RESOURCE_DIR_NAME);
    }

    @Test
    @SneakyThrows
    public void shouldRemoveDoubleHyphensOnlyInAnnotations() {
        runner.test(transformToc, "transformDoubleHyphensIntoEmDashes");
    }

    @Test
    @SneakyThrows
    public void shouldSubstituteTocHeaders() {
        transformToc.getBookDefinition().setSubstituteTocHeaders(true);
        runner.test(transformToc, "substituteTocHeaders");
    }

    @Configuration
    @Profile("IntegrationTests")
    @Import(CommonTestContextConfiguration.class)
    public static class Config {
        @Bean
        public TransformToc transformToc() {
            return new TransformToc();
        }

        @Bean
        public TransformTocService transformTocService() {
            return new TransformTocServiceImpl();
        }

        @Bean
        public TocHeadersSubstitutionService tocHeadersSubstitutionService() {
            return new TocHeadersSubstitutionService();
        }
    }
}
