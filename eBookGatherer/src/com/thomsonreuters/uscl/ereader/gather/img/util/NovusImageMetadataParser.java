package com.thomsonreuters.uscl.ereader.gather.img.util;

import org.jetbrains.annotations.NotNull;

import com.thomsonreuters.uscl.ereader.gather.util.ImgMetadataInfo;

/**
 * Parser to map Novus image metadata to object
 * @author Ilia Bochkarev UC220946
 *
 */
public interface NovusImageMetadataParser
{
    /**
     * Get metadata info from Novus
     * @param metadata string representation of metadata
     * @return metadata object
     */
    @NotNull
    ImgMetadataInfo parse(@NotNull String metadata);
}
