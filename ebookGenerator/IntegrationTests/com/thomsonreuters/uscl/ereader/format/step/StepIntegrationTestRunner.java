package com.thomsonreuters.uscl.ereader.format.step;

import static com.thomsonreuters.uscl.ereader.StepTestUtil.givenJobExecutionContext;
import static com.thomsonreuters.uscl.ereader.StepTestUtil.validateDirsOnExpected;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.IOException;

import com.thomsonreuters.uscl.ereader.JobParameterKey;
import com.thomsonreuters.uscl.ereader.common.filesystem.AssembleFileSystem;
import com.thomsonreuters.uscl.ereader.common.filesystem.AssembleFileSystemImpl;
import com.thomsonreuters.uscl.ereader.common.filesystem.BookFileSystem;
import com.thomsonreuters.uscl.ereader.common.filesystem.FormatFileSystem;
import com.thomsonreuters.uscl.ereader.common.filesystem.FormatFileSystemImpl;
import com.thomsonreuters.uscl.ereader.common.filesystem.GatherFileSystem;
import com.thomsonreuters.uscl.ereader.common.filesystem.GatherFileSystemImpl;
import com.thomsonreuters.uscl.ereader.common.step.BaseStep;
import com.thomsonreuters.uscl.ereader.context.CommonTestContextConfiguration;
import com.thomsonreuters.uscl.ereader.core.book.domain.BookDefinition;
import org.apache.commons.io.FileUtils;
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

    @Autowired
    private BookFileSystem bookFileSystem;
    private File workDir;

    public void setUp(final BaseStep step) {
        final ExecutionContext jobExecutionContext = Mockito.mock(ExecutionContext.class);
        final ChunkContext chunkContext = Mockito.mock(ChunkContext.class, Answers.RETURNS_DEEP_STUBS.get());
        final BookDefinition bookDefinition = new BookDefinition();

        step.setChunkContext(chunkContext);
        when(jobExecutionContext.get(JobParameterKey.EBOOK_DEFINITON)).thenReturn(bookDefinition);
        givenJobExecutionContext(chunkContext, jobExecutionContext);
    }

    public void test(final BaseStep step, final File resource) throws Exception {
        init(resource);

        step.executeStep();

        validateDirsOnExpected(new File(resource, EXPECTED), workDir);
        tearDown();
    }

    private void init(final File resource) throws IOException {
        workDir = bookFileSystem.getWorkDirectory(null);
        FileUtils.copyDirectory(new File(resource, SOURCE), workDir);
        new File(resource, EXPECTED).mkdir();
    }

    private void tearDown() {
        FileUtils.deleteQuietly(workDir);
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
