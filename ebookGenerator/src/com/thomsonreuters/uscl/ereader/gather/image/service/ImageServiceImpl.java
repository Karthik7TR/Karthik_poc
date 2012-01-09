/*
* Copyright 2011: Thomson Reuters Global Resources. All Rights Reserved.
* Proprietary and Confidential information of TRGR. Disclosure, Use or
* Reproduction without the written authorization of TRGR is prohibited
*/
package com.thomsonreuters.uscl.ereader.gather.image.service;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.List;

import org.springframework.beans.factory.annotation.Required;
import org.springframework.http.MediaType;
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
	public static String SINGLE_IMAGE_URL_PATTERN = "{context}/{version}/images/ttype/null/guid/{imageGuid}";
	public static String SINGLE_IMAGE_METADATA_URL_PATTERN = "{context}/{version}/images/ttype/null/guid/{imageGuid}/meta";
	
	private ImageVerticalRestTemplateFactory imageVerticalRestTemplateFactory;
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

		// Iterate the image GUID's and fetch the image bytes and metadata for each
		for (String imageGuid : imageGuids) {
			// First, fetch the image meta-data
			SingleImageMetadata imageMetadata = null;
			try {
				// Fetch the image meta-data and persist it to the database
				SingleImageMetadataResponse metadataContainer = fetchImageMetadata(imageGuid);
				ServiceStatus serviceStatus = metadataContainer.getServiceStatus();
				imageMetadata = metadataContainer.getImageMetadata();
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
				// Create the REST template we will use to make HTTP request to Image Vertical REST service
				MediaType mediaType = MediaType.valueOf(imageMetadata.getMimeType());
				ImageVerticalRestTemplate imageVerticalRestTemplate = imageVerticalRestTemplateFactory.create(imageDirectory, imageGuid, mediaType);
				// Invoke the Image Vertical REST web service to GET a single image byte stream, and read/store the response byte stream to a file.
				// The actual reading/saving of the image bytes is done in the SingleImageMessageHttpMessageConverter which is injected into our custom REST template.
				
				imageVerticalRestTemplate.getForObject(SINGLE_IMAGE_URL_PATTERN,
						SingleImageResponse.class,
						imageVerticalRestServiceUrl.toString(), urlVersion, imageGuid);
				
				// Intentionally pause between invocations of the Image Vertical REST service as not to pound on it
				Thread.sleep(sleepIntervalBetweenImages);
			} catch (Exception e) {
				throw new ImageException(String.format("Error fetching image from Image Vertical: imageGuid=%s", imageGuid), e);
			}
		}
	}

	@Override
	public SingleImageMetadataResponse fetchImageMetadata(String imageGuid) {
		SingleImageMetadataResponse response = singletonRestTemplate.getForObject(SINGLE_IMAGE_METADATA_URL_PATTERN,
				SingleImageMetadataResponse.class, 
				imageVerticalRestServiceUrl.toString(), urlVersion, imageGuid);
		return response;
	}

	@Override
	@Transactional
	public List<ImageMetadataEntity> findImageMetadata(long jobInstanceId) {
		return imageDao.findImageMetadata(jobInstanceId);
	}

	/**
	 * Map the container data from the REST service into an entity that is persisted
	 * @param responseMetadata from the Image Vertical REST service
	 * @return the entity to be persisted to a database table
	 */
	public static ImageMetadataEntity createImageMetadataEntity(SingleImageMetadataResponse responseMetadata,
			long jobInstanceId, String titleId) {
		SingleImageMetadata singleImageMetadata = responseMetadata.getImageMetadata();
		ImageMetadataEntityKey pk = new ImageMetadataEntityKey(jobInstanceId, singleImageMetadata.getGuid());
		ImageMetadataEntity entity = new ImageMetadataEntity(pk, titleId,
				singleImageMetadata.getWidth(),
				singleImageMetadata.getHeight(),
				singleImageMetadata.getSize(),
				singleImageMetadata.getDpi(),
				singleImageMetadata.getDimUnit());
		return entity;
	}
	
	@Override
	@Transactional
	public ImageMetadataEntityKey saveImageMetadata(final ImageMetadataEntity metadata) {
		ImageMetadataEntityKey primaryKey = imageDao.saveImageMetadata(metadata);
		return primaryKey;
	}

	@Override
	@Transactional
	public ImageMetadataEntityKey saveImageMetadata(final SingleImageMetadataResponse metadataResponse, long jobInstanceId, String titleId) {
		ImageMetadataEntity entity = createImageMetadataEntity(metadataResponse, jobInstanceId, titleId);
		// Persist the image meta-data entity
		return this.saveImageMetadata(entity);
	}

	/**
	 * Create the empty image file (with no extension) that will hold the image bytes once the download begins.
	 * If the file already exists, it will be deleted and recreated.
	 * @param imageDir container directory for the image file
	 * @param imageGuid the key for the image itself
	 * @return the File object what will hold the image bytes, created in the filesystem
	 * @throws IOException on file creation error
	 */
//	public static File createEmptyImageFile(File imageDir, String imageGuid) throws IOException {
//		imageDir.mkdirs();
//		String imageFileBasename = imageGuid;
//		File imageFile = new File(imageDir, imageFileBasename);
//		if (imageFile.exists()) {
//			imageFile.delete();  // Delete any existing file
//		}
//		imageFile.createNewFile();	// Create a new empty file
//		return imageFile;
//	}

	@Required
	public void setImageVerticalRestTemplateFactory(ImageVerticalRestTemplateFactory factory) {
		this.imageVerticalRestTemplateFactory = factory;
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
