package com.thomsonreuters.uscl.ereader.assemble.service;

import java.io.File;

import com.thomsonreuters.uscl.ereader.assemble.exception.EBookAssemblyException;

/**
 * EBookAssemblyService is responsible for creating a valid compressed tarball from whichever directory is passed as input to the assembleEBook method.
 *
 * <p>This service makes use of recursion to create the archive and cannot cope with circular directory structures.
 * This service is not responsible for determining that the eBook format, which ProView expects, is correct.</p>
 *
 * @author <a href="mailto:christopher.schwartz@thomsonreuters.com">Chris Schwartz</a> u0081674
 */
public interface EBookAssemblyService {
    /**
     * Archives and compresses an eBook to create an artifact that can be delivered to ProView.
     *
     * @param eBookParentDirectory the parent directory that contains artwork, documents, assets and title.xml
     * @param eBook the File where the output will be written (must not be a directory as reported by java.io.File).
     *
     * @throws EBookAssemblyException if an error occurs during the process.
     */
    void assembleEBook(File eBookDirectory, File eBook) throws EBookAssemblyException;

    /**
     * Iterates through all the contents of given directory and returns the size of largest content.
     * @param contentFolder
     * @return
     */

    long getLargestContent(String contentFolderPath, String fileExtention);

    /**
     *	gets list of documents in given folder location.
     * @param contentFolderPath
     * @return
     */
    long getDocumentCount(String contentFolderPath);
}
