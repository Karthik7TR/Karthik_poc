package com.thomsonreuters.uscl.ereader.format.step;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import com.thomsonreuters.uscl.ereader.JobExecutionKey;
import com.thomsonreuters.uscl.ereader.StatsUpdateTypeEnum;
import com.thomsonreuters.uscl.ereader.common.filesystem.FormatFileSystem;
import com.thomsonreuters.uscl.ereader.common.filesystem.GatherFileSystem;
import com.thomsonreuters.uscl.ereader.common.notification.step.FailureNotificationType;
import com.thomsonreuters.uscl.ereader.common.notification.step.SendFailureNotificationPolicy;
import com.thomsonreuters.uscl.ereader.common.publishingstatus.step.SavePublishingStatusPolicy;
import com.thomsonreuters.uscl.ereader.common.step.BookStepImpl;
import com.thomsonreuters.uscl.ereader.core.book.domain.BookDefinition;
import com.thomsonreuters.uscl.ereader.core.service.JsoupService;
import com.thomsonreuters.uscl.ereader.format.service.InlineIndexService;
import com.thomsonreuters.uscl.ereader.gather.domain.GatherDocRequest;
import com.thomsonreuters.uscl.ereader.gather.restclient.service.GatherService;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.batch.core.ExitStatus;
import org.springframework.beans.factory.annotation.Autowired;

import static java.util.Optional.of;

@SendFailureNotificationPolicy(FailureNotificationType.GENERATOR)
@SavePublishingStatusPolicy(StatsUpdateTypeEnum.GENERAL)
public class InlineIndexStep extends BookStepImpl {
    private static final String INLINE_INDEX = "inlineIndex";
    private static final String INLINE_INDEX_ANCHOR = "inlineIndexAnchor";
    private static final String DOCS_DYNAMIC_GUIDS_LINE = "%s,%s|";
    private static final String XML = ".xml";

    @Autowired
    private GatherFileSystem gatherFileSystem;

    @Autowired
    private FormatFileSystem formatFileSystem;

    @Autowired
    private GatherService gatherService;

    @Autowired
    private InlineIndexService inlineIndexService;

    @Autowired
    private JsoupService jsoup;

    @Override
    public ExitStatus executeStep() throws Exception {
        if (getBookDefinition().isIndexIncluded()) {
            final boolean pages = getJobExecutionPropertyBoolean(JobExecutionKey.WITH_PAGE_NUMBERS);
            final File indexDocDir = gatherFileSystem.getGatherIndexDocsDir(this);
            final File outputDir = formatFileSystem.getTransformedDirectory(this);

            List<String> indexDocGuids = gatherIndexPages(getBookDefinition(), indexDocDir);

            if (CollectionUtils.isNotEmpty(indexDocGuids)) {
                inlineIndexService.generateInlineIndex(indexDocGuids, indexDocDir, outputDir, pages);
                updateContext();
            }
        }
        return ExitStatus.COMPLETED;
    }

    private List<String> gatherIndexPages(final BookDefinition bookDefinition, final File indexDocsDir) {
        File indexTocDir = gatherFileSystem.getGatherIndexTocDir(this);
        gatherIndexToc(bookDefinition, indexTocDir);
        return gatherIndexDocs(bookDefinition, indexTocDir, indexDocsDir);
    }

    private void gatherIndexToc(final BookDefinition bookDefinition, final File indexTocDir) {
        gatherService.getDoc(new GatherDocRequest(
                Collections.singletonList(bookDefinition.getIndexTocRootGuid()),
                bookDefinition.getIndexTocCollectionName(),
                indexTocDir,
                gatherFileSystem.getGatherMetadataDir(indexTocDir),
                bookDefinition.isFinalStage(),
                bookDefinition.getUseReloadContent()
        ));
    }

    private List<String> gatherIndexDocs(final BookDefinition bookDefinition, final File indexTocDir, final File indexDocDir) {
        List<String> indexDocGuids = of(new File(indexTocDir, bookDefinition.getIndexTocRootGuid() + XML))
                .filter(File::exists)
                .map(this::extractIndexDocGuids)
                .orElse(Collections.emptyList());

        gatherService.getDoc(new GatherDocRequest(
                indexDocGuids,
                bookDefinition.getIndexTocCollectionName(),
                indexDocDir,
                gatherFileSystem.getGatherMetadataDir(indexDocDir),
                bookDefinition.isFinalStage(),
                bookDefinition.getUseReloadContent()
        ));
        return indexDocGuids;
    }

    private List<String> extractIndexDocGuids(final File indexToc) {
        Document toc = jsoup.loadDocument(indexToc);
        Elements entries = toc.getElementsByTag("index.entry");
        return entries.stream()
                .map(entry -> entry.getElementsByTag("cite.query"))
                .filter(Objects::nonNull)
                .map(citeQuery -> citeQuery.attr("w-normalized-cite"))
                .filter(StringUtils::isNotBlank)
                .collect(Collectors.toList())
        ;
    }

    private void updateContext() throws IOException {
        updateStatsDocCount();
        appendToGuildsFile();
        setJobExecutionProperty(JobExecutionKey.WITH_INLINE_INDEX, Boolean.TRUE);
    }

    private void updateStatsDocCount() {
        final int numDocsInTOC = getJobExecutionPropertyInt(JobExecutionKey.EBOOK_STATS_DOC_COUNT);
        setJobExecutionProperty(JobExecutionKey.EBOOK_STATS_DOC_COUNT, numDocsInTOC + 1);
    }

    private void appendToGuildsFile() throws IOException {
        Files.write(Paths.get(getJobExecutionPropertyString(JobExecutionKey.DOCS_DYNAMIC_GUIDS_FILE)),
            String.format(DOCS_DYNAMIC_GUIDS_LINE, INLINE_INDEX, INLINE_INDEX_ANCHOR).getBytes(), StandardOpenOption.APPEND);
    }
}
