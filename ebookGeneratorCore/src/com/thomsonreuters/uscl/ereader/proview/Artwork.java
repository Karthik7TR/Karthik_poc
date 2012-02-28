/*
* Copyright 2011: Thomson Reuters Global Resources. All Rights Reserved.
* Proprietary and Confidential information of TRGR. Disclosure, Use or
* Reproduction without the written authorization of TRGR is prohibited
*/
package com.thomsonreuters.uscl.ereader.proview;

import org.apache.commons.lang.StringUtils;

/**
 * This class represents the cover art for a given title.
 * 
 * @author <a href="mailto:christopher.schwartz@thomsonreuters.com">Chris Schwartz</a> u0081674
 */
public class Artwork {

	private String src;
	private String type = "cover";
	
	public Artwork() {
		
	}
	
	public Artwork (String src) {
		if (StringUtils.isBlank(src)){
			throw new IllegalArgumentException("'src' parameter is required in order to create cover art.");
		}
		this.src = src;
	}
	
	public String getSrc() {
		return src;
	}

	public String getType() {
		return type;
	}
}
