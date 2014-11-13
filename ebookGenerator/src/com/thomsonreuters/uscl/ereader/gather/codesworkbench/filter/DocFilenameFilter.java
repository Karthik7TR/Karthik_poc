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
