package com.thomsonreuters.uscl.ereader.gather.util;

import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

public class ImgMetadataInfo {

	private String 	imgGuid;		// image GUID to retrieve	
	private String mimeType;	// image mime-type
	private Long 	size;		// image size in bytes
	private String 	dimUnit;	// dimension units, like "px"
	private Long 	height;		// image height in the dimensional units
	private Long 	width;		// image width in the dimensional units
	private Long 	dpi;		// dots per inch
	private String docGuid;
	
	public ImgMetadataInfo(){
	}
	
	public String getImgGuid() {
		return imgGuid;
	}


	public void setImgGuid(String imgGuid) {
		this.imgGuid = imgGuid;
	}
	
	public String getDocGuid() {
		return docGuid;
	}

	public void setDocGuid(String docGuid) {
		this.docGuid = docGuid;
	}

	public String getDimUnit() {
		return dimUnit;
	}
	
	public Long getDpi() {
		return dpi;
	}
	
	public String getGuid() {
		return imgGuid;
	}
	
	public Long getHeight() {
		return height;
	}
	
	public String getMimeType() {
		return mimeType;
	}
	
	
	
	public Long getSize() {
		return size;
	}
	
	public Long getWidth() {
		return width;
	}
	
	
	public void setDimUnit(String dimUnit) {
		this.dimUnit = dimUnit;
	}
	public void setDpi(Long dpi) {
		this.dpi = dpi;
	}
	public void setGuid(String guid) {
		this.imgGuid = guid;
	}
	public void setHeight(Long height) {
		this.height = height;
	}
	public void setMimeType(String mimeType) {
		this.mimeType = mimeType;
	}
	
	public void setSize(Long size) {
		this.size = size;
	}
	public void setWidth(Long width) {
		this.width = width;
	}
	public String toString() {
		return ReflectionToStringBuilder.toString(this, ToStringStyle.SHORT_PREFIX_STYLE);
	}
}
