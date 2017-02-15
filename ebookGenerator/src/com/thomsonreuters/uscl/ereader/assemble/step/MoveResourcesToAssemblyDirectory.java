package com.thomsonreuters.uscl.ereader.assemble.step;

import java.io.File;

import com.thomsonreuters.uscl.ereader.JobExecutionKey;
import com.thomsonreuters.uscl.ereader.StatsUpdateTypeEnum;
import com.thomsonreuters.uscl.ereader.orchestrate.core.tasklet.AbstractSbTasklet;
import com.thomsonreuters.uscl.ereader.stats.domain.PublishingStats;
import com.thomsonreuters.uscl.ereader.stats.service.PublishingStatsService;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.beans.factory.annotation.Required;

/**
 * This step is responsible for moving resources, identified by well-known JobExecutionKeys, to the assembly directory
 *
 * @author <a href="mailto:christopher.schwartz@thomsonreuters.com">Chris Schwartz</a> u0081674
 *
 */
public class MoveResourcesToAssemblyDirectory extends AbstractSbTasklet
{
    /**
     * To update publishingStatsService table.
     */
    private PublishingStatsService publishingStatsService;

    private MoveResourcesUtil moveResourcesUtil;

    /*
     * (non-Javadoc)
     *
     * @see com.thomsonreuters.uscl.ereader.orchestrate.core.tasklet.
     * AbstractSbTasklet#executeStep(org.springframework.batch.core. StepContribution,
     * org.springframework.batch.core.scope.context.ChunkContext)
     */
    @Override
    public ExitStatus executeStep(final StepContribution contribution, final ChunkContext chunkContext) throws Exception
    {
        final ExecutionContext jobExecutionContext = getJobExecutionContext(chunkContext);
        final Long jobId = getJobInstance(chunkContext).getId();
        final PublishingStats jobstats = new PublishingStats();
        jobstats.setJobInstanceId(jobId);
        String publishStatus = "Completed";

        try
        {
            final File ebookDirectory =
                new File(getRequiredStringProperty(jobExecutionContext, JobExecutionKey.EBOOK_DIRECTORY));
            final File assetsDirectory = createAssetsDirectory(ebookDirectory);
            final File artworkDirectory = createArtworkDirectory(ebookDirectory);
            final File documentsDirectory = createDocumentsDirectory(ebookDirectory);

            final File frontMatter =
                new File(getRequiredStringProperty(jobExecutionContext, JobExecutionKey.FORMAT_FRONT_MATTER_HTML_DIR));
            moveResourcesUtil.copySourceToDestination(frontMatter, documentsDirectory);

            final File transformedDocsDir = new File(
                getRequiredStringProperty(jobExecutionContext, JobExecutionKey.FORMAT_DOCUMENTS_READY_DIRECTORY_PATH));
            moveResourcesUtil.copySourceToDestination(transformedDocsDir, documentsDirectory);

            // Images
            final File dynamicImagesDir =
                new File(getRequiredStringProperty(jobExecutionContext, JobExecutionKey.IMAGE_DYNAMIC_DEST_DIR));
            final File staticImagesDir =
                new File(getRequiredStringProperty(jobExecutionContext, JobExecutionKey.IMAGE_STATIC_DEST_DIR));
            moveResourcesUtil.copySourceToDestination(dynamicImagesDir, assetsDirectory);
            moveResourcesUtil.copySourceToDestination(staticImagesDir, assetsDirectory);

            moveResourcesUtil.moveCoverArt(jobExecutionContext, artworkDirectory);
            moveResourcesUtil.moveFrontMatterImages(jobExecutionContext, assetsDirectory, true);
            moveResourcesUtil.moveStylesheet(assetsDirectory);
        }
        catch (final Exception e)
        {
            publishStatus = "Failed";
            throw e;
        }
        finally
        {
            jobstats.setPublishStatus("moveResourcesToAssemblyDirectory: " + publishStatus);
            publishingStatsService.updatePublishingStats(jobstats, StatsUpdateTypeEnum.GENERAL);
        }

        return ExitStatus.COMPLETED;
    }

    private File createDocumentsDirectory(final File ebookDirectory)
    {
        return new File(ebookDirectory, "documents");
    }

    private File createArtworkDirectory(final File ebookDirectory)
    {
        final File artworkDirectory = new File(ebookDirectory, "artwork");
        artworkDirectory.mkdirs();
        return artworkDirectory;
    }

    private File createAssetsDirectory(final File parentDirectory)
    {
        final File assetsDirectory = new File(parentDirectory, "assets");
        return assetsDirectory;
    }

    @Required
    public void setPublishingStatsService(final PublishingStatsService publishingStatsService)
    {
        this.publishingStatsService = publishingStatsService;
    }

    public MoveResourcesUtil getMoveResourcesUtil()
    {
        return moveResourcesUtil;
    }

    @Required
    public void setMoveResourcesUtil(final MoveResourcesUtil moveResourcesUtil)
    {
        this.moveResourcesUtil = moveResourcesUtil;
    }
}
