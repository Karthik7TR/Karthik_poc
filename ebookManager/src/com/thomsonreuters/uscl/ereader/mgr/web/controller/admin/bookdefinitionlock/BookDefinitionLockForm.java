/*
 * Copyright 2011: Thomson Reuters Global Resources. All Rights Reserved.
 * Proprietary and Confidential information of TRGR. Disclosure, Use or
 * Reproduction without the written authorization of TRGR is prohibited
 */
package com.thomsonreuters.uscl.ereader.mgr.web.controller.admin.bookdefinitionlock;

import java.util.Date;

import com.thomsonreuters.uscl.ereader.core.book.domain.BookDefinitionLock;

public class BookDefinitionLockForm {
	//private static final Logger log = Logger.getLogger(BookDefinitionLockForm.class);
	public static final String FORM_NAME = "bookDefinitionLockForm";
	
	private Long bookDefinitionLockId;
	private Long bookDefinitionId;
	private String username;
	private String fullName;
	private Date checkoutTimestamp;
	
	public BookDefinitionLockForm() {
		super();
	}
	
	public void initialize(BookDefinitionLock lock){
		bookDefinitionLockId = lock.getEbookDefinitionLockId();
		bookDefinitionId = lock.getEbookDefinition().getEbookDefinitionId();
		username = lock.getUsername();
		fullName = lock.getFullName();
		checkoutTimestamp = lock.getCheckoutTimestamp();
	}

	public Long getBookDefinitionLockId() {
		return bookDefinitionLockId;
	}

	public void setBookDefinitionLockId(Long bookDefinitionLockId) {
		this.bookDefinitionLockId = bookDefinitionLockId;
	}

	public Long getBookDefinitionId() {
		return bookDefinitionId;
	}

	public void setBookDefinitionId(Long bookDefinitionId) {
		this.bookDefinitionId = bookDefinitionId;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getFullName() {
		return fullName;
	}

	public void setFullName(String fullName) {
		this.fullName = fullName;
	}

	public Date getCheckoutTimestamp() {
		return checkoutTimestamp;
	}

	public void setCheckoutTimestamp(Date checkoutTimestamp) {
		this.checkoutTimestamp = checkoutTimestamp;
	}
}
