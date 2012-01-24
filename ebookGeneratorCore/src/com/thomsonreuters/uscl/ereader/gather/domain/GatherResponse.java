/*
 * Copyright 2011: Thomson Reuters Global Resources. All Rights Reserved.
 * Proprietary and Confidential information of TRGR. Disclosure, Use or
 * Reproduction without the written authorization of TRGR is prohibited
 */


package com.thomsonreuters.uscl.ereader.gather.domain;

import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

public class GatherResponse {
	
	public static final int CODE_SUCCESS= 0;
	public static final int CODE_NOVUS_ERROR = 1;
	public static final int CODE_FILE_ERROR = 2;
	
	public String errorMessage;
	public int errorCode;
	
	public GatherResponse() {
		this(0, null);
	}
	
	public GatherResponse(int errorCode,String errorMessage) {
		setErrorCode(errorCode);
		setErrorMessage(errorMessage);
	}

	public String getErrorMessage() {
		return errorMessage;
	}
	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}
	public int getErrorCode() {
		return errorCode;
	}
	public void setErrorCode(int errorCode) {
		this.errorCode = errorCode;
	}
	public String toString()
	{
		return ReflectionToStringBuilder.toString(this, ToStringStyle.SHORT_PREFIX_STYLE);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + errorCode;
		result = prime * result
				+ ((errorMessage == null) ? 0 : errorMessage.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		GatherResponse other = (GatherResponse) obj;
		if (errorCode != other.errorCode)
			return false;
		if (errorMessage == null) {
			if (other.errorMessage != null)
				return false;
		} else if (!errorMessage.equals(other.errorMessage))
			return false;
		return true;
	}
}
