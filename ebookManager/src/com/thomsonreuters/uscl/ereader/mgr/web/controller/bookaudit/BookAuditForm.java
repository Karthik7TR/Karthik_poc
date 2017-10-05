package com.thomsonreuters.uscl.ereader.mgr.web.controller.bookaudit;

import com.thomsonreuters.uscl.ereader.mgr.web.controller.PageAndSort;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class BookAuditForm {
    public static final String FORM_NAME = "ebookAuditForm";

    public enum DisplayTagSortProperty {
        TITLE_ID,
        BOOK_NAME,
        BOOK_DEFINITION_ID,
        SUBMITTED_DATE,
        ACTION,
        SUBMITTED_BY,
        COMMENT
    }

    private PageAndSort<DisplayTagSortProperty> pageAndSort = new PageAndSort<>(); // sort, page, dir, objectsPerPage

    public BookAuditForm() {
        super();
    }

    public String getDir() {
        return (pageAndSort.isAscendingSort()) ? "asc" : "desc";
    }

    public Integer getPage() {
        return pageAndSort.getPageNumber();
    }

    public DisplayTagSortProperty getSort() {
        return pageAndSort.getSortProperty();
    }

    public Integer getObjectsPerPage() {
        return pageAndSort.getObjectsPerPage();
    }

    public boolean isAscendingSort() {
        return pageAndSort.isAscendingSort();
    }

    public void setDir(final String direction) {
        pageAndSort.setAscendingSort("asc".equals(direction));
    }

    public void setObjectsPerPage(final Integer objectsPerPage) {
        pageAndSort.setObjectsPerPage(objectsPerPage);
    }

    public void setPage(final Integer pageNumber) {
        pageAndSort.setPageNumber(pageNumber);
    }

    public void setSort(final DisplayTagSortProperty sortProperty) {
        pageAndSort.setSortProperty(sortProperty);
    }

    @Override
    public String toString() {
        return ReflectionToStringBuilder.toString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }
}
