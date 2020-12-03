package com.thomsonreuters.uscl.ereader.format.step;

import static org.mockito.Mockito.when;

import java.net.URISyntaxException;

import com.thomsonreuters.uscl.ereader.JobExecutionKey;
import com.thomsonreuters.uscl.ereader.context.CommonTestContextConfiguration;
import com.thomsonreuters.uscl.ereader.format.links.CiteQueryAdapter;
import com.thomsonreuters.uscl.ereader.format.service.CiteQueryService;
import com.thomsonreuters.uscl.ereader.format.service.ReorderFootnotesService;
import lombok.SneakyThrows;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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

    @Before
    public void setUp() throws URISyntaxException {
        runner.setUp(step, "resourceProcessPages");
    }

    @Test
    public void shouldTransformPages() throws Exception {
        when(step.getJobExecutionContext().get(JobExecutionKey.WITH_PAGE_NUMBERS)).thenReturn(Boolean.TRUE);
        runner.test(step, "transformTest");
    }

    @Test
    public void shouldTransformPagesWithSectionbreaks() throws Exception {
        when(step.getJobExecutionContext().get(JobExecutionKey.WITH_PAGE_NUMBERS)).thenReturn(Boolean.TRUE);
        runner.test(step, "transformWithSectionbreaksTest");
    }

    @Test
    public void shouldCopyPages() throws Exception {
        runner.test(step, "copyTest");
    }

    @Configuration
    @Profile("IntegrationTests")
    @Import(CommonTestContextConfiguration.class)
    public static class Config {
        @Value("${mud.domain}")
        private String mudDomain;

        @Value("${mud.parameter.rs}")
        private String mudParameterRs;

        @Value("${mud.parameter.vr}")
        private String mudParameterVr;

        @Bean
        public ProcessPages processPages() {
            return new ProcessPages();
        }

        @Bean
        public ReorderFootnotesService reorderFootnotesService() {
            return new ReorderFootnotesService();
        }

        @Bean
        public CiteQueryService CiteQueryService() {
            return new CiteQueryService();
        }

        @Bean
        @SneakyThrows
        public CiteQueryAdapter citeQueryAdapter() {
            final CiteQueryAdapter adapter = new CiteQueryAdapter();

            adapter.setHostname(mudDomain);
            adapter.setRs(mudParameterRs);
            adapter.setVr(mudParameterVr);

            return adapter;
        }
    }
}
