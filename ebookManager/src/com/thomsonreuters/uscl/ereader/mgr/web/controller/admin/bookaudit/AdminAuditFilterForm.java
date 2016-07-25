/*
 * Copyright 2016: Thomson Reuters Global Resources. All Rights Reserved.
 * Proprietary and Confidential information of TRGR. Disclosure, Use or
 * Reproduction without the written authorization of TRGR is prohibited
 */

package com.thomsonreuters.uscl.ereader.mgr.web.controller.admin.bookaudit;

import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.springframework.util.StringUtils;

/**
 * The form backing object that holds the data the user enters into the Audit book filter HTML form.
 */
public class AdminAuditFilterForm {
	
	public static final String FORM_NAME = "adminAuditFilterForm";
	
	//private static final Logger log = LogManager.getLogger(AdminAuditFilterForm.class);
	
	private String titleId;
	private String proviewDisplayName;
	private String isbn;
	
	public AdminAuditFilterForm() {
		initialize();
	}

	/**
	 * Set all values back to defaults.
	 * Used in resetting the form.
	 */
	public void initialize() {
		populate(null, null, null);
	}

	public void populate(String titleId, String proviewDisplayName, String isbn) {
		this.titleId = titleId;
		this.proviewDisplayName = proviewDisplayName;
		this.isbn = isbn;
	}
	
	public String getProviewDisplayName() {
		return proviewDisplayName;
	}
	public String getTitleId() {
		return titleId;
	}
	public String getIsbn() {
		return isbn;
	}
	
	public void setProviewDisplayName(String name) {
		this.proviewDisplayName = (name != null) ? name.trim() : null;
	}
	public void setTitleId(String titleId) {
		this.titleId = (titleId != null) ? titleId.trim() : null;
	}
	public void setIsbn(String isbn) {
		this.isbn = (isbn != null) ? isbn.trim() : null;
	}
	
	public String toString() {
		return ReflectionToStringBuilder.toString(this, ToStringStyle.SHORT_PREFIX_STYLE);
	}
	
	public boolean isEmpty() {
		return StringUtils.isEmpty(titleId) && StringUtils.isEmpty(proviewDisplayName) && StringUtils.isEmpty(isbn);
	}
}
