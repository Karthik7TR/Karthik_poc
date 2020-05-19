package com.thomsonreuters.uscl.ereader.mgr.web.controller.proviewlist;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class ProviewListFilterForm {
    public static final String FORM_NAME = "proviewListFilterForm";

    public enum FilterCommand {
        SEARCH,
        RESET
    };

    @Getter
    private String proviewDisplayName;
    @Getter
    private String titleId;
    @Getter
    private String minVersions;
    @Getter
    private String maxVersions;
    @Getter @Setter
    private Integer minVersionsInt;
    @Getter @Setter
    private Integer maxVersionsInt;
    @Getter @Setter
    private FilterCommand filterCommand;

    public void initNull() {
        init(null, null, null, null);
    }

    private void init(
        final String proviewDisplayName,
        final String titleId,
        final String minVersions,
        final String maxVersions) {
        this.proviewDisplayName = proviewDisplayName;
        this.titleId = titleId;
        this.minVersions = minVersions;
        this.maxVersions = maxVersions;
    }

    public void setMinVersions(final String minVersions) {
        this.minVersions = minVersions == null ? null : minVersions.trim();
        try {
            minVersionsInt = Integer.parseInt(minVersions);
        } catch (final NumberFormatException e) {
            this.minVersions = null;
            minVersionsInt = 0;
        }
    }

    public void setMaxVersions(final String maxVersions) {
        this.maxVersions = maxVersions == null ? null : maxVersions.trim();
        try {
            maxVersionsInt = Integer.parseInt(maxVersions);
        } catch (final NumberFormatException e) {
            this.maxVersions = null;
            maxVersionsInt = 99999;
        }
    }

    public void setProviewDisplayName(final String proviewDisplayName) {
        this.proviewDisplayName = proviewDisplayName == null ? null : proviewDisplayName.trim();
    }

    public void setTitleId(final String titleId) {
        this.titleId = titleId == null ? null : titleId.trim();
    }

    @Override
    public String toString() {
        return ReflectionToStringBuilder.toString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }
}
