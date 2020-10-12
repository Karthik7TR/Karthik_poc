package com.thomsonreuters.uscl.ereader.assemble.step;

import com.thomsonreuters.uscl.ereader.JobExecutionKey;
import com.thomsonreuters.uscl.ereader.StatsUpdateTypeEnum;
import com.thomsonreuters.uscl.ereader.common.filesystem.AssembleFileSystem;
import com.thomsonreuters.uscl.ereader.common.filesystem.FormatFileSystem;
import com.thomsonreuters.uscl.ereader.common.filesystem.ImageFileSystem;
import com.thomsonreuters.uscl.ereader.common.notification.step.FailureNotificationType;
import com.thomsonreuters.uscl.ereader.common.notification.step.SendFailureNotificationPolicy;
import com.thomsonreuters.uscl.ereader.common.publishingstatus.step.SavePublishingStatusPolicy;
import com.thomsonreuters.uscl.ereader.common.step.BookStepImpl;
import com.thomsonreuters.uscl.ereader.stats.domain.PublishingStats;
import com.thomsonreuters.uscl.ereader.stats.service.PublishingStatsService;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.Resource;
import java.io.File;

/**
 * This step is responsible for moving resources, identified by well-known JobExecutionKeys, to the assembly directory
 *
 * @author <a href="mailto:christopher.schwartz@thomsonreuters.com">Chris Schwartz</a> u0081674
 *
 */
@SendFailureNotificationPolicy(FailureNotificationType.GENERATOR)
@SavePublishingStatusPolicy(StatsUpdateTypeEnum.GENERAL)
public class MoveResourcesToAssemblyDirectory extends BookStepImpl {
    /**
     * To update publishingStatsService table.
     */
    @Autowired
    private PublishingStatsService publishingStatsService;

    @Autowired
    private MoveResourcesUtil moveResourcesUtil;

    @Resource(name = "assembleFileSystem")
    private AssembleFileSystem assembleFileSystem;
    @Autowired
    private FormatFileSystem formatFileSystem;
    @Autowired
    private ImageFileSystem imageFileSystem;
    /*
     * (non-Javadoc)
     *
     * @see com.thomsonreuters.uscl.ereader.orchestrate.core.tasklet.
     * AbstractSbTasklet#executeStep(org.springframework.batch.core. StepContribution,
     * org.springframework.batch.core.scope.context.ChunkContext)
     */
    @Override
    public ExitStatus executeStep()
        throws Exception {
        final ExecutionContext jobExecutionContext = getJobExecutionContext();
        final Long jobId = getJobInstanceId();
        final PublishingStats jobstats = new PublishingStats();
        jobstats.setJobInstanceId(jobId);
        String publishStatus = "Completed";

        try {
            final File ebookDirectory = assembleFileSystem.getTitleDirectory(this);
            final File assetsDirectory = createAssetsDirectory(ebookDirectory);
            final File artworkDirectory = createArtworkDirectory(ebookDirectory);
            final File documentsDirectory = createDocumentsDirectory(ebookDirectory);

            final File frontMatter = formatFileSystem.getFrontMatterHtmlDir(this);
            moveResourcesUtil.copySourceToDestination(frontMatter, documentsDirectory);

            final File transformedDocsDir = new File(
                    getJobExecutionPropertyString(JobExecutionKey.FORMAT_DOCUMENTS_READY_DIRECTORY_PATH));
            moveResourcesUtil.copySourceToDestination(transformedDocsDir, documentsDirectory);

            // Images
            final File dynamicImagesDir = imageFileSystem.getImageDynamicDirectory(this);
            final File staticImagesDir = imageFileSystem.getImageStaticDirectory(this);
            moveResourcesUtil.copySourceToDestination(dynamicImagesDir, assetsDirectory);
            moveResourcesUtil.copySourceToDestination(staticImagesDir, assetsDirectory);

            moveResourcesUtil.moveCoverArt(jobExecutionContext, artworkDirectory);
            moveResourcesUtil.moveFrontMatterImages(this, assetsDirectory, true);
            moveResourcesUtil.moveStylesheet(assetsDirectory);
            moveResourcesUtil.moveThesaurus(this, assembleFileSystem.getAssetsDirectory(this));
        } catch (final Exception e) {
            publishStatus = "Failed";
            throw e;
        } finally {
            jobstats.setPublishStatus("moveResourcesToAssemblyDirectory: " + publishStatus);
            publishingStatsService.updatePublishingStats(jobstats, StatsUpdateTypeEnum.GENERAL);
        }

        return ExitStatus.COMPLETED;
    }

    private File createDocumentsDirectory(final File ebookDirectory) {
        return new File(ebookDirectory, "documents");
    }

    private File createArtworkDirectory(final File ebookDirectory) {
        final File artworkDirectory = new File(ebookDirectory, "artwork");
        artworkDirectory.mkdirs();
        return artworkDirectory;
    }

    private File createAssetsDirectory(final File parentDirectory) {
        final File assetsDirectory = new File(parentDirectory, "assets");
        return assetsDirectory;
    }
}
