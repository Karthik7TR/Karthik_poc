/*
 * Copyright 2016: Thomson Reuters Global Resources. All Rights Reserved.
 * Proprietary and Confidential information of TRGR. Disclosure, Use or
 * Reproduction without the written authorization of TRGR is prohibited
 */

package com.thomsonreuters.uscl.ereader.gather.image.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
 import org.apache.log4j.LogManager; import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.http.MediaType;
import org.springframework.transaction.annotation.Transactional;

import com.thomsonreuters.uscl.ereader.gather.image.dao.ImageDao;
import com.thomsonreuters.uscl.ereader.gather.image.domain.ImageException;
import com.thomsonreuters.uscl.ereader.gather.image.domain.ImageMetadataEntity;
import com.thomsonreuters.uscl.ereader.gather.image.domain.ImageMetadataEntityKey;
import com.thomsonreuters.uscl.ereader.gather.util.ImgMetadataInfo;

public class ImageServiceImpl implements ImageService {
	 private static final Logger log = LogManager.getLogger(ImageServiceImpl.class);
	
	
	/** The DAO for persisting image meta-data */
	private ImageDao imageDao;
	
	private File staticContentDirectory;

	@Required
	public void setStaticContentDirectory(File staticContentDirectory) {
		this.staticContentDirectory = staticContentDirectory;
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
	public static ImageMetadataEntity createImageMetadataEntity(ImgMetadataInfo imgMetadataInfo,
																long jobInstanceId, String titleId) {
		
		ImageMetadataEntityKey pk = new ImageMetadataEntityKey(jobInstanceId, imgMetadataInfo.getImgGuid(), imgMetadataInfo.getDocGuid());
		MediaType mediaType = MediaType.valueOf(imgMetadataInfo.getMimeType());
		
		// Create the entity that will be persisted
		ImageMetadataEntity entity = new ImageMetadataEntity(pk, titleId,
				imgMetadataInfo.getWidth(),
				imgMetadataInfo.getHeight(),
				imgMetadataInfo.getSize(),
				imgMetadataInfo.getDpi(),
				imgMetadataInfo.getDimUnit(),
				mediaType);
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
	public ImageMetadataEntityKey saveImageMetadata(final ImgMetadataInfo imgMetadataInfo, long jobInstanceId, String titleId) {
		ImageMetadataEntity entity = createImageMetadataEntity(imgMetadataInfo, jobInstanceId, titleId);
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
		File staticImageFile = new File(staticContentDirectory.getAbsolutePath()+"/images", basename);
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
	
	public static void copyFile(File sourceFile, File destFile) throws IOException {
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
	
	/**
	 * This method gives document Image mapping for a jobInstanceId.
	 * @param jobInstanceId
	 * @return mapping all the Images corresponding to a document
	 */
	@Override
	public Map<String, List<String>> getDocImageListMap(Long jobInstanceId)
	{
		List<ImageMetadataEntity> imageMetadataEntityList = findImageMetadata(jobInstanceId);
		Map<String, List<String>> mapping = new HashMap<String, List<String>>();
		for (ImageMetadataEntity imageMetadataEntity : imageMetadataEntityList)
		{
			ImageMetadataEntityKey primaryKey = imageMetadataEntity.getPrimaryKey();
			   String key = primaryKey.getDocUuid();
			   //img holds file name. IMG guid + mediatype (application/pdf)
			   String img = primaryKey.getImageGuid()+"."+StringUtils.substringAfterLast(imageMetadataEntity.getContentType(), "/");
			   List<String> value = new ArrayList<String>();
			   
			   if (mapping.containsKey(key)){
				   value = mapping.get(key);
				   mapping.put(key,value);
			   }				   
			   value.add(img);
			   mapping.put(key,value);
		}
		
		return mapping;
	}

	
	@Required
	public void setImageDao(ImageDao dao) {
		this.imageDao = dao;
	}

}
