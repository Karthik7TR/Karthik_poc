/*
* Copyright 2011: Thomson Reuters Global Resources. All Rights Reserved.
* Proprietary and Confidential information of TRGR. Disclosure, Use or
* Reproduction without the written authorization of TRGR is prohibited
*/
package com.thomsonreuters.uscl.ereader.gather.service;

import java.io.File;
import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Required;
import org.springframework.web.client.RestTemplate;

public class ImageServiceImpl implements ImageService {
	
	/** Communications with the Image Vertical REST web service */
	private RestTemplate imageVerticalRestTemplate;

	@Override
	public List<?> fetchImages(String guid, File destinationDirectory) {
// TODO: implement this	stub
		return Collections.EMPTY_LIST;
	}
	
	@Required
	public void setRestTemplate(RestTemplate restTemplate) {
		this.imageVerticalRestTemplate = restTemplate;
	}
}
