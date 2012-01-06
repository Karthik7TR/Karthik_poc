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

import org.springframework.beans.factory.annotation.Required;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import com.thomsonreuters.uscl.ereader.gather.image.dao.ImageDao;
import com.thomsonreuters.uscl.ereader.gather.image.domain.ImageException;
import com.thomsonreuters.uscl.ereader.gather.image.domain.ImageMetadataEntity;
import com.thomsonreuters.uscl.ereader.gather.image.domain.ImageMetadataEntityKey;
import com.thomsonreuters.uscl.ereader.gather.image.domain.ServiceStatus;
import com.thomsonreuters.uscl.ereader.gather.image.domain.SingleImageMetadata;
import com.thomsonreuters.uscl.ereader.gather.image.domain.SingleImageMetadataResponse;
import com.thomsonreuters.uscl.ereader.gather.image.domain.SingleImageResponse;

public class ImageServiceImpl implements ImageService {
	// private static final Logger log = Logger.getLogger(ImageServiceImpl.class);
	
	/** The singleton REST template with the static message converters injected.  Used for reading the meta-data */
	private RestTemplate singletonRestTemplate;
	/** URL (environment specific) of the Image Vertical (REST) service. */
	private URL imageVerticalRestServiceUrl;
	/** The dynamic version number, like "v1", used in the REST service request URL. */
	private String urlVersion;
	/** Milliseconds to sleep between each meta-data/bytes fetch */
	private long sleepIntervalBetweenImages;  //
	/** The DAO for persisting image meta-data */
	private ImageDao imageDao;

	@Override
	@Transactional
	public void fetchImages(final List<String> imageGuids, File imageDirectory, long jobInstanceId, String titleId)
						throws ImageException {
		// Set up the REST template with the message converter that reads the image bytes
		RestTemplate customRestTemplate = new RestTemplate();
		SingleImageResponseHttpMessageConverter imageDownloader = new SingleImageResponseHttpMessageConverter();
		List<HttpMessageConverter<?>> messageConverters = new ArrayList<HttpMessageConverter<?>>(1);
		messageConverters.add(imageDownloader);
		customRestTemplate.setMessageConverters(messageConverters);
		
		// Iterate the image GUID's and fetch the image bytes and metadata for each
		for (String imageGuid : imageGuids) {
			
			// First, fetch the image meta-data
			try {
				// Fetch the image meta-data and persist it to the database
				SingleImageMetadataResponse metadataContainer = fetchImageMetadata(imageGuid);
				ServiceStatus serviceStatus = metadataContainer.getServiceStatus();
				if (serviceStatus.getStatusCode() != 0) {
					throw new ImageException(String.format("Non-zero status code was returned from Image Vertical when fetching metadata: code=%d, description=%s",
							imageGuid, serviceStatus.getStatusCode(), serviceStatus.getDescription()));
				}
				saveImageMetadata(metadataContainer, jobInstanceId, titleId);
			} catch (Exception e) {
				throw new ImageException(e);
			}
			
			// Second, fetch and save the image bytes to a file
			try {
				// Set up and create an empty image file to hold the bytes read from the REST service response
				File imageFile = createEmptyImageFile(imageDirectory, imageGuid);
				imageDownloader.setImageFile(imageFile);  // Set the image file name into the message converter that reads the image bytes			
	
				// Invoke the Image Vertical REST web service to GET a single image byte stream, and read/store the response byte stream to a file.
				// The actual reading/saving of the image bytes is done in the SingleImageMessageHttpMessageConverter which is injected into our custom REST template.
				String restServiceUrl = String.format(
						"%s/%s/images/ttype/null/guid/%s", imageVerticalRestServiceUrl.toString(), urlVersion, imageGuid);
				customRestTemplate.getForObject(restServiceUrl, SingleImageResponse.class);
				
				// Intentionally pause between invocations of the Image Vertical REST service as not to pound on it
				Thread.sleep(sleepIntervalBetweenImages);
			} catch (Exception e) {
				throw new ImageException(String.format("Error fetching image from Image Vertical: imageGuid=%s", imageGuid), e);
			}
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
	public void saveImageMetadata(final SingleImageMetadataResponse metadata, long jobInstanceId, String titleId) {
		// Map the container data from the REST service into an entity that is persisted
		SingleImageMetadata singleImageMetadata = metadata.getImageMetadata();
		ImageMetadataEntityKey pk = new ImageMetadataEntityKey(jobInstanceId, singleImageMetadata.getGuid());
		ImageMetadataEntity entity = new ImageMetadataEntity(pk, titleId,
															 singleImageMetadata.getWidth(),
															 singleImageMetadata.getHeight(),
															 singleImageMetadata.getSize(),
															 singleImageMetadata.getDpi(),
															 singleImageMetadata.getDimUnit());
		// Persist the image meta-data entity
		this.saveImageMetadata(entity);
	}

	@Override
	@Transactional
	public void saveImageMetadata(final ImageMetadataEntity metadata) {
		imageDao.saveImageMetadata(metadata);
	}
	
	@Override
	@Transactional
	public List<ImageMetadataEntity> findImageMetadata(long jobInstanceId) {
		return imageDao.findImageMetadata(jobInstanceId);
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
	public void setSleepIntervalBetweenImages(long interval) {
		this.sleepIntervalBetweenImages = interval;
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
