package com.thomsonreuters.uscl.ereader.proviewaudit.domain;

import java.util.Date;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * The filter criteria used when searching for jobs to display in the Job Summary table.
 * A null or blank property value indicates that it is to be ignored and not included as part of the search criteria.
 */
public class ProviewAuditFilter
{
    // Creation properties
    private Date from; // start date on and after this calendar date (inclusive)
    private Date to; // start date on and before this calendar date (inclusive)

    // Book Definition properties
    private String titleId;
    private String submittedBy;
    private String action;

    public ProviewAuditFilter()
    {
        super();
    }

    public ProviewAuditFilter(final Date from, final Date to, final String action, final String titleId, final String submittedBy)
    {
        this.from = from;
        this.to = to;
        this.action = action;
        this.titleId = (titleId != null) ? titleId.trim() : null;
        this.submittedBy = (submittedBy != null) ? submittedBy.trim() : null;
    }

    /** Include executions with a start time from the start of (00:00:00) of this calendar date and after. */
    public Date getFrom()
    {
        return from;
    }

    /** Filter to date entered by user, normalized to (00:00:00) of the entered day. */
    public Date getTo()
    {
        return to;
    }

    public String getAction()
    {
        return action;
    }

    /**
     * Get the match-anywhere title ID, where this string will be compared against
     * the actual definition title ID as a 'like' comparison '%titleID%'.
     * @return
     */
    public String getTitleId()
    {
        return titleId;
    }

    public String getSubmittedBy()
    {
        return submittedBy;
    }

    @Override
    public String toString()
    {
        return ReflectionToStringBuilder.toString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }
}
