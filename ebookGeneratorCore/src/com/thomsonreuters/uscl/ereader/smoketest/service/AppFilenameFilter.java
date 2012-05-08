package com.thomsonreuters.uscl.ereader.smoketest.service;

import java.io.File;
import java.io.FilenameFilter;

public class AppFilenameFilter implements FilenameFilter {

	@Override
	public boolean accept(File dir, String name) {
		if(name.endsWith("X")) {
			return false;
		} else {
			return true;
		}
	}
	
}

