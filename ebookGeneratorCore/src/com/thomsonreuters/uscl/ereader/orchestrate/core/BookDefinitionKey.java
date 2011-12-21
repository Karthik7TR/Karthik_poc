/*
 * Copyright 2011: Thomson Reuters Global Resources. All Rights Reserved.
 * Proprietary and Confidential information of TRGR. Disclosure, Use or
 * Reproduction without the written authorization of TRGR is prohibited
 */
package com.thomsonreuters.uscl.ereader.orchestrate.core;

import java.io.File;
import java.io.Serializable;

import javax.persistence.Embeddable;
import javax.persistence.Transient;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

/**
 * The primary key for a book definition.
 */
@Embeddable
public class BookDefinitionKey implements Serializable {
	private static final long serialVersionUID = 8902970407236193203L;

	/**
	 * The fully-qualified title ID from the book definition database that may or may not include other namespace
	 * components with separating slashes.  Example: "uscl/cr/ak_2010_federal"
	 */
	private String  fullyQualifiedTitleId;

	/**
	 * The main version number of the book.
	 */
	private Long 	majorVersion;

	public BookDefinitionKey() {
		super();
	}
	/**
	 * Full constructor for the key
	 * @param fullyQualifiedTitleId a full-path book title ID using slashes to separate the namespace components, may not be null
	 * @param majorVersion book major version number, may not be blank
	 */
	public BookDefinitionKey(String fullyQualifiedTitleId, Long bookMajorVersion) {
		setFullyQualifiedTitleId(fullyQualifiedTitleId);
		setMajorVersion(bookMajorVersion);
	}
	/**
	 * Returns the fully-qualified title ID, like "uscl/cr/ak_2010_federal" 
	 * @return the title ID as entered into the database book definition table.
	 */
	public String getFullyQualifiedTitleId() {
		return fullyQualifiedTitleId;
	}
	public Long getMajorVersion() {
		return majorVersion;
	}
	/**
	 * The base title ID, without any of the leading namespace components.  Example: "ak_2010_federal".
	 * This is a transient field because we are making the space-for-time tradeoff and
	 * calculating this value once when the fullTitleId is set.  The TITLE_ID column in the database
	 * holds the fully-qualified value.
	 */
	@Transient
	public String getTitleId() {
		return (new File(fullyQualifiedTitleId)).getName();
	}

	public void setFullyQualifiedTitleId(String fullId) {
		if (StringUtils.isBlank(fullId)) {
			throw new IllegalArgumentException("Book definition key may not have a blank Title ID.");
		}
		this.fullyQualifiedTitleId = fullId;
	}
	public void setMajorVersion(Long majorVersion) {
		if (majorVersion == null) {
			throw new IllegalArgumentException("Book definition key may not have a null major version number.");
		}
		this.majorVersion = majorVersion;
	}
	/**
	 * Creates a string representation of the compound key for use as the HTML select value.
	 * @return the title id and major version separated by a single comma, like "uscl/cr/fl_2010_state,1".
	 */
	public String toKeyString() {
		return String.format("%s,%d", fullyQualifiedTitleId, majorVersion);
	}
	public String toString() {
		return ReflectionToStringBuilder.toString(this, ToStringStyle.SHORT_PREFIX_STYLE);
	}
	
	@Override 
	public int hashCode() {
		int hashCode = 0;
		if (StringUtils.isNotBlank(fullyQualifiedTitleId)) {
			hashCode += fullyQualifiedTitleId.hashCode();
		}
		if (majorVersion != null) {
			hashCode += majorVersion.hashCode();
		}
		return hashCode;
	}
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof BookDefinitionKey)) {
			return false;
		}
		BookDefinitionKey that = (BookDefinitionKey) obj;
		return (this.fullyQualifiedTitleId.equals(that.fullyQualifiedTitleId) && this.majorVersion.equals(that.majorVersion));
	}
}
