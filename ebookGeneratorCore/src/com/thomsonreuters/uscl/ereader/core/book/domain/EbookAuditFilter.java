package com.thomsonreuters.uscl.ereader.core.book.domain;

import java.util.Date;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * The filter criteria used when searching for jobs to display in the Job Summary table.
 * A null or blank property value indicates that it is to be ignored and not included as part of the search criteria.
 */
public class EbookAuditFilter {
    // Job Execution properties
    private Date from; // start date on and after this calendar date (inclusive)
    private Date to; // start date on and before this calendar date (inclusive)

    // Book Definition properties
    private String titleId;
    private String bookName;
    private String submittedBy;
    private String action;
    private String isbn;
    private Long bookDefinitionId;
    private Boolean filterEditedIsbn = false;

    public EbookAuditFilter() {
        super();
    }

    public EbookAuditFilter(final String titleId, final String bookName, final String isbn) {
        this.titleId = (titleId != null) ? titleId.trim() : null;
        this.bookName = (bookName != null) ? bookName.trim() : null;
        this.isbn = (isbn != null) ? isbn.trim() : null;
        filterEditedIsbn = true;
    }

    public EbookAuditFilter(
        final Date from,
        final Date to,
        final String action,
        final String titleId,
        final String bookName,
        final String submittedBy,
        final Long bookDefinitionId) {
        this.from = from;
        this.to = to;
        this.action = action;
        this.titleId = (titleId != null) ? titleId.trim() : null;
        this.bookName = (bookName != null) ? bookName.trim() : null;
        this.submittedBy = (submittedBy != null) ? submittedBy.trim() : null;
        this.bookDefinitionId = bookDefinitionId;
    }

    /** Include executions with a start time from the start of (00:00:00) of this calendar date and after. */
    public Date getFrom() {
        return from;
    }

    /** Filter to date entered by user, normalized to (00:00:00) of the entered day. */
    public Date getTo() {
        return to;
    }

    public String getAction() {
        return action;
    }

    /**
     * Get the match-anywhere title ID, where this string will be compared against
     * the actual definition title ID as a 'like' comparison '%titleID%'.
     * @return
     */
    public String getTitleId() {
        return titleId;
    }

    public String getBookName() {
        return bookName;
    }

    public String getIsbn() {
        return isbn;
    }

    public String getSubmittedBy() {
        return submittedBy;
    }

    public Long getBookDefinitionId() {
        return bookDefinitionId;
    }

    public Boolean getFilterEditedIsbn() {
        return filterEditedIsbn;
    }

    @Override
    public String toString() {
        return ReflectionToStringBuilder.toString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }
}
