/*
* Copyright 2011: Thomson Reuters Global Resources. All Rights Reserved.
* Proprietary and Confidential information of TRGR. Disclosure, Use or
* Reproduction without the written authorization of TRGR is prohibited
*/
package com.thomsonreuters.uscl.ereader.gather.image.domain;

import java.io.File;

/**
 * The ResetTemplate response object, since the Image Service response for a single image request
 * is a stream of image bytes that is saved to a file.
 * This class is used in the HttpMessageConverter to report back what file was created.
 */
public class SingleImageResponse {
	
	/** The name of the image file created from the Image Vertical service single image HTTP GET request. */
	private File imageFile;
	
	public SingleImageResponse(File file) {
		this.imageFile = file;
	}
	
	public File getImageFile() {
		return imageFile;
	}
}
