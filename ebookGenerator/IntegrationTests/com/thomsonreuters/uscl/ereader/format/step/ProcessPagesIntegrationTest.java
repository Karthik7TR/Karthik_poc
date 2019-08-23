package com.thomsonreuters.uscl.ereader.format.step;

import static com.thomsonreuters.uscl.ereader.StepTestUtil.givenJobExecutionContext;
import static com.thomsonreuters.uscl.ereader.common.filesystem.FileContentMatcher.hasSameContentAs;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.thomsonreuters.uscl.ereader.JobExecutionKey;
import com.thomsonreuters.uscl.ereader.JobParameterKey;
import com.thomsonreuters.uscl.ereader.common.filesystem.FormatFileSystem;
import com.thomsonreuters.uscl.ereader.common.filesystem.GatherFileSystem;
import com.thomsonreuters.uscl.ereader.common.filesystem.NortTocCwbFileSystemConstants;
import com.thomsonreuters.uscl.ereader.context.CommonTestContextConfiguration;
import com.thomsonreuters.uscl.ereader.core.book.domain.BookDefinition;
import com.thomsonreuters.uscl.ereader.core.service.JsoupService;
import com.thomsonreuters.uscl.ereader.format.service.ReorderFootnotesService;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.mockito.Answers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = ProcessPagesIntegrationTest.Config.class)
@ActiveProfiles("IntegrationTests")
public final class ProcessPagesIntegrationTest {
    @InjectMocks
    @Autowired
    private ProcessPages step;
    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private ChunkContext chunkContext;
    @Mock
    private ExecutionContext jobExecutionContext;
    @Mock
    private FormatFileSystem formatFileSystem;
    @Mock
    private GatherFileSystem gatherFileSystem;

    private BookDefinition bookDefinition;

    private File resourceDir;
    private File sourceDirectory;
    private File destinationDirectory;
    private Set<String> uuids;

    @Rule
    public TemporaryFolder tempDirectory = new TemporaryFolder();

    @Before
    public void setUp() throws URISyntaxException, IOException {
        org.mockito.MockitoAnnotations.initMocks(this);
        bookDefinition = new BookDefinition();
        when(jobExecutionContext.get(JobParameterKey.EBOOK_DEFINITON)).thenReturn(bookDefinition);
        givenJobExecutionContext(chunkContext, jobExecutionContext);

        resourceDir = new File(ProcessPagesIntegrationTest.class.getResource("resourceProcessPages").toURI());

        initTempDirs(resourceDir);

        uuids = Stream.of(sourceDirectory.listFiles()).map(File::getName).map(FilenameUtils::removeExtension).collect(Collectors.toSet());
    }

    @Test
    public void shouldTransformPages() throws Exception {
        when(jobExecutionContext.get(JobExecutionKey.PAGE_NUMBERS_EXIST_IN_SOURCE_DOCS)).thenReturn(Boolean.TRUE);

        bookDefinition.setPrintPageNumbers(true);

        step.executeStep();

        validate(new File(resourceDir, "expected"));
    }

    @Test
    public void shouldCopyPages() throws Exception {
        step.executeStep();

        validate(sourceDirectory);
    }

    @After
    public void tearDown() {
        FileUtils.deleteQuietly(tempDirectory.getRoot());
    }

    private void initTempDirs(final File resourceDir) throws IOException {
        tempDirectory.create();

        final File gatherDirectory = tempDirectory.newFolder(NortTocCwbFileSystemConstants.GATHER_DIR.getName());
        final File formatDirectory = tempDirectory.newFolder(NortTocCwbFileSystemConstants.FORMAT_DIR.getName());

        final File sourceXmlDirectory = new File(gatherDirectory, NortTocCwbFileSystemConstants.GATHER_DOCS_DIR.getName());
        sourceDirectory = new File(formatDirectory, NortTocCwbFileSystemConstants.FORMAT_HTML_WRAPPER_DIR.getName());
        destinationDirectory = new File(formatDirectory, NortTocCwbFileSystemConstants.FORMAT_PROCESS_PAGES_DIR.getName());

        when(gatherFileSystem.getGatherRootDirectory(step)).thenReturn(gatherDirectory);
        when(formatFileSystem.getFormatDirectory(step)).thenReturn(formatDirectory);

        copyDir(resourceDir, "source/Gather", sourceXmlDirectory);
        copyDir(resourceDir, "source/Format", sourceDirectory);
    }

    private void copyDir(final File baseSourceDir, final String sourceDirName, final File destDir) throws IOException {
        final File sourceDir = new File(baseSourceDir, sourceDirName);
        FileUtils.copyDirectory(sourceDir, destDir);
    }

    private void validate(final File expectedDir) {
        uuids.stream().map(uuid -> uuid + ".html").forEach(fileName -> {
            final File expected = new File(expectedDir, fileName);
            final File actual = new File(destinationDirectory, fileName);
            assertThat(expected, hasSameContentAs(actual));
        });
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
