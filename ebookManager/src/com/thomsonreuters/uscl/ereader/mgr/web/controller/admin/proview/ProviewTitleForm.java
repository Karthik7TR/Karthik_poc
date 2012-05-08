/*
 * Copyright 2012: Thomson Reuters Global Resources. All Rights Reserved.
 * Proprietary and Confidential information of TRGR. Disclosure, Use or
 * Reproduction without the written authorization of TRGR is prohibited
 */

package com.thomsonreuters.uscl.ereader.mgr.web.controller.admin.proview;

public class ProviewTitleForm {
	public static final String FORM_NAME = "proviewTitleInfoForm";
	private String titleId;
	private String version;
	private String status;

	public ProviewTitleForm() {
		super();
		// TODO Auto-generated constructor stub
	}

	public ProviewTitleForm(String titleId, String version, String status) {
		super();
		this.titleId = titleId;
		this.version = version;
		this.status = status;
	}

	public String getTitleId() {
		return titleId;
	}

	public void setTitleId(String titleId) {
		this.titleId = titleId;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

}
