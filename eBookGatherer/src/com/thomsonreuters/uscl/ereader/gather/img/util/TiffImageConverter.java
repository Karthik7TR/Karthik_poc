package com.thomsonreuters.uscl.ereader.gather.img.util;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import javax.imageio.ImageIO;

import com.thomsonreuters.uscl.ereader.gather.util.images.ImageConverterException;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service("tiffImageConverter")
public class TiffImageConverter implements ImageConverter {
    private TiffReader tiffReader;

    private File substituteImagesDir;

    @Autowired
    public TiffImageConverter(
        final TiffReader tiffReader,
        @Value("${img.substitute.directory}") final File substituteImagesDir) {
        this.tiffReader = tiffReader;
        this.substituteImagesDir = substituteImagesDir;
    }

    @Override
    public BufferedImage convertByteImg(final byte[] imgBytes, final String outputImagePath, final String formatName) {
        final BufferedImage convertedImage = convertImage(imgBytes, outputImagePath, formatName);
        if (isCorrectImage(outputImagePath)) {
            return convertedImage;
        } else {
            return substituteFile(outputImagePath);
        }
    }

    private BufferedImage substituteFile(final String outputImagePath) {
        final File outputFile = new File(outputImagePath);
        final File substituteFile = new File(substituteImagesDir, outputFile.getName());
        try {
            FileUtils.copyFile(substituteFile, outputFile);
            return ImageIO.read(outputFile);
        } catch (final IOException e) {
            throw new ImageConverterException(
                String.format("Substitute image %s not found", substituteFile.getName()),
                e);
        }
    }

    private BufferedImage convertImage(final byte[] imgBytes, final String outputImagePath, final String formatName) {
        try (OutputStream os = new FileOutputStream(outputImagePath)) {
            final BufferedImage image = tiffReader.readTiff(imgBytes);
            ImageIO.write(image, formatName, os);
            return image;
        } catch (final IOException e) {
            throw new ImageConverterException(e);
        }
    }

    private boolean isCorrectImage(final String outputImagePath) {
        final File file = new File(outputImagePath);
        return file.exists() && file.length() > 0;
    }
}
