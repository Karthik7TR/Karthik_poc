package com.thomsonreuters.uscl.ereader.gather.image.service;

import java.io.File;
import java.util.List;
import java.util.Map;

import com.thomsonreuters.uscl.ereader.gather.image.domain.ImageException;
import com.thomsonreuters.uscl.ereader.gather.image.domain.ImageMetadataEntity;
import com.thomsonreuters.uscl.ereader.gather.image.domain.ImageMetadataEntityKey;
import com.thomsonreuters.uscl.ereader.gather.util.ImgMetadataInfo;

public interface ImageService {
    /**
     * Copy a collection of image files to the well-known static image directory for the current job instance.
     * @param basenames a list of image file base names (no dir path info) that are to be searched for within a filesystem tree of images.
     * @param imageDestinationDirectory the filesystem directory where the requested files will be copied to, not created, must already exist
     * @throws ImageException on an IO error copying the files, or if one of the image files does not exist
     */
    void fetchStaticImages(List<String> basenames, File imageDestinationDirectory) throws ImageException;

    /**
     * Find all the images for a given job run instance from the image meta-data database table.
     * @param jobInstanceId the Spring Batch job instance identifier.
     * @return a list of all image meta-data associated with a specific job.
     */
    List<ImageMetadataEntity> findImageMetadata(long jobInstanceId);

    /**
     * Fetch the meta-data for a specific image.
     * @param key the primary key of the entity.
     * @return the meta-data for the specified image, or null if not found
     */
    ImageMetadataEntity findImageMetadata(ImageMetadataEntityKey key);

    /**
     * Persist a single meta-data entity to the image meta-data table.
     * @param metadata domain object for metadata
     * @param jobInstanceId used to associate metadata to a specific job run
     * @param titleId the unique key for the book for which we are gathering images
     */

    ImageMetadataEntityKey saveImageMetadata(ImageMetadataEntity metadata);

    /**
     * Persist the image meta-data from the REST service response to a database table
     * @param metadata the meta-data container returned from the Image Vertical REST service
     * @param jobInstanceId uniquely identifies the book generating job so meta-data can be stored in the database grouped by job
     * @param titleId the unique key for the book for which we are gathering images
     * @return the primary key of the created record.
     */
    ImageMetadataEntityKey saveImageMetadata(ImgMetadataInfo metadata, long jobInstanceId, String titleId);

    Map<String, List<String>> getDocImageListMap(Long jobInstanceId);
}
