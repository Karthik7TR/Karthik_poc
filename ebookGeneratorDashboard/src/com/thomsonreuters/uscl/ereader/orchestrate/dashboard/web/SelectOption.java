package com.thomsonreuters.uscl.ereader.orchestrate.dashboard.web;

import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

/**
 * A HTML select option value and label.
 * A model object used to display dynamic lists of options in HTML select components (drop-downs).
 * Populated in the controller and placed on the HttpRequest under a unique attribute key.
 */
public class SelectOption {
	
	private String label;
	private String value;

	public SelectOption(String label, String value) {
		this.label = label;
		this.value = value;
	}
	
	public String getLabel() {
		return label;
	}
	public String getValue() {
		return value;
	}
	public String toString() {
		return ReflectionToStringBuilder.toString(this, ToStringStyle.SHORT_PREFIX_STYLE);
	}
}
