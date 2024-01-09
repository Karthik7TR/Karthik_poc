package com.thomsonreuters.uscl.ereader.gather.img.util;

import java.io.File;
import java.util.Map;
import java.util.Set;

import org.jetbrains.annotations.NotNull;

/**
 * Utils to interact with Format/doc-to-image-manifest.txt
 *
 * @author Ilia Bochkarev UC220946
 *
 */
public interface DocToImageManifestUtil {
    /**
     * Get map containing set of image IDs for every doc ID
     *
     * @param docToImageManifestFile
     *            doc-to-image-manifest.txt file. Should exist.
     * @return map with image IDs for every doc ID
     */
    @NotNull
    Map<String, Set<String>> getDocsWithImages(@NotNull File docToImageManifestFile);
}
