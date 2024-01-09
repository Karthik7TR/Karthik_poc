package com.thomsonreuters.uscl.ereader.gather.img.service;

import org.springframework.beans.factory.annotation.Required;

/**
 * Returns corresponding image service.
 */
public class ImageServiceFactory {
    private ImageService novusImageService;
    private ImageService xppImageService;

    public ImageService getImageService(final boolean isXpp) {
        return isXpp ? xppImageService : novusImageService;
    }

    @Required
    public void setNovusImageService(final ImageService imageServcie) {
        novusImageService = imageServcie;
    }

    @Required
    public void setXppImageService(final ImageService imageServcie) {
        xppImageService = imageServcie;
    }
}
