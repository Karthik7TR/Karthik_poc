package com.thomsonreuters.uscl.ereader.deliver.service;

import com.thomsonreuters.uscl.ereader.core.book.model.TitleId;
import com.thomsonreuters.uscl.ereader.core.book.model.Version;
import lombok.*;
import org.apache.commons.lang.StringUtils;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.Date;
import java.util.List;

@Getter
@Setter
@ToString(of = {"id", "version", "name"})
@EqualsAndHashCode(of = {"id", "version", "name"})
@AllArgsConstructor
@NoArgsConstructor
public class ProviewTitleReportInfo implements TitleInfo, Serializable, Comparable<ProviewTitleReportInfo> {

    private static final long serialVersionUID = -4229230493652304110L;

    private String id; //titleId
    //private String version;
    private String status;
    private String name; //bookName
    private List<String> authors;
    private String isbn;
    private String lastupdate;
    private List<ProviewTitleReportKeyword> keyword;
    private String materialId;
    private Integer totalNumberOfVersions;
    @Setter(AccessLevel.NONE)
    private List<String> splitParts;
    private String publisher;

    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    private TitleId titleId;

    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    private Version version;

    public String getTitle() { return id; }
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

    @Override
    public BigInteger getMajorVersion() {
        return version == null ? null : version.getMajorNumber();
    }

    @Override
    public BigInteger getMinorVersion() {
        return version == null ? null : version.getMinorNumber();
    }

    @Override
    public int compareTo(final ProviewTitleReportInfo info) {
        int versionDiff = compare(info.getMajorVersion(), getMajorVersion());
        return versionDiff == 0 ? compare(getTitleIdObject(), info.getTitleIdObject()) : versionDiff;
    }

    private <T extends Comparable<T>> int compare(T a, T b) {
        return (a == b) ? 0 : a.compareTo(b);
    }

}
