package com.thomsonreuters.uscl.ereader.util;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;

/**
 * Concrete implementation that delegates to Apache Commons IO {@link FileUtils} for most interaction with the filesystem.
 *
 * @author <a href="mailto:christopher.schwartz@thomsonreuters.com">Chris Schwartz</a> u0081674
 *
 */
public class FileUtilsFacadeImpl implements FileUtilsFacade {
    @Override
    public void copyFile(final File sourceFile, final File destinationFile) throws IOException {
        FileUtils.copyFile(sourceFile, destinationFile);
    }
}