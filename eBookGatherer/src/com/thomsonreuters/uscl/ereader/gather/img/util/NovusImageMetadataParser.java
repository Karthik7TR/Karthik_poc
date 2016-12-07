/*
 * Copyright 2016: Thomson Reuters Global Resources. All Rights Reserved.
 * Proprietary and Confidential information of TRGR. Disclosure, Use or
 * Reproduction without the written authorization of TRGR is prohibited
 */
package com.thomsonreuters.uscl.ereader.gather.img.util;

import org.jetbrains.annotations.NotNull;

import com.thomsonreuters.uscl.ereader.gather.util.ImgMetadataInfo;

/**
 * Parser to map Novus image metadata to object
 * @author Ilia Bochkarev UC220946
 *
 */
public interface NovusImageMetadataParser {

	/**
	 * Get metadata info from Novus
	 * @param metadata string representation of metadata
	 * @return metadata object
	 */
	@NotNull
	ImgMetadataInfo parse(@NotNull String metadata);
}
