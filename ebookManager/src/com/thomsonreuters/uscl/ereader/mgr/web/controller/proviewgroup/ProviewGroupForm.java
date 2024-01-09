package com.thomsonreuters.uscl.ereader.mgr.web.controller.proviewgroup;

import static org.apache.commons.lang3.StringUtils.isBlank;

import com.thomsonreuters.uscl.ereader.mgr.web.controller.PageAndSort;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ProviewGroupForm {
    public static final String FORM_NAME = "proviewGroupForm";

    private static final String ASC_SORT = "asc";
    private static final String DESC_SORT = "desc";

    public enum Command {
        REFRESH
    }

    private Command command;
    private String objectsPerPage;

    // filtering parameters
    private String groupFilterName;
    private String groupFilterId;

    public enum DisplayTagSortProperty {
        GROUP_NAME,
        GROUP_ID,
        LATEST_STATUS,
        TOTAL_VERSIONS,
        LATEST_VERSION,
        LATEST_STATUS_UPDATE
    }

    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    private final PageAndSort<DisplayTagSortProperty> pageAndSort =
            new PageAndSort<>(1, DisplayTagSortProperty.GROUP_NAME, true); // sort, page, dir, objectsPerPage

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

    public boolean areAllFiltersBlank() {

        return isBlank(getGroupFilterName()) && isBlank(getGroupFilterId());
    }
}
