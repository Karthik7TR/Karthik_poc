package com.thomsonreuters.uscl.ereader.orchestrate.engine.throttle;

import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

public class SimpleThrottleConfig implements ThrottleConfig {
	
	private int maximum = Integer.MAX_VALUE;

	@Override
	public int getMaximum() {
		return maximum;
	}
	@Override
	public void setMaximum(int max) {
		this.maximum = max;
	}
	public String toString() {
		return ReflectionToStringBuilder.toString(this, ToStringStyle.SHORT_PREFIX_STYLE);
	}
}
