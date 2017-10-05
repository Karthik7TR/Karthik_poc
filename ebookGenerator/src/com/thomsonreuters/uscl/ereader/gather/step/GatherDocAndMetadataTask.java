package com.thomsonreuters.uscl.ereader.gather.step;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.thomsonreuters.uscl.ereader.JobExecutionKey;
import com.thomsonreuters.uscl.ereader.StatsUpdateTypeEnum;
import com.thomsonreuters.uscl.ereader.core.book.domain.BookDefinition;
import com.thomsonreuters.uscl.ereader.gather.domain.GatherDocRequest;
import com.thomsonreuters.uscl.ereader.gather.domain.GatherResponse;
import com.thomsonreuters.uscl.ereader.gather.exception.GatherException;
import com.thomsonreuters.uscl.ereader.gather.metadata.service.DocMetaDataGuidParserService;
import com.thomsonreuters.uscl.ereader.gather.restclient.service.GatherService;
import com.thomsonreuters.uscl.ereader.orchestrate.core.tasklet.AbstractSbTasklet;
import com.thomsonreuters.uscl.ereader.stats.domain.PublishingStats;
import com.thomsonreuters.uscl.ereader.stats.service.PublishingStatsService;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.beans.factory.annotation.Required;

/**
 * This step persists the Novus Metadata xml to DB.
 *
 * @author <a href="mailto:Nirupam.Chatterjee@thomsonreuters.com">Nirupam Chatterjee</a> u0072938
 */
public class GatherDocAndMetadataTask extends AbstractSbTasklet {
    //TODO: Use logger API to get Logger instance to job-specific appender.
    private static final Logger LOG = LogManager.getLogger(GatherDocAndMetadataTask.class);
    private DocMetaDataGuidParserService docMetaDataParserService;
    private GatherService gatherService;
    private PublishingStatsService publishingStatsService;

    @Override
    public ExitStatus executeStep(final StepContribution contribution, final ChunkContext chunkContext)
        throws Exception {
        final ExecutionContext jobExecutionContext = getJobExecutionContext(chunkContext);
        String publishStatus = "Completed";

        final BookDefinition bookDefinition =
            (BookDefinition) jobExecutionContext.get(JobExecutionKey.EBOOK_DEFINITION);

        final File tocFile = new File(getRequiredStringProperty(jobExecutionContext, JobExecutionKey.GATHER_TOC_FILE));
        final File docsDir = new File(getRequiredStringProperty(jobExecutionContext, JobExecutionKey.GATHER_DOCS_DIR));
        final File docsMetadataDir =
            new File(getRequiredStringProperty(jobExecutionContext, JobExecutionKey.GATHER_DOCS_METADATA_DIR));
        final File docsGuidsFile =
            new File(getRequiredStringProperty(jobExecutionContext, JobExecutionKey.DOCS_DYNAMIC_GUIDS_FILE));
        final String docCollectionName = bookDefinition.getDocCollectionName();
        final Long jobInstance =
            chunkContext.getStepContext().getStepExecution().getJobExecution().getJobInstance().getId();
        final PublishingStats jobstatsDoc = new PublishingStats();
        jobstatsDoc.setJobInstanceId(jobInstance);

        try {
            docMetaDataParserService.generateDocGuidList(tocFile, docsGuidsFile);

            final List<String> docGuids = readDocGuidsFromTextFile(docsGuidsFile);

            final GatherDocRequest gatherDocRequest = new GatherDocRequest(
                docGuids,
                docCollectionName,
                docsDir,
                docsMetadataDir,
                bookDefinition.isFinalStage(),
                bookDefinition.getUseReloadContent());
            final GatherResponse gatherResponse = gatherService.getDoc(gatherDocRequest);
            LOG.debug(gatherResponse);

            jobstatsDoc.setGatherDocRetrievedCount(gatherResponse.getDocCount());
            jobstatsDoc.setGatherDocExpectedCount(gatherResponse.getNodeCount());
            jobstatsDoc.setGatherDocRetryCount(gatherResponse.getRetryCount());
            jobstatsDoc.setGatherMetaRetryCount(gatherResponse.getRetryCount2());
            jobstatsDoc.setGatherMetaRetrievedCount(gatherResponse.getDocCount2());
            jobstatsDoc.setGatherMetaExpectedCount(gatherResponse.getNodeCount());
            jobExecutionContext.putInt(JobExecutionKey.EBOOK_STATS_DOC_COUNT, gatherResponse.getDocCount());

            if (gatherResponse.getErrorCode() != 0) {
                final GatherException gatherException =
                    new GatherException(gatherResponse.getErrorMessage(), gatherResponse.getErrorCode());
                throw gatherException;
            }
        } catch (final Exception e) {
            publishStatus = "Failed";
            throw (e);
        } finally {
            jobstatsDoc.setPublishStatus("getDocAndMetadata : " + publishStatus);
            publishingStatsService.updatePublishingStats(jobstatsDoc, StatsUpdateTypeEnum.GATHERDOC);
        }

        return ExitStatus.COMPLETED;
    }

    /**
     * Reads the contents of a text file and return the guid before the first comma
     * as an element in the returned list.
     * The file is assumed to already exist.
     * @file textFile the text file to process
     * @return a list of text strings, representing each file of the specified file
     */
    public static List<String> readDocGuidsFromTextFile(final File textFile) throws IOException {
        final List<String> lineList = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(textFile))) {
            String textLine;
            while ((textLine = reader.readLine()) != null) {
                if (StringUtils.isNotBlank(textLine)) {
                    final int i = textLine.indexOf(",");
                    if (i != -1) {
                        textLine = textLine.substring(0, textLine.indexOf(","));
                        lineList.add(textLine.trim());
                    }
                }
            }
        }
        return lineList;
    }

    @Required
    public void setDocMetadataGuidParserService(final DocMetaDataGuidParserService docMetadataSvc) {
        docMetaDataParserService = docMetadataSvc;
    }

    @Required
    public void setGatherService(final GatherService service) {
        gatherService = service;
    }

    @Required
    public void setPublishingStatsService(final PublishingStatsService publishingStatsService) {
        this.publishingStatsService = publishingStatsService;
    }
}
