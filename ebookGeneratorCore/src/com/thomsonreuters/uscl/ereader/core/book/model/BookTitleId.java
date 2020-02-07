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
        int versionStartIndex = titleIdWithMajorVersion.lastIndexOf("/v");
        Assert.isTrue(versionStartIndex != -1, "String should match pattern: <titleId>v<major_version>.[<minor_version>]");
        String titleIdWithoutVersion = StringUtils.substringBeforeLast(titleIdWithMajorVersion, "/v");
        String version = titleIdWithMajorVersion.substring(versionStartIndex + 1);
        this.version = new Version(version);
        this.titleId = titleIdWithoutVersion;
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

    @Override
    public int compareTo(final BookTitleId o) {
        return titleId.compareTo(o.titleId);
    }
}
