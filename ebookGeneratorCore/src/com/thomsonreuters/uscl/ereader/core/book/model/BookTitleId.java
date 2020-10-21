/*
 * Copyright 2016: Thomson Reuters Global Resources. All Rights Reserved.
 * Proprietary and Confidential information of TRGR. Disclosure, Use or
 * Reproduction without the written authorization of TRGR is prohibited
 */
package com.thomsonreuters.uscl.ereader.core.book.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.util.Assert;

@Data
@AllArgsConstructor
public class BookTitleId implements Comparable<BookTitleId> {
    private static final String TITLE_WITH_VERSION = "%s/%s";
    @NonNull
    private final String titleId;
    @NonNull
    private final Version version;

    public BookTitleId(@NonNull String titleIdWithMajorVersion) {
        Assert.isTrue(isTitleWithVersion(titleIdWithMajorVersion), "String should match pattern: <titleId>v<major_version>.[<minor_version>]");
        String titleIdWithoutVersion = StringUtils.substringBeforeLast(titleIdWithMajorVersion, "/");
        String version = StringUtils.substringAfterLast(titleIdWithMajorVersion, "/");
        this.version = new Version(version);
        this.titleId = titleIdWithoutVersion;
    }

    private static boolean isTitleWithVersion(@NonNull final String titleIdWithMajorVersion) {
        return StringUtils.countMatches(titleIdWithMajorVersion, "/") == 3;
    }

    @NotNull
    public String getTitleIdWithMajorVersion() {
        return String.format(TITLE_WITH_VERSION, titleId, version.getMajorVersion());
    }

    @NotNull
    public String getTitleIdWithVersion() {
        return String.format(TITLE_WITH_VERSION, titleId, version.getFullVersion());
    }

    @NotNull
    public String getHeadTitleIdWithMajorVersion() {
        return String.format(TITLE_WITH_VERSION, new TitleId(titleId).getHeadTitleId(), version.getMajorVersion());
    }

    public static String getTitleIdWithoutVersion(final String titleId) {
        return isTitleWithVersion(titleId)
                ? new BookTitleId(titleId).getTitleId()
                : titleId;
    }

    @Override
    public int compareTo(final BookTitleId o) {
        return titleId.compareTo(o.titleId);
    }
}
