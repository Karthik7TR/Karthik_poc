package com.thomsonreuters.uscl.ereader.gather.image.service;

import java.io.File;

import org.springframework.http.MediaType;

public interface ImageVerticalRestTemplateFactory {

	/**
	 * Create a REST template object that will be used to download images from the Image Vertical REST service.
	 * This is done as a factory because of the dynamic media types and because we need to create a mock
	 * REST template when we unit test the ImageService.
	 * @param imageDirectory target directory for the image file
	 * @param imageGuid the image key
	 * @param desiredMediaType the value sent on the HTTP request Accept header, indicates what content type we expect
	 * in return.
	 */
	public ImageVerticalRestTemplate create(File imageDirectory, String imageGuid, MediaType desiredMediaType);
}
