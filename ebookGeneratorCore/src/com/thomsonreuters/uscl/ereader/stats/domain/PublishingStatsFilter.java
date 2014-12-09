/*
 * Copyright 2011: Thomson Reuters Global Resources. All Rights Reserved.
 * Proprietary and Confidential information of TRGR. Disclosure, Use or
 * Reproduction without the written authorization of TRGR is prohibited
 */
package com.thomsonreuters.uscl.ereader.stats.domain;

import java.util.Date;

import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

/**
 * The filter criteria used when searching for publishing stats to display in the PUBLISHING_STATS table.
 * A null or blank property value indicates that it is to be ignored and not included as part of the search criteria.
 */
public class PublishingStatsFilter {

	// Publishing Stats properties
	private Date from;	// job submit date on and after this calendar date (inclusive)
	private Date to;	// job submit date on and before this calendar date (inclusive)
	
	// Book Definition properties
	private String titleId;
	private String bookName;
	private Long bookDefinitionId;
	private String isbn;
	
	public PublishingStatsFilter() {
		super();
	}
	
	public PublishingStatsFilter(String titleId, String bookName, String isbn) {
		populate(null, null, titleId, bookName, null, isbn);
	}
	
	public PublishingStatsFilter(Long bookDefinitionId) {
		super();
		populate(null, null, null, null, bookDefinitionId, null);
	}
	
	public PublishingStatsFilter(Date from, Date to, String titleId, String bookName, Long bookDefinitionId) {
		super();
		populate(from, to, titleId, bookName, bookDefinitionId, null);
	}
	
	private void populate(Date from, Date to, String titleId, String bookName, Long bookDefinitionId, String isbn) {
		this.from = from;
		this.to = to;
		this.titleId = (titleId != null) ? titleId.trim() : null;
		this.bookName = (bookName != null) ? bookName.trim() : null;
		this.bookDefinitionId = bookDefinitionId;
		this.isbn = (isbn != null) ? isbn.trim() : null;
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
	public Long getBookDefinitionId() {
		return bookDefinitionId;
	}
	public String getIsbn() {
		return isbn;
	}
	public String toString() {
		return ReflectionToStringBuilder.toString(this, ToStringStyle.SHORT_PREFIX_STYLE);
	}
}
