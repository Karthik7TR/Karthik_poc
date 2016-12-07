/*
 * Copyright 2016: Thomson Reuters Global Resources. All Rights Reserved.
 * Proprietary and Confidential information of TRGR. Disclosure, Use or
 * Reproduction without the written authorization of TRGR is prohibited
 */
package com.thomsonreuters.uscl.ereader.gather.img.util;

import java.io.File;
import java.util.List;
import java.util.Map;

import org.jetbrains.annotations.NotNull;

/**
 * Utils to interact with Format/doc-to-image-manifest.txt
 * 
 * @author Ilia Bochkarev UC220946
 *
 */
public interface DocToImageManifestUtil {

	/**
	 * Get map containing list of image IDs for every doc ID
	 * 
	 * @param docToImageManifestFile
	 *            doc-to-image-manifest.txt file. Should exist.
	 * @return map with image IDs for every doc ID
	 */
	@NotNull
	Map<String, List<String>> getDocsWithImages(@NotNull File docToImageManifestFile);
}
