/*
* Copyright 2011: Thomson Reuters Global Resources. All Rights Reserved.
* Proprietary and Confidential information of TRGR. Disclosure, Use or
* Reproduction without the written authorization of TRGR is prohibited
*/
package com.thomsonreuters.uscl.ereader.proview;

import org.apache.commons.lang.StringUtils;

/**
 * Represents a doc within title.xml.
 * 
 * @author <a href="mailto:christopher.schwartz@thomsonreuters.com">Chris Schwartz</a> u0081674
 */
public class Doc {
	private String id;
	private String src;
	
	public Doc(String id, String src) {
		if (StringUtils.isBlank(id)){
			throw new IllegalArgumentException("'id' is a required field on <doc>.");
		}
		if (StringUtils.isBlank(src)){
			throw new IllegalArgumentException("'src' is a required field on <doc>.");
		}
		this.id = id;
		this.src = src;
	}
}
