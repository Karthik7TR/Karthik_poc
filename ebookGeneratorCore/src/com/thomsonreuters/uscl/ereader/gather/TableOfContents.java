/*
* Copyright 2012: Thomson Reuters Global Resources. All Rights Reserved.
* Proprietary and Confidential information of TRGR. Disclosure, Use or
* Reproduction without the written authorization of TRGR is prohibited
*/
package com.thomsonreuters.uscl.ereader.gather;

import java.util.ArrayList;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import com.thomsonreuters.uscl.ereader.proview.TocEntry;

/**
 * Represents a table of contents as created by the RESTful TOC gather service.
 * 
 * @author <a href="mailto:christopher.schwartz@thomsonreuters.com">Chris Schwartz</a> u0081674
 */
public class TableOfContents {
	private ArrayList<TocEntry> tocEntries;
	
	public ArrayList<TocEntry> getTocEntries() {
		return tocEntries;
	}
	
	public void setTocEntries(ArrayList<TocEntry> tocEntries){
		this.tocEntries = tocEntries;
	}
	
	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
	}
}
