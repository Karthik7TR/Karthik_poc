/*
* Copyright 2011: Thomson Reuters Global Resources. All Rights Reserved.
* Proprietary and Confidential information of TRGR. Disclosure, Use or
* Reproduction without the written authorization of TRGR is prohibited
*/
package com.thomsonreuters.uscl.ereader.gather.image.service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.NotImplementedException;
import org.apache.log4j.Logger;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.AbstractHttpMessageConverter;

import com.thomsonreuters.uscl.ereader.gather.image.domain.SingleImageResponse;

/**
 * Image downloader that reads and saves the image data read from the HTTP response body after a request
 * to the Image Vertical REST service for a single image.
 * This is injected into the RestTemplate that is configured for use in communicating with the Image Vertical REST service.
 */
public class SingleImageResponseHttpMessageConverter extends
		AbstractHttpMessageConverter<SingleImageResponse> {
	private static final Logger log = Logger.getLogger(SingleImageResponseHttpMessageConverter.class);
	public static final String IMAGE_SUFFIX = ".png";
	private static List<MediaType> SUPPORTED_MEDIA_TYPES = new ArrayList<MediaType>();
	static {
		SUPPORTED_MEDIA_TYPES.add(MediaType.IMAGE_PNG);
	}
	/** The destination for the downloaded image */
	private File imageFile;
	
	public SingleImageResponseHttpMessageConverter(File imageDirectory, String imageGuid, MediaType mediaType) {
		this.imageFile = createImageFile(imageDirectory, imageGuid, mediaType);
log.debug("ImageFile: " + imageFile.getAbsolutePath()); // DEBUG
	}
	
	@Override
	public boolean canRead(Class<?> clazz, MediaType mediaType) {
		if (mediaType != null) {
			String type = mediaType.getType();  // like "image"
			return (supports(clazz) && type.equals("image"));
		}
		return false;
	}
	
	@Override
	public boolean canWrite(Class<?> clazz, MediaType mediaType) {
		return false;  // This is not an uploader
	}
	
	@Override
	public List<MediaType> getSupportedMediaTypes() {
		return SUPPORTED_MEDIA_TYPES;
	}
	
	/**
	 * Reads the body of the HTTP request to the Image Vertical REST web service.
	 * The body is assumed to be an image that will be written to an output file in the image directory.
	 */
	@Override
	public SingleImageResponse readInternal(Class<? extends SingleImageResponse> clazz,
											HttpInputMessage inputMessage) throws IOException {
		InputStream inStream = null;
		FileOutputStream fileStream = null;
		if (imageFile.exists()) {
			imageFile.delete();  // Delete any existing file
		}
		imageFile.createNewFile();	// Create a new empty file
		try {
			inStream = inputMessage.getBody();
			fileStream = new FileOutputStream(imageFile);
			byte[] contentBuffer = new byte[2^14];
			int bytesAvailableToRead;
			while ((bytesAvailableToRead = inStream.available()) > 0) {
				int bytesToRead = Math.min(contentBuffer.length, bytesAvailableToRead);
				int bytesRead = inStream.read(contentBuffer, 0, bytesToRead);
				fileStream.write(contentBuffer, 0, bytesRead);
			}
		} finally {
			if (fileStream != null) {
				fileStream.close();
			}
			if (inStream != null) {
				inStream.close();
			}
		}
		return new SingleImageResponse(imageFile);
	}
	
	public static File createImageFile(File imageDirectory, String imageGuid, MediaType mediaType) {
		String extension = "." + mediaType.getSubtype();  // like ".png"
		File imageFile = new File(imageDirectory, imageGuid + extension); 
		return imageFile;
	}
	
	@Override
	public boolean supports(Class<?> clazz) {
		 return SingleImageResponse.class.equals(clazz);
	}

	@Override
	public void writeInternal(SingleImageResponse obj, HttpOutputMessage outputMessage) {
		 throw new NotImplementedException("No HTTP request body for an Image Vertical GET single image request.");
	}
}
