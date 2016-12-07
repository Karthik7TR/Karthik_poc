/*
 * Copyright 2016: Thomson Reuters Global Resources. All Rights Reserved.
 * Proprietary and Confidential information of TRGR. Disclosure, Use or
 * Reproduction without the written authorization of TRGR is prohibited
 */
package com.thomsonreuters.uscl.ereader.gather.img.service;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.PostConstruct;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Required;

import com.thomsonreuters.uscl.ereader.gather.img.model.NovusImage;
import com.thomsonreuters.uscl.ereader.gather.img.util.ImageConverter;
import com.thomsonreuters.uscl.ereader.gather.util.ImgMetadataInfo;

/**
 * Get images form Novus, write them to local folder and collect metadata
 * 
 * @author Ilia Bochkarev UC220946
 *
 */
public class NovusImageProcessorImpl implements NovusImageProcessor {
	private static final Logger Log = LogManager.getLogger(NovusImageProcessorImpl.class);

	private static final String PNG = "png";
	private static final String PNG_FORMAT = "PNG";

	private NovusImageFinder imageFinder;
	private File dynamicImageDirectory;
	private String missingImageGuidsFileBasename;
	private ImageConverter imageConverter;

	@NotNull
	private List<ImgMetadataInfo> imagesMetadata = new ArrayList<>();
	private Set<String> processed = new HashSet<>();
	private int missingImageCount = 0;
	private Writer missingImageFileWriter;

	@PostConstruct
	public void init() throws FileNotFoundException, UnsupportedEncodingException {
		File missingImagesFile = new File(dynamicImageDirectory.getParent(), missingImageGuidsFileBasename);
		FileOutputStream stream = new FileOutputStream(missingImagesFile);
		missingImageFileWriter = new OutputStreamWriter(stream, StandardCharsets.UTF_8);
	}

	@Override
	public void process(@NotNull String imageId, @NotNull String docId) {
		processed.add(imageId);

		NovusImage image = imageFinder.getImage(imageId);
		if (image == null) {
			processFail(imageId, docId);
			return;
		}

		try {
			processSuccess(image, imageId, docId);
		} catch (Exception e) {
			Log.error("Failed while writing the image for imageGuid " + imageId, e);
			processFail(imageId, docId);
		}
	}

	private void processSuccess(NovusImage image, String imageId, String docId) throws IOException {
		String extension = image.isTiffImage() ? PNG : image.getMediaSubTypeString();
		File imageFile = new File(dynamicImageDirectory, imageId + "." + extension);
		
		writeImage(image, imageFile);
		if (image.isUnknownFormat()) {
			Log.debug("Unfamiliar Image format " + image.getMediaSubTypeString() + " found for imageGuid " + imageId);
		}

		ImgMetadataInfo metadata = image.getMetadata();
		metadata.setMimeType(image.getMediaTypeString() + "/" + extension);
		metadata.setSize(imageFile.length());
		metadata.setDocGuid(docId);
		metadata.setImgGuid(imageId);
		imagesMetadata.add(metadata);
	}

	private void writeImage(NovusImage image, File imageFile) throws IOException {
		byte[] content = image.getContent();
		if (image.isTiffImage()) {
			imageConverter.convertByteImg(content, imageFile.getAbsolutePath(), PNG_FORMAT);
		} else {
			FileUtils.writeByteArrayToFile(imageFile, content);
		}
	}

	private void processFail(String imageId, String docId) {
		missingImageCount++;
		try {
			missingImageFileWriter.write(imageId + "," + docId);
			missingImageFileWriter.write("\n");
		} catch (IOException e) {
			throw new RuntimeException("Cannot write to missing images file", e);
		}
	}

	@Override
	public boolean isProcessed(@NotNull String imageId) {
		return processed.contains(imageId);
	}

	@NotNull
	@Override
	public List<ImgMetadataInfo> getImagesMetadata() {
		return imagesMetadata;
	}

	@Override
	public int getMissingImageCount() {
		return missingImageCount;
	}

	@Override
	public void close() throws Exception {
		imageFinder.close();
		missingImageFileWriter.close();
	}

	@Required
	public void setImageFinder(NovusImageFinder imageFinder) {
		this.imageFinder = imageFinder;
	}

	@Required
	public void setDynamicImageDirectory(File dynamicImageDirectory) {
		this.dynamicImageDirectory = dynamicImageDirectory;
	}

	@Required
	public void setMissingImageGuidsFileBasename(String missingImageGuidsFileBasename) {
		this.missingImageGuidsFileBasename = missingImageGuidsFileBasename;
	}

	@Required
	public void setImageConverter(ImageConverter imageConverter) {
		this.imageConverter = imageConverter;
	}
}
