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

public class EbookKeyword implements Serializable {
	private static final long serialVersionUID = 3300480045778205483L;
	//private static final Logger log = Logger.getLogger(Author.class);
	
	private Integer typeValueId;
	private BookDefinition book;
	
	public EbookKeyword() {
		super();
	}
	


	public Integer getTypeValueId() {
		return typeValueId;
	}



	public void setTypeValueId(Integer typeValueId) {
		this.typeValueId = typeValueId;
	}



	public BookDefinition getBookDefinitionId() {
		return book;
	}



	public void setBookDefinitionId(BookDefinition book) {
		this.book = book;
	}



	@Override
	public String toString() {
		return ReflectionToStringBuilder.toString(this, ToStringStyle.SHORT_PREFIX_STYLE);
	}
	
}
