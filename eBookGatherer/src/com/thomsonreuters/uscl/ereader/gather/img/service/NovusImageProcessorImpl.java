package com.thomsonreuters.uscl.ereader.gather.img.service;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.PostConstruct;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Required;

import com.thomsonreuters.uscl.ereader.gather.img.model.NovusImage;
import com.thomsonreuters.uscl.ereader.gather.img.util.ImageConverter;
import com.thomsonreuters.uscl.ereader.gather.util.ImgMetadataInfo;

/**
 * Get images form Novus, write them to local folder and collect metadata
 *
 * @author Ilia Bochkarev UC220946
 *
 */
public class NovusImageProcessorImpl implements NovusImageProcessor
{
    private static final Logger Log = LogManager.getLogger(NovusImageProcessorImpl.class);

    private static final String PNG = "png";
    private static final String PNG_FORMAT = "PNG";

    private NovusImageFinder imageFinder;
    private File dynamicImageDirectory;
    private String missingImageGuidsFileBasename;
    private ImageConverter imageConverter;

    @NotNull
    private List<ImgMetadataInfo> imagesMetadata = new ArrayList<>();
    private Map<String, Set<String>> processed = new HashMap<>();
    private int missingImageCount;
    private Writer missingImageFileWriter;

    @PostConstruct
    public void init() throws FileNotFoundException
    {
        final File missingImagesFile = new File(dynamicImageDirectory.getParent(), missingImageGuidsFileBasename);
        final FileOutputStream stream = new FileOutputStream(missingImagesFile);
        missingImageFileWriter = new OutputStreamWriter(stream, StandardCharsets.UTF_8);
    }

    @Override
    public void process(@NotNull final String imageId, @NotNull final String docId)
    {
        saveProcessed(docId, imageId);

        final NovusImage image = imageFinder.getImage(imageId);
        if (image == null)
        {
            processFail(imageId, docId);
            return;
        }

        try
        {
            processSuccess(image, imageId, docId);
        }
        catch (final Exception e)
        {
            Log.error("Failed while writing the image for imageGuid " + imageId, e);
            processFail(imageId, docId);
        }
    }

    private void saveProcessed(final String docId, final String imageId)
    {
        if (processed.get(docId) == null)
        {
            processed.put(docId, new HashSet<String>());
        }
        processed.get(docId).add(imageId);
    }

    private void processSuccess(final NovusImage image, final String imageId, final String docId) throws IOException
    {
        final String extension = image.isTiffImage() ? PNG : image.getMediaSubTypeString();
        final File imageFile = new File(dynamicImageDirectory, imageId + "." + extension);

        writeImage(image, imageFile);
        if (image.isUnknownFormat())
        {
            Log.debug("Unfamiliar Image format " + image.getMediaSubTypeString() + " found for imageGuid " + imageId);
        }

        final ImgMetadataInfo metadata = image.getMetadata();
        metadata.setMimeType(image.getMediaTypeString() + "/" + extension);
        metadata.setSize(imageFile.length());
        metadata.setDocGuid(docId);
        metadata.setImgGuid(imageId);
        imagesMetadata.add(metadata);
    }

    private void writeImage(final NovusImage image, final File imageFile) throws IOException
    {
        final byte[] content = image.getContent();
        if (image.isTiffImage())
        {
            imageConverter.convertByteImg(content, imageFile.getAbsolutePath(), PNG_FORMAT);
        }
        else
        {
            FileUtils.writeByteArrayToFile(imageFile, content);
        }
    }

    private void processFail(final String imageId, final String docId)
    {
        missingImageCount++;
        try
        {
            missingImageFileWriter.write(imageId + "," + docId);
            missingImageFileWriter.write("\n");
        }
        catch (final IOException e)
        {
            throw new RuntimeException("Cannot write to missing images file", e);
        }
    }

    @Override
    public boolean isProcessed(@NotNull final String imageId, @NotNull final String docId)
    {
        final Set<String> imageIds = processed.get(docId);
        if (imageIds == null)
        {
            return false;
        }
        return imageIds.contains(imageId);
    }

    @NotNull
    @Override
    public List<ImgMetadataInfo> getImagesMetadata()
    {
        return imagesMetadata;
    }

    @Override
    public int getMissingImageCount()
    {
        return missingImageCount;
    }

    @Override
    public void close() throws Exception
    {
        imageFinder.close();
        missingImageFileWriter.close();
    }

    @Required
    public void setImageFinder(final NovusImageFinder imageFinder)
    {
        this.imageFinder = imageFinder;
    }

    @Required
    public void setDynamicImageDirectory(final File dynamicImageDirectory)
    {
        this.dynamicImageDirectory = dynamicImageDirectory;
    }

    @Required
    public void setMissingImageGuidsFileBasename(final String missingImageGuidsFileBasename)
    {
        this.missingImageGuidsFileBasename = missingImageGuidsFileBasename;
    }

    @Required
    public void setImageConverter(final ImageConverter imageConverter)
    {
        this.imageConverter = imageConverter;
    }
}
