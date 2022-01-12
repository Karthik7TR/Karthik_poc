package com.thomsonreuters.uscl.ereader.gather.img.util;

import com.thomsonreuters.uscl.ereader.gather.util.images.ImageConverterException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;

@Service
@Slf4j
public class ImageTypeResolverImpl implements ImageTypeResolver {

    private static final String TIF = "tif";
    private static final String TIFF = "tiff";

    @Autowired
    private TiffReader tiffReader;

    @Override
    public boolean isTiff(final File image) {
        return hasTiffExtension(image) ? true : canReadTiff(image);
    }

    @Override
    public boolean hasTiffExtension(final File image) {
        final String extension = FilenameUtils.getExtension(image.getName());
        return extension.equalsIgnoreCase(TIF) || extension.equalsIgnoreCase(TIFF);
    }

    private boolean canReadTiff(final File image) {
        try {
            tiffReader.readTiff(image);
            return true;
        } catch (final ImageConverterException e) {
            log.info(String.format("Failed to read as TIFF: %s", image.getAbsolutePath()), e);
            return false;
        }
    }
}
