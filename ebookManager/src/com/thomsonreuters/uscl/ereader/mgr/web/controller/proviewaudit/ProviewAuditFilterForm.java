package com.thomsonreuters.uscl.ereader.mgr.web.controller.proviewaudit;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.thomsonreuters.uscl.ereader.core.CoreConstants;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.apache.commons.lang3.time.DateUtils;

/**
 * The form backing object that holds the data the user enters into the proview audit filter HTML form.
 */
public class ProviewAuditFilterForm {
    public static final String FORM_NAME = "proviewAuditFilterForm";

    public enum FilterCommand {
        SEARCH,
        RESET
    };

    public enum Action {
        PROMOTE,
        DELETE,
        REMOVE
    };

    //private static final Logger log = LogManager.getLogger(ProviewAuditFilterForm.class);

    private String titleId;
    private String username;
    private String fromRequestDateString;
    private String toRequestDateString;
    private Action action;
    private FilterCommand command;

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
        final String fromRequestDateString,
        final String toRequestDateString,
        final Action action) {
        this.titleId = titleId;
        username = submittedBy;
        this.fromRequestDateString = fromRequestDateString;
        this.toRequestDateString = toRequestDateString;
        this.action = action;
    }

    public FilterCommand getFilterCommand() {
        return command;
    }

    public String getTitleId() {
        return titleId;
    }

    public Date getRequestFromDate() {
        return parseDate(fromRequestDateString);
    }

    public String getRequestFromDateString() {
        return fromRequestDateString;
    }

    public Date getRequestToDate() {
        return parseDate(toRequestDateString);
    }

    public String getRequestToDateString() {
        return toRequestDateString;
    }

    public Action getAction() {
        return action;
    }

    public String getUsername() {
        return username;
    }

    public void setFilterCommand(final FilterCommand cmd) {
        command = cmd;
    }

    public void setTitleId(final String titleId) {
        this.titleId = (titleId != null) ? titleId.trim() : null;
    }

    public void setRequestFromDate(final Date fromDate) {
        fromRequestDateString = parseDate(fromDate);
    }

    public void setRequestFromDateString(final String fromDate) {
        fromRequestDateString = fromDate;
    }

    public void setRequestToDateString(final String toDate) {
        toRequestDateString = toDate;
    }

    public void setRequestToDate(final Date toDate) {
        toRequestDateString = parseDate(toDate);
    }

    public void setAction(final Action action) {
        this.action = action;
    }

    public void setUsername(final String username) {
        this.username = username;
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

    @Override
    public String toString() {
        return ReflectionToStringBuilder.toString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }
}
