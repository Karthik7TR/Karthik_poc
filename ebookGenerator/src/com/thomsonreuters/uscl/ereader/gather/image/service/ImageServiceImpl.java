/*
* Copyright 2011: Thomson Reuters Global Resources. All Rights Reserved.
* Proprietary and Confidential information of TRGR. Disclosure, Use or
* Reproduction without the written authorization of TRGR is prohibited
*/
package com.thomsonreuters.uscl.ereader.gather.image.service;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import com.thomsonreuters.uscl.ereader.gather.image.dao.ImageDao;
import com.thomsonreuters.uscl.ereader.gather.image.domain.ImageMetadataEntity;
import com.thomsonreuters.uscl.ereader.gather.image.domain.SingleImageMetadata;
import com.thomsonreuters.uscl.ereader.gather.image.domain.SingleImageMetadataResponse;
import com.thomsonreuters.uscl.ereader.gather.image.domain.SingleImageResponse;

public class ImageServiceImpl implements ImageService {
	private static final Logger log = Logger.getLogger(ImageServiceImpl.class);
	
	/** The singleton REST template with the static message converters injected. */
	private RestTemplate singletonRestTemplate;
	/** URL (environment specific) of the Image Vertical (REST) service. */
	private URL imageVerticalRestServiceUrl;
	/** The dynamic version number, like "v1", used in the REST service request URL. */
	private String urlVersion;
	private ImageDao imageDao;

	@Override
	@Transactional
	public void fetchImages(final List<String> imageGuids, File imageDirectory, long jobInstanceId) throws Exception {
		// Set up the REST template with the message converter that reads the image bytes
		RestTemplate customRestTemplate = new RestTemplate();
		SingleImageResponseHttpMessageConverter imageReader = new SingleImageResponseHttpMessageConverter();
		List<HttpMessageConverter<?>> messageConverters = new ArrayList<HttpMessageConverter<?>>(1);
		messageConverters.add(imageReader);
		customRestTemplate.setMessageConverters(messageConverters);
		
		// Iterate the image GUID's and fetch the image bytes and metadata for each
		for (String imageGuid : imageGuids) {
			
			// Fetch the image meta-data and persist it to the database
			SingleImageMetadataResponse metadata = fetchImageMetadata(imageGuid);
log.debug("IV: " + metadata);			
			saveImageMetadata(metadata, jobInstanceId);
			
			// Intentionally pause between invocations of the Image Vertical REST service as not to pound on it
			Thread.sleep(500);
			
			// Set up and create an empty image file to hold the bytes read from the REST service response
			File imageFile = createEmptyImageFile(imageDirectory, imageGuid);
			imageReader.setImageFile(imageFile);  // Set the image file name into the message converter that reads the image bytes
log.debug("IV: " + imageFile);			

			// Invoke the Image Vertical REST web service to GET a single image byte stream, and read/store the response byte stream to a file.
			// The actual reading/saving of the image bytes is done in the SingleImageMessageHttpMessageConverter which is injected into our custom REST template.
			String restServiceUrl = String.format(
					"%s/%s/images/ttype/null/guid/%s", imageVerticalRestServiceUrl.toString(), urlVersion, imageGuid);
			customRestTemplate.getForObject(restServiceUrl, SingleImageResponse.class);
		}
	}

	@Override
	public SingleImageMetadataResponse fetchImageMetadata(String imageGuid) {
		String restServiceUrl = String.format(
		"%s/%s/images/ttype/null/guid/%s/meta", imageVerticalRestServiceUrl.toString(), urlVersion, imageGuid);
		SingleImageMetadataResponse response = singletonRestTemplate.getForObject(restServiceUrl, SingleImageMetadataResponse.class);
		return response;
	}
	
	@Override
	@Transactional
	public void saveImageMetadata(final SingleImageMetadataResponse metadata, long jobInstanceId) {
		// Map the container data from the REST service into an entity that is persisted
		SingleImageMetadata singleImageMetadata = metadata.getImageMetadata();
		ImageMetadataEntity entity = new ImageMetadataEntity(jobInstanceId, singleImageMetadata.getGuid(),
															"TODO_titleId", 
															singleImageMetadata.getWidth(),
															singleImageMetadata.getHeight(),
															singleImageMetadata.getSize(),
															singleImageMetadata.getDpi(),
															null); // singleImageMetadata.getDimUnits());
		// Persist the entity
		this.saveImageMetadata(entity, jobInstanceId);
	}

	@Override
	@Transactional
	public void saveImageMetadata(final ImageMetadataEntity metadata, long jobInstanceId) {
		imageDao.saveImageMetadata(metadata);
	}
	
	public List<ImageMetadataEntity> findImageMetadata(long jobInstanceId) {
		return null;  // TODO
	}


	/**
	 * Create the empty image file that will hold the image bytes once the download begins.
	 * If the file already exists, it will be deleted and recreated.
	 * @param imageDir container directory for the image file
	 * @param imageGuid the key for the image itself
	 * @return the File object what will hold the image bytes, created in the filesystem
	 * @throws IOException on file creation error
	 */
	private static File createEmptyImageFile(File imageDir, String imageGuid) throws IOException {
		imageDir.mkdirs();
		String imageFileBasename = imageGuid + ".png";
		File imageFile = new File(imageDir, imageFileBasename);
log.debug(imageFile); // DEBUG		
		if (imageFile.exists()) {
			imageFile.delete();  // Delete any existing file
		}
		imageFile.createNewFile();	// Create a new empty file
		return imageFile;
	}
	
	@Required
	public void setSingletonRestTemplate(RestTemplate template) {
		this.singletonRestTemplate = template;
	}
	@Required
	public void setImageVerticalRestServiceUrl(URL url) {
		this.imageVerticalRestServiceUrl = url;
	}
	@Required
	public void setUrlVersion(String ver) {
		this.urlVersion = ver;
	}
	@Required
	public void setImageDao(ImageDao dao) {
		this.imageDao = dao;
	}

}
