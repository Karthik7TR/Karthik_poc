package com.thomsonreuters.uscl.ereader.mgr.web.controller.booklibrary;

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

@Getter
@Setter
public class BookLibraryFilterForm {
    public static final String FORM_NAME = "bookLibraryFilterForm";
    private static final String ASC_SORT = "asc";
    private static final String DESC_SORT = "desc";

    public enum Action {
        READY,
        INCOMPLETE,
        DELETED
    }

    public enum Command {
        GENERATE
    }

    public enum DisplayTagSortProperty {
        PROVIEW_DISPLAY_NAME,
        SOURCE_TYPE,
        TITLE_ID,
        LAST_GENERATED_DATE,
        DEFINITION_STATUS,
        LAST_EDIT_DATE,
        COMB_BOOK_FLAG
    }

    private String[] selectedEbookKeys;
    private Command command;
    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    private final PageAndSort<DisplayTagSortProperty> pageAndSort =
            new PageAndSort<>(1, DisplayTagSortProperty.PROVIEW_DISPLAY_NAME, true); // sort, page, dir, objectsPerPage
    @Setter(AccessLevel.NONE)
    private String proviewDisplayName;
    private String sourceType;
    private String toString;
    private String fromString;
    @Setter(AccessLevel.NONE)
    private String titleId;
    @Setter(AccessLevel.NONE)
    private String isbn;
    @Setter(AccessLevel.NONE)
    private String materialId;
    private Long proviewKeyword;
    private Action action;

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

    public void setMaterialId(final String materialId) {
        this.materialId = materialId == null ? null : materialId.trim();
    }

    public void setProviewDisplayName(final String proviewDisplayName) {
        this.proviewDisplayName = proviewDisplayName == null ? null : proviewDisplayName.trim();
    }

    public Date getFrom() {
        return parseDate(fromString);
    }

    public void setFrom(final Date from) {
        fromString = parseDate(from);
    }

    public Date getTo() {
        return parseDate(toString);
    }

    public void setTo(final Date to) {
        toString = parseDate(to);
    }

    public void setTitleId(final String titleId) {
        this.titleId = titleId == null ? null : titleId.trim();
    }

    public void setIsbn(final String isbn) {
        this.isbn = isbn == null ? null : isbn.trim();
    }

    @Override
    public String toString() {
        return ReflectionToStringBuilder.toString(this, ToStringStyle.SHORT_PREFIX_STYLE);
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
            // Intentionally left blank
        }
        return date;
    }

    public boolean areAllFiltersBlank() {
        return isBlank(getProviewDisplayName())
                && isBlank(getSourceType())
                && isBlank(getFromString())
                && isBlank(getToString())
                && isBlank(getTitleId())
                && isBlank(getIsbn())
                && isBlank(getMaterialId())
                && getProviewKeyword() == null
                && getAction() == null;
    }
}
