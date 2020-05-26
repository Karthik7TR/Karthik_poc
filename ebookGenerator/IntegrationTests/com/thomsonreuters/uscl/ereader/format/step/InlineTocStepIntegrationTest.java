package com.thomsonreuters.uscl.ereader.format.step;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.File;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

import com.thomsonreuters.uscl.ereader.JobExecutionKey;
import com.thomsonreuters.uscl.ereader.context.CommonTestContextConfiguration;
import com.thomsonreuters.uscl.ereader.core.service.JsoupService;
import com.thomsonreuters.uscl.ereader.format.service.CssStylingService;
import com.thomsonreuters.uscl.ereader.format.service.InlineTocService;
import com.thomsonreuters.uscl.ereader.gather.metadata.service.DocMetadataService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {InlineTocStepIntegrationTest.Config.class, StepIntegrationTestRunner.Config.class})
@ActiveProfiles("IntegrationTests")
public final class InlineTocStepIntegrationTest {
    @Autowired
    private InlineTocStep step;

    @Autowired
    private StepIntegrationTestRunner runner;

    private File resourceDir;

    @Before
    public void setUp() throws URISyntaxException {
        runner.setUp(step);
        resourceDir = new File(InlineTocStepIntegrationTest.class.getResource("resourceInlineToc").toURI());
    }

    @Test
    public void shouldCreateTocWithPages() throws Exception {
        step.getBookDefinition().setInlineTocIncluded(true);
        when(step.getJobExecutionContext().get(JobExecutionKey.WITH_PAGE_NUMBERS)).thenReturn(Boolean.TRUE);

        runner.test(step, new File(resourceDir, "inlineTocWithPagesTest"));

        verify(step.getJobExecutionContext()).put(JobExecutionKey.WITH_INLINE_TOC, Boolean.TRUE);
    }

    @Test
    public void shouldCreateTocWithoutPages() throws Exception {
        step.getBookDefinition().setInlineTocIncluded(true);

        runner.test(step, new File(resourceDir, "inlineTocWithoutPagesTest"));

        verify(step.getJobExecutionContext()).put(JobExecutionKey.WITH_INLINE_TOC, Boolean.TRUE);
    }

    @Test
    public void shouldCreateTocWithDefaultStyles() throws Exception {
        step.getBookDefinition().setInlineTocIncluded(true);

        runner.test(step, new File(resourceDir, "noInlineTocAttributes"));

        verify(step.getJobExecutionContext()).put(JobExecutionKey.WITH_INLINE_TOC, Boolean.TRUE);
    }

    @Test
    public void shouldNotCreateToc() throws Exception {
        step.getBookDefinition().setInlineTocIncluded(false);

        step.executeStep();

        verify(step.getJobExecutionContext(), never()).put(JobExecutionKey.WITH_INLINE_TOC, Boolean.TRUE);
    }

    private static Map<String, String> getFamilyGuidsMap() {
        final Map<String, String> map = new HashMap<>();

        map.put("I10000000000000000000000000000000", "I5885e15011c411e68073fa8dc60233b8");
        map.put("I20000000000000000000000000000000", "I0b12194095cc11e4a692d49183167da5");
        map.put("I30000000000000000000000000000000", "I89e5c1200f5a11da9cd1b8ea82133782");
        map.put("I40000000000000000000000000000000", "I8a0fb7510f5a11da9cd1b8ea82133782");
        map.put("I50000000000000000000000000000000", "I957a4ba0558a11dc9c6a0000837bc6dd");

        return map;
    }

    @Configuration
    @Profile("IntegrationTests")
    @Import(CommonTestContextConfiguration.class)
    public static class Config {
        @Bean
        public InlineTocStep inlineTocStep() {
            return new InlineTocStep();
        }

        @Bean
        public InlineTocService inlineTocService() {
            return new InlineTocService();
        }

        @Bean
        public CssStylingService cssStylingService() {
            return new CssStylingService();
        }

        @Bean
        public DocMetadataService docMetadataService() {
            final DocMetadataService docMetadataService = Mockito.mock(DocMetadataService.class);
            when(docMetadataService.findDistinctProViewFamGuidsByJobId(any())).thenReturn(getFamilyGuidsMap());
            return docMetadataService;
        }

        @Bean
        public JsoupService jsoupService() {
            return new JsoupService();
        }
    }
}
