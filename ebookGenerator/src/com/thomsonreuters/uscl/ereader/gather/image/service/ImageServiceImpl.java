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
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.nio.channels.FileChannel;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
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
	 private static final Logger log = Logger.getLogger(ImageServiceImpl.class);
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
	
	private String missingImageGuidsFileBasename;  // like "missing_image_guids.txt"
	/** Image download failure retries before passing over this image and noting the guid in the missing image file */
	private int imageServiceMaxRetries;				
	/** image metadata fetch failure retries before giving up, guid is noted in missing image file */
	private int imageMetadataServiceMaxRetries;
	
	@Override
	public void fetchImageVerticalImages(final Map<String,String> imgDocGuidMap,
						File imageDestinationDirectory, long jobInstanceId, String titleId) throws Exception {
		File missingImagesFile = new File(imageDestinationDirectory.getParent(), missingImageGuidsFileBasename);
		FileOutputStream stream = new FileOutputStream(missingImagesFile);
		Writer fileWriter = new OutputStreamWriter(stream, "UTF-8");
		int missingImageCount = 0;
		int missingImageMetadataCount = 0;
		
		try {
			// Iterate the image GUID's and first fetch image data and then download the image bytes
			for (String docGuid : imgDocGuidMap.keySet()) {
				String imgGuidList = imgDocGuidMap.get(docGuid);
				if (imgGuidList != null)
				{
				   String[] imgDocsArray = imgGuidList.split(",");
				   for (String imgGuid : imgDocsArray)
				   {
					// First, fetch the image meta-data
						SingleImageMetadata imageMetadata = null;
						// Fetch the image meta-data and persist it to the database
						SingleImageMetadataResponse metadataResponse = fetchImageVerticalImageMetadata(imgGuid, fileWriter, docGuid);
						if (metadataResponse == null) {
							missingImageMetadataCount++;
							log.error(String.format("No image metadata was returned from Image Vertical for guid [%s], continuing on...", imgGuid));
							continue;
						}
						ServiceStatus serviceStatus = metadataResponse.getServiceStatus();
						if (serviceStatus.getStatusCode() == 0) { // success
							imageMetadata = metadataResponse.getImageMetadata();
							saveImageMetadata(metadataResponse, jobInstanceId, titleId);
						} else { // failure
							missingImageMetadataCount++;
							log.error(String.format("Status code %d (a failure) was returned from Image Vertical when fetching image metadata for guid [%s], error description: %s, continuing on...",
									  serviceStatus.getStatusCode(), imgGuid, serviceStatus.getDescription()));
							writeFailedImageGuidToFile(fileWriter, imgGuid, docGuid);
							continue;	// do not try and get the image if we could not get the metadata
						}

						// Second, download and save the image bytes to a file
						// Create the REST template we will use to make HTTP request to Image Vertical REST service
						MediaType desiredMediaType = fetchDesiredMediaType(imageMetadata.getMediaType());
						ImageVerticalRestTemplate imageVerticalRestTemplate = imageVerticalRestTemplateFactory.create(
															imageDestinationDirectory, imgGuid, desiredMediaType);
						// Invoke the Image Vertical REST web service to GET a single image byte stream, and read/store the response byte stream to a file.
						// The actual reading/saving of the image bytes is done in the SingleImageMessageHttpMessageConverter which is injected into our custom REST template.
						// This is the counter for checking how many Image service retries we
						// are making
						int failures = 0;
						int timeouts = 0;
						while (failures < imageServiceMaxRetries) {
							try {
								imageVerticalRestTemplate.getForObject(SINGLE_IMAGE_URL_PATTERN, SingleImageResponse.class,
										imageVerticalRestServiceUrl.toString(), urlVersion, imgGuid);			
								break;  // break out of retry loop
							} catch (Exception exception) {
								failures++;
								Throwable cause = exception.getCause();
								if ((cause != null) && (cause instanceof SocketTimeoutException)) {
									timeouts++;
									log.warn(String.format("Timeout #%d (maximum %d) has occurred while downloading image guid [%s]", timeouts, imageServiceMaxRetries, imgGuid));
									if (timeouts == imageServiceMaxRetries) {
										// Note: we are not recording image download timeouts to failed image guid file
										String errMesg = String.format("%d successive timeouts have occurred when attempting to download image guid [%s]", timeouts, imgGuid);
										log.error(errMesg);
										throw new ImageException(errMesg);
									}
								}
								if (failures == imageServiceMaxRetries) {  // fail after max successive errors
									missingImageCount++;
									log.error(String.format("Image download failed after %d successive failure retries for guid [%s], continuing on...", imageServiceMaxRetries, imgGuid));
									writeFailedImageGuidToFile(fileWriter, imgGuid, docGuid);
								}						
							}
						}								

						// Intentionally pause between invocations of the Image Vertical REST service as not to pound on it
						Thread.sleep(sleepIntervalBetweenImages);
					
					} // end of for-loop
				  }
			}	// end of for-loop
			if ((missingImageCount > 0) || (missingImageMetadataCount > 0)) {
				throw new ImageException(String.format("Download of dynamic images failed because there was %d missing image(s) and %d missing metadata.", missingImageCount, missingImageMetadataCount));
			}	
		} finally {
			fileWriter.close();
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
		
		return ("image".equals(metadataMediaType.getType())) ? 
				MediaType.IMAGE_PNG : metadataMediaType;
	}

	@Override
	public SingleImageMetadataResponse fetchImageVerticalImageMetadata(String imageGuid, Writer missingImageFileWriter, String docGuid) throws IOException {
		SingleImageMetadataResponse response = null;
		int failures = 0;		
		while (failures < imageMetadataServiceMaxRetries) {
			try {
				response = singletonRestTemplate.getForObject(SINGLE_IMAGE_METADATA_URL_PATTERN,
						SingleImageMetadataResponse.class, 
						imageVerticalRestServiceUrl.toString(), urlVersion, imageGuid);				
				break;  // break out of retry loop
			} catch (Exception exception) {
				failures++;
				if (failures == imageMetadataServiceMaxRetries) {
					writeFailedImageGuidToFile(missingImageFileWriter, imageGuid, docGuid);
				}
			}
		}
		return response;
	}
	
	private static void writeFailedImageGuidToFile(Writer missingImageFileWriter, String imageGuid, String docGuid) throws IOException {
		missingImageFileWriter.write(imageGuid + "," + docGuid);
		missingImageFileWriter.write("\n");
	}
	
	@Override
	public void fetchStaticImages(final List<String> basenames, File imageDestinationDirectory) throws ImageException {
		
		// Iterate the list of image base names
		for (String basename : basenames) {
			File sourceFile = searchFileTree(basename);
			if (sourceFile == null) {
				throw new ImageException("Static image not found: " + basename);
			}
			String destFileName = basename;
			if (destFileName.contains("/"))
			{
				destFileName = destFileName.substring(destFileName.lastIndexOf("/") + 1);
			}
			File destFile = new File(imageDestinationDirectory, destFileName);
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
	 * Search the directory tree of static images for a file with the specified
	 * basename.
	 * 
	 * @param basename
	 *            the file basename of an image file
	 * @return the absolute path the the image file, or null if it was not found
	 *         in the tree
	 */
	private File searchFileTree(String basename) {
		File staticImageFile = new File(
				"/apps/eBookBuilder/staticContent/images", basename);
		if (staticImageFile.exists()) {
			return staticImageFile;
		} else {
			return null;
		}
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

	@Required
	public void setMissingImageGuidsFileBasename(String basename) {
		this.missingImageGuidsFileBasename = basename;
	}
	@Required
	public void setImageServiceMaxRetries(int max) {
		this.imageServiceMaxRetries = max;
	}
	@Required
	public void setImageMetadataServiceMaxRetries(int max) {
		this.imageMetadataServiceMaxRetries = max;
	}
}
