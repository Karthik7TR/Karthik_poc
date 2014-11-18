/*
 * Copyright 2014: Thomson Reuters Global Resources. All Rights Reserved.
 * Proprietary and Confidential information of TRGR. Disclosure, Use or
 * Reproduction without the written authorization of TRGR is prohibited
 */
package com.thomsonreuters.uscl.ereader.gather.codesworkbench.filter;

import java.io.File;
import java.io.FilenameFilter;

public class DocFilenameFilter implements FilenameFilter {

	@Override
	public boolean accept(File dir, String name) {
		if(name.endsWith("doc.xml")) {
			return true;
		} else {
			return false;
		}
	}

}
