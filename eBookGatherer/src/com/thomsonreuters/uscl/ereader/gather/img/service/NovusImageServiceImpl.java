package com.thomsonreuters.uscl.ereader.gather.img.service;

import java.io.File;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.thomsonreuters.uscl.ereader.gather.domain.GatherResponse;
import com.thomsonreuters.uscl.ereader.gather.exception.GatherException;
import com.thomsonreuters.uscl.ereader.gather.img.model.ImageRequestParameters;
import com.thomsonreuters.uscl.ereader.gather.img.util.DocToImageManifestUtil;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Required;

public class NovusImageServiceImpl implements ImageService {
    private DocToImageManifestUtil docToImageManifestUtil;
    private NovusImageProcessor imageProcessor;

    @Override
    @NotNull
    public GatherResponse getImages(@NotNull final ImageRequestParameters imageRequestParameters)
        throws GatherException {
        final File docToImageManifestFile = imageRequestParameters.getDocToImageManifestFile();
        final Map<String, Set<String>> docsWithImages =
            docToImageManifestUtil.getDocsWithImages(docToImageManifestFile);

        try (NovusImageProcessor processor = imageProcessor) {
            for (final Entry<String, Set<String>> e : docsWithImages.entrySet()) {
                final String docId = e.getKey();
                for (final String imageId : e.getValue()) {
                    if (!processor.isProcessed(imageId, docId)) {
                        processor.process(imageId, docId);
                    }
                }
            }
            final GatherResponse response = new GatherResponse();
            response.setImageMetadataList(processor.getImagesMetadata());
            response.setMissingImgCount(processor.getMissingImageCount());
            return response;
        } catch (final Exception e) {
            throw new GatherException("Cannot process images from Novus", e);
        }
    }

    @Required
    public void setDocToImageManifestUtil(final DocToImageManifestUtil docToImageManifestUtil) {
        this.docToImageManifestUtil = docToImageManifestUtil;
    }

    @Required
    public void setImageProcessor(final NovusImageProcessor imageProcessor) {
        this.imageProcessor = imageProcessor;
    }
}
