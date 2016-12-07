/*
 * Copyright 2016: Thomson Reuters Global Resources. All Rights Reserved.
 * Proprietary and Confidential information of TRGR. Disclosure, Use or
 * Reproduction without the written authorization of TRGR is prohibited
 */
package com.thomsonreuters.uscl.ereader.gather.img.service;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Required;

import com.thomsonreuters.uscl.ereader.gather.domain.GatherResponse;
import com.thomsonreuters.uscl.ereader.gather.exception.GatherException;
import com.thomsonreuters.uscl.ereader.gather.img.model.ImageRequestParameters;
import com.thomsonreuters.uscl.ereader.gather.img.util.DocToImageManifestUtil;

public class NovusImageServiceImpl implements NovusImageService {
	private DocToImageManifestUtil docToImageManifestUtil;
	private NovusImageProcessor imageProcessor;

	@Override
	@NotNull
	public GatherResponse getImagesFromNovus(@NotNull ImageRequestParameters imageRequestParameters)
			throws GatherException {
		File docToImageManifestFile = imageRequestParameters.getDocToImageManifestFile();
		Map<String, List<String>> docsWithImages = docToImageManifestUtil.getDocsWithImages(docToImageManifestFile);

		try (NovusImageProcessor processor = imageProcessor) {
			for (Entry<String, List<String>> e : docsWithImages.entrySet()) {
				String docId = e.getKey();
				for (String imageId : e.getValue()) {
					if (!processor.isProcessed(imageId)) {
						processor.process(imageId, docId);
					}
				}
			}
			GatherResponse response = new GatherResponse();
			response.setImageMetadataList(processor.getImagesMetadata());
			response.setMissingImgCount(processor.getMissingImageCount());
			return response;
		} catch (Exception e) {
			throw new GatherException("Cannot process images from Novus", e);
		}
	}

	@Required
	public void setDocToImageManifestUtil(DocToImageManifestUtil docToImageManifestUtil) {
		this.docToImageManifestUtil = docToImageManifestUtil;
	}

	@Required
	public void setImageProcessor(NovusImageProcessor imageProcessor) {
		this.imageProcessor = imageProcessor;
	}

}
