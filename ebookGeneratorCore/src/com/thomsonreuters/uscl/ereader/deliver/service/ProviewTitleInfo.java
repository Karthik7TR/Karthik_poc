package com.thomsonreuters.uscl.ereader.deliver.service;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import com.thomsonreuters.uscl.ereader.core.book.model.TitleId;
import com.thomsonreuters.uscl.ereader.core.book.model.Version;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.apache.commons.lang.StringUtils;

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

    private String titleIdCaseSensitive;
    private String publisher;
    private String lastupdate;
    private String status;
    private String title;
    private Integer totalNumberOfVersions;
    private String lastStatusUpdateDate;
    @Setter(AccessLevel.NONE)
    private List<String> splitParts;

    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    private TitleId titleId;

    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    private Version version;

    public String getVersion() {
        return version == null ? null : version.getVersion();
    }

    public void setVersion(final String version) {
        this.version = StringUtils.isEmpty(version) ? null : new Version(version);
    }

    public TitleId getTitleIdObject() {
        return titleId;
    }

    public String getTitleId() {
        return titleId == null ? null : titleId.getTitleId();
    }

    public void setTitleId(final String titleId) {
        this.titleId = new TitleId(titleId == null ? StringUtils.EMPTY : titleId);
    }

    public void setSplitParts(final List<String> splitParts) {
        this.splitParts = new CopyOnWriteArrayList<>(splitParts);
    }

    @Override
    public BigInteger getMajorVersion() {
        return version == null ? null : version.getMajorNumber();
    }

    @Override
    public BigInteger getMinorVersion() {
        return version == null ? null : version.getMinorNumber();
    }

    @Override
    public int compareTo(final ProviewTitleInfo info) {
        int versionDiff = compare(info.getMajorVersion(), getMajorVersion());
        return versionDiff == 0 ? compare(getTitleIdObject(), info.getTitleIdObject()) : versionDiff;
    }

    private <T extends Comparable<T>> int compare(T a, T b) {
        return (a == b) ? 0 : a.compareTo(b);
    }
}
