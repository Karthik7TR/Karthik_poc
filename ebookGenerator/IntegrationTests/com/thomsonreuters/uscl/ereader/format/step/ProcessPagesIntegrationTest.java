package com.thomsonreuters.uscl.ereader.format.step;

import static org.mockito.Mockito.when;

import java.io.File;
import java.net.URISyntaxException;

import com.thomsonreuters.uscl.ereader.JobExecutionKey;
import com.thomsonreuters.uscl.ereader.context.CommonTestContextConfiguration;
import com.thomsonreuters.uscl.ereader.core.service.JsoupService;
import com.thomsonreuters.uscl.ereader.format.service.ReorderFootnotesService;
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
@ContextConfiguration(classes = {ProcessPagesIntegrationTest.Config.class, StepIntegrationTestRunner.Config.class})
@ActiveProfiles("IntegrationTests")
public final class ProcessPagesIntegrationTest {
    @Autowired
    private ProcessPages step;

    @Autowired
    private StepIntegrationTestRunner runner;

    private File resourceDir;

    @Before
    public void setUp() throws URISyntaxException {
        runner.setUp(step);
        resourceDir = new File(ProcessPagesIntegrationTest.class.getResource("resourceProcessPages").toURI());
    }

    @Test
    public void shouldTransformPages() throws Exception {
        when(step.getJobExecutionContext().get(JobExecutionKey.WITH_PAGE_NUMBERS)).thenReturn(Boolean.TRUE);
        runner.test(step, new File(resourceDir, "transformTest"));
    }

    @Test
    public void shouldCopyPages() throws Exception {
        runner.test(step, new File(resourceDir, "copyTest"));
    }

    @Configuration
    @Profile("IntegrationTests")
    @Import(CommonTestContextConfiguration.class)
    public static class Config {
        @Bean
        public ProcessPages processPages() {
            return new ProcessPages();
        }

        @Bean
        public ReorderFootnotesService reorderFootnotesService() {
            return new ReorderFootnotesService();
        }

        @Bean
        public JsoupService jsoupService() {
            return new JsoupService();
        }
    }
}
