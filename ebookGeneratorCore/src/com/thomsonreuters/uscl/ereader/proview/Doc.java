/*
* Copyright 2011: Thomson Reuters Global Resources. All Rights Reserved.
* Proprietary and Confidential information of TRGR. Disclosure, Use or
* Reproduction without the written authorization of TRGR is prohibited
*/
package com.thomsonreuters.uscl.ereader.proview;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

/**
 * Represents a doc within title.xml.
 * 
 * @author <a href="mailto:christopher.schwartz@thomsonreuters.com">Chris Schwartz</a> u0081674
 */
public class Doc {
	private String id;
	private String src;
	private int splitTitlePart;	
	private List<String> imageIdList;

	public Doc() {}
	
	public Doc(String id, String src, int splitTitlePart, List<String> imageIdList) {
		if (StringUtils.isBlank(id)){
			throw new IllegalArgumentException("'id' is a required field on <doc>.");
		}
		if (StringUtils.isBlank(src)){
			throw new IllegalArgumentException("'src' is a required field on <doc>.");
		}
		if (splitTitlePart > 0){
			this.splitTitlePart = splitTitlePart;
		}
		if(imageIdList !=null && imageIdList.size()>0){
			this.imageIdList = imageIdList;
		}
		this.id = id;
		this.src = src;
	}
	
	public String getSrc() {
		return src;
	}
	
	public String getId() {
		return id;
	}
	
	public int getSplitTitlePart() {
		return splitTitlePart;
	}

	public void setSplitTitlePart(int splitTitlePart) {
		this.splitTitlePart = splitTitlePart;
	}


	public List<String> getImageIdList() {		
		return imageIdList;
	}

	public void setImageIdList(List<String> imageIdList) {
		if(imageIdList == null){
			imageIdList = new ArrayList<String>();
		}
		this.imageIdList = imageIdList;
	}
	
	@Override
	public String toString() {
		StringBuilder buffer = new StringBuilder();
		buffer.append("Doc [id=").append(id).append(", ");
		buffer.append("src=").append(src).append(", ");
		buffer.append("splitTitlePart=").append(splitTitlePart);
		if (imageIdList != null)
		buffer.append(", ").append("imgSize=").append(imageIdList.size()).append(", ");
		buffer.append("]");
		
		return buffer.toString();
	}
	
}
