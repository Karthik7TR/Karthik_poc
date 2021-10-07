package com.thomsonreuters.uscl.ereader.mgr.web.controller.proviewaudit;

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
 * The form backing object that holds the data the user enters into the proview audit filter HTML form.
 */
@Getter
@Setter
public class ProviewAuditFilterForm {
    public static final String FORM_NAME = "proviewAuditFilterForm";
    public static final String ASC_SORT = "asc";
    public static final String DESC_SORT = "desc";

    public enum Action {
        PROMOTE,
        DELETE,
        REMOVE
    }

    public enum DisplayTagSortProperty {
        TITLE_ID,
        BOOK_VERSION,
        BOOK_LAST_UPDATED,
        USERNAME,
        PROVIEW_REQUEST,
        REQUEST_DATE
    }

    private final PageAndSort<DisplayTagSortProperty> pageAndSort = new PageAndSort<>();
    @Setter(AccessLevel.NONE)
    private String titleId;
    private String username;
    private String requestFromDateString;
    private String requestToDateString;
    private Action action;

    public ProviewAuditFilterForm() {
        initialize();
    }

    /**
     * Set all values back to defaults.
     * Used in resetting the form.
     */
    public void initialize() {
        populate(null, null, null, null, null);
    }

    public void populate(
        final String titleId,
        final String submittedBy,
        final String requestFromDateString,
        final String requestToDateString,
        final Action action) {
        this.titleId = titleId;
        this.username = submittedBy;
        this.requestFromDateString = requestFromDateString;
        this.requestToDateString = requestToDateString;
        this.action = action;
    }

    public Date getRequestFromDate() {
        return parseDate(requestFromDateString);
    }

    public Date getRequestToDate() {
        return parseDate(requestToDateString);
    }

    public void setTitleId(final String titleId) {
        this.titleId = (titleId != null) ? titleId.trim() : null;
    }

    public void setRequestFromDate(final Date fromDate) {
        requestFromDateString = parseDate(fromDate);
    }

    public void setRequestToDate(final Date toDate) {
        requestToDateString = parseDate(toDate);
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

    public String getDir() {
        return (pageAndSort.isAscendingSort()) ? ASC_SORT : DESC_SORT;
    }

    public void setDir(final String direction) {
        pageAndSort.setAscendingSort(ASC_SORT.equals(direction));
    }

    public boolean isAscendingSort() {
        return pageAndSort.isAscendingSort();
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

    public Integer getObjectsPerPage() {
        return pageAndSort.getObjectsPerPage();
    }

    public void setObjectsPerPage(final Integer objectsPerPage) {
        pageAndSort.setObjectsPerPage(objectsPerPage);
    }

    public boolean areAllFiltersBlank() {
        return isBlank(getTitleId()) && isBlank(getUsername()) && isBlank(getRequestFromDateString())
                && isBlank(getRequestToDateString()) && getAction() == null;
    }

    @Override
    public String toString() {
        return ReflectionToStringBuilder.toString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }
}
