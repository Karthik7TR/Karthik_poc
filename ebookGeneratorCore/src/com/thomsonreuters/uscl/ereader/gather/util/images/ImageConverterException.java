/*
 * Copyright 2016: Thomson Reuters Global Resources. All Rights Reserved.
 * Proprietary and Confidential information of TRGR. Disclosure, Use or
 * Reproduction without the written authorization of TRGR is prohibited
 */
package com.thomsonreuters.uscl.ereader.gather.util.images;

public class ImageConverterException extends Exception {

	private static final long serialVersionUID = 7270462432999665730L;
	
	public ImageConverterException(String message) {
		super(message);
	}
	
	public ImageConverterException(Throwable t) {
		super(t);
	}

}
