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

public class BookLibraryFilterForm {

	public static final String FORM_NAME = "bookLibraryFilterForm";

	public enum FilterCommand {SEARCH, RESET};
	public enum Action {COMPLETE, INCOMPLETE, DELETED};

	private String proviewDisplayName;
	private String toString;
	private String fromString;
	private String titleId;
	private String isbn;
	private String materialId;
	private Long proviewKeyword;
	private FilterCommand filterCommand;
	private Action action;
	
	public BookLibraryFilterForm() {
		initialize();
	}

	/**
	 * Set all values back to defaults.
	 * Used in resetting the form.
	 */
	public void initialize() {
		populate(null, null, null, null, null, null, null, null);
	}

	public void populate(String proviewDisplayName, String toString, String fromString, 
			String titleId, String isbn, String materialId, Action action, Long proviewKeyword) {
		this.proviewDisplayName = proviewDisplayName;
		this.toString = toString;
		this.fromString = fromString;
		this.titleId = titleId;
		this.isbn = isbn;
		this.materialId = materialId;
		this.action = action;
		this.proviewKeyword = proviewKeyword;
	}

	public Action getAction() {
		return action;
	}

	public void setAction(Action action) {
		this.action = action;
	}

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
		this.materialId = materialId == null ? null : materialId.trim();
	}

	public Long getProviewKeyword() {
		return proviewKeyword;
	}

	public void setProviewKeyword(Long proviewKeyword) {
		this.proviewKeyword = proviewKeyword;
	}

	public String getProviewDisplayName() {
		return proviewDisplayName;
	}

	public void setProviewDisplayName(String proviewDisplayName) {
		this.proviewDisplayName = proviewDisplayName == null ? null
				: proviewDisplayName.trim();
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

	public String getTitleId() {
		return titleId;
	}

	public void setTitleId(String titleId) {
		this.titleId = titleId == null ? null : titleId.trim();
	}

	public String getIsbn() {
		return isbn;
	}

	public void setIsbn(String isbn) {
		this.isbn = isbn == null ? null : isbn.trim();
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
