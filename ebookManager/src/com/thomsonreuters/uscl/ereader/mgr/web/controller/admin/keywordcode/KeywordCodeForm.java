/*
 * Copyright 2016: Thomson Reuters Global Resources. All Rights Reserved.
 * Proprietary and Confidential information of TRGR. Disclosure, Use or
 * Reproduction without the written authorization of TRGR is prohibited
 */

package com.thomsonreuters.uscl.ereader.mgr.web.controller.admin.keywordcode;

import com.thomsonreuters.uscl.ereader.core.book.domain.KeywordTypeCode;


public class KeywordCodeForm {
	//private static final Logger log = LogManager.getLogger(KeywordCodeForm.class);
	public static final String FORM_NAME = "KeywordCodeForm";
	
	private Long id;
	private String name;
	private boolean isRequired;
	
	public KeywordCodeForm() {
		super();
	}
	
	public void initialize(KeywordTypeCode code) {
		this.id = code.getId();
		this.name = code.getName();
		this.isRequired = code.getIsRequired();
	}
	
	public KeywordTypeCode makeCode() {
		KeywordTypeCode code = new KeywordTypeCode();
		code.setId(id);
		code.setName(name);
		code.setIsRequired(isRequired);
		
		return code;
	}
	
	public Long getCodeId() {
		return id;
	}
	
	public void setCodeId(Long id) {
		this.id = id;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}

	public boolean isRequired() {
		return isRequired;
	}

	public void setRequired(boolean isRequired) {
		this.isRequired = isRequired;
	}
	
}
