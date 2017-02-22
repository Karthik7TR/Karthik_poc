package com.thomsonreuters.uscl.ereader.gather.img.service;

import com.thomsonreuters.uscl.ereader.gather.domain.GatherResponse;
import com.thomsonreuters.uscl.ereader.gather.exception.GatherException;
import com.thomsonreuters.uscl.ereader.gather.img.model.ImageRequestParameters;
import org.jetbrains.annotations.NotNull;

/**
 * Service for getting images Novus and performing all related tasks e.g writing
 * missing images file, etc.
 *
 * @author Ilia Bochkarev UC220946
 *
 */
public interface ImageService
{
    /**
     * Get images from Novus, do all related tasks and get result response
     *
     * @param imageRequestParameters
     *            all parameters to get and process images
     * @return response with meta info about images
     * @throws GatherException if failed to get images from Novus
     */
    @NotNull
    GatherResponse getImages(@NotNull ImageRequestParameters imageRequestParameters)
        throws GatherException;
}
