package com.thomsonreuters.uscl.ereader.xpp.transformation.toc.step.rutter;

import static com.thomsonreuters.uscl.ereader.common.filesystem.FileContentMatcher.hasSameContentAs;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.Collections;

import javax.annotation.Resource;

import com.thomsonreuters.uscl.ereader.JobParameterKey;
import com.thomsonreuters.uscl.ereader.context.CommonTestContextConfiguration;
import com.thomsonreuters.uscl.ereader.core.book.domain.BookDefinition;
import com.thomsonreuters.uscl.ereader.request.domain.XppBundle;
import com.thomsonreuters.uscl.ereader.xpp.transformation.service.XppFormatFileSystem;
import com.thomsonreuters.uscl.ereader.xpp.transformation.toc.group.FileGroupHelper;
import com.thomsonreuters.uscl.ereader.xpp.transformation.toc.step.ExtractTocStep;
import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Answers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
@ActiveProfiles("IntegrationTests")
public final class RutterChapterTocIntegrationTest {
    private static final String MATERIAL_NUMBER = "42086195";
    private static final String WRAPPER_CHAPTER = "1-TRG_CADEBT_6.DIVXML.xml";
    private static final String CHILD_CHAPTER = "2-TRG_CADEBT_6_6A.DIVXML.xml";

    @Resource(name = "extractTocTask")
    @InjectMocks
    private ExtractTocStep step;
    @Autowired
    private XppFormatFileSystem fileSystem;

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private ChunkContext chunkContext;
    @Mock
    private BookDefinition bookDef;

    @Before
    public void setUp() throws IOException, URISyntaxException {
        org.mockito.MockitoAnnotations.initMocks(this);
        final XppBundle bundle = new XppBundle();
        bundle.setMaterialNumber(MATERIAL_NUMBER);
        bundle.setOrderedFileList(Arrays.asList(WRAPPER_CHAPTER, CHILD_CHAPTER));
        bundle.setProductType("bound");

        when(
            chunkContext.getStepContext()
                .getStepExecution()
                .getJobExecution()
                .getExecutionContext()
                .get(JobParameterKey.XPP_BUNDLES)).thenReturn(Collections.singletonList(bundle));

        when(
            chunkContext.getStepContext()
                .getStepExecution()
                .getJobExecution()
                .getExecutionContext()
                .get(JobParameterKey.EBOOK_DEFINITON)).thenReturn(bookDef);

        when(bookDef.isSplitBook()).thenReturn(false);
        FileUtils.copyDirectory(new File(this.getClass().getResource("./Format").toURI()), fileSystem.getFormatDirectory(step));
    }

    @After
    public void clean() throws IOException {
        FileUtils.cleanDirectory(fileSystem.getFormatDirectory(step).getParentFile());
    }

    @Test
    public void shouldCreateTocFileBasedBundleMainContentOriginalFile() throws Exception {
        //given
        final File expectedToc = new File(this.getClass().getResource("./expected/toc.xml").toURI());
        final File actualToc = fileSystem.getTocFile(step);
        //when
        step.executeStep();
        //then
        assertThat(expectedToc, hasSameContentAs(actualToc));
    }

    @Configuration
    @Profile("IntegrationTests")
    @Import(CommonTestContextConfiguration.class)
    public static class Config {
        @Bean(name = "extractTocTask")
        public ExtractTocStep extractTocTask() {
            return new ExtractTocStep();
        }

        @Bean
        public FileGroupHelper fileGroupHelper() {
            return new FileGroupHelper();
        }
    }
}
