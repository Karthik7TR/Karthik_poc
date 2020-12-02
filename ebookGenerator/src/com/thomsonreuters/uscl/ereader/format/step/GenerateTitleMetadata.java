package com.thomsonreuters.uscl.ereader.format.step;

import com.thomsonreuters.uscl.ereader.JobExecutionKey;
import com.thomsonreuters.uscl.ereader.JobParameterKey;
import com.thomsonreuters.uscl.ereader.StatsUpdateTypeEnum;
import com.thomsonreuters.uscl.ereader.assemble.service.TitleMetadataService;
import com.thomsonreuters.uscl.ereader.assemble.step.CoverArtUtil;
import com.thomsonreuters.uscl.ereader.common.exception.EBookException;
import com.thomsonreuters.uscl.ereader.common.filesystem.AssembleFileSystem;
import com.thomsonreuters.uscl.ereader.common.filesystem.FormatFileSystem;
import com.thomsonreuters.uscl.ereader.common.filesystem.NasFileSystem;
import com.thomsonreuters.uscl.ereader.common.notification.step.FailureNotificationType;
import com.thomsonreuters.uscl.ereader.common.notification.step.SendFailureNotificationPolicy;
import com.thomsonreuters.uscl.ereader.common.proview.feature.FeaturesListBuilder;
import com.thomsonreuters.uscl.ereader.common.proview.feature.ProviewFeaturesListBuilderFactory;
import com.thomsonreuters.uscl.ereader.common.publishingstatus.step.SavePublishingStatusPolicy;
import com.thomsonreuters.uscl.ereader.common.step.BookStepImpl;
import com.thomsonreuters.uscl.ereader.core.book.domain.BookDefinition;
import com.thomsonreuters.uscl.ereader.core.book.model.Version;
import com.thomsonreuters.uscl.ereader.core.service.DateProvider;
import com.thomsonreuters.uscl.ereader.deliver.service.ProviewHandler;
import com.thomsonreuters.uscl.ereader.deliver.service.ProviewTitleInfo;
import com.thomsonreuters.uscl.ereader.format.exception.EBookFormatException;
import com.thomsonreuters.uscl.ereader.proview.Doc;
import com.thomsonreuters.uscl.ereader.proview.TitleMetadata;
import com.thomsonreuters.uscl.ereader.proview.TitleMetadata.TitleMetadataBuilder;
import com.thomsonreuters.uscl.ereader.stats.domain.PublishingStats;
import com.thomsonreuters.uscl.ereader.stats.service.PublishingStatsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.ExitStatus;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static java.util.Optional.ofNullable;

/**
 * This class is responsible for generating title metadata based on information taken from the Job Parameters, Execution Context, and the file-system.
 *
 * @author <a href="mailto:christopher.schwartz@thomsonreuters.com">Chris Schwartz</a> u0081674
 */
@Slf4j
@SendFailureNotificationPolicy(FailureNotificationType.GENERATOR)
@SavePublishingStatusPolicy(StatsUpdateTypeEnum.GENERAL)
public class GenerateTitleMetadata extends BookStepImpl {
    @Autowired
    private TitleMetadataService titleMetadataService;
    @Autowired
    private NasFileSystem nasFileSystem;
    /**
     * To update publishingStatsService table.
     */
    @Autowired
    private PublishingStatsService publishingStatsService;
    @Autowired
    private ProviewFeaturesListBuilderFactory featuresListBuilderFactory;
    @Autowired
    private FormatFileSystem formatFileSystem;
    @Autowired
    private AssembleFileSystem assembleFileSystem;
    @Autowired
    private DateProvider dateProvider;
    @Autowired
    private CoverArtUtil coverArtUtil;
    @Autowired
    private ProviewHandler proviewHandler;

    @Override
    public ExitStatus executeStep() {
        final BookDefinition bookDefinition = getBookDefinition();
        final String versionNumber = getJobParameters().getString(JobParameterKey.BOOK_VERSION_SUBMITTED);

        final TitleMetadataBuilder titleMetadataBuilder =
            TitleMetadata.builder(bookDefinition)
                .titleIdCaseSensitive(getTitleIdCaseSensitive(bookDefinition))
                .versionNumber(versionNumber)
                .lastUpdated(dateProvider.getDate())
                .inlineToc(getJobExecutionPropertyBoolean(JobExecutionKey.WITH_INLINE_TOC))
                .indexIncluded(getJobExecutionPropertyBoolean(JobExecutionKey.WITH_INLINE_INDEX))
                .isPagesEnabled(getJobExecutionPropertyBoolean(JobExecutionKey.WITH_PAGE_NUMBERS));

        final Long jobInstanceId =
            chunkContext.getStepContext().getStepExecution().getJobExecution().getJobInstance().getId();

        log.debug("Generated title metadata for display name: " + bookDefinition.getProviewDisplayName());

        final File titleXml = assembleFileSystem.getTitleXml(this);

        final File tocXmlFile = formatFileSystem.getTransformedToc(this);
        String status = "Completed";

        try (OutputStream titleManifest = new FileOutputStream(titleXml);
            InputStream tocXml = new FileInputStream(tocXmlFile)) {
            final File documentsDirectory = assembleFileSystem.getDocumentsDirectory(this);
            //TODO: refactor the titleMetadataService to use the method that takes a book definition instead of a titleManifest object.

            final FeaturesListBuilder featureListBuilder =
                featuresListBuilderFactory.create(bookDefinition)
                    .withBookVersion(new Version("v" + versionNumber))
                    .withPageNumbers(getJobExecutionPropertyBoolean(JobExecutionKey.WITH_PAGE_NUMBERS))
                    .withThesaurus(getJobExecutionPropertyBoolean(JobExecutionKey.WITH_THESAURUS))
                    .withMinorVersionMapping(Objects.nonNull(bookDefinition.getVersionWithPreviousDocIds()));
            if (bookDefinition.isSplitBook()) {
                splitBookTitle(titleMetadataBuilder.build());
            } else {
                titleMetadataBuilder
                    .artworkFile(coverArtUtil.getCoverArt(bookDefinition))
                    .assetFilesFromDirectory(assembleFileSystem.getAssetsDirectory(this))
                    .proviewFeatures(featureListBuilder.getFeatures())
                    .build();
                titleMetadataService.generateTitleManifest(
                    titleManifest,
                    tocXml,
                    titleMetadataBuilder.build(),
                    jobInstanceId,
                    documentsDirectory,
                    nasFileSystem.getPilotBookCsvDirectory().getAbsolutePath());
            }
        } catch (final Exception e) {
            status = "Failed";
            throw new EBookException(e);
        } finally {
            final PublishingStats jobstats = new PublishingStats();
            jobstats.setJobInstanceId(getJobInstanceId());
            jobstats.setPublishStatus("generateTitleManifest : " + status);
            publishingStatsService.updatePublishingStats(jobstats, StatsUpdateTypeEnum.GENERAL);
        }

        return ExitStatus.COMPLETED;
    }

    private String getTitleIdCaseSensitive(final BookDefinition bookDefinition) {
        final String titleId = bookDefinition.getFullyQualifiedTitleId();
        if (bookDefinition.isSplitBook()) {
            return titleId;
        } else {
            try {
                if (bookDefinition.getVersionWithPreviousDocIds() != null) {
                    return ofNullable(proviewHandler.getTitleIdCaseSensitiveForVersion(titleId, getBookDefinition().getVersionWithPreviousDocIds()))
                            .orElseThrow(() -> new EBookException("Can not retrieve case sensitive titleId for " + titleId));
                } else {
                    return ofNullable(proviewHandler.getLatestPublishedProviewTitleInfo(titleId))
                            .map(ProviewTitleInfo::getTitleIdCaseSensitive)
                            .orElseThrow(() -> new EBookException("Can not retrieve case sensitive titleId for " + titleId));
                }
            } catch (Exception e) {
                e.printStackTrace();
                return titleId;
            }
        }
    }

    protected void splitBookTitle(final TitleMetadata titleMetadata) throws Exception {
        final File splitTitleXml = formatFileSystem.getSplitTitleXml(this);
        final String splitTocXmlFile = getJobExecutionPropertyString(JobExecutionKey.FORMAT_SPLITTOC_FILE);

        try (OutputStream splitTitleManifest = new FileOutputStream(splitTitleXml);
            InputStream splitTocXml = new FileInputStream(splitTocXmlFile)) {
            final File transformedDocsDir = new File(
                    getJobExecutionPropertyString(JobExecutionKey.FORMAT_DOCUMENTS_READY_DIRECTORY_PATH));
            final File docToSplitBookFile = formatFileSystem.getDocToSplitBook(this);
            final String splitNodeInfoFile = getJobExecutionPropertyString(JobExecutionKey.SPLIT_NODE_INFO_FILE);
            titleMetadataService.generateSplitTitleManifest(
                splitTitleManifest,
                splitTocXml,
                titleMetadata,
                getJobInstanceId(),
                transformedDocsDir,
                docToSplitBookFile,
                splitNodeInfoFile);
        }
    }

    /**
     * Reads in a list of TOC Guids that are associated to each Doc Guid to later be used
     * for anchor insertion and generates a map.
     *
     * @param docGuidsFile file containing the DOC to TOC guid relationships
     * @return documentlist ordered by file
     */
    protected List<Doc> readTOCGuidList(final File docGuidsFile) throws EBookFormatException {
        try (BufferedReader reader = new BufferedReader(new FileReader(docGuidsFile));) {
            log.info("Reading in TOC anchor map file...");
            final List<Doc> docToTocGuidList = reader.lines()
                .map(line -> line.split(",", -1))
                .map(line -> new Doc(line[0], line[0] + ".html", 0, null))
                .collect(Collectors.toCollection(ArrayList::new));
            log.info("Generated Doc List " + docToTocGuidList.size());
            return docToTocGuidList;
        } catch (final IOException e) {
            final String message = "Could not read the DOC guid list file: " + docGuidsFile.getAbsolutePath();
            log.error(message);
            throw new EBookFormatException(message, e);
        }
    }
}
