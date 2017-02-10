package com.thomsonreuters.uscl.ereader.gather.img.util;

import com.thomsonreuters.uscl.ereader.gather.util.images.ImageConverterException;

public interface ImageConverter
{
    /**
     * Convert an image to specified format and write result to file system
     *
     * @param imgBytes
     *            image bytes
     * @param outputImagePath
     *            path to write converted image
     * @param formatName
     *            format to convert image
     * @throws ImageConverterException
     */
    void convertByteImg(byte[] imgBytes, String outputImagePath, String formatName);
}
