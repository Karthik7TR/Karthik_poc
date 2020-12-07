package com.thomsonreuters.uscl.ereader.format.step;

import static com.thomsonreuters.uscl.ereader.StepTestUtil.givenJobExecutionContext;
import static com.thomsonreuters.uscl.ereader.StepTestUtil.validateDirsOnExpected;
import static org.mockito.Mockito.when;

import java.io.File;
import java.net.URISyntaxException;

import com.thomsonreuters.uscl.ereader.JobParameterKey;
import com.thomsonreuters.uscl.ereader.common.filesystem.AssembleFileSystem;
import com.thomsonreuters.uscl.ereader.common.filesystem.AssembleFileSystemImpl;
import com.thomsonreuters.uscl.ereader.common.filesystem.BookFileSystem;
import com.thomsonreuters.uscl.ereader.common.filesystem.FormatFileSystem;
import com.thomsonreuters.uscl.ereader.common.filesystem.FormatFileSystemImpl;
import com.thomsonreuters.uscl.ereader.common.filesystem.GatherFileSystem;
import com.thomsonreuters.uscl.ereader.common.filesystem.GatherFileSystemImpl;
import com.thomsonreuters.uscl.ereader.common.filesystem.NasFileSystem;
import com.thomsonreuters.uscl.ereader.common.filesystem.TestBookFileSystemImpl;
import com.thomsonreuters.uscl.ereader.common.filesystem.TestNasFileSystemImpl;
import com.thomsonreuters.uscl.ereader.common.step.BaseStep;
import com.thomsonreuters.uscl.ereader.context.CommonTestContextConfiguration;
import com.thomsonreuters.uscl.ereader.core.book.domain.BookDefinition;
import com.thomsonreuters.uscl.ereader.core.book.domain.DocumentTypeCode;
import com.thomsonreuters.uscl.ereader.core.book.util.FileUtils;
import lombok.Getter;
import org.mockito.Answers;
import org.mockito.Mockito;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
public class StepIntegrationTestRunner {
    private static final String SOURCE = "source";
    private static final String EXPECTED = "expected";
    private static final String TITLE_ID = "uscl/an/test_book";
    private static final int THRESHOLD_VALUE = 10;
    private static final String NAS = "nas";

    @Autowired
    private BookFileSystem bookFileSystem;
    @Autowired
    private NasFileSystem nasFileSystem;
    private File workDir;
    private File nasDir;

    @Getter
    private File resourceRootDir;

    public void setUp(final BaseStep step, final String resourceDirName) throws URISyntaxException {
        setUp(step, resourceDirName, false);
    }

    public void setUp(final BaseStep step, final String resourceDirName, final boolean isSpy) throws URISyntaxException {
        resourceRootDir = new File(StepIntegrationTestRunner.class.getResource(resourceDirName).toURI());
        setUp(step, isSpy);
    }

    public void setUp(final BaseStep step, final boolean isSpy) {
        final ExecutionContext jobExecutionContext = isSpy ? Mockito.spy(ExecutionContext.class) : Mockito.mock(ExecutionContext.class);
        final ChunkContext chunkContext = Mockito.mock(ChunkContext.class, Answers.RETURNS_DEEP_STUBS.get());

        step.setChunkContext(chunkContext);
        when(jobExecutionContext.get(JobParameterKey.EBOOK_DEFINITON)).thenReturn(getBookDefinition());
        givenJobExecutionContext(chunkContext, jobExecutionContext);
    }

    private BookDefinition getBookDefinition() {
        final BookDefinition bookDefinition = new BookDefinition();
        bookDefinition.setFullyQualifiedTitleId(TITLE_ID);
        bookDefinition.setDocumentTypeCodes(getDocumentTypeCode());
        return bookDefinition;
    }

    private DocumentTypeCode getDocumentTypeCode() {
        final DocumentTypeCode documentTypeCode = new DocumentTypeCode();
        documentTypeCode.setThresholdValue(THRESHOLD_VALUE);
        return documentTypeCode;
    }

    public void testWithSourceOnly(final BaseStep step, final String resourceTestDir) throws Exception {
        test(step, resourceTestDir, true, false);
    }

    public void testWithExpectedOnly(final BaseStep step, final String resourceTestDir) throws Exception {
        test(step, resourceTestDir, false, true);
    }

    public void test(final BaseStep step, final String resourceTestDir) throws Exception {
        test(step, resourceTestDir, true, true);
    }

    public void test(final BaseStep step) throws Exception {
        test(step, null, false, false);
    }

    private void test(final BaseStep step, final String testDirName, boolean withSourceDir, boolean withExpectedDir) throws Exception {
        File resource = getTestDir(testDirName);
        initWorkDir();
        copyNasDir();
        copySourceDir(resource, withSourceDir);

        step.executeStep();

        validateResult(resource, withExpectedDir);
        tearDown();
    }

    private void initWorkDir() {
        workDir = bookFileSystem.getWorkDirectory(null);
        nasDir = ((TestNasFileSystemImpl) nasFileSystem).getRootDirectory();
    }

    private void tearDown() {
        FileUtils.deleteQuietly(workDir);
        FileUtils.deleteQuietly(nasDir);
        ((TestBookFileSystemImpl) bookFileSystem).reset();
        ((TestNasFileSystemImpl) nasFileSystem).reset();
    }

    private void copyNasDir() {
        File staticDir = new File(resourceRootDir, NAS);
        if (staticDir.exists()) {
            FileUtils.copyDirectory(staticDir, nasDir);
        }
    }
    private void copySourceDir(File resource, boolean withSourceDir) {
        if (withSourceDir) {
            FileUtils.copyDirectory(new File(resource, SOURCE), workDir);
        }
    }
    private void validateResult(File resource, boolean withExpectedDir) {
        if (withExpectedDir) {
            validateDirsOnExpected(new File(resource, EXPECTED), workDir);
        }
    }

    public File getTestDir(final String resourceTestDir) {
        return resourceTestDir != null ? new File(resourceRootDir, resourceTestDir): null;
    }

    @Configuration
    @Profile("IntegrationTests")
    @Import(CommonTestContextConfiguration.class)
    public static class Config {
        @Bean
        public GatherFileSystem gatherFileSystem() {
            return new GatherFileSystemImpl();
        }

        @Bean
        public FormatFileSystem formatFileSystem() {
            return new FormatFileSystemImpl();
        }

        @Bean
        public AssembleFileSystem assembleFileSystem() {
            return new AssembleFileSystemImpl();
        }

        @Bean
        public StepIntegrationTestRunner stepIntegrationTestRunner() {
            return new StepIntegrationTestRunner();
        }
    }
}
