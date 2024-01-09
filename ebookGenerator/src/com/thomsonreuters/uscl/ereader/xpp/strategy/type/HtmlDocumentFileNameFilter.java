package com.thomsonreuters.uscl.ereader.xpp.strategy.type;

import java.io.File;
import java.io.FilenameFilter;

public class HtmlDocumentFileNameFilter implements FilenameFilter {
    private final BundleFileType bundleFileType;
    private String fileName;

    public HtmlDocumentFileNameFilter(final BundleFileType bundleFileType, final String fileName) {
        this.bundleFileType = bundleFileType;
        this.fileName = fileName;
    }

    @Override
    public boolean accept(final File dir, final String name) {
        final boolean sameBundleType = bundleFileType == BundleFileType.getByDocumentFileName(name);
        final boolean fileNameIsSimilar = name.startsWith(fileName);
        return sameBundleType && fileNameIsSimilar;
    }
}
