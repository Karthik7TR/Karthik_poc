package com.thomsonreuters.uscl.ereader.gather.step;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import com.thomsonreuters.uscl.ereader.JobExecutionKey;
import com.thomsonreuters.uscl.ereader.StatsUpdateTypeEnum;
import com.thomsonreuters.uscl.ereader.core.book.domain.BookDefinition;
import com.thomsonreuters.uscl.ereader.gather.domain.GatherImgRequest;
import com.thomsonreuters.uscl.ereader.gather.domain.GatherResponse;
import com.thomsonreuters.uscl.ereader.gather.image.domain.ImageException;
import com.thomsonreuters.uscl.ereader.gather.image.service.ImageService;
import com.thomsonreuters.uscl.ereader.gather.image.service.ImageServiceImpl;
import com.thomsonreuters.uscl.ereader.gather.restclient.service.GatherService;
import com.thomsonreuters.uscl.ereader.gather.util.ImgMetadataInfo;
import com.thomsonreuters.uscl.ereader.orchestrate.core.tasklet.AbstractSbTasklet;
import com.thomsonreuters.uscl.ereader.stats.domain.PublishingStats;
import com.thomsonreuters.uscl.ereader.stats.service.PublishingStatsService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.JobInstance;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.util.Assert;

/**
 * Fetch book images from the Image Vertical REST web service and save them
 * into a specified image destination directory.
 */
public class GatherDynamicImagesTask extends AbstractSbTasklet
{
    //private static final Logger log = LogManager.getLogger(GatherDynamicImagesTask.class);
    private ImageService imageService;
    private PublishingStatsService publishingStatsService;
    private GatherService gatherService;

    @Required
    public void setGatherService(final GatherService service)
    {
        gatherService = service;
    }

    @Override
    public ExitStatus executeStep(final StepContribution contribution, final ChunkContext chunkContext) throws Exception
    {
        final ExecutionContext jobExecutionContext = getJobExecutionContext(chunkContext);
        final JobInstance jobInstance = getJobInstance(chunkContext);

        final BookDefinition bookDefinition =
            (BookDefinition) jobExecutionContext.get(JobExecutionKey.EBOOK_DEFINITION);

        // Assert the state of the filesystem image directory and expected input files
        final File dynamicImageDestinationDirectory =
            new File(getRequiredStringProperty(jobExecutionContext, JobExecutionKey.IMAGE_DYNAMIC_DEST_DIR));
        final File imageGuidFile =
            new File(getRequiredStringProperty(jobExecutionContext, JobExecutionKey.IMAGE_TO_DOC_MANIFEST_FILE));
        final String xppSourceImageGirectory =
            jobExecutionContext.containsKey(JobExecutionKey.XPP_IMAGES_UNPACK_DIR) ? jobExecutionContext.getString(JobExecutionKey.XPP_IMAGES_UNPACK_DIR) : null;
        Assert.isTrue(
            dynamicImageDestinationDirectory.exists(),
            String.format(
                "The dynamic image destination directory does not exist in the filesystem: "
                    + dynamicImageDestinationDirectory,
                dynamicImageDestinationDirectory.getAbsolutePath()));
        Assert.isTrue(
            dynamicImageDestinationDirectory.canWrite(),
            String.format(
                "The dynamic image destination directory is not writable: " + dynamicImageDestinationDirectory,
                dynamicImageDestinationDirectory.getAbsolutePath()));
        Assert.isTrue(
            imageGuidFile.exists(),
            "The dynamic image GUID list text file does not exist: "
                + imageGuidFile
                + " - This file contains image GUID's, one per line, that are requested from the Image Vertical REST service.");

        // Fetch the image metadata and file bytes
        final long jobInstanceId = jobInstance.getId();
        final String titleId = bookDefinition.getFullyQualifiedTitleId();
        int imageGuidNum = 0;
        int retrievedCount = 0;

        String stepStatus = "Completed";
        try
        {
            // Remove all existing image files from image destination directory, covers case of this step failing and restarting the step.
            ImageServiceImpl.removeAllFilesInDirectory(dynamicImageDestinationDirectory);

            final Set<String> imgGuidSet = readImageGuidsFromTextFile(imageGuidFile);

            imageGuidNum = imgGuidSet.size();

            if (imageGuidNum > 0)
            {
                final GatherImgRequest imgRequest = new GatherImgRequest(
                    imageGuidFile,
                    dynamicImageDestinationDirectory,
                    jobInstanceId,
                    bookDefinition.isFinalStage());
                //imgRequest.setXpp(isXpp);
                imgRequest.setXppSourceImageDirectory(xppSourceImageGirectory);
                final GatherResponse gatherResponse = gatherService.getImg(imgRequest);

                if (gatherResponse.getMissingImgCount() > 0)
                {
                    retrievedCount = imageGuidNum - gatherResponse.getMissingImgCount();
                    throw new ImageException(
                        String.format(
                            "Download of dynamic images failed because there were %d missing image(s)",
                            gatherResponse.getMissingImgCount()));
                }
                retrievedCount = imageGuidNum;

                if (gatherResponse.getImageMetadataList() != null)
                {
                    for (final ImgMetadataInfo metadata : gatherResponse.getImageMetadataList())
                    {
                        imageService.saveImageMetadata(metadata, jobInstanceId, titleId);
                    }
                }
            }
        }
        catch (final ImageException e)
        {
            stepStatus = "Failed";
            throw e;
        }
        catch (final Exception e)
        {
            stepStatus = "Failed";
            throw e;
        }
        finally
        {
            final PublishingStats jobstatsDoc = new PublishingStats();
            jobstatsDoc.setJobInstanceId(jobInstanceId);
            jobstatsDoc.setGatherImageExpectedCount(imageGuidNum);
            jobstatsDoc.setGatherImageRetrievedCount(retrievedCount);
            jobstatsDoc.setPublishStatus("getDynamicImages : " + stepStatus);
            publishingStatsService.updatePublishingStats(jobstatsDoc, StatsUpdateTypeEnum.GATHERIMAGE);
        }

        return ExitStatus.COMPLETED;
    }

    /**
     * Reads the contents of a text file and return each line as an element in the returned list.
     * The file is assumed to already exist.
     * @file textFile the text file to process
     * @return a set of text strings, representing each file of the specified file
     */
    private static Set<String> readImageGuidsFromTextFile(final File textFile) throws IOException
    {
        final Set<String> imgGuidSet = new HashSet<>();
        try (FileReader fileReader = new FileReader(textFile);
             BufferedReader reader = new BufferedReader(fileReader))
        {
            String textLine;
            while ((textLine = reader.readLine()) != null)
            {
                if (StringUtils.isNotBlank(textLine))
                {
                    final String[] imgGuids = textLine.split("\\|");
                    if (imgGuids.length > 1)
                    {
                        final String[] imgGuidsList = imgGuids[1].split(",");
                        for (final String imgGuid : imgGuidsList)
                        {
                            imgGuidSet.add(imgGuid);
                        }
                    }
                }
            }
        }
        return imgGuidSet;
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
