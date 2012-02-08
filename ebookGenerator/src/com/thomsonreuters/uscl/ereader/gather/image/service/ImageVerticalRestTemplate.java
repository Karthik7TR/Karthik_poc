/*
* Copyright 2011: Thomson Reuters Global Resources. All Rights Reserved.
* Proprietary and Confidential information of TRGR. Disclosure, Use or
* Reproduction without the written authorization of TRGR is prohibited
*/
package com.thomsonreuters.uscl.ereader.gather.image.service;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

/**
 * A RestTemplate used to make HTTP request to the Image Vertical REST web service for single images
 * and read back (download) the bytes that IV serves up.  This is not used for fetching meta-data.
 */
public class ImageVerticalRestTemplate extends RestTemplate {
	
//	private static final Logger log = Logger.getLogger(ImageVerticalRestTemplate.class);
	
	/**
	 * @param imageDirectory target directory to place the imageGuid.subtype image file.
	 * @param imageGuid key of the image we are after
	 * @param desiredMediaType the mime type specified in the HTTP request header.  Indicates the media type
	 * that is expected back from the REST service.  This implies that any image conversion (say TIF to PNG)
	 * occurs within the Image Vertical service.  May be null to indicate that no Accept header is to be set.
	 */
	public ImageVerticalRestTemplate(File imageDirectory, String imageGuid, MediaType desiredMediaType) {
		// Assign the message converter that reads the image bytes from the response and creates a flat image file
		SingleImageResponseHttpMessageConverter imageDownloader = new SingleImageResponseHttpMessageConverter(imageDirectory, imageGuid, desiredMediaType);
		List<HttpMessageConverter<?>> messageConverters = new ArrayList<HttpMessageConverter<?>>(1);
		messageConverters.add(imageDownloader);
		super.setMessageConverters(messageConverters);
		
		// Assign the intercepter that sets the "Accept" header in the HTTP request made to the Image Vertical REST service
//log.debug("desiredMediaType="+desiredMediaType);
		if (desiredMediaType != null) {
			List<ClientHttpRequestInterceptor> interceptors = new ArrayList<ClientHttpRequestInterceptor>(1);
			interceptors.add(new ImageVerticalHttpRequestInterceptor(desiredMediaType));
			super.setInterceptors(interceptors);
		}
	}
}
