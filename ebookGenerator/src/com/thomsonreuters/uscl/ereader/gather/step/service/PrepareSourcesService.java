package com.thomsonreuters.uscl.ereader.gather.step.service;

import java.io.File;

public interface PrepareSourcesService {
    File getTocFile(final File rootTocFile, final String titleId);

    File getDocsGuidsFile(final File rootTocFile, final String titleId);
}
