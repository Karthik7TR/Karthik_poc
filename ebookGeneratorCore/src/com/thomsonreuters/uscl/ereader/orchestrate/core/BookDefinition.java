/*
 * Copyright 2011: Thomson Reuters Global Resources. All Rights Reserved.
 * Proprietary and Confidential information of TRGR. Disclosure, Use or
 * Reproduction without the written authorization of TRGR is prohibited
 */
package com.thomsonreuters.uscl.ereader.orchestrate.core;

import javax.persistence.Column;
import javax.persistence.Table;

@Table(name="BOOK_DEFINITION", schema="TODO_SCHEMA")
public class BookDefinition {
	
	private BookDefinitionKey key;	// book title ID & major version number
	private String name;			// title of the book
	private Long minorVersion;
	private String rootTocGuid;
	
// TODO: other fields and properties and hibernate mapping work...
	
	public BookDefinitionKey getPrimaryKey() {
		return key;
	}
	@Column(name="NAME", length=100)
	public String getName() {
		return name;
	}
	public Long getMinorVersion() {
		return minorVersion;
	}
	public String getTitleId() {
		return key.getBookTitleId();
	}
	
	
	public void setBookDefinitionKey(BookDefinitionKey key) {
		this.key = key;
	}
	public void setMinorVersion(Long minorVersion) {
		this.minorVersion = minorVersion;
	}
	public void setPrimaryKey(BookDefinitionKey key) {
		this.key = key;
	}
	public void setName(String name) {
		this.name = name;
	}
}
