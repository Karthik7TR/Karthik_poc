package com.thomsonreuters.uscl.ereader.gather.img.util;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Iterator;

import javax.imageio.ImageIO;
import javax.imageio.ImageReadParam;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;

import com.thomsonreuters.uscl.ereader.gather.util.images.ImageConverterException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class TiffReaderImpl implements TiffReader
{
    private static final String NO_TIFF_READER_FOUND = "No TIFF reader found";
    private static final String PREFERABLE_TIFF_READER_NOT_FOUND = "Preferable TIFF reader not found: ";

    private final String tiffReaderClass;

    @Autowired
    public TiffReaderImpl(@Value("it.geosolutions.imageioimpl.plugins.tiff.TIFFImageReader") final String tiffReaderClass)
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
        this.tiffReaderClass = tiffReaderClass;
    }

    @Override
    public BufferedImage readTiff(final File image)
    {
        try
        {
            return readTiff(Files.readAllBytes(image.toPath()));
        }
        catch (final IOException e)
        {
            throw new ImageConverterException(String.format("Cannot read file %s", image.getAbsolutePath()), e);
        }
    }

    @Override
    public BufferedImage readTiff(final byte[] imageBytes)
    {
        ImageReader reader = null;
        try (ImageInputStream iis = ImageIO.createImageInputStream(new ByteArrayInputStream(imageBytes)))
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
        catch (final IOException | ClassNotFoundException e)
        {
            throw new ImageConverterException(e);
        }
        finally
        {
            if (reader != null)
                reader.dispose();
        }
    }

    private ImageReader getReader(final ImageInputStream iis) throws ClassNotFoundException
    {
        final Class<?> readerClassName = Class.forName(tiffReaderClass);
        final Iterator<ImageReader> it = ImageIO.getImageReaders(iis);
        while (it.hasNext())
        {
            final ImageReader candidate = it.next();
            if (readerClassName.isInstance(candidate))
            {
                return candidate;
            }
        }
        throw new ImageConverterException(PREFERABLE_TIFF_READER_NOT_FOUND + tiffReaderClass);
    }
}
