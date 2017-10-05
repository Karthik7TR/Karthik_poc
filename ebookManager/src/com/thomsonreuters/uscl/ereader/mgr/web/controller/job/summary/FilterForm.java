package com.thomsonreuters.uscl.ereader.mgr.web.controller.job.summary;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import com.thomsonreuters.uscl.ereader.core.CoreConstants;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.batch.core.BatchStatus;

/**
 * The form backing object that holds the data the user enters into the Job List job filter HTML form.
 */
public class FilterForm {
    public static final String FORM_NAME = "jobSummaryFilterForm";

    public enum FilterCommand {
        SEARCH,
        RESET
    };

    //private static final Logger log = LogManager.getLogger(FilterForm.class);

    private String titleId;
    private String proviewDisplayName;
    private String submittedBy;
    private String fromDateString;
    private String toDateString;
    private BatchStatus[] batchStatus;
    private FilterCommand command;

    public FilterForm() {
        initialize();
    }

    /**
     * Set all values back to defaults.
     * Used in resetting the form.
     */
    public void initialize() {
        // Default the from date to 1 day ago
        final Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_MONTH, -1);
        final String fromDate = parseDate(cal.getTime());
        final BatchStatus[] selectedBatchStatus = {
            BatchStatus.STARTED,
            BatchStatus.STOPPED,
            BatchStatus.FAILED,
            BatchStatus.COMPLETED,
            BatchStatus.STARTING,
            BatchStatus.STOPPING,
            BatchStatus.ABANDONED};
        populate(null, null, null, fromDate, null, selectedBatchStatus);
    }

    public void populate(
        final String titleId,
        final String proviewDisplayName,
        final String submittedBy,
        final String fromDateString,
        final String toDateString,
        final BatchStatus[] batchStatus) {
        this.titleId = titleId;
        this.proviewDisplayName = proviewDisplayName;
        this.submittedBy = submittedBy;
        this.fromDateString = fromDateString;
        this.toDateString = toDateString;
        this.batchStatus = batchStatus;
    }

    public String getProviewDisplayName() {
        return proviewDisplayName;
    }

    public FilterCommand getFilterCommand() {
        return command;
    }

    public String getTitleId() {
        return titleId;
    }

    public Date getFromDate() {
        return parseDate(fromDateString);
    }

    public String getFromDateString() {
        return fromDateString;
    }

    public Date getToDate() {
        return parseDate(toDateString);
    }

    public String getToDateString() {
        return toDateString;
    }

    public BatchStatus[] getBatchStatus() {
        return batchStatus;
    }

    public String getSubmittedBy() {
        return submittedBy;
    }

    public void setProviewDisplayName(final String name) {
        proviewDisplayName = (name != null) ? name.trim() : null;
    }

    public void setFilterCommand(final FilterCommand cmd) {
        command = cmd;
    }

    public void setTitleId(final String titleId) {
        this.titleId = (titleId != null) ? titleId.trim() : null;
    }

    public void setFromDate(final Date fromDate) {
        fromDateString = parseDate(fromDate);
    }

    public void setFromDateString(final String fromDate) {
        fromDateString = fromDate;
    }

    public void setToDateString(final String toDate) {
        toDateString = toDate;
    }

    public void setToDate(final Date toDate) {
        toDateString = parseDate(toDate);
    }

    public void setBatchStatus(final BatchStatus[] batchStatus) {
        this.batchStatus = batchStatus;
    }

    public void setSubmittedBy(final String username) {
        submittedBy = username;
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
