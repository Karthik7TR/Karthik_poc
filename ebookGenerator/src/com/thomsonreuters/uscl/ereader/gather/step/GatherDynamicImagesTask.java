package com.thomsonreuters.uscl.ereader.gather.step;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.Resource;

import com.thomsonreuters.uscl.ereader.JobExecutionKey;
import com.thomsonreuters.uscl.ereader.StatsUpdateTypeEnum;
import com.thomsonreuters.uscl.ereader.common.filesystem.FormatFileSystem;
import com.thomsonreuters.uscl.ereader.common.filesystem.ImageFileSystem;
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
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.batch.core.ExitStatus;
import org.springframework.util.Assert;

/**
 * Fetch book images from the Image REST web service and save them
 * into a specified image destination directory.
 */
@SendFailureNotificationPolicy(FailureNotificationType.GENERATOR)
@SavePublishingStatusPolicy(StatsUpdateTypeEnum.GATHERIMAGE)
public class GatherDynamicImagesTask extends BookStepImpl {
    @Resource(name = "imageService")
    private ImageService imageService;

    @Resource(name = "formatFileSystem")
    private FormatFileSystem formatFileSystem;

    @Resource(name = "imageFileSystem")
    private ImageFileSystem imageFileSystem;

    @Resource(name = "gatherService")
    private GatherService gatherService;

    @Override
    public ExitStatus executeStep() throws Exception {
        // Assert the state of the filesystem image directory and expected input files
        final File dynamicImageDestinationDirectory = imageFileSystem.getImageDynamicDirectory(this);
        final File imageGuidFile = formatFileSystem.getImageToDocumentManifestFile(this);

        if (!dynamicImageDestinationDirectory.exists()) {
            FileUtils.forceMkdir(dynamicImageDestinationDirectory);
        } else {
            Assert.isTrue(
                dynamicImageDestinationDirectory.canWrite(),
                String.format(
                    "The dynamic image destination directory is not writable: " + dynamicImageDestinationDirectory,
                    dynamicImageDestinationDirectory.getAbsolutePath()));
            // Remove all existing image files from image destination directory, covers case of this step failing and restarting the step.
            ImageServiceImpl.removeAllFilesInDirectory(dynamicImageDestinationDirectory);
        }

        Assert.isTrue(
            imageGuidFile.exists(),
            "The dynamic image GUID list text file does not exist: "
                + imageGuidFile
                + " - This file contains image GUID's, one per line, that are requested from the Image Vertical REST service.");

        // Fetch the image metadata and file bytes
        final long jobInstanceId = getJobInstanceId();
        final Set<String> imgGuidSet = readImageGuidsFromTextFile(imageGuidFile);

        int imageGuidNum = imgGuidSet.size();
        int retrievedCount = 0;
        try {
            if (imageGuidNum > 0) {
                final GatherImgRequest imgRequest =
                        constructGatherImageRequest(dynamicImageDestinationDirectory, imageGuidFile, jobInstanceId);
                final GatherResponse gatherResponse = gatherService.getImg(imgRequest);
                if (gatherResponse.getMissingImgCount() > 0) {
                    retrievedCount = imageGuidNum - gatherResponse.getMissingImgCount();
                    List<String> missedImagesList = gatherResponse.getMissingImagesList();
                    throw new ImageException(
                            String.format(
                                    "Download of dynamic images failed because there were %d missing image(s)" + missedImagesList,
                                    gatherResponse.getMissingImgCount()));
                }
                retrievedCount = imageGuidNum;
                if (gatherResponse.getImageMetadataList() != null) {
                    for (final ImgMetadataInfo metadata : gatherResponse.getImageMetadataList()) {
                        imageService.saveImageMetadata(metadata, jobInstanceId, getBookDefinition().getFullyQualifiedTitleId());
                    }
                }
            }
        } finally {
            storeImagesInfo(imageGuidNum, retrievedCount);
        }
        return ExitStatus.COMPLETED;
    }

    protected GatherImgRequest constructGatherImageRequest(
        final File dynamicImageDestinationDirectory,
        final File imageGuidFile,
        final long jobInstanceId) {
        return new GatherImgRequest(
            imageGuidFile,
            dynamicImageDestinationDirectory,
            jobInstanceId,
            getBookDefinition().isFinalStage());
    }

    /**
     * Reads the contents of a text file and return each line as an element in the returned list.
     * The file is assumed to already exist.
     * @file textFile the text file to process
     * @return a set of text strings, representing each file of the specified file
     */
    private static Set<String> readImageGuidsFromTextFile(final File textFile) throws IOException {
        final Set<String> imgGuidSet = new HashSet<>();
        try (FileReader fileReader = new FileReader(textFile); BufferedReader reader = new BufferedReader(fileReader)) {
            String textLine;
            while ((textLine = reader.readLine()) != null) {
                if (StringUtils.isNotBlank(textLine)) {
                    final String[] imgGuids = textLine.split("\\|");
                    if (imgGuids.length > 1) {
                        final String[] imgGuidsList = imgGuids[1].split(",");
                        for (final String imgGuid : imgGuidsList) {
                            imgGuidSet.add(imgGuid);
                        }
                    }
                }
            }
        }
        return imgGuidSet;
    }

    private void storeImagesInfo(final int imageGuidNum, final int retrievedCount) {
        setJobExecutionProperty(JobExecutionKey.IMAGE_GUID_NUM, imageGuidNum);
        setJobExecutionProperty(JobExecutionKey.RETRIEVED_IMAGES_COUNT, retrievedCount);
    }
}
