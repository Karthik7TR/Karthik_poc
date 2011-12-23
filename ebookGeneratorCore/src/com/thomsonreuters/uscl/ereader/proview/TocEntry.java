/*
* Copyright 2011: Thomson Reuters Global Resources. All Rights Reserved.
* Proprietary and Confidential information of TRGR. Disclosure, Use or
* Reproduction without the written authorization of TRGR is prohibited
*/
package com.thomsonreuters.uscl.ereader.proview;

import java.util.ArrayList;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

/**
 * This class represents a single entry in the TOC manifest within title.xml.
 * 
 * @author <a href="mailto:christopher.schwartz@thomsonreuters.com">Chris Schwartz</a> u0081674
 */
public class TocEntry {
	private String anchorReference;
	private String text;
	private ArrayList<TocEntry> children;
	
	public TocEntry(){
		
	}
	
	public TocEntry (String anchorReference, String text){
		if (StringUtils.isBlank(anchorReference)){
			throw new IllegalArgumentException("'anchorReference' is required.");
		}
		if (StringUtils.isBlank(text)){
			throw new IllegalArgumentException("'text' is required.");
		}
		this.anchorReference = anchorReference;
		this.text = text;
	}
	
	public String toString() {
		return ReflectionToStringBuilder.toString(this, ToStringStyle.SHORT_PREFIX_STYLE);
	}
	
	public void setChildren(ArrayList<TocEntry> children){
		this.children = children;
	}
}
