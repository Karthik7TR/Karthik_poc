package com.thomsonreuters.uscl.ereader.mgr.web.controller.stats;

import static org.apache.commons.lang3.StringUtils.isBlank;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.thomsonreuters.uscl.ereader.core.CoreConstants;
import com.thomsonreuters.uscl.ereader.mgr.web.controller.PageAndSort;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.apache.commons.lang3.time.DateUtils;

/**
 * The form backing object that holds the data the user enters into the Publishing Stats filter HTML form.
 */
@Getter
@Setter
public class PublishingStatsFilterForm {
    public static final String FORM_NAME = "publishingStatsFilterForm";
    private static final String ASC_SORT = "asc";
    private static final String DESC_SORT = "desc";

    public enum DisplayTagSortProperty {
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

    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    private final PageAndSort<DisplayTagSortProperty> pageAndSort =
            new PageAndSort<>(1, DisplayTagSortProperty.JOB_SUBMIT_TIMESTAMP, false);
    @Setter(AccessLevel.NONE)
    private String proviewDisplayName;
    @Setter(AccessLevel.NONE)
    private String titleId;
    private Long bookDefinitionId;
    private String fromDateString;
    private String toDateString;

    public String getDir() {
        return (pageAndSort.isAscendingSort()) ? ASC_SORT : DESC_SORT;
    }

    public void setDir(final String direction) {
        pageAndSort.setAscendingSort(ASC_SORT.equals(direction));
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

    public Integer getObjectsPerPage() {
        return pageAndSort.getObjectsPerPage();
    }

    public void setObjectsPerPage(final Integer objectsPerPage) {
        pageAndSort.setObjectsPerPage(objectsPerPage);
    }

    public Date getFromDate() {
        return parseDate(fromDateString);
    }

    public Date getToDate() {
        return parseDate(toDateString);
    }

    public void setProviewDisplayName(final String name) {
        proviewDisplayName = (name != null) ? name.trim() : null;
    }

    public void setTitleId(final String titleId) {
        this.titleId = (titleId != null) ? titleId.trim() : null;
    }

    public void setFromDate(final Date fromDate) {
        fromDateString = parseDate(fromDate);
    }

    public void setToDate(final Date toDate) {
        toDateString = parseDate(toDate);
    }

    public static String parseDate(final Date date) {
        if (date != null) {
            final SimpleDateFormat sdf = new SimpleDateFormat(CoreConstants.DATE_TIME_FORMAT_PATTERN);
            return sdf.format(date);
        }
        return null;
    }

    public static Date parseDate(final String dateString) {
        Date date = null;
        try {
            if (StringUtils.isNotBlank(dateString)) {
                final String[] parsePatterns = {CoreConstants.DATE_TIME_FORMAT_PATTERN};
                date = DateUtils.parseDate(dateString, parsePatterns);
            }
        } catch (final ParseException e) {
            //Intentionally left blank
        }
        return date;
    }

    public boolean areAllFiltersBlank() {
        return isBlank(getProviewDisplayName())
                && isBlank(getTitleId())
                && getBookDefinitionId() == null
                && isBlank(getFromDateString())
                && isBlank(getToDateString());
    }

    @Override
    public String toString() {
        return ReflectionToStringBuilder.toString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }
}
