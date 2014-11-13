package com.thomsonreuters.uscl.ereader.gather.codesworkbench.filter;

import java.io.File;
import java.io.FilenameFilter;

public class NortFilenameFilter implements FilenameFilter {

	@Override
	public boolean accept(File dir, String name) {
		if(name.endsWith("nort.xml")) {
			return true;
		} else {
			return false;
		}
	}

}
