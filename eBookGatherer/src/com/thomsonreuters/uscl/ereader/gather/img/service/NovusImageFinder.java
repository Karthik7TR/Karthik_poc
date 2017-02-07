package com.thomsonreuters.uscl.ereader.gather.img.service;

import com.thomsonreuters.uscl.ereader.gather.img.model.NovusImage;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Finds image from Novus using Novus API
 *
 * @author Ilia Bochkarev UC220946
 *
 */
public interface NovusImageFinder extends AutoCloseable
{
    /**
     * Get image from Novus
     *
     * @param imageId
     *            image id
     * @return image data and metadata or {@code null} if failed to get image
     *         from Novus
     */
    @Nullable
    NovusImage getImage(@NotNull String imageId);
}
