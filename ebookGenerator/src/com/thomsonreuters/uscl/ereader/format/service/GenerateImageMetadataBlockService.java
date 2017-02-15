package com.thomsonreuters.uscl.ereader.format.service;

import java.io.File;

import com.thomsonreuters.uscl.ereader.format.exception.EBookFormatException;

/**
 * Defines the service that generates the ImageMetadata xml block that is appended
 * to the Novus document to help the transform embed images properly during the
 * transformation process.
 *
 * @author <a href="mailto:Selvedin.Alic@thomsonreuters.com">Selvedin Alic</a> u0095869
 */
public interface GenerateImageMetadataBlockService
{
    /**
     * Using the document to image manifest as a blue print the service generates one
     * file per document of ImageMetadata blocks for all images embedded in the documents.
     * If there are no images embedded an empty file will be created.
     *
     * @param docToImgManifest manifest of what images are embedded in each document
     * @param targetDirectory directory to which the generated Image Metadata blocks will be writen to
     * @param jobInstanceId job instance id assigned to the current job run
     *
     * @return number of ImageMetadata block files generated
     */
    int generateImageMetadata(File docToImgManifest, File targetDirectory, long jobInstanceId)
        throws EBookFormatException;
}
