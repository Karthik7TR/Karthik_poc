/*
 * Copyright 2011: Thomson Reuters Global Resources. All Rights Reserved.
 * Proprietary and Confidential information of TRGR. Disclosure, Use or
 * Reproduction without the written authorization of TRGR is prohibited
 */
package com.thomsonreuters.uscl.ereader.mgr.web.controller.admin.publishtypecode;

import com.thomsonreuters.uscl.ereader.core.book.domain.PubTypeCode;


public class PublishTypeCodeForm {
	//private static final Logger log = Logger.getLogger(EditBookDefinitionForm.class);
	public static final String FORM_NAME = "pubTypeCodeForm";
	
	private Long id;
	private String name;
	
	public PublishTypeCodeForm() {
		super();
	}
	
	public void initialize(PubTypeCode code) {
		this.id = code.getId();
		this.name = code.getName();
	}
	
	public PubTypeCode makeCode() {
		PubTypeCode code = new PubTypeCode();
		code.setId(id);
		code.setName(name);
		
		return code;
	}
	
	public Long getPubTypeId() {
		return id;
	}
	
	public void setPubTypeId(Long id) {
		this.id = id;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
}
