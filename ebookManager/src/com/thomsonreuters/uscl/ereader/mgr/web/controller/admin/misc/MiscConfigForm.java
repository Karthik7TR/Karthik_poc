package com.thomsonreuters.uscl.ereader.mgr.web.controller.admin.misc;

import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.apache.log4j.Level;
import org.springframework.util.Assert;

import com.thomsonreuters.uscl.ereader.core.job.domain.AppConfig;
import com.thomsonreuters.uscl.ereader.core.job.domain.LoggingConfig;

public class MiscConfigForm {
	
	public static final String FORM_NAME = "miscConfigForm";

	private Level appLogLevel = AppConfig.DEFAULT_APP_LOG_LEVEL;
	private Level rootLogLevel = AppConfig.DEFAULT_ROOT_LOG_LEVEL;
	
	public MiscConfigForm() {
		super();
	}
	
	public void initialize(LoggingConfig config) {
		setAppLogLevel(config.getAppLogLevel());
		setRootLogLevel(config.getRootLogLevel());
	}

	public Level getAppLogLevel() {
		return appLogLevel;
	}
	public Level getRootLogLevel() {
		return rootLogLevel;
	}
	public LoggingConfig getLoggingConfig() {
		return AppConfig.createLoggingConfig(appLogLevel, rootLogLevel);
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
