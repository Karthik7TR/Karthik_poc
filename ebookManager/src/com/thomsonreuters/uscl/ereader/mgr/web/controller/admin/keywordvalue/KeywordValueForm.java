/*
 * Copyright 2011: Thomson Reuters Global Resources. All Rights Reserved.
 * Proprietary and Confidential information of TRGR. Disclosure, Use or
 * Reproduction without the written authorization of TRGR is prohibited
 */
package com.thomsonreuters.uscl.ereader.mgr.web.controller.admin.keywordvalue;

import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import com.thomsonreuters.uscl.ereader.core.book.domain.KeywordTypeCode;
import com.thomsonreuters.uscl.ereader.core.book.domain.KeywordTypeValue;


public class KeywordValueForm {
	//private static final Logger log = Logger.getLogger(EditBookDefinitionForm.class);
	public static final String FORM_NAME = "KeywordValueForm";
	
	private Long id;
	private String name;
	private KeywordTypeCode keywordTypeCode;
	
	public KeywordValueForm() {
		super();
	}
	
	public void initialize(KeywordTypeValue value) {
		this.id = value.getId();
		this.name = value.getName();
		this.keywordTypeCode = value.getKeywordTypeCode();
	}
	
	public KeywordTypeValue makeKeywordTypeValue() {
		KeywordTypeValue value = new KeywordTypeValue();
		value.setId(id);
		value.setName(name);
		value.setKeywordTypeCode(keywordTypeCode);
		
		return value;
	}
	
	public Long getTypeId() {
		return id;
	}
	
	public void setTypeId(Long id) {
		this.id = id;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}

	public KeywordTypeCode getKeywordTypeCode() {
		return keywordTypeCode;
	}

	public void setKeywordTypeCode(KeywordTypeCode keywordTypeCode) {
		this.keywordTypeCode = keywordTypeCode;
	}

	@Override
	public String toString() {
		return ReflectionToStringBuilder.toString(this, ToStringStyle.SHORT_PREFIX_STYLE);
	}
	

}
