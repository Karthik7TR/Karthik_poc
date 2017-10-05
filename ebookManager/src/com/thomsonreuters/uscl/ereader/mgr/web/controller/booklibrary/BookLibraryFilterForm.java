package com.thomsonreuters.uscl.ereader.mgr.web.controller.booklibrary;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.thomsonreuters.uscl.ereader.core.CoreConstants;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.apache.commons.lang3.time.DateUtils;

public class BookLibraryFilterForm {
    public static final String FORM_NAME = "bookLibraryFilterForm";

    public enum FilterCommand {
        SEARCH,
        RESET
    };

    public enum Action {
        READY,
        INCOMPLETE,
        DELETED
    };

    private String proviewDisplayName;
    private String toString;
    private String fromString;
    private String titleId;
    private String isbn;
    private String materialId;
    private Long proviewKeyword;
    private FilterCommand filterCommand;
    private Action action;

    public BookLibraryFilterForm() {
        initialize();
    }

    /**
     * Set all values back to defaults.
     * Used in resetting the form.
     */
    public void initialize() {
        populate(null, null, null, null, null, null, null, null);
    }

    public void populate(
        final String proviewDisplayName,
        final String toString,
        final String fromString,
        final String titleId,
        final String isbn,
        final String materialId,
        final Action action,
        final Long proviewKeyword) {
        this.proviewDisplayName = proviewDisplayName;
        this.toString = toString;
        this.fromString = fromString;
        this.titleId = titleId;
        this.isbn = isbn;
        this.materialId = materialId;
        this.action = action;
        this.proviewKeyword = proviewKeyword;
    }

    public Action getAction() {
        return action;
    }

    public void setAction(final Action action) {
        this.action = action;
    }

    public String getToString() {
        return toString;
    }

    public void setToString(final String toString) {
        this.toString = toString;
    }

    public String getFromString() {
        return fromString;
    }

    public void setFromString(final String fromString) {
        this.fromString = fromString;
    }

    public FilterCommand getFilterCommand() {
        return filterCommand;
    }

    public void setFilterCommand(final FilterCommand filterCommand) {
        this.filterCommand = filterCommand;
    }

    public String getMaterialId() {
        return materialId;
    }

    public void setMaterialId(final String materialId) {
        this.materialId = materialId == null ? null : materialId.trim();
    }

    public Long getProviewKeyword() {
        return proviewKeyword;
    }

    public void setProviewKeyword(final Long proviewKeyword) {
        this.proviewKeyword = proviewKeyword;
    }

    public String getProviewDisplayName() {
        return proviewDisplayName;
    }

    public void setProviewDisplayName(final String proviewDisplayName) {
        this.proviewDisplayName = proviewDisplayName == null ? null : proviewDisplayName.trim();
    }

    public static String getFormName() {
        return FORM_NAME;
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

    public String getTitleId() {
        return titleId;
    }

    public void setTitleId(final String titleId) {
        this.titleId = titleId == null ? null : titleId.trim();
    }

    public String getIsbn() {
        return isbn;
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
}
