/*
* Copyright 2011: Thomson Reuters Global Resources. All Rights Reserved.
* Proprietary and Confidential information of TRGR. Disclosure, Use or
* Reproduction without the written authorization of TRGR is prohibited
*/
package com.thomsonreuters.uscl.ereader.gather.image.service;

import java.io.File;
import java.io.Writer;
import java.util.List;

import com.thomsonreuters.uscl.ereader.gather.image.domain.ImageException;
import com.thomsonreuters.uscl.ereader.gather.image.domain.ImageMetadataEntity;
import com.thomsonreuters.uscl.ereader.gather.image.domain.ImageMetadataEntityKey;
import com.thomsonreuters.uscl.ereader.gather.image.domain.SingleImageMetadataResponse;


public interface ImageService {
	/**
	 * Get the image meta-data for a specific image by key from the Image Vertical REST web service.
	 * @param imageGuid the key of the image
	 * @return the status and meta-data of the image.
	 */
	public SingleImageMetadataResponse fetchImageVerticalImageMetadata(String imageGuid, Writer missingImgGuidsWriter);
	
	/**
	 * Reads image bytes and meta-data from the Image Vertical REST web service for the book based on the specified image GUID's.
	 * Stores the image bytes to a file with a name in the form "<imageDirectory>/<imageGuid>.png".
	 * Updates the image meta-data database table with the meta-data for each image.
	 * @param imageGuids a list of keys for the images desired.
	 * @param imageDestinationDirectory the filesystem directory in which the image files will be created, must already exist.
	 * @param jobInstanceId the unique identifier for which book generating job we are fetching static images for.
	 * 						Used to key the saved image meta-data in the database.
	 * @param titleId The unique key for the book for which we are gathering images.
	 */
	public void fetchImageVerticalImages(final List<String> imageGuids, File imageDestinationDirectory,
							long jobInstanceId, String titleId) throws ImageException;
	
	
	/**
	 * Copy a collection of image files to the well-known static image directory for the current job instance.
	 * @param basenames a list of image file base names (no dir path info) that are to be searched for within a filesystem tree of images.
	 * @param imageDestinationDirectory the filesystem directory where the requested files will be copied to, not created, must already exist
	 * @throws ImageException on an IO error copying the files, or if one of the image files does not exist
	 */
	public void fetchStaticImages(final List<String> basenames, File imageDestinationDirectory) throws ImageException;
	
	/**
	 * Find all the images for a given job run instance from the image meta-data database table.
	 * @param jobInstanceId the Spring Batch job instance identifier.
	 * @return a list of all image meta-data associated with a specific job.
	 */
	public List<ImageMetadataEntity> findImageMetadata(long jobInstanceId);

	/**
	 * Fetch the meta-data for a specific image.
	 * @param key the primary key of the entity.
	 * @return the meta-data for the specified image, or null if not found
	 */
	public ImageMetadataEntity findImageMetadata(ImageMetadataEntityKey key);

	/**
	 * Persist a single meta-data entity to the image meta-data table.
	 * @param metadata domain object for metadata
	 * @param jobInstanceId used to associate metadata to a specific job run
	 * @param titleId the unique key for the book for which we are gathering images
	 */

	public ImageMetadataEntityKey saveImageMetadata(final ImageMetadataEntity metadata);
	/**
	 * Persist the image meta-data from the REST service response to a database table
	 * @param metadata the meta-data container returned from the Image Vertical REST service
	 * @param jobInstanceId uniquely identifies the book generating job so meta-data can be stored in the database grouped by job
	 * @param titleId the unique key for the book for which we are gathering images
	 * @return the primary key of the created record.
	 */
	public ImageMetadataEntityKey saveImageMetadata(final SingleImageMetadataResponse metadata, long jobInstanceId, String titleId);
	
}
