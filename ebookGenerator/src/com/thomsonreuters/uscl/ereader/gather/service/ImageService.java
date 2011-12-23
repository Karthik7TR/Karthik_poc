/*
* Copyright 2011: Thomson Reuters Global Resources. All Rights Reserved.
* Proprietary and Confidential information of TRGR. Disclosure, Use or
* Reproduction without the written authorization of TRGR is prohibited
*/
package com.thomsonreuters.uscl.ereader.gather.service;

import java.io.File;
import java.util.List;

public interface ImageService {
	
	/**
	 * Get physical images files for the book based on the specified GUID.
	 * @param guid key for the images
	 * @param destinatinDirectory filesystem directory where the images files will be placed
	 * @return 
	 */
	public List<?> fetchImages(String guid, File destinationDirectory);  // TODO: what does the Image Vertical REST service look like?
	
}
