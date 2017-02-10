package com.thomsonreuters.uscl.ereader.gather.img.util;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Iterator;

import javax.imageio.ImageIO;
import javax.imageio.ImageReadParam;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;

import com.thomsonreuters.uscl.ereader.gather.util.images.ImageConverterException;

public class TiffImageConverterImpl implements ImageConverter
{
    private static final Logger LOG = Logger.getLogger(TiffImageConverterImpl.class);

    private static final String NO_TIFF_READER_FOUND = "No TIFF reader found";
    private static final String PREFERABLE_TIFF_READER_NOT_FOUND = "Preferable TIFF reader not found: ";

    private String tiffReaderClass;

    @Required
    public void setTiffReaderClass(final String tiffReaderClass)
    {
        this.tiffReaderClass = tiffReaderClass;
    }

    public void init()
    {
        /*
         * IIORegistry is not thread save and by default initialized when
         * ImageIO is called for the first time. If several threads will try to
         * do it then ConcurrentModificationException will be thrown. So we
         * should initialize it manually at start time. More info:
         * http://stackoverflow.com/questions/15432462/java-servlets-and-imageio
         * -error
         */
        ImageIO.scanForPlugins();
    }

    @Override
    public void convertByteImg(final byte[] imgBytes, final String outputImagePath, final String formatName)
    {
        try (OutputStream os = new FileOutputStream(outputImagePath);
            InputStream is = new ByteArrayInputStream(imgBytes))
        {
            final BufferedImage image = readTiff(is);
            ImageIO.write(image, formatName, os);
            checkImage(outputImagePath);
        }
        catch (final IOException e)
        {
            throw new ImageConverterException(e);
        }
    }

    private void checkImage(final String outputImagePath)
    {
        final File file = new File(outputImagePath);
        if (!file.exists() || file.length() == 0)
        {
            throw new ImageConverterException("Image " + outputImagePath + " was not converted successfully");
        }
    }

    private BufferedImage readTiff(final InputStream is) throws IOException
    {
        ImageReader reader = null;
        try (ImageInputStream iis = ImageIO.createImageInputStream(is))
        {
            reader = getReader(iis);
            if (reader == null)
            {
                throw new ImageConverterException(NO_TIFF_READER_FOUND);
            }
            final ImageReadParam readerParam = reader.getDefaultReadParam();
            reader.setInput(iis, true, true);
            return reader.read(0, readerParam);
        }
        finally
        {
            if (reader != null)
                reader.dispose();
        }
    }

    private ImageReader getReader(final ImageInputStream iis)
    {
        final Iterator<ImageReader> it = ImageIO.getImageReaders(iis);
        ImageReader imageReader = null;
        while (it.hasNext())
        {
            final ImageReader candidate = it.next();
            if (candidate.getClass().getName().equals(tiffReaderClass))
            {
                return candidate;
            }
            else
            {
                imageReader = candidate;
            }
        }
        LOG.warn(PREFERABLE_TIFF_READER_NOT_FOUND + tiffReaderClass);
        return imageReader;
    }
}
