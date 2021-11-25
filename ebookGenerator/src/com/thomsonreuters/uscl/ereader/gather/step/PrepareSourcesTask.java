package com.thomsonreuters.uscl.ereader.gather.step;

import com.thomsonreuters.uscl.ereader.JobExecutionKey;
import com.thomsonreuters.uscl.ereader.StatsUpdateTypeEnum;
import com.thomsonreuters.uscl.ereader.common.exception.EBookException;
import com.thomsonreuters.uscl.ereader.common.notification.step.FailureNotificationType;
import com.thomsonreuters.uscl.ereader.common.notification.step.SendFailureNotificationPolicy;
import com.thomsonreuters.uscl.ereader.common.publishingstatus.step.SavePublishingStatusPolicy;
import com.thomsonreuters.uscl.ereader.common.step.BookStepImpl;
import com.thomsonreuters.uscl.ereader.core.book.domain.BookDefinition;
import com.thomsonreuters.uscl.ereader.core.book.domain.CombinedBookDefinition;
import com.thomsonreuters.uscl.ereader.core.book.util.FileUtils;
import com.thomsonreuters.uscl.ereader.gather.domain.GatherResponse;
import com.thomsonreuters.uscl.ereader.gather.service.TocValidationService;
import com.thomsonreuters.uscl.ereader.gather.step.service.container.SourceContainer;
import com.thomsonreuters.uscl.ereader.gather.step.service.container.DocsGuidsContainer;
import com.thomsonreuters.uscl.ereader.gather.step.service.container.TocContainer;
import com.thomsonreuters.uscl.ereader.gather.step.service.PrepareSourcesService;
import com.thomsonreuters.uscl.ereader.gather.step.service.RetrieveService;
import com.thomsonreuters.uscl.ereader.gather.step.service.RetrieveServiceLookup;
import com.thomsonreuters.uscl.ereader.stats.PublishingStatus;
import com.thomsonreuters.uscl.ereader.stats.domain.PublishingStats;
import com.thomsonreuters.uscl.ereader.stats.service.PublishingStatsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.List;

import static com.thomsonreuters.uscl.ereader.stats.PublishingStatus.COMPLETED;
import static com.thomsonreuters.uscl.ereader.stats.PublishingStatus.FAILED;

@Component
@Slf4j
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@SendFailureNotificationPolicy(FailureNotificationType.GENERATOR)
@SavePublishingStatusPolicy(StatsUpdateTypeEnum.PREPARE_SOURCES)
public class PrepareSourcesTask extends BookStepImpl {
    private final RetrieveServiceLookup retrieveServiceLookup;
    private final PublishingStatsService publishingStatsService;
    private final PrepareSourcesService prepareSourcesService;
    private final TocValidationService validator;

    @Override
    public ExitStatus executeStep() throws Exception {
        PublishingStatus publishStatus = COMPLETED;
        final ExecutionContext jobExecutionContext = getJobExecutionContext();
        final File tocFile = new File(getJobExecutionPropertyString(JobExecutionKey.GATHER_TOC_FILE));
        final File docsGuidsFile = new File(getJobExecutionPropertyString(JobExecutionKey.DOCS_DYNAMIC_GUIDS_FILE));
        final PublishingStats publishingStatsSum = PublishingStats.initStats(getJobInstanceId());
        final CombinedBookDefinition combinedBookDefinition = (CombinedBookDefinition) jobExecutionContext.get(JobExecutionKey.COMBINED_BOOK_DEFINITION);
        final SourceContainer tocContainer = new TocContainer();
        final SourceContainer docsGuidContainer = new DocsGuidsContainer();
        final List<BookDefinition> orderedBookDefinitions = combinedBookDefinition.getOrderedBookDefinitionList();
        try {
            for (BookDefinition book : orderedBookDefinitions) {
                final RetrieveService retrieveService = retrieveServiceLookup.getRetrieveService(book.getSourceType());
                final File sourceTocFile = prepareSourcesService.getTocFile(tocFile, book.getFullyQualifiedTitleId());
                final GatherResponse tocGatherResponse = retrieveService.retrieveToc(book, sourceTocFile, chunkContext);
                validateToc(sourceTocFile, book);
                publishingStatsService.addTocStats(publishingStatsSum, tocGatherResponse);
                tocContainer.addSource(sourceTocFile, book);
                final File sourceDocsGuidsFile = prepareSourcesService.getDocsGuidsFile(docsGuidsFile, book.getFullyQualifiedTitleId());
                final GatherResponse docAndMetadataResponse = retrieveService.retrieveDocsAndMetadata(book, sourceTocFile, sourceDocsGuidsFile, chunkContext);
                publishingStatsService.addDocsAndMetadataStats(publishingStatsSum, docAndMetadataResponse);
                docsGuidContainer.addSource(sourceDocsGuidsFile, book);
            }
        } catch (Exception e) {
            publishStatus = FAILED;
            log.error(e.getMessage());
            throw e;
        } finally {
            publishingStatsSum.setPublishStatus("prepareSourcesTask (toc, docs and metadata) : " + publishStatus);
            publishingStatsService.updatePublishingStats(publishingStatsSum, StatsUpdateTypeEnum.PREPARE_SOURCES);
        }
        jobExecutionContext.putInt(JobExecutionKey.EBOOK_STATS_DOC_COUNT, publishingStatsSum.getGatherDocRetrievedCount());
        saveToFile(tocFile, tocContainer);
        saveToFile(docsGuidsFile, docsGuidContainer);
        return ExitStatus.COMPLETED;
    }

    private void validateToc(final File sourceTocFile, final BookDefinition book) {
        try {
            validator.validateToc(sourceTocFile);
        } catch (EBookException e) {
            throw new EBookException(String.format(
                    "Exception while validating Toc of titleId = %s; %s", book.getFullyQualifiedTitleId(), e.getMessage()));
        }
    }

    private void saveToFile(final File file, final SourceContainer builder) {
        FileUtils.writeLines(file, builder.getSources());
    }
}
