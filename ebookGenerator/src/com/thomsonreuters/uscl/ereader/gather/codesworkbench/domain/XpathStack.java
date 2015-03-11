/*
* Copyright 2015: Thomson Reuters Global Resources. All Rights Reserved.
* Proprietary and Confidential information of TRGR. Disclosure, Use or
* Reproduction without the written authorization of TRGR is prohibited
*/
package com.thomsonreuters.uscl.ereader.gather.codesworkbench.domain;

import java.util.Stack;

public class XpathStack extends Stack<String>{
	private static final long serialVersionUID = 1L;

	public String toXPathString() {
		String tempString;
		tempString = super.toString();
		return tempString.replaceAll("\\[|,\\s", "/").replaceAll("\\]", "");
	}
}