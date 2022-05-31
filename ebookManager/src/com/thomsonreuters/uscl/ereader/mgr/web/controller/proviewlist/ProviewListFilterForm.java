package com.thomsonreuters.uscl.ereader.mgr.web.controller.proviewlist;

import static org.apache.commons.lang3.StringUtils.isBlank;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.thomsonreuters.uscl.ereader.deliver.service.ProviewTitleInfo;
import com.thomsonreuters.uscl.ereader.mgr.web.controller.PageAndSort;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class ProviewListFilterForm implements Serializable {
    public static final long serialVersionUID = 2337345234238574L;
    public static final String FORM_NAME = "proviewListFilterForm";
    private static final Integer MIN_VERSIONS_DEFAULT = 0;
    private static final Integer MAX_VERSIONS_DEFAULT = 99999;
    private static final String ASC_SORT = "asc";
    private static final String DESC_SORT = "desc";

    public enum Command {
        REFRESH
    }

    public enum DisplayTagSortProperty {
        PROVIEW_DISPLAY_NAME,
        TITLE_ID,
        TOTAL_VERSIONS,
        SPLIT_PARTS,
        LATEST_VERSION,
        STATUS,
        LAST_UPDATE,
        PUBLISHER,
        LATEST_STATUS_UPDATE,
        ACTION
    }
    @Getter @Setter
    private Integer proviewTitleListFullSize =0;
    @Getter @Setter
    private List<ProviewTitleInfo> proviewTitleListFull= new ArrayList<ProviewTitleInfo>();
    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    private final PageAndSort<DisplayTagSortProperty> pageAndSort =
            new PageAndSort<>(1, DisplayTagSortProperty.PROVIEW_DISPLAY_NAME, true); // sort, page, dir, objectsPerPage

    public String getDir() {
        return (pageAndSort.isAscendingSort()) ? ASC_SORT : DESC_SORT;
    }

    public void setDir(final String direction) {
        pageAndSort.setAscendingSort(ASC_SORT.equals(direction));
    }

    public Integer getObjectsPerPage() {
        return pageAndSort.getObjectsPerPage();
    }

    public void setObjectsPerPage(final Integer objectsPerPage) {
        pageAndSort.setObjectsPerPage(objectsPerPage);
    }

    public Integer getPage() {
        return pageAndSort.getPageNumber();
    }

    public void setPage(final Integer pageNumber) {
        pageAndSort.setPageNumber(pageNumber);
    }

    public DisplayTagSortProperty getSort() {
        return pageAndSort.getSortProperty();
    }

    public void setSort(final DisplayTagSortProperty sortProperty) {
        pageAndSort.setSortProperty(sortProperty);
    }

    public boolean isAscendingSort() {
        return pageAndSort.isAscendingSort();
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
