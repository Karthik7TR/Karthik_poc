package com.thomsonreuters.uscl.ereader.gather.img.service;

import javax.annotation.PostConstruct;

import com.thomsonreuters.uscl.ereader.gather.domain.GatherResponse;
import com.thomsonreuters.uscl.ereader.gather.exception.GatherException;
import com.thomsonreuters.uscl.ereader.gather.img.model.NovusImage;
import com.thomsonreuters.uscl.ereader.gather.img.util.NovusImageMetadataParser;
import com.thomsonreuters.uscl.ereader.gather.services.NovusFactory;
import com.thomsonreuters.uscl.ereader.gather.services.NovusUtility;
import com.thomsonreuters.uscl.ereader.gather.util.ImgMetadataInfo;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.http.MediaType;

import com.westgroup.novus.productapi.BLOB;
import com.westgroup.novus.productapi.Find;
import com.westgroup.novus.productapi.Novus;
import com.westgroup.novus.productapi.NovusException;

public class NovusImageFinderImpl implements NovusImageFinder {
    private static final Logger Log = LogManager.getLogger(NovusImageFinderImpl.class);

    private NovusFactory novusFactory;
    private NovusUtility novusUtility;
    private NovusImageMetadataParser parser;
    private boolean isFinalStage;
    /** Milliseconds to sleep between each meta-data/bytes fetch */
    private long sleepIntervalBetweenImages;

    private Novus novus;
    private Find find;
    private int retryCount;

    @PostConstruct
    public void init() throws GatherException {
        try {
            novus = novusFactory.createNovus(isFinalStage);
            find = novus.getFind();
            find.setResolveIncludes(true);
            retryCount = Integer.valueOf(novusUtility.getImgRetryCount());
        } catch (final NovusException e) {
            throw new GatherException(
                "Novus error occurred while creating Novus object " + e,
                GatherResponse.CODE_NOVUS_ERROR);
        }
    }

    @Override
    @Nullable
    public NovusImage getImage(@NotNull final String imageId) {
        for (int i = 0; i < retryCount; i++) {
            try {
                final BLOB blob = find.getBLOB(null, imageId);
                final String mimeType = blob.getMimeType();
                final String metaData = blob.getMetaData();
                if (StringUtils.isBlank(mimeType) || StringUtils.isBlank(metaData)) {
                    throw new Exception("MimeType/Metadata is null for image Guid " + imageId);
                }
                final ImgMetadataInfo imgMetadataInfo = parser.parse(metaData);
                final MediaType mediaType = MediaType.valueOf(mimeType);
                return new NovusImage(mediaType, imgMetadataInfo, blob.getContents());
            } catch (final Exception e) {
                Log.error(
                    "Exception ocuured while retreiving image for imageGuid "
                        + imageId
                        + ".  Retry Count # is "
                        + (i + 1),
                    e);
                // Intentionally pause between invocations of the
                // Image Vertical REST service as not to pound on it
                try {
                    Thread.sleep(sleepIntervalBetweenImages);
                } catch (final InterruptedException e1) {
                    Log.error("Interrupted Exception: " + e1.getMessage());
                    return null;
                }
            }
        }
        return null;
    }

    @Override
    public void close() throws Exception {
        novus.shutdownMQ();
    }

    @Required
    public void setNovusFactory(final NovusFactory factory) {
        novusFactory = factory;
    }

    @Required
    public void setNovusUtility(final NovusUtility novusUtil) {
        novusUtility = novusUtil;
    }

    @Required
    public void setFinalStage(final boolean isFinalStage) {
        this.isFinalStage = isFinalStage;
    }

    @Required
    public void setParser(final NovusImageMetadataParser parser) {
        this.parser = parser;
    }

    @Required
    public void setSleepIntervalBetweenImages(final long sleepIntervalBetweenImages) {
        this.sleepIntervalBetweenImages = sleepIntervalBetweenImages;
    }
}
