/*
 * Copyright 2011: Thomson Reuters Global Resources. All Rights Reserved.
 * Proprietary and Confidential information of TRGR. Disclosure, Use or
 * Reproduction without the written authorization of TRGR is prohibited
 */
package com.thomsonreuters.uscl.ereader.mgr.web.controller.admin.outage;

import com.thomsonreuters.uscl.ereader.core.outage.domain.OutageType;

public class OutageTypeForm {
	//private static final Logger log = Logger.getLogger(OutageTypeForm.class);
	public static final String FORM_NAME = "outageTypeForm";
	
	private Long outageTypeId;
	private String system;
	private String subSystem;
	
	public OutageTypeForm() {
		super();
	}
	
	public void initialize(OutageType outageType) {
		this.outageTypeId = outageType.getId();
		this.system = outageType.getSystem();
		this.subSystem = outageType.getSubSystem();
	}
	
	public OutageType createOutageType() {
		OutageType outageType = new OutageType();
		outageType.setId(outageTypeId);
		outageType.setSystem(system);
		outageType.setSubSystem(subSystem);
		return outageType;
	}

	public Long getOutageTypeId() {
		return outageTypeId;
	}

	public void setOutageTypeId(Long id) {
		this.outageTypeId = id;
	}

	public String getSystem() {
		return system;
	}

	public void setSystem(String system) {
		this.system = system;
	}

	public String getSubSystem() {
		return subSystem;
	}

	public void setSubSystem(String subSystem) {
		this.subSystem = subSystem;
	}

}
