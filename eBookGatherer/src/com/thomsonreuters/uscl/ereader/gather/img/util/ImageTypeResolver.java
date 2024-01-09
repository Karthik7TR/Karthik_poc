package com.thomsonreuters.uscl.ereader.gather.img.util;

import java.io.File;

/**
 * Finds out image type
 */
public interface ImageTypeResolver {
    /**
     * @param image image file
     * @return {@code true} if this image is TIFF, {@code false} otherwise
     */
    boolean isTiff(File image);

    /**
     * @param image image file
     * @return {@code true} if this image has TIFF extension, {@code false} otherwise
     */
    boolean hasTiffExtension(File image);
}
