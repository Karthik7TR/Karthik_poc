package com.thomsonreuters.uscl.ereader.format.service;

import java.io.File;

import com.thomsonreuters.uscl.ereader.format.exception.EBookFormatException;

/**
 * Parses XML files and identifies all image referencesand prints them to a file.
 *
 * @author <a href="mailto:Selvedin.Alic@thomsonreuters.com">Selvedin Alic</a> u0095869
 */
public interface XMLImageParserService {
    /**
     * Reads through all the XML files found in the provided directory and parses out all
     * image references and generates the specified file that contains a list of GUIDs for
     * the referenced images.
     *
     * @param xmlDir directory that contains the XML files to be parsed
     * @param imgRef file which will be written that contains a list of all the referenced image GUIDs
     * @param docImageMap file which will be written that contains a list of images embedded in each document
     *
     * @return number of documents processed to generate lists
     * @throws EBookFormatException if any fatal errors are encountered
     */
    int generateImageList(File xmlDir, File imgRef, File docImageMap) throws EBookFormatException;
}
