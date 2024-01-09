package com.thomsonreuters.uscl.ereader.gather.image.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.thomsonreuters.uscl.ereader.common.filesystem.NasFileSystem;
import com.thomsonreuters.uscl.ereader.gather.image.dao.ImageDao;
import com.thomsonreuters.uscl.ereader.gather.image.domain.ImageException;
import com.thomsonreuters.uscl.ereader.gather.image.domain.ImageMetadataEntity;
import com.thomsonreuters.uscl.ereader.gather.image.domain.ImageMetadataEntityKey;
import com.thomsonreuters.uscl.ereader.gather.util.ImgMetadataInfo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.http.MediaType;
import org.springframework.transaction.annotation.Transactional;

public class ImageServiceImpl implements ImageService {
    /** The DAO for persisting image meta-data */
    private ImageDao imageDao;

    @Autowired
    private NasFileSystem nasFileSystem;

    /**
     * If the metadata content type is an image, then return a desired type of "image/png"
     * otherwise return null which indicates to return it in whatever form it is sorted.
     * This covers the case of application/png.
     * @param metadataMediaType indicated content type from an image metadata request.
     * @return
     */
    public static MediaType fetchDesiredMediaType(final MediaType metadataMediaType) {
        if (metadataMediaType == null) {
            return null;
        }

        return ("image".equals(metadataMediaType.getType())) ? MediaType.IMAGE_PNG : metadataMediaType;
    }

    @Override
    public void fetchStaticImages(final List<String> basenames, final File imageDestinationDirectory)
        throws ImageException {
        // Iterate the list of image base names
        for (final String basename : basenames) {
            final File sourceFile = searchFileTree(basename);
            if (sourceFile == null) {
                throw new ImageException("Static image not found: " + basename);
            }
            String destFileName = basename;
            if (destFileName.contains("/")) {
                destFileName = destFileName.substring(destFileName.lastIndexOf("/") + 1);
            }
            final File destFile = new File(imageDestinationDirectory, destFileName);
            try {
                copyFile(sourceFile, destFile);
            } catch (final IOException e) {
                // Remove all existing destination dir files on failure
                removeAllFilesInDirectory(imageDestinationDirectory);
                throw new ImageException("Failed to copy static image file: " + sourceFile, e);
            }
        }
    }

    @Override
    @Transactional
    public List<ImageMetadataEntity> findImageMetadata(final long jobInstanceId) {
        return imageDao.findImageMetadata(jobInstanceId);
    }

    @Override
    @Transactional
    public ImageMetadataEntity findImageMetadata(final ImageMetadataEntityKey key) {
        return imageDao.findImageMetadataByPrimaryKey(key);
    }

    /**
     * Map the container data from the REST service into an entity that is persisted
     * @param imgMetadataInfo from the Image Vertical REST service
     * @return the entity to be persisted to a database table
     */
    public static ImageMetadataEntity createImageMetadataEntity(
        final ImgMetadataInfo imgMetadataInfo,
        final long jobInstanceId,
        final String titleId) {
        final ImageMetadataEntityKey pk =
            new ImageMetadataEntityKey(jobInstanceId, imgMetadataInfo.getImgGuid(), imgMetadataInfo.getDocGuid());
        final MediaType mediaType = MediaType.valueOf(imgMetadataInfo.getMimeType());

        // Create the entity that will be persisted
        return new ImageMetadataEntity(
            pk,
            titleId,
            imgMetadataInfo.getWidth(),
            imgMetadataInfo.getHeight(),
            imgMetadataInfo.getSize(),
            imgMetadataInfo.getDpi(),
            imgMetadataInfo.getDimUnit(),
            mediaType);
    }

    @Override
    @Transactional
    public ImageMetadataEntityKey saveImageMetadata(final ImageMetadataEntity metadata) {
        return imageDao.saveImageMetadata(metadata);
    }

    @Override
    @Transactional
    public ImageMetadataEntityKey saveImageMetadata(
        final ImgMetadataInfo imgMetadataInfo,
        final long jobInstanceId,
        final String titleId) {
        final ImageMetadataEntity entity = createImageMetadataEntity(imgMetadataInfo, jobInstanceId, titleId);
        // Persist the image meta-data entity
        return this.saveImageMetadata(entity);
    }

    /**
     * Search the directory tree of static images for a file with the specified
     * basename.
     *
     * @param basename
     *            the file basename of an image file
     * @return the absolute path the the image file, or null if it was not found
     *         in the tree
     */
    private File searchFileTree(final String basename) {
        final File staticImageFile = new File(nasFileSystem.getStaticContentDirectory().getAbsolutePath() + "/images", basename);
        if (staticImageFile.exists()) {
            return staticImageFile;
        } else {
            return null;
        }
    }

    /**
     * Delete all files in the specified directory.
     * @param directory directory whose files will be removed
     */
    public static void removeAllFilesInDirectory(final File directory) {
        final File[] files = directory.listFiles();
        for (final File file : files) {
            file.delete();
        }
    }

    public static void copyFile(final File sourceFile, final File destFile) throws IOException {
        if (!destFile.exists()) {
            destFile.createNewFile();
        }
        try (FileInputStream sourceStream = new FileInputStream(sourceFile)) {
            final FileChannel source = sourceStream.getChannel();
            try (FileOutputStream destStream = new FileOutputStream(destFile)) {
                final FileChannel destination = destStream.getChannel();
                destination.transferFrom(source, 0, source.size());
            }
        }
    }

    /**
     * This method gives document Image mapping for a jobInstanceId.
     * @param jobInstanceId
     * @return mapping all the Images corresponding to a document
     */
    @Override
    public Map<String, List<String>> getDocImageListMap(final Long jobInstanceId) {
        final List<ImageMetadataEntity> imageMetadataEntityList = findImageMetadata(jobInstanceId);
        final Map<String, List<String>> mapping = new HashMap<>();
        for (final ImageMetadataEntity imageMetadataEntity : imageMetadataEntityList) {
            final ImageMetadataEntityKey primaryKey = imageMetadataEntity.getPrimaryKey();
            final String key = primaryKey.getDocUuid();
            //img holds file name. IMG guid + mediatype (application/pdf)
            final String img = primaryKey.getImageGuid()
                + "."
                + StringUtils.substringAfterLast(imageMetadataEntity.getContentType(), "/");
            List<String> value = new ArrayList<>();

            if (mapping.containsKey(key)) {
                value = mapping.get(key);
                mapping.put(key, value);
            }
            value.add(img);
            mapping.put(key, value);
        }

        return mapping;
    }

    @Required
    public void setImageDao(final ImageDao dao) {
        imageDao = dao;
    }
}
