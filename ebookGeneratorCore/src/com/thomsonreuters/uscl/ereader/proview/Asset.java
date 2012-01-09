/*
* Copyright 2011: Thomson Reuters Global Resources. All Rights Reserved.
* Proprietary and Confidential information of TRGR. Disclosure, Use or
* Reproduction without the written authorization of TRGR is prohibited
*/
package com.thomsonreuters.uscl.ereader.proview;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

/**
 * Represents an asset within title.xml
 * 
 * @author <a href="mailto:christopher.schwartz@thomsonreuters.com">Chris Schwartz</a> u0081674
 */
public class Asset {
	private String id;
	private String src;
	
	public Asset(){}
	
	public Asset(String id, String src){
		if (StringUtils.isBlank(id)) {
			throw new IllegalArgumentException("'id' is a required field.");
		}
		if (StringUtils.isBlank(src)){
			throw new IllegalArgumentException("'src' is a required field.");
		}
		this.id = id;
		this.src = src;
	}
	
	public String toString() {
		return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
	}
}
