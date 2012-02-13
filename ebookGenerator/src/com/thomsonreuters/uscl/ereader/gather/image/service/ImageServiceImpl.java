/*
* Copyright 2011: Thomson Reuters Global Resources. All Rights Reserved.
* Proprietary and Confidential information of TRGR. Disclosure, Use or
* Reproduction without the written authorization of TRGR is prohibited
*/
package com.thomsonreuters.uscl.ereader.gather.image.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.channels.FileChannel;
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
	private long sleepIntervalBetweenImages;
	/** The DAO for persisting image meta-data */
	private ImageDao imageDao;

	@Override
	//@Transactional
	public void fetchImageVerticalImages(final List<String> imageGuids,
						File imageDestinationDirectory, long jobInstanceId, String titleId) throws ImageException {

		// Iterate the image GUID's and fetch the image bytes and metadata for each
		for (String imageGuid : imageGuids) {
			
			// First, fetch the image meta-data
			SingleImageMetadata imageMetadata = null;
			try {
				// Fetch the image meta-data and persist it to the database
				SingleImageMetadataResponse metadataResponse = fetchImageVerticalImageMetadata(imageGuid);
				ServiceStatus serviceStatus = metadataResponse.getServiceStatus();
				imageMetadata = metadataResponse.getImageMetadata();
				if (serviceStatus.getStatusCode() != 0) {
					throw new ImageException(String.format("Non-zero status code was returned from Image Vertical when fetching metadata: code=%d, description=%s",
							imageGuid, serviceStatus.getStatusCode(), serviceStatus.getDescription()));
				}
				saveImageMetadata(metadataResponse, jobInstanceId, titleId);
			} catch (Exception e) {
				// Remove all existing downloaded files on failure
				removeAllFilesInDirectory(imageDestinationDirectory);
				throw new ImageException(String.format("Error fetching image metadata from Image Vertical: imageGuid=%s", imageGuid), e);
			}
			
			// Second, download and save the image bytes to a file
			try {
				MediaType desiredMediaType = fetchDesiredMediaType(imageMetadata.getMediaType());
				// Create the REST template we will use to make HTTP request to Image Vertical REST service
				ImageVerticalRestTemplate imageVerticalRestTemplate = imageVerticalRestTemplateFactory.create(
													imageDestinationDirectory, imageGuid, desiredMediaType);
				// Invoke the Image Vertical REST web service to GET a single image byte stream, and read/store the response byte stream to a file.
				// The actual reading/saving of the image bytes is done in the SingleImageMessageHttpMessageConverter which is injected into our custom REST template.
				imageVerticalRestTemplate.getForObject(SINGLE_IMAGE_URL_PATTERN, SingleImageResponse.class,
						imageVerticalRestServiceUrl.toString(), urlVersion, imageGuid);
				
				// Intentionally pause between invocations of the Image Vertical REST service as not to pound on it
				Thread.sleep(sleepIntervalBetweenImages);
			} catch (Exception e) {
				// Remove all existing downloaded files on failure
				removeAllFilesInDirectory(imageDestinationDirectory);
				throw new ImageException(String.format("Error fetching image from Image Vertical: imageGuid=%s", imageGuid), e);
			}
		}
	}
	
	/**
	 * If the metadata content type is an image, then return a desired type of "image/png"
	 * otherwise return null which indicates to return it in whatever form it is sorted.
	 * This covers the case of application/png.
	 * @param metadataMediaType indicated content type from an image metadata request.
	 * @return
	 */
	public static MediaType fetchDesiredMediaType(MediaType metadataMediaType) {

		if (metadataMediaType == null) {
			return null;
		}
		/** TODO: this is the production implementation 
		return ("image".equals(metadataMediaType.getType())) ? 
				MediaType.IMAGE_PNG : metadataMediaType;
		*/
		
// TODO: DELETE this when TIF to PNG conversion works in the Image Vertical
return metadataMediaType;		// DELETE 	
	
	}

	@Override
	public SingleImageMetadataResponse fetchImageVerticalImageMetadata(String imageGuid) {
		SingleImageMetadataResponse response = singletonRestTemplate.getForObject(SINGLE_IMAGE_METADATA_URL_PATTERN,
				SingleImageMetadataResponse.class, 
				imageVerticalRestServiceUrl.toString(), urlVersion, imageGuid);
		return response;
	}
	
	@Override
	public void fetchStaticImages(final List<String> basenames, File imageDestinationDirectory) throws ImageException {
		
		// Iterate the list of image base names
		for (String basename : basenames) {
			File sourceFile = searchFileTree(basename);
			if (sourceFile == null) {
				throw new ImageException("Static image not found: " + basename);
			}
			File destFile = new File(imageDestinationDirectory, basename);
			try {
				copyFile(sourceFile, destFile);
			} catch (IOException e) {
				// Remove all existing destination dir files on failure
				removeAllFilesInDirectory(imageDestinationDirectory);
				throw new ImageException("Failed to copy static image file: " + sourceFile, e);
			}
		}
	}

	@Override
	@Transactional
	public List<ImageMetadataEntity> findImageMetadata(long jobInstanceId) {
		return imageDao.findImageMetadata(jobInstanceId);
	}
	
	@Override
	@Transactional
	public ImageMetadataEntity findImageMetadata(ImageMetadataEntityKey key) {
		return imageDao.findImageMetadataByPrimaryKey(key);
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
		// Convert the media type from say "image/tif" to "image/png" which reflect the image as we want it to be converted,
		// and as we expect it to be returned from the Image Vertical REST service.
		MediaType desiredMediaType = fetchDesiredMediaType(singleImageMetadata.getMediaType());
		
		// Create the entity that will be persisted
		ImageMetadataEntity entity = new ImageMetadataEntity(pk, titleId,
				singleImageMetadata.getWidth(),
				singleImageMetadata.getHeight(),
				singleImageMetadata.getSize(),
				singleImageMetadata.getDpi(),
				singleImageMetadata.getDimUnit(),
				desiredMediaType);
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
	 * Search the directory tree of static images for a file with the specified basename.
	 * @param basename the file basename of an image file
	 * @return the absolute path the the image file, or null if it was not found in the tree
	 */
	private File searchFileTree(String basename) {
		// TODO: implement this
		return new File("/nas/StaticImages", basename);
	}
	
	/**
	 * Delete all files in the specified directory.
	 * @param directory directory whose files will be removed
	 */
	public static void removeAllFilesInDirectory(File directory) {
		File[] files = directory.listFiles();
		for (File file : files) {
			file.delete();
		}
	}
	
	private static void copyFile(File sourceFile, File destFile) throws IOException {
		if (!destFile.exists()) {
			destFile.createNewFile();
		}
		FileChannel source = null;
		FileChannel destination = null;
		try {
		  source = new FileInputStream(sourceFile).getChannel();
		  destination = new FileOutputStream(destFile).getChannel();
		  destination.transferFrom(source, 0, source.size());
		} finally {
			if(source != null) {
				source.close();
			}
			if(destination != null) {
				destination.close();
			}
		}
	}

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
