/*
 * Copyright 2016: Thomson Reuters Global Resources. All Rights Reserved.
 * Proprietary and Confidential information of TRGR. Disclosure, Use or
 * Reproduction without the written authorization of TRGR is prohibited
 */
package com.thomsonreuters.uscl.ereader.gather.util.images;

public interface ImageConverter {
	void convertByteImg(byte[] imgBytes, String outputImagePath, String formatName) throws ImageConverterException;
}