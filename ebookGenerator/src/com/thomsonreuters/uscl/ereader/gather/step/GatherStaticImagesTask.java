package com.thomsonreuters.uscl.ereader.gather.step;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.thomsonreuters.uscl.ereader.JobExecutionKey;
import com.thomsonreuters.uscl.ereader.StatsUpdateTypeEnum;
import com.thomsonreuters.uscl.ereader.gather.image.service.ImageService;
import com.thomsonreuters.uscl.ereader.gather.image.service.ImageServiceImpl;
import com.thomsonreuters.uscl.ereader.orchestrate.core.tasklet.AbstractSbTasklet;
import com.thomsonreuters.uscl.ereader.stats.domain.PublishingStats;
import com.thomsonreuters.uscl.ereader.stats.service.PublishingStatsService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.util.Assert;

/**
 * Fetch static book images from a filesystem tree and copy them to the holding destination directory.
 */
public class GatherStaticImagesTask extends AbstractSbTasklet
{
    //private static final Logger log = LogManager.getLogger(GatherStaticImagesTask.class);
    private ImageService imageService;
    private PublishingStatsService publishingStatsService;

    @Override
    public ExitStatus executeStep(final StepContribution contribution, final ChunkContext chunkContext) throws Exception
    {
        final ExecutionContext jobExecutionContext = getJobExecutionContext(chunkContext);

        final Long jobInstance =
            chunkContext.getStepContext().getStepExecution().getJobExecution().getJobInstance().getId();

        // Assert the state of the filesystem image directory and expected input files
        final File staticImageDestinationDirectory =
            new File(getRequiredStringProperty(jobExecutionContext, JobExecutionKey.IMAGE_STATIC_DEST_DIR));
        final File manifestFile =
            new File(getRequiredStringProperty(jobExecutionContext, JobExecutionKey.IMAGE_STATIC_MANIFEST_FILE));
        Assert.isTrue(
            staticImageDestinationDirectory.exists(),
            String.format(
                "The static image destination directory does not exist in the filesystem: "
                    + staticImageDestinationDirectory,
                staticImageDestinationDirectory.getAbsolutePath()));
        Assert.isTrue(
            staticImageDestinationDirectory.canWrite(),
            String.format(
                "The static image destination directory is not writable: " + staticImageDestinationDirectory,
                staticImageDestinationDirectory.getAbsolutePath()));
        Assert.isTrue(
            manifestFile.exists(),
            "The static image manifest file does not exist: "
                + manifestFile
                + " - This file contains static image basenames (no directory path info), one per line, that are copied to a destination directory.");

        String publishStatus = "Completed";
        try
        {
            // Remove all existing image files from the static image destination directory, covers case of this step failing and restarting the step.
            ImageServiceImpl.removeAllFilesInDirectory(staticImageDestinationDirectory);

            // Read the image file basenames, one per line from the manifest file
            final List<String> basenames = readLinesFromTextFile(manifestFile);

            // Copy all the static image files from their location in the tree to the destination directory
            imageService.fetchStaticImages(basenames, staticImageDestinationDirectory);
        }
        catch (final Exception e)
        {
            publishStatus = "Failed";
            throw e;
        }
        finally
        {
            final PublishingStats jobstats = new PublishingStats();
            jobstats.setJobInstanceId(jobInstance);
            jobstats.setPublishStatus("gatherStaticImagesTask: " + publishStatus);
            publishingStatsService.updatePublishingStats(jobstats, StatsUpdateTypeEnum.GENERAL);
        }

        return ExitStatus.COMPLETED;
    }

    /**
     * Reads the contents of a text file and return each line as an element in the returned list.
     * The file is assumed to already exist.
     * @file textFile the text file to process
     * @return a list of text strings, representing each file of the specified file
     */
    public static List<String> readLinesFromTextFile(final File textFile) throws IOException
    {
        final List<String> lineList = new ArrayList<>();
        final FileReader fileReader = new FileReader(textFile);
        try
        {
            final BufferedReader reader = new BufferedReader(fileReader);
            String textLine;
            while ((textLine = reader.readLine()) != null)
            {
                if (StringUtils.isNotBlank(textLine))
                {
                    lineList.add(textLine.trim());
                }
            }
        }
        finally
        {
            if (fileReader != null)
            {
                fileReader.close();
            }
        }
        return lineList;
    }

    @Required
    public void setImageService(final ImageService imageService)
    {
        this.imageService = imageService;
    }

    @Required
    public void setPublishingStatsService(final PublishingStatsService publishingStatsService)
    {
        this.publishingStatsService = publishingStatsService;
    }
}
