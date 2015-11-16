package com.thomsonreuters.uscl.ereader.gather.services;

import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.util.Map;

import com.thomsonreuters.uscl.ereader.gather.domain.GatherResponse;
import com.thomsonreuters.uscl.ereader.gather.exception.GatherException;
import com.westgroup.novus.productapi.Find;

public interface NovusImgService {

	public GatherResponse getImagesFromNovus(File imgToDocManifestFile, File getDynamicImageDirectory,
			boolean isFinalStage);

	public String getImagesAndMetadata(Find find, String imageGuid, Writer missingImageFileWriter, String docGuid,
			File imageDirectory) throws GatherException, IOException;

	public GatherResponse fetchImages(final Map<String, String> imgDocGuidMap, File imageDestinationDirectory,
			boolean isFinalStage) throws Exception;

	public void getMetadataFromString(String imgMetadata);

}
