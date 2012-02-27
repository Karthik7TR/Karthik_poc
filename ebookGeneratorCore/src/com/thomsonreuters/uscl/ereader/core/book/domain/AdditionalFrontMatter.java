/*
 * Copyright 2011: Thomson Reuters Global Resources. All Rights Reserved.
 * Proprietary and Confidential information of TRGR. Disclosure, Use or
 * Reproduction without the written authorization of TRGR is prohibited
 */
package com.thomsonreuters.uscl.ereader.core.book.domain;

import java.io.Serializable;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import com.thomsonreuters.uscl.ereader.orchestrate.core.BookDefinition;

public class AdditionalFrontMatter implements Serializable {
	private static final long serialVersionUID = 4680526731352225863L;
	//private static final Logger log = Logger.getLogger(Author.class);
	private Long frontMatterId;
	private BookDefinition book;
	private String additionalFrontMatterText;
	private Long sequenceNumber;
	
	public AdditionalFrontMatter() {
		super();
	}
	

	public Long getFrontMatterId() {
		return frontMatterId;
	}


	public void setFrontMatterId(Long frontMatterId) {
		this.frontMatterId = frontMatterId;
	}


	public BookDefinition getBook() {
		return book;
	}


	public void setBook(BookDefinition book) {
		this.book = book;
	}


	public String getAdditionalFrontMatterText() {
		return additionalFrontMatterText;
	}


	public void setAdditionalFrontMatterText(String additionalFrontMatterText) {
		this.additionalFrontMatterText = additionalFrontMatterText;
	}


	public Long getSequenceNumber() {
		return sequenceNumber;
	}


	public void setSequenceNumber(Long sequenceNumber) {
		this.sequenceNumber = sequenceNumber;
	}
	
	public boolean isEmpty() {
		return StringUtils.isBlank(this.additionalFrontMatterText) & this.sequenceNumber == null;
	}


	@Override
	public String toString() {
		return ReflectionToStringBuilder.toString(this, ToStringStyle.SHORT_PREFIX_STYLE);
	}
}
