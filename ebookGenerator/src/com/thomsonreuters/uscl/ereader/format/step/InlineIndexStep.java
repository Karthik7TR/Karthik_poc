package com.thomsonreuters.uscl.ereader.format.step;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

import com.thomsonreuters.uscl.ereader.JobExecutionKey;
import com.thomsonreuters.uscl.ereader.StatsUpdateTypeEnum;
import com.thomsonreuters.uscl.ereader.common.filesystem.FormatFileSystem;
import com.thomsonreuters.uscl.ereader.common.filesystem.GatherFileSystem;
import com.thomsonreuters.uscl.ereader.common.notification.step.FailureNotificationType;
import com.thomsonreuters.uscl.ereader.common.notification.step.SendFailureNotificationPolicy;
import com.thomsonreuters.uscl.ereader.common.publishingstatus.step.SavePublishingStatusPolicy;
import com.thomsonreuters.uscl.ereader.common.step.BookStepImpl;
import com.thomsonreuters.uscl.ereader.core.book.domain.BookDefinition;
import com.thomsonreuters.uscl.ereader.format.service.InlineIndexService;
import com.thomsonreuters.uscl.ereader.gather.domain.GatherTocRequest;
import com.thomsonreuters.uscl.ereader.gather.restclient.service.GatherService;
import org.springframework.batch.core.ExitStatus;
import org.springframework.beans.factory.annotation.Autowired;

@SendFailureNotificationPolicy(FailureNotificationType.GENERATOR)
@SavePublishingStatusPolicy(StatsUpdateTypeEnum.GENERAL)
public class InlineIndexStep extends BookStepImpl {
    private static final String INLINE_INDEX = "inlineIndex";
    private static final String INLINE_INDEX_ANCHOR = "inlineIndexAnchor";

    @Autowired
    private GatherFileSystem gatherFileSystem;

    @Autowired
    private FormatFileSystem formatFileSystem;

    @Autowired
    private GatherService gatherService;

    @Autowired
    private InlineIndexService inlineIndexService;

    @Override
    public ExitStatus executeStep() throws Exception {
        if (getBookDefinition().isIndexIncluded()) {
            final boolean pages = getJobExecutionPropertyBoolean(JobExecutionKey.WITH_PAGE_NUMBERS);
            final File indexXml = gatherFileSystem.getGatherIndexFile(this);
            final File outputDir = formatFileSystem.getTransformedDirectory(this);

            gatherIndexPages(getBookDefinition(), indexXml);
            inlineIndexService.generateInlineIndex(indexXml, outputDir, pages);

            updateStatsDocCount();
            appendToGuildsFile();
        }
        return ExitStatus.COMPLETED;
    }

    private void gatherIndexPages(final BookDefinition bookDefinition, final File indexGatheredFile) {
        gatherService.getToc(new GatherTocRequest(
            bookDefinition.getIndexTocRootGuid(),
            bookDefinition.getIndexTocCollectionName(),
            indexGatheredFile,
            null,
            null,
            bookDefinition.isFinalStage(),
            null,
            bookDefinition.getDocumentTypeCodes().getThresholdValue()));
    }

    private void appendToGuildsFile() throws IOException {
        Files.write(Paths.get(getJobExecutionPropertyString(JobExecutionKey.DOCS_DYNAMIC_GUIDS_FILE)),
            String.format("%s,%s|", INLINE_INDEX, INLINE_INDEX_ANCHOR).getBytes(), StandardOpenOption.APPEND);
    }

    private void updateStatsDocCount() {
        final int numDocsInTOC = getJobExecutionPropertyInt(JobExecutionKey.EBOOK_STATS_DOC_COUNT);
        setJobExecutionProperty(JobExecutionKey.EBOOK_STATS_DOC_COUNT, numDocsInTOC + 1);
    }
}
