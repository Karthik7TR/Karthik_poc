/*
 * Copyright 2011: Thomson Reuters Global Resources. All Rights Reserved.
 * Proprietary and Confidential information of TRGR. Disclosure, Use or
 * Reproduction without the written authorization of TRGR is prohibited
 */
package com.thomsonreuters.uscl.ereader.mgr.library.vdo;

import java.util.Calendar;
import java.util.Date;

import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

/**
 * The filter criteria used when searching for book definitions to display in the Library List table.
 * A null or blank property value indicates that it is to be ignored and not included as part of the search criteria.
 */
public class LibraryListFilter {

	// Book Definition properties
	private Date from;	// start date on and after this calendar date (inclusive)
	private Date to;	// start date on and before this calendar date (inclusive)
	private String titleId;
	private String proviewDisplayName;
	private String action;
	private String isbn;
	private String materialId;
	
	
	public LibraryListFilter() {
		super();
	}
	public LibraryListFilter(Date from, Date to, String action, String titleId, String proviewDisplayName, String isbn, String materialId) {
		this.from = from;
		this.to = to;
		this.action = action;
		this.titleId = (titleId != null) ? titleId.trim() : null;
		this.proviewDisplayName = (proviewDisplayName != null) ? proviewDisplayName.trim() : null;
		this.isbn = (isbn != null) ? isbn.trim() : null;
		this.materialId = (materialId != null) ? materialId.trim() : null;
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
	public String getProviewDisplayName() {
		return proviewDisplayName;
	}
	
	public String getIsbn() {
		return isbn;
	}
	
	public String getMaterialId() {
		return materialId;
	}
	

	public String toString() {
		return ReflectionToStringBuilder.toString(this, ToStringStyle.SHORT_PREFIX_STYLE);
	}
}