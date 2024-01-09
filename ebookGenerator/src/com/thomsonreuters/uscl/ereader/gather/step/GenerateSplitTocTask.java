package com.thomsonreuters.uscl.ereader.gather.step;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.thomsonreuters.uscl.ereader.StatsUpdateTypeEnum;
import com.thomsonreuters.uscl.ereader.common.filesystem.FormatFileSystem;
import com.thomsonreuters.uscl.ereader.common.notification.step.FailureNotificationType;
import com.thomsonreuters.uscl.ereader.common.notification.step.SendFailureNotificationPolicy;
import com.thomsonreuters.uscl.ereader.common.publishingstatus.step.SavePublishingStatusPolicy;
import com.thomsonreuters.uscl.ereader.common.step.BookStepImpl;
import com.thomsonreuters.uscl.ereader.core.book.domain.BookDefinition;
import com.thomsonreuters.uscl.ereader.core.book.domain.SplitDocument;
import com.thomsonreuters.uscl.ereader.format.service.AutoSplitGuidsService;
import com.thomsonreuters.uscl.ereader.format.service.SplitBookTocParseService;
import com.thomsonreuters.uscl.ereader.format.step.DocumentInfo;
import com.thomsonreuters.uscl.ereader.gather.metadata.service.DocMetadataService;
import com.thomsonreuters.uscl.ereader.ioutil.FileHandlingHelper;
import com.thomsonreuters.uscl.ereader.stats.service.PublishingStatsService;
import org.springframework.batch.core.ExitStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
@SendFailureNotificationPolicy(FailureNotificationType.GENERATOR)
@SavePublishingStatusPolicy(StatsUpdateTypeEnum.GENERAL)
public class GenerateSplitTocTask extends BookStepImpl {
    private final PublishingStatsService publishingStatsService;
    private final SplitBookTocParseService splitBookTocParseService;
    private final DocMetadataService docMetadataService;
    private final FileHandlingHelper fileHandlingHelper;
    private final AutoSplitGuidsService autoSplitGuidsService;
    private final FormatFileSystem formatFileSystem;

    @Autowired
    public GenerateSplitTocTask(final PublishingStatsService publishingStatsService,
                                final SplitBookTocParseService splitBookTocParseService,
                                final DocMetadataService docMetadataService,
                                @Qualifier("transformedFileHandlingHelper") final FileHandlingHelper fileHandlingHelper,
                                final AutoSplitGuidsService autoSplitGuidsService,
                                final FormatFileSystem formatFileSystem) {
        this.publishingStatsService = publishingStatsService;
        this.splitBookTocParseService = splitBookTocParseService;
        this.docMetadataService = docMetadataService;
        this.fileHandlingHelper = fileHandlingHelper;
        this.autoSplitGuidsService = autoSplitGuidsService;
        this.formatFileSystem = formatFileSystem;
    }

    @Override
    public ExitStatus executeStep() throws Exception {
        final Long jobInstanceId = getJobInstanceId();
        final BookDefinition bookDefinition = getBookDefinition();
        final File splitTocFilePath = formatFileSystem.getSplitTocFile(this);
        final File tocXmlFile = formatFileSystem.getTransformedToc(this);
        final File transformDir = formatFileSystem.getTransformedDirectory(this);
        final String splitTitleId = bookDefinition.getFullyQualifiedTitleId();

        try (InputStream tocXml = new FileInputStream(tocXmlFile);
            OutputStream splitTocXml = new FileOutputStream(splitTocFilePath)) {
            final List<String> splitTocGuidList;
            final Integer tocNodeCount =
                publishingStatsService.findPublishingStatsByJobId(jobInstanceId).getGatherTocNodeCount();
            if (bookDefinition.isSplitBook() && bookDefinition.isSplitTypeAuto()) {
                try (InputStream tocInputSteam = new FileInputStream(tocXmlFile)) {
                    final boolean metrics = false;
                    splitTocGuidList = autoSplitGuidsService
                        .getAutoSplitNodes(tocInputSteam, bookDefinition, tocNodeCount, jobInstanceId, metrics);
                } catch (final IOException iox) {
                    throw new RuntimeException("Unable to find File : " + tocXmlFile + " " + iox);
                }
            } else {
                splitTocGuidList = getCombinedBookDefinition().getOrderedBookDefinitionList().stream()
                        .map(BookDefinition::getSplitDocuments)
                        .flatMap(Collection::stream)
                        .map(SplitDocument::getTocGuid)
                        .collect(Collectors.toList());
            }
            generateAndUpdateSplitToc(tocXml, splitTocXml, splitTocGuidList, transformDir, jobInstanceId, splitTitleId);
        }
        return ExitStatus.COMPLETED;
    }

    public void generateAndUpdateSplitToc(
        final InputStream tocXml,
        final OutputStream splitTocXml,
        final List<String> splitTocGuidList,
        final File transformDir,
        final Long jobInstanceId,
        final String splitTitleId) throws Exception {
        final  List<File> transformedDocFiles = new ArrayList<>();
        final Map<String, DocumentInfo> documentInfoMap =
            splitBookTocParseService.generateSplitBookToc(tocXml, splitTocXml, splitTocGuidList, splitTitleId);

        if (transformDir == null || !transformDir.isDirectory()) {
            throw new IllegalArgumentException("transformDir must be a directory, not null or a regular file.");
        }

        try {
            fileHandlingHelper.getFileList(transformDir, transformedDocFiles);
        } catch (final FileNotFoundException e) {
            throw new RuntimeException(
                "No transformed files were found in the specified directory " + transformDir.getAbsolutePath(),
                e);
        }

        // Update docmetadata
        for (final File docFile : transformedDocFiles) {
            final String fileName = docFile.getName();
            final String guid = fileName.substring(0, fileName.indexOf("."));
            if (documentInfoMap.containsKey(guid)) {
                final DocumentInfo documentInfo = documentInfoMap.get(guid);
                documentInfo.setDocSize(docFile.length());
                documentInfoMap.put(guid, documentInfo);
            }
        }

        docMetadataService.updateSplitBookFields(jobInstanceId, documentInfoMap);
    }
}
