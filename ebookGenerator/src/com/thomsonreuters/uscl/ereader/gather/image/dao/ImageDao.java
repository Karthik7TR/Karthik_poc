/*
* Copyright 2011: Thomson Reuters Global Resources. All Rights Reserved.
* Proprietary and Confidential information of TRGR. Disclosure, Use or
* Reproduction without the written authorization of TRGR is prohibited
*/
package com.thomsonreuters.uscl.ereader.gather.image.dao;

import java.util.List;

import com.thomsonreuters.uscl.ereader.gather.image.domain.ImageMetadataEntity;
import com.thomsonreuters.uscl.ereader.gather.image.domain.ImageMetadataEntityKey;

public interface ImageDao {
	
	/**
	 * Find from the IMAGE_METADATA table all the image meta-data for a specific book generation job run.
	 * @param jobInstanceId unique job run identifier
	 * @return a list of metadata for every image.
	 */
	public List<ImageMetadataEntity> findImageMetadata(long jobInstanceId);
	
	/**
	 * Fetch image meta-data by its primary key.
	 * @param key compount key of jobInstanceId and the image GUID.
	 * @return the entity found, or null if not found
	 */
	public ImageMetadataEntity findImageMetadataByPrimaryKey(ImageMetadataEntityKey key);
	
	/**
	 * Persist a single set of image metadata to the IMAGE_METADATA table. 
	 * @param metadata mapped entity to save
	 * @return the key of new newly created record
	 */
	public ImageMetadataEntityKey saveImageMetadata(ImageMetadataEntity metadata);
}
