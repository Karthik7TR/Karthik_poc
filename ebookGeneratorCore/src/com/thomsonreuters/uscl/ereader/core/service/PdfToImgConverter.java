package com.thomsonreuters.uscl.ereader.core.service;

import com.thomsonreuters.uscl.ereader.common.exception.EBookException;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
public class PdfToImgConverter {
    public static final String IMG_SUFFIX = "_img";
    private static final String PNG = "png";

    public void convert(final File sourcePdfFile, final File destDir) {
        try {
            FileUtils.copyFile(convert(sourcePdfFile), new File(destDir, getImageFileName(sourcePdfFile.getName())));
        } catch (IOException e) {
            throw new EBookException(sourcePdfFile.getName(), e);
        }
    }

    public static String getImageFileName(final String pdfFileName) {
        return FilenameUtils.removeExtension(pdfFileName) + IMG_SUFFIX + "." + PNG;
    }

    private File convert(final File pdf) throws IOException {
        List<BufferedImage> pageImages = readPdfPages(pdf);
        BufferedImage image = mergeImages(pageImages);
        return saveToFile(pdf, image);
    }

    private List<BufferedImage> readPdfPages(final File pdf) throws IOException {
        PDDocument document = PDDocument.load(pdf);
        PDFRenderer pdfRenderer = new PDFRenderer(document);

        try {
            return IntStream.range(0, document.getNumberOfPages())
                    .mapToObj(page -> renderImage(pdfRenderer, page))
                    .collect(Collectors.toList());
        } finally {
            document.close();
        }
    }

    private BufferedImage renderImage(final PDFRenderer pdfRenderer, final int page) {
        try {
            return pdfRenderer.renderImage(page);
        } catch (IOException e) {
            throw new EBookException(e);
        }
    }

    private BufferedImage mergeImages(final List<BufferedImage> pageImages) {
        if(pageImages.size() == 1) {
            return pageImages.get(0);
        }

        int maxWidth = pageImages.stream().mapToInt(BufferedImage::getWidth).max()
                .orElseThrow(() -> new EBookException("Unexpected zero PDF width."));
        int totalHeight = pageImages.stream().mapToInt(BufferedImage::getHeight).sum();

        BufferedImage mergedImage = new BufferedImage(
                maxWidth,
                totalHeight,
                BufferedImage.TYPE_INT_RGB);

        boolean imageDrawn = true;
        int verticalCoordinate = 0;
        for (BufferedImage page : pageImages) {
            imageDrawn &= mergedImage.createGraphics().drawImage(page, 0, verticalCoordinate, null);
            verticalCoordinate += page.getHeight();
        }

        if (!imageDrawn) throw new EBookException("Exception during concatenating PDF pages.");

        return mergedImage;
    }

    private File saveToFile(final File pdf, final BufferedImage image) throws IOException {
        File tempFile = Files.createTempFile(pdf.getName(), PNG).toFile();
        ImageIO.write(image, PNG, tempFile);
        return tempFile;
    }
}
