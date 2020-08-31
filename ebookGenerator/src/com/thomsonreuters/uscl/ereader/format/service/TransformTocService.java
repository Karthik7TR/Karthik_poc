package com.thomsonreuters.uscl.ereader.format.service;

import java.io.File;

public interface TransformTocService {
    void transformToc(File toc, File destDir);
}
