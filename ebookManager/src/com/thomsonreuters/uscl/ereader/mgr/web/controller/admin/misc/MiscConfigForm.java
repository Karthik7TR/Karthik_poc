package com.thomsonreuters.uscl.ereader.mgr.web.controller.admin.misc;

import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import com.thomsonreuters.uscl.ereader.core.job.domain.MiscConfig;

public class MiscConfigForm extends MiscConfig {
	
	public static final String FORM_NAME = "miscConfigForm";

	public void initialize(MiscConfig config) {
		setAppLogLevel(config.getAppLogLevel());
		setRootLogLevel(config.getRootLogLevel());
		setNovusEnvironment(config.getNovusEnvironment());
		setProviewHostname(config.getProviewHostname());
		setDisableExistingSingleTitleSplit(config.getDisableExistingSingleTitleSplit());
	}

	public MiscConfig createMiscConfig() {
		MiscConfig config = new MiscConfig();
		config.copy(this);
		return config;
	}
	public String toString() {
		return ReflectionToStringBuilder.toString(this, ToStringStyle.SHORT_PREFIX_STYLE);
	}
}
