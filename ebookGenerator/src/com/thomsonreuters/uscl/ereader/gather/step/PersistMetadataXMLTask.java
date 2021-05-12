package com.thomsonreuters.uscl.ereader.gather.step;

import java.io.File;

import com.thomsonreuters.uscl.ereader.JobExecutionKey;
import com.thomsonreuters.uscl.ereader.StatsUpdateTypeEnum;
import com.thomsonreuters.uscl.ereader.core.book.domain.BookDefinition;
import com.thomsonreuters.uscl.ereader.gather.metadata.service.DocMetadataService;
import com.thomsonreuters.uscl.ereader.orchestrate.core.tasklet.AbstractSbTasklet;
import com.thomsonreuters.uscl.ereader.stats.domain.PublishingStats;
import com.thomsonreuters.uscl.ereader.stats.service.PublishingStatsService;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.JobInstance;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.beans.factory.annotation.Required;

/**
 * This step persists the Novus Metadata xml to DB.
 *
 * @author <a href="mailto:Nirupam.Chatterjee@thomsonreuters.com">Nirupam
 *         Chatterjee</a> u0072938
 */
public class PersistMetadataXMLTask extends AbstractSbTasklet {
    // TODO: Use logger API to get Logger instance to job-specific appender.
    private static final Logger LOG = LogManager.getLogger(PersistMetadataXMLTask.class);
    private DocMetadataService docMetadataService;
    private PublishingStatsService publishingStatsService;

    @Override
    public ExitStatus executeStep(final StepContribution contribution, final ChunkContext chunkContext)
        throws Exception {
        final ExecutionContext jobExecutionContext = getJobExecutionContext(chunkContext);
        final JobInstance jobInstance = getJobInstance(chunkContext);

        final BookDefinition bookDefinition =
            (BookDefinition) jobExecutionContext.get(JobExecutionKey.EBOOK_DEFINITION);

        final String titleId = bookDefinition.getTitleId();
        final Long jobInstanceId = jobInstance.getId();

        final File metaDataDirectory =
            new File(getRequiredStringProperty(jobExecutionContext, JobExecutionKey.GATHER_DOCS_METADATA_DIR));

        // TODO: Set value below based on execution context value

        int numDocsMetaDataRun = 0;

        String publishStatus = "Completed";
        try {
            // recursively read the directory for parsing the document metadata
            if (metaDataDirectory.isDirectory()) {
                final File[] allFiles = metaDataDirectory.listFiles();
                for (final File metadataFile : allFiles) {
                    docMetadataService
                        .parseAndStoreDocMetadata(titleId, jobInstanceId, metadataFile);
                    numDocsMetaDataRun++;
                }
            }
        } catch (final Exception e) {
            publishStatus = "Failed " + e.getMessage();
            throw (e);
        } finally {
            final int dupDocCount = docMetadataService.updateProviewFamilyUUIDDedupFields(jobInstanceId);
            updateTitleDocCountStats(jobInstanceId, dupDocCount, publishStatus);
        }
        LOG.debug(
            "Persisted " + numDocsMetaDataRun + " Metadata XML files from " + metaDataDirectory.getAbsolutePath());

        return ExitStatus.COMPLETED;
    }

    private void updateTitleDocCountStats(final Long jobId, final int titleDupDocCount, final String status) {
        final PublishingStats jobstatsFormat = new PublishingStats();
        jobstatsFormat.setJobInstanceId(jobId);
        jobstatsFormat.setTitleDupDocCount(titleDupDocCount);
        jobstatsFormat.setPublishStatus("persistMetadata : " + status);

        LOG.debug("titleDupDocCount =" + titleDupDocCount);

        publishingStatsService.updatePublishingStats(jobstatsFormat, StatsUpdateTypeEnum.TITLEDUPDOCCOUNT);
    }

    @Required
    public void setDocMetadataService(final DocMetadataService docMetadataSvc) {
        docMetadataService = docMetadataSvc;
    }

    @Required
    public void setPublishingStatsService(final PublishingStatsService publishingStatsService) {
        this.publishingStatsService = publishingStatsService;
    }
}
