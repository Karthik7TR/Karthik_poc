package com.thomsonreuters.uscl.ereader.gather.img.service;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;

import javax.activation.MimetypesFileTypeMap;
import javax.imageio.ImageIO;

import com.thomsonreuters.uscl.ereader.gather.domain.GatherResponse;
import com.thomsonreuters.uscl.ereader.gather.exception.GatherException;
import com.thomsonreuters.uscl.ereader.gather.img.model.ImageRequestParameters;
import com.thomsonreuters.uscl.ereader.gather.img.util.DocToImageManifestUtil;
import com.thomsonreuters.uscl.ereader.gather.img.util.ImageConverter;
import com.thomsonreuters.uscl.ereader.gather.img.util.ImageTypeResolver;
import com.thomsonreuters.uscl.ereader.gather.util.ImgMetadataInfo;
import com.thomsonreuters.uscl.ereader.gather.util.images.ImageConverterException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Copies images from unpacked XPP archive to work folder.
 */
@Slf4j
public class XppImageService implements ImageService {

    private static final String PNG = "PNG";
    private final MimetypesFileTypeMap mimetypesFileTypeMap = new MimetypesFileTypeMap();
    @Autowired
    private DocToImageManifestUtil docToImageManifestUtil;
    @Autowired
    private ImageConverter imageConverter;
    @Autowired
    private ImageTypeResolver imageTypeResolver;

    @Override
    public GatherResponse getImages(final ImageRequestParameters imageRequestParameters) throws GatherException, IOException {
        final Map<String, ImgMetadataInfo> imageFiles = copyImagesToWorkDir(
            imageRequestParameters.getXppSourceImageDirectory(),
            imageRequestParameters.getDynamicImageDirectory());

        final Map<String, Set<String>> docsWithImages =
            docToImageManifestUtil.getDocsWithImages(imageRequestParameters.getDocToImageManifestFile());

        final List<ImgMetadataInfo> imagesMetadata = new ArrayList<>();
        final List<String> missingImagesList = fillMetadataWithDocIds(imagesMetadata, imageFiles, docsWithImages);

        return populateResponse(imagesMetadata, missingImagesList);
    }

    private GatherResponse populateResponse(final List<ImgMetadataInfo> imagesMetadata, final List<String> missingImagesList) {
        final GatherResponse response = new GatherResponse();
        response.setImageMetadataList(imagesMetadata);
        response.setMissingImagesList(missingImagesList);
        response.setMissingImgCount(missingImagesList.size());

        return response;
    }

    private List<String> fillMetadataWithDocIds(
        final List<ImgMetadataInfo> imagesMetadata,
        final Map<String, ImgMetadataInfo> imageFiles,
        final Map<String, Set<String>> docsWithImages) {
        final List<String> missingImagesList = imageFiles.entrySet().stream().map(Entry::getValue).
            filter(ImgMetadataInfo::getIsMissed).map(ImgMetadataInfo::getImgGuid).collect(Collectors.toList());

        for (final Entry<String, Set<String>> e : docsWithImages.entrySet()) {
            final String docId = e.getKey();
            for (final String imageId : e.getValue()) {
                try {
                    final ImgMetadataInfo imageMetadata = new ImgMetadataInfo(imageFiles.get(imageId));
                    imageMetadata.setDocGuid(docId);
                    imagesMetadata.add(imageMetadata);
                } catch (final Exception e1) {
                    log.error(e1.getMessage());
                    missingImagesList.add(imageId);
                }
            }
        }
        return missingImagesList;
    }

    private ImgMetadataInfo getImageMetadata(
        final BufferedImage bufferedImage,
        final String imageId,
        final File imageFile) throws IOException {
        final ImgMetadataInfo metadata = new ImgMetadataInfo();
        metadata.setMimeType(getMimeType(imageFile));
        metadata.setSize(imageFile.length());
        metadata.setImgGuid(imageId);
        if (bufferedImage == null) {
            metadata.setIsMissed(true);
        } else {
        metadata.setWidth((long) bufferedImage.getWidth());
        metadata.setHeight((long) bufferedImage.getHeight());
        }
        return metadata;
    }

    private String getMimeType(final File imageFile) throws IOException {
        String mimeType = Files.probeContentType(imageFile.toPath());

        if (mimeType == null) {
            mimeType = mimetypesFileTypeMap.getContentType(imageFile.getAbsolutePath());
        }
        return mimeType;
    }

    private Map<String, ImgMetadataInfo> copyImagesToWorkDir(
        final Collection<String> xppSourceImageDirectories,
        final File destDir) {
        final Map<String, ImgMetadataInfo> imageFiles = new HashMap<>();

        for (final String xppSourceImageDirectory : xppSourceImageDirectories) {
            imageFiles.putAll(copyImagesToWorkDir(xppSourceImageDirectory, destDir));
        }
        return imageFiles;
    }

    private Map<String, ImgMetadataInfo> copyImagesToWorkDir(final String xppSourceImageDirectory, final File destDir) {
        final File[] srcImages = new File(xppSourceImageDirectory).listFiles();
        final Map<String, ImgMetadataInfo> imageFiles = new HashMap<>();
        String imageId = null;
        if (srcImages != null) {
            for (final File srcImage : srcImages) {
                ImgMetadataInfo metadata = null;
                try {
                    imageId = FilenameUtils.removeExtension(srcImage.getName());
                    metadata = writeImage(imageId, srcImage, destDir);
                    imageFiles.put(imageId, metadata);
                } catch (final IOException e) {
                    log.error(e.getMessage());
                    imageFiles.put(imageId, metadata);
                }
            }
        }
        return imageFiles;
    }

    private ImgMetadataInfo writeImage(final String imageId, final File srcImage, final File destDir)
        throws IOException {
        final boolean isTiff = imageTypeResolver.isTiff(srcImage);
        final File destImage = new File(destDir, getDestImageName(imageId, srcImage, isTiff));

        BufferedImage image = null;
        if (isTiff) {
            final byte[] imgBytes;
            try {
                imgBytes = Files.readAllBytes(srcImage.toPath());
                image = imageConverter.convertByteImg(imgBytes, destImage.getAbsolutePath(), PNG);
            } catch (final ImageConverterException exeption) {
                log.error(exeption.getMessage());
                image = null;
            }
        } else {
            FileUtils.copyFile(srcImage, destImage);
            image = ImageIO.read(destImage);
        }
        return getImageMetadata(image, imageId, destImage);
    }

    private String getDestImageName(final String imageId, final File srcImage, final boolean isTiff) {
        final boolean hasTiffExtension = imageTypeResolver.hasTiffExtension(srcImage);
        final boolean noExtension = FilenameUtils.getExtension(srcImage.getName()).isEmpty();
        return hasTiffExtension || (noExtension && isTiff) ? imageId + "." + PNG : srcImage.getName();
    }
}
