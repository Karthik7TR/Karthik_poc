package com.thomsonreuters.uscl.ereader.mgr.web.controller.proviewlist;

import static org.apache.commons.lang3.StringUtils.isBlank;

import java.io.Serializable;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class ProviewListFilterForm implements Serializable {
    public static final long serialVersionUID = 2337345234238574L;
    public static final String FORM_NAME = "proviewListFilterForm";
    private static final Integer MIN_VERSIONS_DEFAULT = 0;
    private static final Integer MAX_VERSIONS_DEFAULT = 99999;

    public enum Command {
        REFRESH
    }

    @Getter
    private String proviewDisplayName;
    @Getter
    private String titleId;
    @Getter
    private String minVersions;
    @Getter
    private String maxVersions;
    @Getter @Setter
    private Integer minVersionsInt = MIN_VERSIONS_DEFAULT;
    @Getter @Setter
    private Integer maxVersionsInt = MAX_VERSIONS_DEFAULT;
    @Getter @Setter
    private Command command;
    @Getter @Setter
    private String objectsPerPage;
    @Getter @Setter
    private String status;

    public boolean areAllFiltersBlank() {
        return isBlank(getProviewDisplayName()) && isBlank(getTitleId())
                && isBlank(getMinVersions()) && isBlank(getMaxVersions())
                && isBlank(getStatus());
    }

    @SuppressWarnings("unused")
    public void setMinVersions(final String minVersions) {
        this.minVersions = minVersions == null ? null : minVersions.trim();
        try {
            minVersionsInt = Integer.parseInt(minVersions);
        } catch (final NumberFormatException e) {
            this.minVersions = null;
            minVersionsInt = MIN_VERSIONS_DEFAULT;
        }
    }

    @SuppressWarnings("unused")
    public void setMaxVersions(final String maxVersions) {
        this.maxVersions = maxVersions == null ? null : maxVersions.trim();
        try {
            maxVersionsInt = Integer.parseInt(maxVersions);
        } catch (final NumberFormatException e) {
            this.maxVersions = null;
            maxVersionsInt = MAX_VERSIONS_DEFAULT;
        }
    }

    @SuppressWarnings("unused")
    public void setProviewDisplayName(final String proviewDisplayName) {
        this.proviewDisplayName = proviewDisplayName == null ? null : proviewDisplayName.trim();
    }

    @SuppressWarnings("unused")
    public void setTitleId(final String titleId) {
        this.titleId = titleId == null ? null : titleId.trim();
    }

    @Override
    public String toString() {
        return ReflectionToStringBuilder.toString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }
}
