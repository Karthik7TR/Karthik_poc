package com.thomsonreuters.uscl.ereader.format.step;

import java.io.File;

import javax.annotation.Resource;

import com.thomsonreuters.uscl.ereader.JobExecutionKey;
import com.thomsonreuters.uscl.ereader.StatsUpdateTypeEnum;
import com.thomsonreuters.uscl.ereader.common.filesystem.FormatFileSystem;
import com.thomsonreuters.uscl.ereader.common.filesystem.GatherFileSystem;
import com.thomsonreuters.uscl.ereader.common.notification.step.FailureNotificationType;
import com.thomsonreuters.uscl.ereader.common.notification.step.SendFailureNotificationPolicy;
import com.thomsonreuters.uscl.ereader.common.publishingstatus.step.SavePublishingStatusPolicy;
import com.thomsonreuters.uscl.ereader.common.step.BookStepImpl;
import com.thomsonreuters.uscl.ereader.core.book.domain.BookDefinition;
import com.thomsonreuters.uscl.ereader.format.exception.EBookFormatException;
import com.thomsonreuters.uscl.ereader.format.service.TransformerService;
import com.thomsonreuters.uscl.ereader.stats.domain.PublishingStats;
import com.thomsonreuters.uscl.ereader.stats.service.PublishingStatsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.ExitStatus;

/**
 * This step transforms the Novus extracted XML documents into HTML.
 *
 * @author <a href="mailto:Selvedin.Alic@thomsonreuters.com">Selvedin Alic</a> u0095869
 */
@Slf4j
@SendFailureNotificationPolicy(FailureNotificationType.GENERATOR)
@SavePublishingStatusPolicy(StatsUpdateTypeEnum.GENERAL)
public class TransformXML extends BookStepImpl {
    @Resource(name = "gatherFileSystem")
    private GatherFileSystem gatherFileSystem;

    @Resource(name = "formatFileSystem")
    private FormatFileSystem formatFileSystem;

    @Resource
    private TransformerService transformerService;

    @Resource(name = "publishingStatsService")
    private PublishingStatsService publishingStatsService;

    @Override
    public ExitStatus executeStep() throws Exception {
        final Long jobId = getJobInstanceId();
        final BookDefinition bookDefinition = getBookDefinition();
        final int numDocsInTOC = getJobExecutionPropertyInt(JobExecutionKey.EBOOK_STATS_DOC_COUNT);

        final File metadataDir = gatherFileSystem.getGatherDocsMetadataDirectory(this);
        final File imgMetadataDir = formatFileSystem.getImageMetadataDirectory(this);
        final File transformCharSequencesDir = formatFileSystem.getTransformCharSequencesDirectory(this);
        final File transformDir = formatFileSystem.getTransformedDirectory(this);

        final PublishingStats jobstats = new PublishingStats();
        jobstats.setJobInstanceId(jobId);
        String stepStatus = "Completed";

        final File staticContentDir = new File(getJobExecutionPropertyString(JobExecutionKey.STATIC_CONTENT_DIR));

        try {
            final int numDocsTransformed = transformerService.transformXMLDocuments(
                transformCharSequencesDir,
                metadataDir,
                imgMetadataDir,
                transformDir,
                jobId,
                bookDefinition,
                staticContentDir);

            if (numDocsTransformed != numDocsInTOC) {
                final String message = "The number of documents transformed did not match the number "
                    + "of documents retrieved from the eBook TOC. Transformed "
                    + numDocsTransformed
                    + " documents while the eBook TOC had "
                    + numDocsInTOC
                    + " documents.";
                log.error(message);
                throw new EBookFormatException(message);
            }
        } catch (final Exception e) {
            stepStatus = "Failed";
            throw e;
        } finally {
            jobstats.setPublishStatus("formatTransformXML : " + stepStatus);
            publishingStatsService.updatePublishingStats(jobstats, StatsUpdateTypeEnum.GENERAL);
        }

        return ExitStatus.COMPLETED;
    }
}
