package com.thomsonreuters.uscl.ereader.gather.image.service;

import java.io.File;

import org.springframework.beans.factory.annotation.Required;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpRequestFactory;

/**
 * Create a REST template object that will be used to download images from the Image Vertical REST service.
 * This is done as a factory because we need to create a mock REST template when we unit test the ImageService.
 */
public class ImageVerticalRestTemplateFactoryImpl implements ImageVerticalRestTemplateFactory {
	
	/** Used to establish a HTTP read timeout */
	private ClientHttpRequestFactory requestFactory;
	
	@Override
	public ImageVerticalRestTemplate create(File imageDirectory, String imageGuid, MediaType desiredMediaType) {
		return new ImageVerticalRestTemplate(requestFactory, imageDirectory, imageGuid, desiredMediaType);
	}
	
	@Required
	public void setRequestFactory(ClientHttpRequestFactory factory) {
		this.requestFactory = factory;
	}
}
