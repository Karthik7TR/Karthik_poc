/*
 * Copyright 2011: Thomson Reuters Global Resources. All Rights Reserved.
 * Proprietary and Confidential information of TRGR. Disclosure, Use or
 * Reproduction without the written authorization of TRGR is prohibited
 */
package com.thomsonreuters.uscl.ereader.core.book.domain;

import java.io.Serializable;

import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

public class KeywordTypeValue implements Serializable {
	//private static final Logger log = Logger.getLogger(Author.class);
	private static final long serialVersionUID = 3967708874931468743L;
	
	private Long valueId;
	private KeywordTypeCode keywordTypeCode;
	private String valueName;
	
	public KeywordTypeValue() {
		super();
	}
	

	public Long getValueId() {
		return valueId;
	}


	public void setValueId(Long valueId) {
		this.valueId = valueId;
	}


	public KeywordTypeCode getKeywordTypeCode() {
		return keywordTypeCode;
	}


	public void setKeywordTypeCode(KeywordTypeCode keywordTypeCode) {
		this.keywordTypeCode = keywordTypeCode;
	}


	public String getValueName() {
		return valueName;
	}

	public void setValueName(String valueName) {
		this.valueName = valueName;
	}

	@Override
	public String toString() {
		return ReflectionToStringBuilder.toString(this, ToStringStyle.SHORT_PREFIX_STYLE);
	}
	
}
