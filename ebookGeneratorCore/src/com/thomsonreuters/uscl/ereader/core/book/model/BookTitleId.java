/*
 * Copyright 2016: Thomson Reuters Global Resources. All Rights Reserved.
 * Proprietary and Confidential information of TRGR. Disclosure, Use or
 * Reproduction without the written authorization of TRGR is prohibited
 */
package com.thomsonreuters.uscl.ereader.core.book.model;

import lombok.Data;
import lombok.NonNull;
import org.jetbrains.annotations.NotNull;

@Data
public class BookTitleId implements Comparable<BookTitleId> {
    private static final String TITLE_WITH_VERSION = "%s/%s";
    @NonNull
    private final String titleId;
    @NonNull
    private final Version version;

    @NotNull
    public String getTitleIdWithMajorVersion() {
        return String.format(TITLE_WITH_VERSION, titleId, version.getMajorVersion());
    }

    @NotNull
    public String getTitleIdWithVersion() {
        return String.format(TITLE_WITH_VERSION, titleId, version.getFullVersion());
    }

    @Override
    public int compareTo(final BookTitleId o) {
        return titleId.compareTo(o.titleId);
    }
}
