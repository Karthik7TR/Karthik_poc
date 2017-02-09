package com.thomsonreuters.uscl.ereader.mgr.web.controller.stats;

import com.thomsonreuters.uscl.ereader.mgr.web.controller.PageAndSort;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class PublishingStatsForm
{
    public static final String FORM_NAME = "publishingStatsForm";

    public enum DisplayTagSortProperty
    {
        JOB_INSTANCE_ID,
        AUDIT_ID,
        EBOOK_DEFINITION_ID,
        JOB_SUBMITTER,
        JOB_SUBMIT_TIMESTAMP,
        BOOK_VERSION,
        PUBLISH_STATUS,
        BOOK_SIZE,
        LARGEST_DOC_SIZE,
        LARGEST_IMAGE_SIZE,
        LARGEST_PDF_SIZE,
        PROVIEW_DISPLAY_NAME,
        TITLE_ID
    }

    private PageAndSort<DisplayTagSortProperty> pageAndSort = new PageAndSort<>(); // sort, page, dir, objectsPerPage

    public PublishingStatsForm()
    {
        super();
    }

    public String getDir()
    {
        return (pageAndSort.isAscendingSort()) ? "asc" : "desc";
    }

    public Integer getPage()
    {
        return pageAndSort.getPageNumber();
    }

    public DisplayTagSortProperty getSort()
    {
        return pageAndSort.getSortProperty();
    }

    public Integer getObjectsPerPage()
    {
        return pageAndSort.getObjectsPerPage();
    }

    public boolean isAscendingSort()
    {
        return pageAndSort.isAscendingSort();
    }

    public void setDir(final String direction)
    {
        pageAndSort.setAscendingSort("asc".equals(direction));
    }

    public void setObjectsPerPage(final Integer objectsPerPage)
    {
        pageAndSort.setObjectsPerPage(objectsPerPage);
    }

    public void setPage(final Integer pageNumber)
    {
        pageAndSort.setPageNumber(pageNumber);
    }

    public void setSort(final DisplayTagSortProperty sortProperty)
    {
        pageAndSort.setSortProperty(sortProperty);
    }

    @Override
    public String toString()
    {
        return ReflectionToStringBuilder.toString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }
}
