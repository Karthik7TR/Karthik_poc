/*
* Copyright 2011: Thomson Reuters Global Resources. All Rights Reserved.
* Proprietary and Confidential information of TRGR. Disclosure, Use or
* Reproduction without the written authorization of TRGR is prohibited
*/
package com.thomsonreuters.uscl.ereader.gather.image.service;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

/**
 * A RestTemplate used to make HTTP request to the Image Vertical REST web service for single images
 * and read back (download) the bytes that IV serves up.  This is not used for fetching meta-data.
 */
public class ImageVerticalRestTemplate extends RestTemplate {
	
	public ImageVerticalRestTemplate(File imageFileToCreate) {
		SingleImageResponseHttpMessageConverter imageDownloader = new SingleImageResponseHttpMessageConverter();
		imageDownloader.setImageFile(imageFileToCreate);  // Set the image file name into the message converter that reads the image bytes
		List<HttpMessageConverter<?>> messageConverters = new ArrayList<HttpMessageConverter<?>>(1);
		messageConverters.add(imageDownloader);
		super.setMessageConverters(messageConverters);
	}
}
