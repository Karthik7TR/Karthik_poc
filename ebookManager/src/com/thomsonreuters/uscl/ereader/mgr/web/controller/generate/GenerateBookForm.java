/*
 * Copyright 2012: Thomson Reuters Global Resources. All Rights Reserved.
 * Proprietary and Confidential information of TRGR. Disclosure, Use or
 * Reproduction without the written authorization of TRGR is prohibited
 */
package com.thomsonreuters.uscl.ereader.mgr.web.controller.generate;

import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import com.thomsonreuters.uscl.ereader.mgr.web.controller.bookdefinition.view.ViewBookDefinitionForm.Command;
import org.apache.commons.lang.builder.ToStringStyle;


public class GenerateBookForm {
	public static final String FORM_NAME = "generateBookForm";
	
	private boolean highPriorityJob;
	private boolean majorVersion;
	private String fullTitleId;
	private Command command;

	public Command getCommand() {
		return command;
	}

	public void setCommand(Command command) {
		this.command = command;
	}

	public boolean isMajorVersion() {
		return majorVersion;
	}

	public void setMajorVersion(boolean majorVersion) {
		this.majorVersion = majorVersion;
	}


	public String getFullyQualifiedTitleId() {
		return this.fullTitleId;
	}

	public boolean isHighPriorityJob() {
		return highPriorityJob;
	}

	public void setHighPriorityJob(boolean high) {
		this.highPriorityJob = high;
	}

	public void setFullyQualifiedTitleId(String fullTitleId) {
		this.fullTitleId = fullTitleId;
	}

	public String toString() {
		return ReflectionToStringBuilder.toString(this,
				ToStringStyle.SHORT_PREFIX_STYLE);
	}
}
