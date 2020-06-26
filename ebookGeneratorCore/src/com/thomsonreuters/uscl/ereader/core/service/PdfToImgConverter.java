package com.thomsonreuters.uscl.ereader.core.service;

import com.thomsonreuters.uscl.ereader.common.exception.EBookException;
import org.apache.commons.io.FilenameUtils;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
public class PdfToImgConverter {
    private static final String PNG = "png";
    private static final String IMAGE_NAME_TEMPLATE_FOR_TEMPLATE = "%s_img_%%0%sd.of.%s";

    public List<String> convert(final File sourcePdfFile, final File destDir) {
        try {
            List<BufferedImage> pageImages = readPdfPages(sourcePdfFile);
            return saveImages(pageImages, destDir, FilenameUtils.removeExtension(sourcePdfFile.getName()));
        } catch (Exception e) {
            throw new EBookException(sourcePdfFile.getName(), e);
        }
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

    private List<String> saveImages(final List<BufferedImage> images, final File destDir, final String baseName) {
        final String template = getImageNameTemplate(images, baseName);
        return IntStream.range(0, images.size())
                .mapToObj(i -> saveToFile(images, destDir, template, i))
                .collect(Collectors.toList());
    }

    private String getImageNameTemplate(final List<BufferedImage> images, final String baseName) {
        return String.format(IMAGE_NAME_TEMPLATE_FOR_TEMPLATE, baseName, getNumberOfDigits(images), images.size());
    }

    private int getNumberOfDigits(final List<BufferedImage> images) {
        return (int) Math.floor(Math.log10(images.size())) + 1;
    }

    private String saveToFile(final List<BufferedImage> images, final File destDir, final String template, final int i) {
        try {
            return saveToFile(images.get(i), destDir, getFileName(template, i));
        } catch (IOException e) {
            throw new EBookException(e);
        }
    }

    private String saveToFile(final BufferedImage image, final File destDir, final String name) throws IOException {
        File imageFile = new File(destDir, name + "." + PNG);
        destDir.mkdirs();
        ImageIO.write(image, PNG, imageFile);
        return name;
    }

    private String getFileName(final String template, final int i) {
        return String.format(template, i + 1);
    }
}
