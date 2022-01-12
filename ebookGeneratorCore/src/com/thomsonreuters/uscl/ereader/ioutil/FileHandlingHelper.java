package com.thomsonreuters.uscl.ereader.ioutil;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.List;

import lombok.extern.slf4j.Slf4j;

/**
 * Static helper that contains generic file handling helper methods.
 *
 * @author <a href="mailto:Selvedin.Alic@thomsonreuters.com">Selvedin Alic</a> u0095869
 */
@Slf4j
public class FileHandlingHelper {

    private FileExtensionFilter filter;

    public void setFilter(final FileExtensionFilter aFilter) {
        filter = aFilter;
    }

    /**
     * Adds files meeting the filter criteria from the specified directory to the specified list.
     *
     * @param directory specifies the directory to search through
     * @param fileList list of files that meet the file filter condition
     * @throws EBookFormatException raised when no XML files have been found in the provided XML directory.
     */
    public void getFileList(final File directory, final List<File> fileList) throws FileNotFoundException {
        if (filter == null) {
            final String errMessage = "No filter specified for the file lookup.";
            log.error(errMessage);
            throw new IllegalStateException(errMessage);
        }

        final File[] files = directory.listFiles(filter);
        fileList.addAll(Arrays.asList(files));
        if (fileList.size() == 0) {
            String extensions = "";
            for (final String extension : filter.getAcceptedFileExtensions()) {
                extensions = extensions + extension + " ";
            }

            final String errMessage = "No '"
                + extensions
                + "' files were found in "
                + directory.getAbsolutePath()
                + " directory. Please verify the source path or make sure previous step succeeded.";
            log.error(errMessage);
            throw new FileNotFoundException(errMessage);
        }
    }
}
