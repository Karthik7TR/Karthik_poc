package com.thomsonreuters.uscl.ereader.gather.image.service;

import java.io.File;

import org.springframework.http.MediaType;

public interface ImageVerticalRestTemplateFactory {

	/**
	 * Create a REST template object that will be used to download images from the Image Vertical REST service.
	 * This is done as a factory because we need to create a mock REST template when we unit test the ImageService.
	 */
	public ImageVerticalRestTemplate create(File imageDirectory, String imageGuid, MediaType mimeType);
}
