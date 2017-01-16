/*
 * Copyright 2016: Thomson Reuters Global Resources. All Rights Reserved.
 * Proprietary and Confidential information of TRGR. Disclosure, Use or
 * Reproduction without the written authorization of TRGR is prohibited
 */
package com.thomsonreuters.uscl.ereader.gather.img.model;

import org.apache.commons.lang.ArrayUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.MediaType;

import com.thomsonreuters.uscl.ereader.gather.util.ImgMetadataInfo;

public class NovusImage {
	private static final String[] KNOWN_IMG_FORMATS = { "PNG", "TIF", "JPG", "GIF", "BMP" };
	private static final String TIF = "tif";

	@NotNull
	private MediaType mediaType;
	@NotNull
	private ImgMetadataInfo metadata;
	@NotNull
	private byte[] content;

	public NovusImage(@NotNull MediaType mediaType, @NotNull ImgMetadataInfo metadata, @NotNull byte[] content) {
		this.mediaType = mediaType;
		this.metadata = metadata;
		this.content = content;
	}

	@NotNull
	public MediaType getMediaType() {
		return mediaType;
	}

	@NotNull
	public ImgMetadataInfo getMetadata() {
		return metadata;
	}

	@NotNull
	public byte[] getContent() {
		return content;
	}

	public boolean isTiffImage() {
		return isImage() && getMediaSubTypeString().equals(TIF);
	}

	public boolean isUnknownFormat() {
		return isImage() && !ArrayUtils.contains(KNOWN_IMG_FORMATS, getMediaSubTypeString().toUpperCase());
	}

	private boolean isImage() {
		return getMediaTypeString().equals("image");
	}

	public String getMediaSubTypeString() {
		return mediaType.getSubtype();
	}
	
	public String getMediaTypeString() {
		return mediaType.getType();
	}
}