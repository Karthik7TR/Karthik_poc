/*
 * Copyright 2011: Thomson Reuters Global Resources. All Rights Reserved.
 * Proprietary and Confidential information of TRGR. Disclosure, Use or
 * Reproduction without the written authorization of TRGR is prohibited
 */
package com.thomsonreuters.uscl.ereader.mgr.web.controller.bookaudit;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.apache.commons.lang.time.DateUtils;

import com.thomsonreuters.uscl.ereader.mgr.web.WebConstants;

/**
 * The form backing object that holds the data the user enters into the Audit List book audit filter HTML form.
 */
public class BookAuditFilterForm {
	
	public static final String FORM_NAME = "ebookAuditFilterForm";
	public enum FilterCommand { SEARCH, RESET };
	public enum Action {DELETE, CREATE, EDIT, RESTORE};
	
	//private static final Logger log = Logger.getLogger(FilterForm.class);
	
	private String titleId;
	private String proviewDisplayName;
	private String submittedBy;
	private String fromDateString;
	private String toDateString;
	private Long bookDefinitionId;
	private Action action;
	private FilterCommand command;
	
	public BookAuditFilterForm() {
		initialize();
	}
	
	public BookAuditFilterForm(Long bookDefinitionId) {
		populate(null, null, null, null, null, null, bookDefinitionId);
	}
	/**
	 * Set all values back to defaults.
	 * Used in resetting the form.
	 */
	public void initialize() {
		populate(null, null, null, null, null, null, null);
	}

	public void populate(String titleId, String proviewDisplayName, String submittedBy, 
			String fromDateString, String toDateString, Action action, Long bookId) {
		this.titleId = titleId;
		this.proviewDisplayName = proviewDisplayName;
		this.submittedBy = submittedBy;
		this.fromDateString = fromDateString;
		this.toDateString = toDateString;
		this.action = action;
		this.bookDefinitionId = bookId;
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
	public Action getAction() {
		return action;
	}
	public String getSubmittedBy() {
		return submittedBy;
	}
	public Long getBookDefinitionId() {
		return bookDefinitionId;
	}
	public void setProviewDisplayName(String name) {
		this.proviewDisplayName = (name != null) ? name.trim() : null;
	}
	public void setFilterCommand(FilterCommand cmd) {
		this.command = cmd;
	}
	public void setTitleId(String titleId) {
		this.titleId = (titleId != null) ? titleId.trim() : null;
	}
	public void setFromDate(Date fromDate) {
		this.fromDateString = parseDate(fromDate);
	}
	public void setFromDateString(String fromDate) {
		this.fromDateString = fromDate;
	}
	public void setToDateString(String toDate) {
		this.toDateString = toDate;
	}
	public void setToDate(Date toDate) {
		this.toDateString = parseDate(toDate);
	}
	public void setAction(Action action) {
		this.action = action;
	}
	public void setSubmittedBy(String username) {
		this.submittedBy = username;
	}
	public void setBookDefinitionId(Long bookDefinitionId) {
		this.bookDefinitionId = bookDefinitionId;
	}
	public static String parseDate(Date date) {
		if (date != null) {
			SimpleDateFormat sdf = new SimpleDateFormat(WebConstants.DATE_TIME_FORMAT_PATTERN);
			return sdf.format(date);
		}
		return null;
	}
	public static Date parseDate(String dateString) {
		Date date = null;
		try {
			if (StringUtils.isNotBlank(dateString)) {
				String[] parsePatterns = { WebConstants.DATE_TIME_FORMAT_PATTERN };
				date = DateUtils.parseDate(dateString, parsePatterns);
			}
		} catch (ParseException e) {
			date = null;
		}
		return date;
	}
	public String toString() {
		return ReflectionToStringBuilder.toString(this, ToStringStyle.SHORT_PREFIX_STYLE);
	}
}
