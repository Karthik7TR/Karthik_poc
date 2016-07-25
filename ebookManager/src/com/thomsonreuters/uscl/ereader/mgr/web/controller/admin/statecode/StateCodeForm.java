/*
 * Copyright 2016: Thomson Reuters Global Resources. All Rights Reserved.
 * Proprietary and Confidential information of TRGR. Disclosure, Use or
 * Reproduction without the written authorization of TRGR is prohibited
 */

package com.thomsonreuters.uscl.ereader.mgr.web.controller.admin.statecode;

import com.thomsonreuters.uscl.ereader.core.book.domain.StateCode;


public class StateCodeForm {
	//private static final Logger log = LogManager.getLogger(EditBookDefinitionForm.class);
	public static final String FORM_NAME = "stateCodeForm";
	
	private Long id;
	private String name;
	
	public StateCodeForm() {
		super();
	}
	
	public void initialize(StateCode code) {
		this.id = code.getId();
		this.name = code.getName();
	}
	
	public StateCode makeCode() {
		StateCode code = new StateCode();
		code.setId(id);
		code.setName(name);
		
		return code;
	}
	
	public Long getStateId() {
		return id;
	}
	
	public void setStateId(Long id) {
		this.id = id;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
}
