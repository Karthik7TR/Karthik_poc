package com.thomsonreuters.uscl.ereader.gather.codesworkbench.filter;

import java.io.File;
import java.io.FilenameFilter;

public class NortFilenameFilter implements FilenameFilter {
    @Override
    public boolean accept(final File dir, final String name) {
        return name.endsWith("nort.xml");
    }
}
