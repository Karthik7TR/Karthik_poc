package com.thomsonreuters.uscl.ereader.core.job.domain;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.springframework.util.Assert;

/**
 * A key/value pair table used for misc application configuration.
 */
@Entity
@Table(name = "APP_PARAMETER")
public class AppParameter {
	
	private String key;
	private String value;
	private Date lastUpdated;
	
	public AppParameter() {
		super();
	}
	public AppParameter(String key, Object value) {
		Assert.notNull(key);
		setKey(key);
		setValue(value.toString());
	}
	
	@Id
	@Column(name = "PARAMETER_KEY")
	public String getKey() {
		return key;
	}
	@Column(name = "PARAMETER_VALUE")
	public String getValue() {
		return value;
	}
	@Column(name = "LAST_UPDATED", nullable = false)
	public Date getLastUpdated() {
		return lastUpdated;
	}
	public void setKey(String key) {
		Assert.notNull(key);
		this.key = key;
	}
	public void setValue(String value) {
		this.value = value;
	}
	public void setLastUpdated(Date lastUpdated) {
		Assert.notNull(lastUpdated);
		this.lastUpdated = lastUpdated;
	}
	public String toString() {
		return ReflectionToStringBuilder.toString(this, ToStringStyle.SHORT_PREFIX_STYLE);
	}
}
