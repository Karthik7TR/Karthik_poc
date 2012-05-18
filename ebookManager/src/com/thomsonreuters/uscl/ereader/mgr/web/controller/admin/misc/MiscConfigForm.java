package com.thomsonreuters.uscl.ereader.mgr.web.controller.admin.misc;

import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.apache.log4j.Level;
import org.springframework.util.Assert;

import com.thomsonreuters.uscl.ereader.core.job.domain.MiscConfig;

public class MiscConfigForm {
	
	public static final String FORM_NAME = "miscConfigForm";

	private Level appLogLevel = MiscConfig.DEFAULT_APP_LOG_LEVEL;
	private Level rootLogLevel = MiscConfig.DEFAULT_ROOT_LOG_LEVEL;
	
	public MiscConfigForm() {
		super();
	}
	
	public void initialize(MiscConfig config) {
		setAppLogLevel(config.getAppLogLevel());
		setRootLogLevel(config.getRootLogLevel());
	}

	public Level getAppLogLevel() {
		return appLogLevel;
	}
	public Level getRootLogLevel() {
		return rootLogLevel;
	}
	public MiscConfig getMiscConfig() {
		return new MiscConfig(appLogLevel, rootLogLevel);
	}
	public void setAppLogLevel(String level) {
		Assert.notNull(level);
		setAppLogLevel(Level.toLevel(level));
	}
	public void setAppLogLevel(Level level) {
		Assert.notNull(level);
		this.appLogLevel = level;
	}
	public void setRootLogLevel(String level) {
		Assert.notNull(level);
		setRootLogLevel(Level.toLevel(level));
	}
	public void setRootLogLevel(Level level) {
		Assert.notNull(level);
		this.rootLogLevel = level;
	}

	public String toString() {
		return ReflectionToStringBuilder.toString(this, ToStringStyle.SHORT_PREFIX_STYLE);
	}
}
