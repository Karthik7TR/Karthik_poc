/*
 * Copyright 2012: Thomson Reuters Global Resources. All Rights Reserved.
 * Proprietary and Confidential information of TRGR. Disclosure, Use or
 * Reproduction without the written authorization of TRGR is prohibited
 */
package com.thomsonreuters.uscl.ereader.mgr.web.controller.generate;

import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

public class GenerateBookForm {
	public static final String FORM_NAME = "generateBookForm";

	public enum Command {
		EDIT, GENERATE, CANCEL
	};

	public enum Version {
		MAJOR, MINOR, OVERWRITE
	};

	private boolean highPriorityJob;
	private String fullyQualifiedTitleId;
	private Command command;
	private Long id;
	private String currentVersion;
	private String newOverwriteVersion;
	private String newMajorVersion;
	private String newMinorVersion;
	private Version newVersion;

	public String getCurrentVersion() {
		return currentVersion;
	}

	public void setCurrentVersion(String currentVersion) {
		this.currentVersion = currentVersion;
	}

	public String getNewOverwriteVersion() {
		return newOverwriteVersion;
	}

	public void setNewOverwriteVersion(String newOverwriteVersion) {
		this.newOverwriteVersion = newOverwriteVersion;
	}

	public Version getNewVersion() {
		return newVersion;
	}

	public void setNewVersion(Version newVersion) {
		this.newVersion = newVersion;
	}

	public String getNewMajorVersion() {
		return newMajorVersion;
	}

	public void setNewMajorVersion(String newMajorVersion) {
		this.newMajorVersion = newMajorVersion;
	}

	public String getNewMinorVersion() {
		return newMinorVersion;
	}

	public void setNewMinorVersion(String newMinorVersion) {
		this.newMinorVersion = newMinorVersion;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Command getCommand() {
		return command;
	}

	public void setCommand(Command command) {
		this.command = command;
	}

	public String getFullyQualifiedTitleId() {
		return this.fullyQualifiedTitleId;
	}

	public boolean isHighPriorityJob() {
		return highPriorityJob;
	}

	public void setHighPriorityJob(boolean high) {
		this.highPriorityJob = high;
	}

	public void setFullyQualifiedTitleId(String fullyQualifiedTitleId) {
		this.fullyQualifiedTitleId = fullyQualifiedTitleId;
	}

	public String toString() {
		return ReflectionToStringBuilder.toString(this,
				ToStringStyle.SHORT_PREFIX_STYLE);
	}
}
