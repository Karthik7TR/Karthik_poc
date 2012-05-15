/*
 * Copyright 2012: Thomson Reuters Global Resources. All Rights Reserved.
 * Proprietary and Confidential information of TRGR. Disclosure, Use or
 * Reproduction without the written authorization of TRGR is prohibited
 */

package com.thomsonreuters.uscl.ereader.mgr.web.controller.proviewlist;

public class ProviewTitleForm {
	public static final String FORM_NAME = "proviewTitleInfoForm";

	public enum Command {
		REMOVE, DELETE, PROMOTE
	};

	private String titleId;
	private String version;
	private String status;
	private Command command;

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

	public Command getCommand() {
		return command;
	}

	public void setCommand(Command cmd) {
		this.command = cmd;
	}
}
