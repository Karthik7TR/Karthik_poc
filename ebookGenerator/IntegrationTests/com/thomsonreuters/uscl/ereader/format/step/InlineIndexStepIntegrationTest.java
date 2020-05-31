package com.thomsonreuters.uscl.ereader.format.step;

import static com.thomsonreuters.uscl.ereader.StepTestUtil.whenJobExecutionPropertyInt;
import static com.thomsonreuters.uscl.ereader.StepTestUtil.whenJobExecutionPropertyString;
import static org.mockito.Mockito.*;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;

import com.thomsonreuters.uscl.ereader.JobExecutionKey;
import com.thomsonreuters.uscl.ereader.common.filesystem.BookFileSystem;
import com.thomsonreuters.uscl.ereader.context.CommonTestContextConfiguration;
import com.thomsonreuters.uscl.ereader.core.service.JsoupService;
import com.thomsonreuters.uscl.ereader.format.links.CiteQueryAdapter;
import com.thomsonreuters.uscl.ereader.format.service.CssStylingService;
import com.thomsonreuters.uscl.ereader.format.service.InlineIndexService;
import com.thomsonreuters.uscl.ereader.gather.restclient.service.GatherService;
import lombok.SneakyThrows;
import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
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
@ContextConfiguration(classes = {InlineIndexStepIntegrationTest.Config.class, StepIntegrationTestRunner.Config.class})
@ActiveProfiles("IntegrationTests")
public final class InlineIndexStepIntegrationTest {
    private static final int DOC_COUNT = 10;

    @Autowired
    private InlineIndexStep step;

    @Autowired
    private StepIntegrationTestRunner runner;

    @Autowired
    private BookFileSystem bookFileSystem;

    private File resourceDir;

    @Before
    public void setUp() throws URISyntaxException, IOException {
        runner.setUp(step);
        resourceDir = new File(InlineTocStepIntegrationTest.class.getResource("resourceInlineIndex").toURI());

        step.getBookDefinition().setIndexIncluded(true);

        final File guids = new File(bookFileSystem.getWorkDirectory(step), "docs.txt");
        guids.createNewFile();
        whenJobExecutionPropertyString(step.getJobExecutionContext(), JobExecutionKey.DOCS_DYNAMIC_GUIDS_FILE, guids.getAbsolutePath());
        whenJobExecutionPropertyInt(step.getJobExecutionContext(), JobExecutionKey.EBOOK_STATS_DOC_COUNT, DOC_COUNT);
    }

    @Test
    public void shouldCreateInlineIndex() throws Exception {
        test("inlineIndexWithoutPagesTest", false);
        verify(step.getJobExecutionContext()).put(JobExecutionKey.WITH_INLINE_INDEX, Boolean.TRUE);
    }

    @Test
    public void shouldCreateIndexWithPages() throws Exception {
        test("inlineIndexWithPagesTest", true);
        verify(step.getJobExecutionContext()).put(JobExecutionKey.WITH_INLINE_INDEX, Boolean.TRUE);
    }

    @Test
    public void shouldNotCreateInlineIndexIfNoIndexToc() throws Exception {
        runner.testWithSourceOnly(step, new File(resourceDir, "noIndexTocTest"));
        verify(step.getJobExecutionContext(), never()).put(JobExecutionKey.WITH_INLINE_INDEX, Boolean.TRUE);
    }

    @Test
    public void shouldNotCreateInlineIndexIfNoIndexTocFile() throws Exception {
        runner.test(step);
        verify(step.getJobExecutionContext(), never()).put(JobExecutionKey.WITH_INLINE_INDEX, Boolean.TRUE);
    }

    private void test(final String testDir, final boolean pageNumbers) throws Exception {
        when(step.getJobExecutionContext().get(JobExecutionKey.WITH_PAGE_NUMBERS)).thenReturn(pageNumbers);
        runner.test(step, new File(resourceDir, testDir));
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
        public InlineIndexStep inlineIndexStep() {
            return new InlineIndexStep();
        }

        @Bean
        public InlineIndexService inlineIndexService() {
            return new InlineIndexService();
        }

        @Bean
        public CssStylingService cssStylingService() {
            return new CssStylingService();
        }

        @Bean
        public JsoupService jsoupService() {
            return new JsoupService();
        }

        @Bean
        public GatherService gatherService() {
            return Mockito.mock(GatherService.class);
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
