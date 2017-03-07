package com.thomsonreuters.uscl.ereader.gather.step;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import javax.annotation.Resource;

import com.thomsonreuters.uscl.ereader.StatsUpdateTypeEnum;
import com.thomsonreuters.uscl.ereader.common.filesystem.FormatFileSystem;
import com.thomsonreuters.uscl.ereader.common.filesystem.ImageFileSystem;
import com.thomsonreuters.uscl.ereader.common.filesystem.XppUnpackFileSystem;
import com.thomsonreuters.uscl.ereader.common.notification.step.FailureNotificationType;
import com.thomsonreuters.uscl.ereader.common.notification.step.SendFailureNotificationPolicy;
import com.thomsonreuters.uscl.ereader.common.publishingstatus.step.SavePublishingStatusPolicy;
import com.thomsonreuters.uscl.ereader.common.step.BookStepImpl;
import com.thomsonreuters.uscl.ereader.gather.domain.GatherImgRequest;
import com.thomsonreuters.uscl.ereader.gather.domain.GatherResponse;
import com.thomsonreuters.uscl.ereader.gather.image.domain.ImageException;
import com.thomsonreuters.uscl.ereader.gather.image.service.ImageService;
import com.thomsonreuters.uscl.ereader.gather.image.service.ImageServiceImpl;
import com.thomsonreuters.uscl.ereader.gather.restclient.service.GatherService;
import com.thomsonreuters.uscl.ereader.gather.util.ImgMetadataInfo;
import com.thomsonreuters.uscl.ereader.stats.service.PublishingStatsService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.batch.core.ExitStatus;
import org.springframework.util.Assert;

/**
 * Fetch book images from the Image Vertical REST web service and save them
 * into a specified image destination directory.
 */
@SendFailureNotificationPolicy(FailureNotificationType.GENERATOR)
@SavePublishingStatusPolicy(StatsUpdateTypeEnum.GATHERIMAGE)
public class GatherDynamicImagesTask extends BookStepImpl
{
    @Resource(name = "imageService")
    private ImageService imageService;

    @Resource(name = "publishingStatsService")
    private PublishingStatsService publishingStatsService;

    @Resource(name = "formatFileSystem")
    private FormatFileSystem formatFileSystem;

    @Resource(name = "imageFileSystem")
    private ImageFileSystem imageFileSystem;

    @Resource(name = "xppUnpackFileSystem")
    private XppUnpackFileSystem xppUnpackFileSystem;

    @Resource(name = "gatherService")
    private GatherService gatherService;

    private int imageGuidNum;
    private int retrievedCount;

    @Override
    public ExitStatus executeStep() throws Exception
    {
        // Assert the state of the filesystem image directory and expected input files
        final File dynamicImageDestinationDirectory = imageFileSystem.getImageDynamicDirectory(this);
        final File imageGuidFile = formatFileSystem.getImageToDocumentManifestFile(this);
        final String xppSourceImageGirectory = xppUnpackFileSystem.getXppAssetsDirectory(this);

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
        final long jobInstanceId = getJobInstanceId();
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
                getBookDefinition().isFinalStage());
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
                    imageService.saveImageMetadata(metadata, jobInstanceId, getBookDefinition().getFullyQualifiedTitleId());
                }
            }
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

    public int getImageGuidNum()
    {
        return imageGuidNum;
    }

    public int getRetrievedCount()
    {
        return retrievedCount;
    }
}
