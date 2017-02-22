package com.thomsonreuters.uscl.ereader.gather.img.service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

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
        final Map<String, File> imageFiles = copyImagesToWorkDir(imageRequestParameters.getXppSourceImageDirectory(), imageRequestParameters.getDynamicImageDirectory());

        final Map<String, List<String>> docsWithImages = docToImageManifestUtil.getDocsWithImages(imageRequestParameters.getDocToImageManifestFile());

        return populateResponse(imageFiles, docsWithImages);
    }

    private GatherResponse populateResponse(
        final Map<String, File> imageFiles,
        final Map<String, List<String>> docsWithImages)
    {
        final List<ImgMetadataInfo> imagesMetadata = new ArrayList<>();
        final int missingImagesCount = filllImagesMetadata(imagesMetadata, imageFiles, docsWithImages);

        final GatherResponse response = new GatherResponse();
        response.setImageMetadataList(imagesMetadata);
        response.setMissingImgCount(missingImagesCount);
        return response;
    }

    private int filllImagesMetadata(
        final List<ImgMetadataInfo> imagesMetadata,
        final Map<String, File> imageFiles,
        final Map<String, List<String>> docsWithImages)
    {
        int missingImagesCount = 0;
        for (final Entry<String, List<String>> e : docsWithImages.entrySet())
        {
            final String docId = e.getKey();
            for (final String imageId : e.getValue())
            {
                final File imageFile = imageFiles.get(imageId);
                try
                {
                    imagesMetadata.add(getImageMetadata(docId, imageId, imageFile));
                }
                catch (final IOException e1)
                {
                    Log.error(e1);
                    missingImagesCount++;
                }
            }
        }
        return missingImagesCount;
    }

    private ImgMetadataInfo getImageMetadata(final String docId, final String imageId, final File imageFile)
        throws IOException
    {
        final ImgMetadataInfo metadata = new ImgMetadataInfo();
        metadata.setMimeType(Files.probeContentType(imageFile.toPath()));
        metadata.setSize(imageFile.length());
        metadata.setDocGuid(docId);
        metadata.setImgGuid(imageId);
        return metadata;
    }

    private Map<String, File> copyImagesToWorkDir(final String xppSourceImageDirectory, final File destDir)
    {
        final File[] srcImages = new File(xppSourceImageDirectory).listFiles();
        final Map<String, File> imageFiles = new HashMap<>();

        for(final File srcImage : srcImages) {
            try
            {
                final String imageId = FilenameUtils.removeExtension(srcImage.getName());
                final File destImage = new File(destDir, getDestImageName(imageId, srcImage));

                writeImage(srcImage, destImage);

                imageFiles.put(imageId, destImage);
            }
            catch (final IOException e)
            {
                Log.error(e);
            }
        }
        return imageFiles;
    }

    private String getDestImageName(final String imageId, final File srcImage)
    {
        return isTiffImage(srcImage) ? imageId + "." + PNG : srcImage.getName();
    }

    private void writeImage(final File srcImage, final File destImage) throws IOException
    {
        if (isTiffImage(srcImage))
        {
            imageConverter.convertByteImg(Files.readAllBytes(srcImage.toPath()), destImage.getAbsolutePath(), PNG);
        }
        else
        {
            FileUtils.copyFile(srcImage, destImage);
        }
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
