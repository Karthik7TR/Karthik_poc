package com.thomsonreuters.uscl.ereader.gather.img.util;

import java.awt.image.BufferedImage;
import java.io.File;

import com.thomsonreuters.uscl.ereader.gather.util.images.ImageConverterException;

/**
 * Reads TIFF images
 */
public interface TiffReader {
    /**
     * @param image image file
     * @return image object
     * @throws ImageConverterException if cannot read file as TIFF image
     */
    BufferedImage readTiff(File image);

    /**
     * @param imageBytes image as byte array
     * @return image object
     * @throws ImageConverterException if cannot read byte array as TIFF image
     */
    BufferedImage readTiff(byte[] imageBytes);
}
