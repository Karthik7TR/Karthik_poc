/*
 * Copyright 2011: Thomson Reuters Global Resources. All Rights Reserved.
 * Proprietary and Confidential information of TRGR. Disclosure, Use or
 * Reproduction without the written authorization of TRGR is prohibited
 */
package com.thomsonreuters.uscl.ereader.core.job.domain;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

@Deprecated
@Entity
@Table(name="BATCH_JOB_PARAMS", schema="EBOOK_SPRINGBATCH")
public class JobParameterEntity implements Serializable {

	private static final long serialVersionUID = 9120622521956210255L;
	public static final String KEY_NAME_PROPERTY_NAME = "keyName";
	public static final String STRING_VAL_PROPERTY_NAME = "stringVal";
	
	private Long 	jobInstanceId;
	private String 	typeCode;
	private String 	keyName;
	private String 	stringVal;
	private Date	dateVal;
	private Long	longVal;
	private Double	doubleVal;
	
	@Id
	@Column(name="JOB_INSTANCE_ID")
	public Long getJobInstanceId() {
		return jobInstanceId;
	}
	@Column(name="TYPE_CD")
	public String getTypeCode() {
		return typeCode;
	}
	@Column(name="KEY_NAME")
	public String getKeyName() {
		return keyName;
	}
	@Column(name="STRING_VAL")
	public String getStringVal() {
		return stringVal;
	}
	@Column(name="DATE_VAL")
	public Date getDateVal() {
		return dateVal;
	}
	@Column(name="LONG_VAL")
	public Long getLongVal() {
		return longVal;
	}
	@Column(name="DOUBLE_VAL")
	public Double getDoubleVal() {
		return doubleVal;
	}
	
	public void setJobInstanceId(Long jobInstanceId) {
		this.jobInstanceId = jobInstanceId;
	}
	public void setTypeCode(String typeCode) {
		this.typeCode = typeCode;
	}
	public void setKeyName(String keyName) {
		this.keyName = keyName;
	}
	public void setStringVal(String stringVal) {
		this.stringVal = stringVal;
	}
	public void setDateVal(Date dateVal) {
		this.dateVal = dateVal;
	}
	public void setLongVal(Long longVal) {
		this.longVal = longVal;
	}
	public void setDoubleVal(Double doubleVal) {
		this.doubleVal = doubleVal;
	}


	
	@Override
	public String toString() {
		return ReflectionToStringBuilder.toString(this, ToStringStyle.SHORT_PREFIX_STYLE);
	}
}
