/*
* Copyright 2011: Thomson Reuters Global Resources. All Rights Reserved.
* Proprietary and Confidential information of TRGR. Disclosure, Use or
* Reproduction without the written authorization of TRGR is prohibited
*/
package com.thomsonreuters.uscl.ereader.proview;

import org.apache.commons.lang.StringUtils;

/**
 * Represents a keyword within title.xml
 * 
 * @author <a href="mailto:christopher.schwartz@thomsonreuters.com">Chris Schwartz</a> u0081674
 */
public class Keyword {
	private String type;
	private String text;
	
	public Keyword(String type, String text){
		if (StringUtils.isBlank(type)) {
			throw new IllegalArgumentException("'type' attribute required for keyword.");
		}
		if (StringUtils.isBlank(text)){
			throw new IllegalArgumentException("must provide keyword text.");
		}
		this.type = type;
		this.text = text;
	}
}
