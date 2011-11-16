package com.thomsonreuters.uscl.ereader.orchestrate.engine.throttle;

import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.springframework.stereotype.Component;

/**
 * Encapsulates the properties that control job launch.
 */
@Component("jobRunThrottleConfig")
public class JobRunThrottleConfig extends SimpleThrottleConfig {
	
	public JobRunThrottleConfig() {
		setMaximum(10);
	}

	public String toString() {
		return ReflectionToStringBuilder.toString(this, ToStringStyle.SHORT_PREFIX_STYLE);
	}
}
