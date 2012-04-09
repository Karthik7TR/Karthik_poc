/*
 * Copyright 2012: Thomson Reuters Global Resources. All Rights Reserved.
 * Proprietary and Confidential information of TRGR. Disclosure, Use or
 * Reproduction without the written authorization of TRGR is prohibited
 */

package com.thomsonreuters.uscl.ereader.mgr.web.controller.booklibrary;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.apache.commons.lang.time.DateUtils;

import com.thomsonreuters.uscl.ereader.mgr.web.WebConstants;
import com.thomsonreuters.uscl.ereader.mgr.web.controller.job.summary.FilterForm.FilterCommand;

public class BookLibraryFilterForm {

	public static final String FORM_NAME = "bookLibraryFilterForm";

	public enum FilterCommand {
		SEARCH, RESET
	};

	private String proviewDisplayName;
	private Date from;
	private Date to;
	private String toString;
	private String fromString;

	public String getToString() {
		return toString;
	}

	public void setToString(String toString) {
		this.toString = toString;
	}

	public String getFromString() {
		return fromString;
	}

	public void setFromString(String fromString) {
		this.fromString = fromString;
	}

	private String eBookDefStatus;
	private String titleId;
	private String isbn;
	private String materialId;
	private FilterCommand filterCommand;

	public FilterCommand getFilterCommand() {
		return filterCommand;
	}

	public void setFilterCommand(FilterCommand filterCommand) {
		this.filterCommand = filterCommand;
	}

	public String getMaterialId() {
		return materialId;
	}

	public void setMaterialId(String materialId) {
		this.materialId = materialId;
	}

	public String getProviewDisplayName() {
		return proviewDisplayName;
	}

	public void setProviewDisplayName(String proviewDisplayName) {
		this.proviewDisplayName = proviewDisplayName;
	}

	public static String getFormName() {
		return FORM_NAME;
	}

	public Date getFrom() {
		return parseDate(this.fromString);
	}

	public void setFrom(Date from) {
		this.fromString = parseDate(from);

	}

	public Date getTo() {
		return parseDate(this.toString);
	}

	public void setTo(Date to) {
		this.toString = parseDate(to);

	}

	public String geteBookDefStatus() {
		return eBookDefStatus;
	}

	public void seteBookDefStatus(String eBookDefStatus) {
		this.eBookDefStatus = eBookDefStatus;
	}

	public String getTitleId() {
		return titleId;
	}

	public void setTitleId(String titleId) {
		this.titleId = titleId;
	}

	public String getIsbn() {
		return isbn;
	}

	public void setIsbn(String isbn) {
		this.isbn = isbn;
	}

	public String toString() {
		return ReflectionToStringBuilder.toString(this,
				ToStringStyle.SHORT_PREFIX_STYLE);
	}

	public static String parseDate(Date date) {
		if (date != null) {
			SimpleDateFormat sdf = new SimpleDateFormat(
					WebConstants.DATE_FORMAT_PATTERN);
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
}
