/*
 * Copyright 2012: Thomson Reuters Global Resources. All Rights Reserved.
 * Proprietary and Confidential information of TRGR. Disclosure, Use or
 * Reproduction without the written authorization of TRGR is prohibited
 */
package com.thomsonreuters.uscl.ereader.mgr.web.controller.booklibrary;

import org.apache.log4j.Logger;

import com.thomsonreuters.uscl.ereader.orchestrate.core.BookDefinition;

/**
 * A View Data Object (VDO) wrapper around a Spring Batch JobExecution object
 * (Decorator/VDO patterns). Exists to provide convenience methods to expose
 * complex calculated data and values which would otherwise be very messy to
 * calculate directly within the JSP.
 */
public class BookDefinitionVdo {
	private static final Logger log = Logger.getLogger(BookDefinitionVdo.class);
	private BookDefinition bookDefinition;
	private boolean isSelected = false;

	public boolean isSelected() {
		return isSelected;
	}

	public void setSelected(boolean isSelected) {
		this.isSelected = isSelected;
	}

	public BookDefinition getBookDefinition() {
		return bookDefinition;
	}

	public BookDefinitionVdo(BookDefinition bookDefinition, boolean isSelected) {
		super();
		this.bookDefinition = bookDefinition;
		this.isSelected = isSelected;
	}

	public void setBookDefinition(BookDefinition bookDefinition) {
		this.bookDefinition = bookDefinition;
	}

	public Long getMajorVersion() {
		return bookDefinition.getMajorVersion();
	}

	public String getTitleId() {
		return bookDefinition.getPrimaryKey().getTitleId();
	}

	public String getFullyQualifiedTitleId() {
		return bookDefinition.getPrimaryKey().getFullyQualifiedTitleId();
	}

	public String getBookName() {
		return bookDefinition.getBookName();
	}

	public String getAuthor() {
		return bookDefinition.getAuthorInfo();
	}

}
