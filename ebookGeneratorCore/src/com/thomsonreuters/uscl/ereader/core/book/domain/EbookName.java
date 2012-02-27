/*
 * Copyright 2011: Thomson Reuters Global Resources. All Rights Reserved.
 * Proprietary and Confidential information of TRGR. Disclosure, Use or
 * Reproduction without the written authorization of TRGR is prohibited
 */
package com.thomsonreuters.uscl.ereader.core.book.domain;

import java.io.Serializable;

import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import com.thomsonreuters.uscl.ereader.orchestrate.core.BookDefinition;

public class EbookName implements Serializable{
	private static final long serialVersionUID = -1186228301263342819L;
	//private static final Logger log = Logger.getLogger(Author.class);
	
	private Long nameId;
	private BookDefinition book;
	private String nameText;
	private Long sequenceNumber;

	
	public EbookName() {
		super();
	}
	
	public Long getNameId() {
		return nameId;
	}

	public void setNameId(Long nameId) {
		this.nameId = nameId;
	}

	public BookDefinition getBook() {
		return book;
	}

	public void setBook(BookDefinition book) {
		this.book = book;
	}

	public String getNameText() {
		return nameText;
	}

	public void setNameText(String nameText) {
		this.nameText = nameText;
	}

	public Long getSequenceNumber() {
		return sequenceNumber;
	}

	public void setSequenceNumber(Long sequenceNumber) {
		this.sequenceNumber = sequenceNumber;
	}

	@Override
	public String toString() {
		return ReflectionToStringBuilder.toString(this, ToStringStyle.SHORT_PREFIX_STYLE);
	}
	
}
