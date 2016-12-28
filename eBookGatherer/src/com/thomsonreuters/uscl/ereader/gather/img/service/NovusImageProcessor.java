/*
 * Copyright 2016: Thomson Reuters Global Resources. All Rights Reserved.
 * Proprietary and Confidential information of TRGR. Disclosure, Use or
 * Reproduction without the written authorization of TRGR is prohibited
 */
package com.thomsonreuters.uscl.ereader.gather.img.service;

import java.util.List;

import org.jetbrains.annotations.NotNull;

import com.thomsonreuters.uscl.ereader.gather.util.ImgMetadataInfo;

/**
 * Designed to perform all tasks with images form Novus and decouple
 * {@link com.thomsonreuters.uscl.ereader.gather.img.service.NovusImageService}
 * from low-level image logic
 * 
 * @author Ilia Bochkarev UC220946
 *
 */
public interface NovusImageProcessor extends AutoCloseable {

	/**
	 * Do all the tasks with one image (e.g. get from Novus, add to collection,
	 * write to file if necessary, etc.)
	 * 
	 * @param imageId
	 * @param docId
	 */
	void process(@NotNull String imageId, @NotNull String docId);

	/**
	 * Was this image already processed or not
	 * 
	 * @param imageId
	 *            image ID
	 * @param docId
	 *            doc ID
	 * @return processed flag
	 */
	boolean isProcessed(@NotNull String imageId, @NotNull String docId);

	/**
	 * Get metadata for all processed images
	 * 
	 * @return images metadata
	 */
	@NotNull
	List<ImgMetadataInfo> getImagesMetadata();

	/**
	 * Get number of images failed to get from process
	 * 
	 * @return number of missed images
	 */
	int getMissingImageCount();
}
