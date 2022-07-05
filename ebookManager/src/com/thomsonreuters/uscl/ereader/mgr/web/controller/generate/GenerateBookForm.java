package com.thomsonreuters.uscl.ereader.mgr.web.controller.generate;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class GenerateBookForm {
    public static final String FORM_NAME = "generateBookForm";

    public enum Command {
        EDIT,
        GENERATE,
        GROUP,
        CANCEL
    };

    public enum Version {
        MAJOR,
        MINOR,
        OVERWRITE,
        CUSTOM_VERSION
    };

    private boolean highPriorityJob;
    private String fullyQualifiedTitleId;
    private Command command;
    private Long id;
    private String currentVersion;
    private String newOverwriteVersion;
    private String newMajorVersion;
    private String newMinorVersion;
    private String newCustomVersion;
    private Version newVersion;
    @Getter
    @Setter
    private boolean isCombined;

    public String getCurrentVersion() {
        return currentVersion;
    }

    public void setCurrentVersion(final String currentVersion) {
        this.currentVersion = currentVersion;
    }

    public String getNewOverwriteVersion() {
        return newOverwriteVersion;
    }

    public void setNewOverwriteVersion(final String newOverwriteVersion) {
        this.newOverwriteVersion = newOverwriteVersion;
    }

    public Version getNewVersion() {
        return newVersion;
    }

    public void setNewVersion(final Version newVersion) {
        this.newVersion = newVersion;
    }

    public String getNewMajorVersion() {
        return newMajorVersion;
    }

    public void setNewMajorVersion(final String newMajorVersion) {
        this.newMajorVersion = newMajorVersion;
    }

    public String getNewMinorVersion() {
        return newMinorVersion;
    }

    public void setNewMinorVersion(final String newMinorVersion) {
        this.newMinorVersion = newMinorVersion;
    }

    public Long getId() {
        return id;
    }

    public void setId(final Long id) {
        this.id = id;
    }

    public Command getCommand() {
        return command;
    }

    public void setCommand(final Command command) {
        this.command = command;
    }

    public String getFullyQualifiedTitleId() {
        return fullyQualifiedTitleId;
    }

    public boolean isHighPriorityJob() {
        return highPriorityJob;
    }

    public void setHighPriorityJob(final boolean high) {
        highPriorityJob = high;
    }

    public void setFullyQualifiedTitleId(final String fullyQualifiedTitleId) {
        this.fullyQualifiedTitleId = fullyQualifiedTitleId;
    }

    public String getNewCustomVersion() {
        return newCustomVersion;
    }

    public void setNewCustomVersion(String newCustomVersion) {
        this.newCustomVersion = newCustomVersion;
    }

    @Override
    public String toString() {
        return ReflectionToStringBuilder.toString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }

}
