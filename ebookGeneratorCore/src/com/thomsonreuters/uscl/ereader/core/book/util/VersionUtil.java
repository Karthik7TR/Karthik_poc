/*
 * Copyright 2016: Thomson Reuters Global Resources. All Rights Reserved.
 * Proprietary and Confidential information of TRGR. Disclosure, Use or
 * Reproduction without the written authorization of TRGR is prohibited
 */
package com.thomsonreuters.uscl.ereader.core.book.util;

import com.thomsonreuters.uscl.ereader.core.book.model.Version;
import org.jetbrains.annotations.NotNull;

public interface VersionUtil {
    /**
     * Check if this is a new major version or not
     * @param current current version
     * @param next new version
     * @return {@code true} if new version is major, {@code false} otherwise
     */
    boolean isMajorUpdate(@NotNull Version current, @NotNull Version next);
}
