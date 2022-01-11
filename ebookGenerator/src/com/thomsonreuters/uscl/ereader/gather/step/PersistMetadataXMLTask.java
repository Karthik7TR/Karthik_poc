package com.thomsonreuters.uscl.ereader.gather.step;

import java.io.File;

import com.thomsonreuters.uscl.ereader.JobExecutionKey;
import com.thomsonreuters.uscl.ereader.StatsUpdateTypeEnum;
import com.thomsonreuters.uscl.ereader.common.notification.step.FailureNotificationType;
import com.thomsonreuters.uscl.ereader.common.notification.step.SendFailureNotificationPolicy;
import com.thomsonreuters.uscl.ereader.common.publishingstatus.step.SavePublishingStatusPolicy;
import com.thomsonreuters.uscl.ereader.common.step.BookStepImpl;
import com.thomsonreuters.uscl.ereader.core.book.domain.BookDefinition;
import com.thomsonreuters.uscl.ereader.gather.metadata.domain.DocMetadata;
import com.thomsonreuters.uscl.ereader.gather.metadata.service.DocMetadataService;
import com.thomsonreuters.uscl.ereader.stats.domain.PublishingStats;
import com.thomsonreuters.uscl.ereader.stats.service.PublishingStatsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * This step persists the Novus Metadata xml to DB.
 *
 * @author <a href="mailto:Nirupam.Chatterjee@thomsonreuters.com">Nirupam
 *         Chatterjee</a> u0072938
 */
@Slf4j
@SendFailureNotificationPolicy(FailureNotificationType.GENERATOR)
@SavePublishingStatusPolicy(StatsUpdateTypeEnum.GENERAL)
public class PersistMetadataXMLTask extends BookStepImpl {
    @Autowired
    private DocMetadataService docMetadataService;

    @Autowired
    private PublishingStatsService publishingStatsService;

    @Override
    public ExitStatus executeStep()
        throws Exception {
        final ExecutionContext jobExecutionContext = getJobExecutionContext();

        final BookDefinition bookDefinition =
            (BookDefinition) jobExecutionContext.get(JobExecutionKey.EBOOK_DEFINITION);

        final String titleId = bookDefinition.getTitleId();
        final Long jobInstanceId = getJobInstanceId();

        final File metaDataDirectory =
            new File(getJobExecutionPropertyString(JobExecutionKey.GATHER_DOCS_METADATA_DIR));

        // TODO: Set value below based on execution context value

        int numDocsMetaDataRun = 0;

        String publishStatus = "Completed";
        try {
            // recursively read the directory for parsing the document metadata
            if (metaDataDirectory.isDirectory()) {
                final File[] allFiles = metaDataDirectory.listFiles();
                for (final File metadataFile : allFiles) {
                    DocMetadata docMetadata = docMetadataService
                        .parseAndStoreDocMetadata(titleId, jobInstanceId, metadataFile);
                    processCanadianDigestIssues(docMetadata);
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
        log.debug("Persisted {} Metadata XML files from {}", numDocsMetaDataRun, metaDataDirectory.getAbsolutePath());

        return ExitStatus.COMPLETED;
    }

    private void processCanadianDigestIssues(final DocMetadata docMetadata) {
        if (docMetadata.isCanadianDigestMissing()) {
            getJobExecutionPropertyCanadianDigestMissing().add(docMetadata.getDocUuid());
        }

        if (docMetadata.isCanadianTopicCodeMissing()) {
            getJobExecutionPropertyCanadianTopicCodeMissing().add(docMetadata.getDocUuid());
        }
    }

    private void updateTitleDocCountStats(final Long jobId, final int titleDupDocCount, final String status) {
        final PublishingStats jobstatsFormat = new PublishingStats();
        jobstatsFormat.setJobInstanceId(jobId);
        jobstatsFormat.setTitleDupDocCount(titleDupDocCount);
        jobstatsFormat.setPublishStatus("persistMetadata : " + status);

        log.debug("titleDupDocCount = {}", titleDupDocCount);

        publishingStatsService.updatePublishingStats(jobstatsFormat, StatsUpdateTypeEnum.TITLEDUPDOCCOUNT);
    }
}
