package com.thomsonreuters.uscl.ereader.mgr.web.controller.bookaudit;

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
 * The form backing object that holds the data the user enters into the Audit List book audit filter HTML form.
 */
@Getter
@Setter
public class BookAuditFilterForm {
    public static final String FORM_NAME = "ebookAuditFilterForm";
    private static final String ASC_SORT = "asc";
    private static final String DESC_SORT = "desc";

    public enum Action {
        DELETE,
        CREATE,
        EDIT,
        RESTORE
    }

    public enum DisplayTagSortProperty {
        TITLE_ID,
        BOOK_NAME,
        BOOK_DEFINITION_ID,
        SUBMITTED_DATE,
        ACTION,
        SUBMITTED_BY,
        COMMENT
    }

    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    private final PageAndSort<DisplayTagSortProperty> pageAndSort =
            new PageAndSort<>(1, DisplayTagSortProperty.SUBMITTED_DATE, false); // sort, page, dir, objectsPerPage
    @Setter(AccessLevel.NONE)
    private String proviewDisplayName;
    @Setter(AccessLevel.NONE)
    private String titleId;
    private Long bookDefinitionId;
    private String submittedBy;
    private String fromDateString;
    private String toDateString;
    private Action action;

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

    public void setFromDate(final Date fromDate) {
        fromDateString = parseDate(fromDate);
    }

    public Date getToDate() {
        return parseDate(toDateString);
    }

    public void setToDate(final Date toDate) {
        toDateString = parseDate(toDate);
    }

    public void setProviewDisplayName(final String name) {
        proviewDisplayName = (name != null) ? name.trim() : null;
    }

    public void setTitleId(final String titleId) {
        this.titleId = (titleId != null) ? titleId.trim() : null;
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
                && isBlank(getSubmittedBy())
                && isBlank(getFromDateString())
                && isBlank(getToDateString())
                && getAction() == null;
    }

    @Override
    public String toString() {
        return ReflectionToStringBuilder.toString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }
}
