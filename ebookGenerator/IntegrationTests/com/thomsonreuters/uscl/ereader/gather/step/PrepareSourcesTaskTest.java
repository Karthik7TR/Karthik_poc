package com.thomsonreuters.uscl.ereader.gather.step;

import com.thomsonreuters.uscl.ereader.JobExecutionKey;
import com.thomsonreuters.uscl.ereader.StepTestUtil;
import com.thomsonreuters.uscl.ereader.context.CommonTestContextConfiguration;
import com.thomsonreuters.uscl.ereader.core.book.domain.BookDefinition;
import com.thomsonreuters.uscl.ereader.core.book.domain.CombinedBookDefinition;
import com.thomsonreuters.uscl.ereader.core.book.domain.CombinedBookDefinitionSource;
import com.thomsonreuters.uscl.ereader.core.book.model.TitleIdAndProviewName;
import com.thomsonreuters.uscl.ereader.format.step.StepIntegrationTestRunner;
import com.thomsonreuters.uscl.ereader.gather.domain.GatherResponse;
import com.thomsonreuters.uscl.ereader.gather.step.service.PrepareSourcesService;
import com.thomsonreuters.uscl.ereader.gather.step.service.RetrieveService;
import com.thomsonreuters.uscl.ereader.gather.step.service.RetrieveServiceLookup;
import com.thomsonreuters.uscl.ereader.gather.step.service.impl.PrepareSourcesServiceImpl;
import com.thomsonreuters.uscl.ereader.gather.step.service.impl.RetrieveServiceLookupImpl;
import com.thomsonreuters.uscl.ereader.stats.PublishingStatus;
import com.thomsonreuters.uscl.ereader.stats.service.PublishingStatsService;
import lombok.SneakyThrows;
import org.apache.commons.io.FileUtils;
import org.jetbrains.annotations.NotNull;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.mockito.Answers;
import org.mockito.Mockito;
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

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.thomsonreuters.uscl.ereader.StepTestUtil.givenJobExecutionContext;
import static com.thomsonreuters.uscl.ereader.common.filesystem.FileContentMatcher.hasSameContentAs;
import static com.thomsonreuters.uscl.ereader.common.filesystem.NortTocCwbFileSystemConstants.GATHER_DOCS_GUIDS_FILE;
import static com.thomsonreuters.uscl.ereader.common.filesystem.NortTocCwbFileSystemConstants.TOC_FILE;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {PrepareSourcesTaskTest.Config.class, StepIntegrationTestRunner.Config.class})
@ActiveProfiles("IntegrationTests")
public class PrepareSourcesTaskTest {
    private static final TitleIdAndProviewName DATA_FILE = new TitleIdAndProviewName("uscl/an/test_1_FILE", "Title1");
    private static final TitleIdAndProviewName DATA_USCL = new TitleIdAndProviewName("uscl/an/test_2_TOC","Title2");
    private File actualTocFile;
    private File actualDocsGuidsFile;
    private File expectedTocFile;
    private File expectedDocsGuidsFile;
    @Autowired
    private PrepareSourcesTask step;
    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    @SneakyThrows
    @Before
    public void setUp() {
        Path rootTestFolder = Paths.get(PrepareSourcesTaskTest.class.getResource("resourcePrepareSources").toURI());
        FileUtils.copyDirectory(rootTestFolder.resolve("source").toFile(), temporaryFolder.getRoot());
        Path tempFolder = Paths.get(temporaryFolder.getRoot().toURI());
        Path expectedFolder = rootTestFolder.resolve("expected");
        expectedTocFile = expectedFolder.resolve(TOC_FILE.getName()).toFile();
        expectedDocsGuidsFile = expectedFolder.resolve(GATHER_DOCS_GUIDS_FILE.getName()).toFile();
        actualTocFile = tempFolder.resolve(TOC_FILE.getName()).toFile();
        actualDocsGuidsFile = tempFolder.resolve(GATHER_DOCS_GUIDS_FILE.getName()).toFile();
    }

    @Test
    public void shouldPrepareSources() throws Exception {
        initStep();
        step.executeStep();
        assertThat(expectedTocFile.getName(), actualTocFile, hasSameContentAs(expectedTocFile));
        assertThat(expectedDocsGuidsFile.getName(), actualDocsGuidsFile, hasSameContentAs(expectedDocsGuidsFile));
    }

    private void initStep() {
        final ExecutionContext jobExecutionContext = mock(ExecutionContext.class);
        when(jobExecutionContext.getString(JobExecutionKey.GATHER_TOC_FILE)).thenReturn(actualTocFile.getAbsolutePath());
        when(jobExecutionContext.getString(JobExecutionKey.DOCS_DYNAMIC_GUIDS_FILE)).thenReturn(actualDocsGuidsFile.getAbsolutePath());
        when(jobExecutionContext.containsKey(any())).thenReturn(true);
        final ChunkContext chunkContext = mock(ChunkContext.class, Answers.RETURNS_DEEP_STUBS.get());
        step.setChunkContext(chunkContext);
        givenJobExecutionContext(chunkContext, jobExecutionContext);
        StepTestUtil.givenCombinedBook(step.getChunkContext(), initCombinedBookDefinition());
    }

    @NotNull
    private CombinedBookDefinition initCombinedBookDefinition() {
        Set<CombinedBookDefinitionSource> sources = Stream.of(DATA_FILE, DATA_USCL)
                .map(data -> {
                    BookDefinition bookDefinition = new BookDefinition();
                    String titleId = data.getTitleId().getHeadTitleId();
                    bookDefinition.setFullyQualifiedTitleId(titleId);
                    bookDefinition.setProviewDisplayName(data.getProviewName());
                    bookDefinition.setSourceType(BookDefinition.SourceType.valueOf(titleId.split("_")[2]));
                    return bookDefinition;
                })
                .map(bookDefinition -> CombinedBookDefinitionSource.builder().bookDefinition(bookDefinition).sequenceNum(Integer.valueOf(bookDefinition.getFullyQualifiedTitleId().split("_")[1])).build()).collect(Collectors.toSet());
        CombinedBookDefinition combinedBookDefinition = new CombinedBookDefinition();
        combinedBookDefinition.setSources(sources);
        return combinedBookDefinition;
    }

    @Configuration
    @Profile("IntegrationTests")
    @Import(CommonTestContextConfiguration.class)
    public static class Config {

        @Autowired
        private PublishingStatsService publishingStatsService;

        @Bean
        public PrepareSourcesTask prepareSourcesTask() {
            return new PrepareSourcesTask(retrieveServiceLookup(), publishingStatsService, prepareSourcesService());
        }

        @Bean
        public RetrieveServiceLookup retrieveServiceLookup() {
            return new RetrieveServiceLookupImpl(cwbRetrieveService(), nortRetrieveService(), tocRetrieveService());
        }

        @Bean
        @SneakyThrows
        public RetrieveService cwbRetrieveService() {
            return getRetrieveService(10);
        }

        @Bean
        @SneakyThrows
        public RetrieveService nortRetrieveService() {
            return getRetrieveService(20);
        }

        @Bean
        @SneakyThrows
        public RetrieveService tocRetrieveService() {
            return getRetrieveService(30);
        }

        @Bean
        public PrepareSourcesService prepareSourcesService() {
            return new PrepareSourcesServiceImpl();
        }

        @NotNull
        private RetrieveService getRetrieveService(final int count) throws Exception {
            RetrieveService retrieveService = Mockito.mock(RetrieveService.class);
            GatherResponse gatherResponseToc = new GatherResponse(0, null, count, 0, count, PublishingStatus.COMPLETED.toString());
            gatherResponseToc.setNodeCount(count);
            GatherResponse gatherResponseDocs = new GatherResponse(0, null, count, count, count, 0, 0, 0, PublishingStatus.COMPLETED.toString());
            when(retrieveService.retrieveToc(any(), any(), any())).thenReturn(gatherResponseToc);
            when(retrieveService.retrieveDocsAndMetadata(any(), any(), any(), any())).thenReturn(gatherResponseDocs);
            return retrieveService;
        }
    }
}
