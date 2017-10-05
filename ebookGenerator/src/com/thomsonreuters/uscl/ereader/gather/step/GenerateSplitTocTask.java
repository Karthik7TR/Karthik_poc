package com.thomsonreuters.uscl.ereader.gather.step;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.thomsonreuters.uscl.ereader.JobExecutionKey;
import com.thomsonreuters.uscl.ereader.StatsUpdateTypeEnum;
import com.thomsonreuters.uscl.ereader.core.book.domain.BookDefinition;
import com.thomsonreuters.uscl.ereader.core.book.domain.SplitDocument;
import com.thomsonreuters.uscl.ereader.format.service.AutoSplitGuidsService;
import com.thomsonreuters.uscl.ereader.format.service.SplitBookTocParseService;
import com.thomsonreuters.uscl.ereader.format.step.DocumentInfo;
import com.thomsonreuters.uscl.ereader.gather.metadata.service.DocMetadataService;
import com.thomsonreuters.uscl.ereader.ioutil.FileHandlingHelper;
import com.thomsonreuters.uscl.ereader.orchestrate.core.tasklet.AbstractSbTasklet;
import com.thomsonreuters.uscl.ereader.stats.domain.PublishingStats;
import com.thomsonreuters.uscl.ereader.stats.service.PublishingStatsService;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.JobInstance;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.beans.factory.annotation.Required;

public class GenerateSplitTocTask extends AbstractSbTasklet {
    // TODO: Use logger API to get Logger instance to job-specific appender.
    //private static final Logger LOG = LogManager.getLogger(GenerateSplitTocTask.class);
    private PublishingStatsService publishingStatsService;

    private SplitBookTocParseService splitBookTocParseService;

    private DocMetadataService docMetadataService;

    private FileHandlingHelper fileHandlingHelper;

    private Map<String, DocumentInfo> documentInfoMap = new HashMap<>();

    private AutoSplitGuidsService autoSplitGuidsService;

    // retrieve list of all transformed files
    private List<File> transformedDocFiles = new ArrayList<>();

    @Override
    public ExitStatus executeStep(final StepContribution contribution, final ChunkContext chunkContext)
        throws Exception {
        final PublishingStats jobstats = new PublishingStats();
        String publishStatus = "Completed";

        final JobInstance jobInstance = getJobInstance(chunkContext);
        final Long jobInstanceId = jobInstance.getId();

        final ExecutionContext jobExecutionContext = getJobExecutionContext(chunkContext);
        final BookDefinition bookDefinition =
            (BookDefinition) jobExecutionContext.get(JobExecutionKey.EBOOK_DEFINITION);
        final String splitTocFilePath = jobExecutionContext.getString(JobExecutionKey.FORMAT_SPLITTOC_FILE);
        final String tocXmlFile = getRequiredStringProperty(jobExecutionContext, JobExecutionKey.GATHER_TOC_FILE);
        final String transformDirectory =
            getRequiredStringProperty(jobExecutionContext, JobExecutionKey.FORMAT_TRANSFORMED_DIR);
        final File transformDir = new File(transformDirectory);
        final String splitTitleId = bookDefinition.getFullyQualifiedTitleId();

        try (InputStream tocXml = new FileInputStream(tocXmlFile);
            OutputStream splitTocXml = new FileOutputStream(splitTocFilePath);) {
            List<String> splitTocGuidList = new ArrayList<>();

            final List<SplitDocument> splitDocuments = bookDefinition.getSplitDocumentsAsList();
            for (final SplitDocument splitDocument : splitDocuments) {
                splitTocGuidList.add(splitDocument.getTocGuid());
            }

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
            }

            generateAndUpdateSplitToc(tocXml, splitTocXml, splitTocGuidList, transformDir, jobInstanceId, splitTitleId);
        } catch (final Exception e) {
            publishStatus = "Failed";
            throw (e);
        } finally {
            jobstats.setJobInstanceId(jobInstanceId);
            jobstats.setPublishStatus("generateSplitToc : " + publishStatus);
            publishingStatsService.updatePublishingStats(jobstats, StatsUpdateTypeEnum.GENERAL);
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
        documentInfoMap =
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
                documentInfo.setDocSize(Long.valueOf(docFile.length()));
                documentInfoMap.put(guid, documentInfo);
            }
        }

        docMetadataService.updateSplitBookFields(jobInstanceId, documentInfoMap);
    }

    public void setfileHandlingHelper(final FileHandlingHelper fileHandlingHelper) {
        this.fileHandlingHelper = fileHandlingHelper;
    }

    public PublishingStatsService getPublishingStatsService() {
        return publishingStatsService;
    }

    public void setPublishingStatsService(final PublishingStatsService publishingStatsService) {
        this.publishingStatsService = publishingStatsService;
    }

    public SplitBookTocParseService getSplitBookTocParseService() {
        return splitBookTocParseService;
    }

    public void setSplitBookTocParseService(final SplitBookTocParseService splitBookTocParseService) {
        this.splitBookTocParseService = splitBookTocParseService;
    }

    public Map<String, DocumentInfo> getDocumentInfoMap() {
        return documentInfoMap;
    }

    public void setDocumentInfoMap(final Map<String, DocumentInfo> documentInfoMap) {
        this.documentInfoMap = documentInfoMap;
    }

    public DocMetadataService getDocMetadataService() {
        return docMetadataService;
    }

    public void setDocMetadataService(final DocMetadataService docMetadataService) {
        this.docMetadataService = docMetadataService;
    }

    public AutoSplitGuidsService getAutoSplitGuidsService() {
        return autoSplitGuidsService;
    }

    @Required
    public void setAutoSplitGuidsService(final AutoSplitGuidsService autoSplitGuidsService) {
        this.autoSplitGuidsService = autoSplitGuidsService;
    }
}
