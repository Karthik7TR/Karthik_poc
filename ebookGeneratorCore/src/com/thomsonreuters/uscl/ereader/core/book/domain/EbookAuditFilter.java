/*
 * Copyright 2011: Thomson Reuters Global Resources. All Rights Reserved.
 * Proprietary and Confidential information of TRGR. Disclosure, Use or
 * Reproduction without the written authorization of TRGR is prohibited
 */
package com.thomsonreuters.uscl.ereader.core.book.domain;

import java.util.Calendar;
import java.util.Date;

import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

/**
 * The filter criteria used when searching for jobs to display in the Job Summary table.
 * A null or blank property value indicates that it is to be ignored and not included as part of the search criteria.
 */
public class EbookAuditFilter {

	// Job Execution properties
	private Date from;	// start date on and after this calendar date (inclusive)
	private Date to;	// start date on and before this calendar date (inclusive)
	
	// Book Definition properties
	private String titleId;
	private String bookName;
	private String submittedBy;
	private String action;
	private Long bookDefinitionId;
	
	public EbookAuditFilter() {
		super();
	}
	public EbookAuditFilter(Date from, Date to, String action, String titleId, String bookName, String submittedBy, Long bookDefinitionId) {
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
	/**
	 * Get the point in time that is one day prior to the 'TO' time, used for a less-than comparison of 'TO' date
	 * to ensure that the specified 'TO' date is included in the range of dates searched.
	 */
	public Date getToInclusive() {
		if (to == null) {
			return null;
		}
		Calendar cal = Calendar.getInstance();
		cal.setTime(to);
		cal.add(Calendar.DAY_OF_MONTH, +1);
		return cal.getTime();
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
	public String getSubmittedBy() {
		return submittedBy;
	}
	public Long getBookDefinitionId() {
		return bookDefinitionId;
	}
	public String toString() {
		return ReflectionToStringBuilder.toString(this, ToStringStyle.SHORT_PREFIX_STYLE);
	}
}
