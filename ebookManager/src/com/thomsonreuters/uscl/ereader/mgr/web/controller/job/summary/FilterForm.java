/*
 * Copyright 2011: Thomson Reuters Global Resources. All Rights Reserved.
 * Proprietary and Confidential information of TRGR. Disclosure, Use or
 * Reproduction without the written authorization of TRGR is prohibited
 */
package com.thomsonreuters.uscl.ereader.mgr.web.controller.job.summary;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.apache.commons.lang.time.DateUtils;
import org.springframework.batch.core.BatchStatus;

import com.thomsonreuters.uscl.ereader.mgr.web.WebConstants;

/**
 * The form backing object that holds the data the user enters into the Job List job filter HTML form.
 */
public class FilterForm {
	
	public static final String FORM_NAME = "jobSummaryFilterForm";
	public enum FilterCommand { SEARCH, RESET };
	
	//private static final Logger log = Logger.getLogger(FilterForm.class);
	
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
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DAY_OF_MONTH, -1);
		String fromDate = parseDate(cal.getTime());
		BatchStatus[] selectedBatchStatus = { BatchStatus.STARTED,  BatchStatus.STOPPED,  BatchStatus.FAILED, BatchStatus.COMPLETED,
											  BatchStatus.STARTING, BatchStatus.STOPPING, BatchStatus.ABANDONED };
		populate(null, null, null, fromDate, null, selectedBatchStatus);
	}

	public void populate(String titleId, String proviewDisplayName, String submittedBy, String fromDateString, String toDateString, BatchStatus[] batchStatus) {
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
	public void setBatchStatus(BatchStatus[] batchStatus) {
		this.batchStatus = batchStatus;
	}
	public void setSubmittedBy(String username) {
		this.submittedBy = username;
	}
	public static String parseDate(Date date) {
		if (date != null) {
			SimpleDateFormat sdf = new SimpleDateFormat(WebConstants.DATE_FORMAT_PATTERN);
			return sdf.format(date);
		}
		return null;
	}
	public static Date parseDate(String dateString) {
		Date date = null;
		try {
			if (StringUtils.isNotBlank(dateString)) {
				String[] parsePatterns = { WebConstants.DATE_FORMAT_PATTERN };
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
