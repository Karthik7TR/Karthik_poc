/*
* Copyright 2011: Thomson Reuters Global Resources. All Rights Reserved.
* Proprietary and Confidential information of TRGR. Disclosure, Use or
* Reproduction without the written authorization of TRGR is prohibited
*/
package com.thomsonreuters.uscl.ereader.proview;

import org.apache.commons.lang.StringUtils;

/**
 * Represents a feature within title.xml.
 * 
 * <p>The only required field is 'name'. If 'value' is not supplied (single-arg constructor) it will be omitted.</p>
 * 
 * @author <a href="mailto:christopher.schwartz@thomsonreuters.com">Chris Schwartz</a> u0081674
 *
 */
public class Feature {
	private String name;
	private String value;
	
	public Feature(String name){
		this.name = name;
	}

	public Feature(String name, String value){
		if (StringUtils.isBlank(name)){
			throw new IllegalArgumentException("'name' parameter is required for all Features.");
		}
		this.name = name;
		this.value = value;
	}
}
