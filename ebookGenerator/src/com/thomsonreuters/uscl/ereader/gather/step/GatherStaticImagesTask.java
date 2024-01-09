package com.thomsonreuters.uscl.ereader.gather.step;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.thomsonreuters.uscl.ereader.StatsUpdateTypeEnum;
import com.thomsonreuters.uscl.ereader.common.filesystem.ImageFileSystem;
import com.thomsonreuters.uscl.ereader.common.notification.step.FailureNotificationType;
import com.thomsonreuters.uscl.ereader.common.notification.step.SendFailureNotificationPolicy;
import com.thomsonreuters.uscl.ereader.common.publishingstatus.step.SavePublishingStatusPolicy;
import com.thomsonreuters.uscl.ereader.common.step.BookStepImpl;
import com.thomsonreuters.uscl.ereader.gather.image.service.ImageService;
import com.thomsonreuters.uscl.ereader.gather.image.service.ImageServiceImpl;
import com.thomsonreuters.uscl.ereader.stats.domain.PublishingStats;
import com.thomsonreuters.uscl.ereader.stats.service.PublishingStatsService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.batch.core.ExitStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;

/**
 * Fetch static book images from a filesystem tree and copy them to the holding destination directory.
 */
@SendFailureNotificationPolicy(FailureNotificationType.GENERATOR)
@SavePublishingStatusPolicy(StatsUpdateTypeEnum.GENERAL)
public class GatherStaticImagesTask extends BookStepImpl {
    @Autowired
    private ImageService imageService;
    @Autowired
    private PublishingStatsService publishingStatsService;
    @Autowired
    private ImageFileSystem imageFileSystem;

    @Override
    public ExitStatus executeStep()
        throws Exception {
        final File staticImageDestinationDirectory = imageFileSystem.getImageStaticDirectory(this);
        final File manifestFile = imageFileSystem.getImageStaticManifestFile(this);
        // Assert the state of the filesystem image directory and expected input files
        Assert.isTrue(
            staticImageDestinationDirectory.canWrite(),
            String.format(
                "The static image destination directory is not writable: " + staticImageDestinationDirectory,
                staticImageDestinationDirectory.getAbsolutePath()));
        String publishStatus = "Completed";
        try {
            // Remove all existing image files from the static image destination directory, covers case of this step failing and restarting the step.
            ImageServiceImpl.removeAllFilesInDirectory(staticImageDestinationDirectory);

            // Read the image file basenames, one per line from the manifest file
            final List<String> basenames = readLinesFromTextFile(manifestFile);

            // Copy all the static image files from their location in the tree to the destination directory
            imageService.fetchStaticImages(basenames, staticImageDestinationDirectory);
        } catch (final Exception e) {
            publishStatus = "Failed";
            throw e;
        } finally {
            final PublishingStats jobstats = new PublishingStats();
            jobstats.setJobInstanceId(getJobInstanceId());
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
    public static List<String> readLinesFromTextFile(final File textFile) throws IOException {
        final List<String> lineList = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(textFile))) {
            String textLine;
            while ((textLine = reader.readLine()) != null) {
                if (StringUtils.isNotBlank(textLine)) {
                    lineList.add(textLine.trim());
                }
            }
        }
        return lineList;
    }
}
