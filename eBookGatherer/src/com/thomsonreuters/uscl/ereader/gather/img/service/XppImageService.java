package com.thomsonreuters.uscl.ereader.gather.img.service;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.imageio.ImageIO;

import com.thomsonreuters.uscl.ereader.gather.domain.GatherResponse;
import com.thomsonreuters.uscl.ereader.gather.exception.GatherException;
import com.thomsonreuters.uscl.ereader.gather.img.model.ImageRequestParameters;
import com.thomsonreuters.uscl.ereader.gather.img.util.DocToImageManifestUtil;
import com.thomsonreuters.uscl.ereader.gather.img.util.ImageConverter;
import com.thomsonreuters.uscl.ereader.gather.util.ImgMetadataInfo;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;

/**
 * Copies images from unpacked XPP archive to work folder.
 */
public class XppImageService implements ImageService
{
    private static final Logger Log = LogManager.getLogger(XppImageService.class);

    private static final String PNG = "PNG";
    private static final String TIFF = "tif";
    private DocToImageManifestUtil docToImageManifestUtil;
    private ImageConverter imageConverter;

    @Override
    public GatherResponse getImages(final ImageRequestParameters imageRequestParameters) throws GatherException
    {
        final Map<String, ImgMetadataInfo> imageFiles = copyImagesToWorkDir(imageRequestParameters.getXppSourceImageDirectory(), imageRequestParameters.getDynamicImageDirectory());

        final Map<String, List<String>> docsWithImages = docToImageManifestUtil.getDocsWithImages(imageRequestParameters.getDocToImageManifestFile());

        final List<ImgMetadataInfo> imagesMetadata = new ArrayList<>();
        final int missingImagesCount = filllMetadataWithDocIds(imagesMetadata, imageFiles, docsWithImages);

        return populateResponse(imagesMetadata, missingImagesCount);
    }

    private GatherResponse populateResponse(
        final List<ImgMetadataInfo> imagesMetadata,
        final int missingImagesCount)
    {
        final GatherResponse response = new GatherResponse();
        response.setImageMetadataList(imagesMetadata);
        response.setMissingImgCount(missingImagesCount);
        return response;
    }

    private int filllMetadataWithDocIds(
        final List<ImgMetadataInfo> imagesMetadata,
        final Map<String, ImgMetadataInfo> imageFiles,
        final Map<String, List<String>> docsWithImages)
    {
        int missingImagesCount = 0;
        for (final Entry<String, List<String>> e : docsWithImages.entrySet())
        {
            final String docId = e.getKey();
            for (final String imageId : e.getValue())
            {
                try
                {
                    final ImgMetadataInfo imageMetadata = new ImgMetadataInfo(imageFiles.get(imageId));
                    imageMetadata.setDocGuid(docId);
                    imagesMetadata.add(imageMetadata);
                }
                catch (final Exception e1)
                {
                    Log.error(e1);
                    missingImagesCount++;
                }
            }
        }
        return missingImagesCount;
    }

    private ImgMetadataInfo getImageMetadata(final BufferedImage bufferedImage, final String imageId, final File imageFile)
        throws IOException
    {
        final ImgMetadataInfo metadata = new ImgMetadataInfo();
        metadata.setMimeType(Files.probeContentType(imageFile.toPath()));
        metadata.setSize(imageFile.length());
        metadata.setImgGuid(imageId);
        metadata.setWidth((long)bufferedImage.getWidth());
        metadata.setHeight((long)bufferedImage.getHeight());
        return metadata;
    }

    private Map<String, ImgMetadataInfo> copyImagesToWorkDir(final String xppSourceImageDirectory, final File destDir)
    {
        final File[] srcImages = new File(xppSourceImageDirectory).listFiles();
        final Map<String, ImgMetadataInfo> imageFiles = new HashMap<>();

        if (srcImages != null)
        {
            for (final File srcImage : srcImages) {
                try
                {
                    final String imageId = FilenameUtils.removeExtension(srcImage.getName());
                    final File destImage = new File(destDir, getDestImageName(imageId, srcImage));

                    final ImgMetadataInfo metadata = writeImage(imageId, srcImage, destImage);

                    imageFiles.put(imageId, metadata);
                }
                catch (final IOException e)
                {
                    Log.error(e);
                }
            }
        }
        return imageFiles;
    }

    private String getDestImageName(final String imageId, final File srcImage)
    {
        return isTiffImage(srcImage) ? imageId + "." + PNG : srcImage.getName();
    }

    private ImgMetadataInfo writeImage(final String imageId, final File srcImage, final File destImage) throws IOException
    {
        BufferedImage image;

        if (isTiffImage(srcImage))
        {
            image= imageConverter.convertByteImg(Files.readAllBytes(srcImage.toPath()), destImage.getAbsolutePath(), PNG);
        }
        else
        {
            FileUtils.copyFile(srcImage, destImage);
            image = ImageIO.read(destImage);
        }

        return getImageMetadata(image, imageId, destImage);
    }

    private boolean isTiffImage(final File sourceImage)
    {
        return FilenameUtils.getExtension(sourceImage.getName()).equalsIgnoreCase(TIFF);
    }

    @Required
    public void setDocToImageManifestUtil(final DocToImageManifestUtil docToImageManifestUtil)
    {
        this.docToImageManifestUtil = docToImageManifestUtil;
    }

    @Required
    public void setImageConverter(final ImageConverter imageConverter)
    {
        this.imageConverter = imageConverter;
    }
}
