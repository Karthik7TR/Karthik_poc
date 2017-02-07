package com.thomsonreuters.uscl.ereader.gather.services;

import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.util.Map;

import com.thomsonreuters.uscl.ereader.gather.domain.GatherResponse;
import com.thomsonreuters.uscl.ereader.gather.exception.GatherException;
import com.thomsonreuters.uscl.ereader.gather.util.ImgMetadataInfo;

import com.westgroup.novus.productapi.Find;

/**
 * @deprecated Should be removed after related integration test will be fixed
 */
@Deprecated
public interface NovusImgService
{
    GatherResponse getImagesFromNovus(
        File imgToDocManifestFile,
        File getDynamicImageDirectory,
        boolean isFinalStage);

    ImgMetadataInfo getImagesAndMetadata(
        Find find,
        String imageGuid,
        Writer missingImageFileWriter,
        String docGuid,
        File imageDirectory) throws GatherException, IOException;

    GatherResponse fetchImages(
        final Map<String, String> imgDocGuidMap,
        File imageDestinationDirectory,
        boolean isFinalStage) throws Exception;
}
