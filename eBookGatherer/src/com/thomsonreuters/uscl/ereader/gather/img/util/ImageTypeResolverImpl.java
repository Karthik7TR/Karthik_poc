package com.thomsonreuters.uscl.ereader.gather.img.util;

import java.io.File;

import com.thomsonreuters.uscl.ereader.gather.util.images.ImageConverterException;
import org.apache.commons.io.FilenameUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ImageTypeResolverImpl implements ImageTypeResolver {
    private static final Logger LOG = Logger.getLogger(ImageTypeResolverImpl.class);

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
            LOG.info(String.format("Failed to read as TIFF: %s", image.getAbsolutePath()), e);
            return false;
        }
    }
}
