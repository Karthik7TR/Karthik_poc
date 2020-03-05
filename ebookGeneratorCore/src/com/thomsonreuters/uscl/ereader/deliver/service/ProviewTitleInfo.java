package com.thomsonreuters.uscl.ereader.deliver.service;

import java.io.Serializable;

import com.thomsonreuters.uscl.ereader.core.book.model.Version;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * Placeholder for proview title info
 *
 * @author U0057241
 *
 */
@Getter
@Setter
@ToString(of = {"titleId", "version", "publisher", "lastupdate", "status", "title"})
@EqualsAndHashCode(of = {"lastupdate", "publisher", "status", "title", "titleId", "version"})
public class ProviewTitleInfo implements TitleInfo, Serializable, Comparable<ProviewTitleInfo> {
    private static final long serialVersionUID = -4229230493652304110L;
    private String titleId;
    private String version;

    private String publisher;
    private String lastupdate;
    private String status;
    private String title;
    private Integer totalNumberOfVersions;
    private String lastStatusUpdateDate;
    private Integer splitParts;

    @Override
    public Integer getMajorVersion() {
        return new Version(version).getMajorNumber();
    }

    @Override
    public Integer getMinorVersion() {
        return new Version(version).getMinorNumber();
    }

    @Override
    public int compareTo(final ProviewTitleInfo info) {
        final int versionDiff = info.getMajorVersion().compareTo(getMajorVersion());
        return versionDiff == 0 ? getTitleId().compareToIgnoreCase(info.getTitleId()) : versionDiff;
    }
}
