package com.thomsonreuters.uscl.ereader.gather.step;

import com.thomsonreuters.uscl.ereader.JobExecutionKey;
import com.thomsonreuters.uscl.ereader.common.filesystem.FormatFileSystem;
import com.thomsonreuters.uscl.ereader.context.CommonTestContextConfiguration;
import com.thomsonreuters.uscl.ereader.core.book.domain.BookDefinition;
import com.thomsonreuters.uscl.ereader.core.book.domain.CombinedBookDefinition;
import com.thomsonreuters.uscl.ereader.core.book.domain.CombinedBookDefinitionSource;
import com.thomsonreuters.uscl.ereader.core.book.domain.SplitDocument;
import com.thomsonreuters.uscl.ereader.format.service.AutoSplitGuidsService;
import com.thomsonreuters.uscl.ereader.format.service.SplitBookTocParseService;
import com.thomsonreuters.uscl.ereader.format.service.SplitBookTocParseServiceImpl;
import com.thomsonreuters.uscl.ereader.format.step.StepIntegrationTestRunner;
import com.thomsonreuters.uscl.ereader.gather.metadata.service.DocMetadataService;
import com.thomsonreuters.uscl.ereader.ioutil.FileExtensionFilter;
import com.thomsonreuters.uscl.ereader.ioutil.FileHandlingHelper;
import com.thomsonreuters.uscl.ereader.stats.domain.PublishingStats;
import com.thomsonreuters.uscl.ereader.stats.service.PublishingStatsService;
import lombok.SneakyThrows;
import org.jetbrains.annotations.NotNull;
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

import java.util.Arrays;
import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.thomsonreuters.uscl.ereader.util.ValueConverter.getStringForBooleanValue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {GenerateSplitTocTaskTest.Config.class, StepIntegrationTestRunner.Config.class})
@ActiveProfiles("IntegrationTests")
public final class GenerateSplitTocTaskTest {
    private static final String TRANSFORMED = ".transformed";
    private static final String RESOURCE_DIR_NAME = "resourceGenerateSplitToc";
    @Autowired
    private GenerateSplitTocTask generateSplitTocTask;
    @Autowired
    private StepIntegrationTestRunner runner;
    @Autowired
    private AutoSplitGuidsService autoSplitGuidsService;

    @Before
    public void setUp() throws Exception {
        runner.setUp(generateSplitTocTask, RESOURCE_DIR_NAME);
    }

    @Test
    @SneakyThrows
    public void shouldGenerateSplitTocForAutoSplitCombinedBook() {
        BookDefinition bookDefinition = generateSplitTocTask.getBookDefinition();
        bookDefinition.setIsSplitBook(true);
        bookDefinition.setIsSplitTypeAuto(true);
        when(autoSplitGuidsService.getAutoSplitNodes(any(), anyObject(), anyInt(), anyLong(), anyBoolean()))
                .thenReturn(Arrays.asList("I94cba210e1e311da9c26e67ca3b71335", "NC12805D0D08E11DA9A6ADEDFA9D0D553"));
        runner.test(generateSplitTocTask, "generateSplitTocAutoSplitCombinedBook");
    }

    @Test
    @SneakyThrows
    public void shouldGenerateSplitTocForManualSplitCombinedBook() {
        BookDefinition bookDefinition1 = generateSplitTocTask.getBookDefinition();
        bookDefinition1.setIsSplitBook(true);
        bookDefinition1.setIsSplitTypeAuto(false);
        bookDefinition1.setSplitDocuments(Collections.singletonList(splitDocument(bookDefinition1, "Icbcfa40095cb11e4a7a2f300942ac02c")
        ));
        BookDefinition bookDefinition2 = new BookDefinition();
        bookDefinition2.setSplitDocuments(Collections.singletonList(splitDocument(bookDefinition2, "NC116C7C0D08E11DA9A6ADEDFA9D0D553")));
        CombinedBookDefinition combinedBookDefinition = new CombinedBookDefinition();
        combinedBookDefinition.setSources(initSources(bookDefinition1, bookDefinition2, combinedBookDefinition));
        when(generateSplitTocTask.getJobExecutionContext().get(JobExecutionKey.COMBINED_BOOK_DEFINITION)).thenReturn(combinedBookDefinition);
        runner.test(generateSplitTocTask, "generateSplitTocManualSplitCombinedBook");
    }

    @Test
    @SneakyThrows
    public void shouldGenerateSplitTocForAutoSplitSingleBook() {
        BookDefinition bookDefinition = generateSplitTocTask.getBookDefinition();
        bookDefinition.setIsSplitBook(true);
        bookDefinition.setIsSplitTypeAuto(true);
        when(autoSplitGuidsService.getAutoSplitNodes(any(), anyObject(), anyInt(), anyLong(), anyBoolean()))
                .thenReturn(Collections.singletonList("I94cba210e1e311da9c26e67ca3b71335"));
        runner.test(generateSplitTocTask, "generateSplitTocAutoSplitSingleBook");
    }

    @Test
    @SneakyThrows
    public void shouldGenerateSplitTocForManualSplitSingleBook() {
        BookDefinition bookDefinition = generateSplitTocTask.getBookDefinition();
        bookDefinition.setIsSplitBook(true);
        bookDefinition.setIsSplitTypeAuto(false);
        bookDefinition.setSplitDocuments(Collections.singletonList(splitDocument(bookDefinition, "Icbcfa40095cb11e4a7a2f300942ac02c")));
        runner.test(generateSplitTocTask, "generateSplitTocManualSplitSingleBook");
    }

    @NotNull
    private SplitDocument splitDocument(final BookDefinition bookDefinition, final String guid) {
        SplitDocument splitDocument = new SplitDocument();
        splitDocument.setBookDefinition(bookDefinition);
        splitDocument.setTocGuid(guid);
        return splitDocument;
    }

    @NotNull
    private Set<CombinedBookDefinitionSource> initSources(final BookDefinition bookDefinition1, final BookDefinition bookDefinition2, final CombinedBookDefinition combinedBookDefinition) {
        return Stream.of(CombinedBookDefinitionSource.builder()
                .bookDefinition(bookDefinition1)
                .combinedBookDefinition(combinedBookDefinition)
                .sequenceNum(1)
                .isPrimarySource(getStringForBooleanValue(true)).build(), CombinedBookDefinitionSource.builder()
                .bookDefinition(bookDefinition2)
                .combinedBookDefinition(combinedBookDefinition)
                .sequenceNum(2)
                .isPrimarySource(getStringForBooleanValue(false)).build()).collect(Collectors.toSet());
    }

    @Configuration
    @Profile("IntegrationTests")
    @Import(CommonTestContextConfiguration.class)
    public static class Config {
        @Autowired
        private PublishingStatsService publishingStatsService;
        @Autowired
        private DocMetadataService docMetadataService;
        @Autowired
        private FormatFileSystem formatFileSystem;

        @Bean
        public GenerateSplitTocTask generateSplitTocTask() {
            PublishingStats publishingStats = new PublishingStats();
            publishingStats.setGatherTocNodeCount(5);
            when(publishingStatsService.findPublishingStatsByJobId(anyLong())).thenReturn(publishingStats);
            return new GenerateSplitTocTask(publishingStatsService,
                    splitBookTocParseService(),
                    docMetadataService,
                    fileHandlingHelper(),
                    autoSplitGuidsService(),
                    formatFileSystem
            );
        }

        @Bean
        public SplitBookTocParseService splitBookTocParseService() {
            return new SplitBookTocParseServiceImpl();
        }

        @Bean
        public FileHandlingHelper fileHandlingHelper() {
            final FileHandlingHelper fileHandlingHelper = new FileHandlingHelper();
            final FileExtensionFilter fileExtFilter = new FileExtensionFilter();
            fileExtFilter.setAcceptedFileExtensions(new String[]{TRANSFORMED});
            fileHandlingHelper.setFilter(fileExtFilter);
            return fileHandlingHelper;
        }

        @Bean
        public AutoSplitGuidsService autoSplitGuidsService() {
            return mock(AutoSplitGuidsService.class);
        }
    }
}
