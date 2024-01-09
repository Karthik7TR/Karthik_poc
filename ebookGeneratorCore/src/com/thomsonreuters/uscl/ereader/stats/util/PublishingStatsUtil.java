package com.thomsonreuters.uscl.ereader.stats.util;

import javax.validation.constraints.NotNull;

/**
 * Calculates the condition of successful publication
 */
public interface PublishingStatsUtil {
    /**
     * @param publishStatus status
     * @return {@code true} if published successfully, {@code false} otherwise
     */
    boolean isPublishedSuccessfully(@NotNull String publishStatus);
}
