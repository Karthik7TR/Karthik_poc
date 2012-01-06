package com.thomsonreuters.uscl.ereader.gather.image.service;

import java.io.File;

/**
 * Create a REST template object that will be used to download images from the Image Vertical REST service.
 * This is done as a factory because we need to create a mock REST template when we unit test the ImageService.
 */
public class ImageVerticalRestTemplateFactoryImpl implements ImageVerticalRestTemplateFactory {
	
	@Override
	public ImageVerticalRestTemplate create(File imageFileToCreate) {
		return new ImageVerticalRestTemplate(imageFileToCreate);
	}
}
