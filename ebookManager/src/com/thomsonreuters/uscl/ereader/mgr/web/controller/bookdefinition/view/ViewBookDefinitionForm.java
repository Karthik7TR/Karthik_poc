/*
 * Copyright 2012: Thomson Reuters Global Resources. All Rights Reserved.
 * Proprietary and Confidential information of TRGR. Disclosure, Use or
 * Reproduction without the written authorization of TRGR is prohibited
 */
package com.thomsonreuters.uscl.ereader.mgr.web.controller.bookdefinition.view;

import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

public class ViewBookDefinitionForm {
	public static final String FORM_NAME = "viewBookDefinitionForm";
	
	public enum Command { EDIT, GENERATE, DELETE, AUDIT_LOG, BOOK_PUBLISH_STATS, COPY, RESTORE };

	private Command command;
	private Long id;

	public Command getCommand() {
		return command;
	}
	public void setCommand(Command cmd) {
		this.command = cmd;
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String toString() {
		return ReflectionToStringBuilder.toString(this, ToStringStyle.SHORT_PREFIX_STYLE);
	}
}
