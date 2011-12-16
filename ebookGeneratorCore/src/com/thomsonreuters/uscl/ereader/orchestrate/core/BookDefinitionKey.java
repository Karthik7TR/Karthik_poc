/*
 * Copyright 2011: Thomson Reuters Global Resources. All Rights Reserved.
 * Proprietary and Confidential information of TRGR. Disclosure, Use or
 * Reproduction without the written authorization of TRGR is prohibited
 */
package com.thomsonreuters.uscl.ereader.orchestrate.core;

import java.io.Serializable;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

/**
 * The primary key for a book definition.
 */
public class BookDefinitionKey implements Serializable {
	private static final long serialVersionUID = 8902970407236193203L;
	
	private String 	bookTitleId;
	private Long 	majorVersion;
	
	public BookDefinitionKey() {
		super();
	}
	/**
	 * Full constructor for the key
	 * @param bookTitleId book title ID, may not be null
	 * @param majorVersion book major version number, may not be blank
	 */
	public BookDefinitionKey(String bookTitleId, Long majorVersion) {
		setBookTitleId(bookTitleId);
		setMajorVersion(majorVersion);
	}
	public String getBookTitleId() {
		return bookTitleId;
	}
	public Long getMajorVersion() {
		return majorVersion;
	}
	public void setBookTitleId(String bookTitleId) {
		if (StringUtils.isBlank(bookTitleId)) {
			throw new IllegalArgumentException("Book title ID may not be blank in key.");
		}
		this.bookTitleId = bookTitleId;
	}
	public void setMajorVersion(Long majorVersion) {
		if (majorVersion == null) {
			throw new IllegalArgumentException("Book major version number may not be null in key.");
		}
		this.majorVersion = majorVersion;
	}
	public String toString() {
		return ReflectionToStringBuilder.toString(this, ToStringStyle.SHORT_PREFIX_STYLE);
	}
	
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof BookDefinitionKey)) {
			return false;
		}
		BookDefinitionKey that = (BookDefinitionKey) obj;
		return (this.bookTitleId.equals(that.bookTitleId) && this.majorVersion.equals(that.majorVersion));
	}
}
