package com.thomsonreuters.uscl.ereader.format.step;

import java.io.File;
import java.util.List;

import com.thomsonreuters.uscl.ereader.JobExecutionKey;
import com.thomsonreuters.uscl.ereader.StatsUpdateTypeEnum;
import com.thomsonreuters.uscl.ereader.common.filesystem.FormatFileSystem;
import com.thomsonreuters.uscl.ereader.common.filesystem.GatherFileSystem;
import com.thomsonreuters.uscl.ereader.common.filesystem.ImageFileSystem;
import com.thomsonreuters.uscl.ereader.common.notification.step.FailureNotificationType;
import com.thomsonreuters.uscl.ereader.common.notification.step.SendFailureNotificationPolicy;
import com.thomsonreuters.uscl.ereader.common.publishingstatus.step.SavePublishingStatusPolicy;
import com.thomsonreuters.uscl.ereader.common.step.BookStepImpl;
import com.thomsonreuters.uscl.ereader.core.book.domain.BookDefinition;
import com.thomsonreuters.uscl.ereader.core.book.domain.TableViewer;
import com.thomsonreuters.uscl.ereader.format.exception.EBookFormatException;
import com.thomsonreuters.uscl.ereader.format.service.HTMLTransformerService;
import com.thomsonreuters.uscl.ereader.stats.domain.PublishingStats;
import com.thomsonreuters.uscl.ereader.stats.service.PublishingStatsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.ExitStatus;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * This step transforms the HTML generated by the transformation process into ProView acceptable HTML.
 *
 * @author <a href="mailto:Selvedin.Alic@thomsonreuters.com">Selvedin Alic</a> u0095869
 */
@Slf4j
@SendFailureNotificationPolicy(FailureNotificationType.GENERATOR)
@SavePublishingStatusPolicy(StatsUpdateTypeEnum.GENERAL)
public class HTMLPostTransform extends BookStepImpl {
    @Autowired
    private HTMLTransformerService transformerService;
    @Autowired
    private PublishingStatsService publishingStatsService;
    @Autowired
    private GatherFileSystem gatherFileSystem;
    @Autowired
    private FormatFileSystem formatFileSystem;
    @Autowired
    private ImageFileSystem imageFileSystem;

    @Override
    public ExitStatus executeStep() throws Exception {
        final BookDefinition bookDefinition = getBookDefinition();
        final String titleId = bookDefinition.getTitleId();
        final Long jobId = getJobInstanceId();
        final String version = getBookVersionString();
        final File sourceDir = formatFileSystem.getTransformedDirectory(this);
        final File targetDir = formatFileSystem.getPostTransformDirectory(this);
        final File staticImgFile = imageFileSystem.getImageStaticManifestFile(this);
        final File docsGuidFile = gatherFileSystem.getGatherDocGuidsFile(this);
        final File deDuppingFile = formatFileSystem.getDeDuppingAnchorFile(this);
        final int numDocsInTOC = getJobExecutionPropertyInt(JobExecutionKey.EBOOK_STATS_DOC_COUNT);
        final List<TableViewer> tableViewers = bookDefinition.getTableViewers();
        final PublishingStats jobstats = new PublishingStats(jobId);
        String stepStatus = "Completed";

        try {
            final int numDocsTransformed = transformerService.transformHTML(
                sourceDir,
                targetDir,
                staticImgFile,
                tableViewers,
                titleId,
                jobId,
                null,
                docsGuidFile,
                deDuppingFile,
                bookDefinition.isInsStyleFlag(),
                bookDefinition.isDelStyleFlag(),
                bookDefinition.isRemoveEditorNoteHeadFlag(),
                version);

            if (numDocsTransformed != numDocsInTOC) {
                final String message = "The number of post transformed documents did not match the number "
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
            jobstats.setPublishStatus("formatHTMLTransformer : " + stepStatus);
            publishingStatsService.updatePublishingStats(jobstats, StatsUpdateTypeEnum.GENERAL);
        }

        return ExitStatus.COMPLETED;
    }
}
