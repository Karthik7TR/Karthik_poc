/*
 * Copyright 2011: Thomson Reuters Global Resources. All Rights Reserved.
 * Proprietary and Confidential information of TRGR. Disclosure, Use or
 * Reproduction without the written authorization of TRGR is prohibited
 */
package com.thomsonreuters.uscl.ereader.mgr.web.controller.proviewaudit;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.apache.commons.lang.time.DateUtils;

import com.thomsonreuters.uscl.ereader.core.CoreConstants;

/**
 * The form backing object that holds the data the user enters into the proview audit filter HTML form.
 */
public class ProviewAuditFilterForm {
	
	public static final String FORM_NAME = "proviewAuditFilterForm";
	public enum FilterCommand { SEARCH, RESET };
	public enum Action {PROMOTE, DELETE, REMOVE};
	
	//private static final Logger log = Logger.getLogger(ProviewAuditFilterForm.class);
	
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

	public void populate(String titleId, String submittedBy, 
			String fromRequestDateString, String toRequestDateString, Action action) {
		this.titleId = titleId;
		this.username = submittedBy;
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
	public void setFilterCommand(FilterCommand cmd) {
		this.command = cmd;
	}
	public void setTitleId(String titleId) {
		this.titleId = (titleId != null) ? titleId.trim() : null;
	}
	public void setRequestFromDate(Date fromDate) {
		this.fromRequestDateString = parseDate(fromDate);
	}
	public void setRequestFromDateString(String fromDate) {
		this.fromRequestDateString = fromDate;
	}
	public void setRequestToDateString(String toDate) {
		this.toRequestDateString = toDate;
	}
	public void setRequestToDate(Date toDate) {
		this.toRequestDateString = parseDate(toDate);
	}
	public void setAction(Action action) {
		this.action = action;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public static String parseDate(Date date) {
		if (date != null) {
			SimpleDateFormat sdf = new SimpleDateFormat(CoreConstants.DATE_TIME_FORMAT_PATTERN);
			return sdf.format(date);
		}
		return null;
	}
	public static Date parseDate(String dateString) {
		Date date = null;
		try {
			if (StringUtils.isNotBlank(dateString)) {
				String[] parsePatterns = { CoreConstants.DATE_TIME_FORMAT_PATTERN };
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
