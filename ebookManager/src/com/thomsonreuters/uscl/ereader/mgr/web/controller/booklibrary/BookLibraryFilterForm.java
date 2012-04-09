/*
 * Copyright 2012: Thomson Reuters Global Resources. All Rights Reserved.
 * Proprietary and Confidential information of TRGR. Disclosure, Use or
 * Reproduction without the written authorization of TRGR is prohibited
 */

package com.thomsonreuters.uscl.ereader.mgr.web.controller.booklibrary;

import java.util.Date;

import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import com.thomsonreuters.uscl.ereader.mgr.web.controller.job.summary.FilterForm.FilterCommand;

public class BookLibraryFilterForm {

	public static final String FORM_NAME = "bookLibraryFilterForm";

	public enum FilterCommand {
		SEARCH, RESET
	};

	private String proviewDisplayName;
	private Date from;
	private Date to;
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
		return from;
	}

	public void setFrom(Date from) {
		this.from = from;
	}

	public Date getTo() {
		return to;
	}

	public void setTo(Date to) {
		this.to = to;
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

}
