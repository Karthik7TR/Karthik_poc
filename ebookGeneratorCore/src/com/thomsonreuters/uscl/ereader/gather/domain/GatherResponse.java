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
	public static final int CODE_UNHANDLED_ERROR = 3;

	public String errorMessage;
	public int errorCode;
	public int docCount;
	public int nodeCount;
	public int skipCount;
	public int retryCount;
	public int expectedCount;
	public String publishStatus;
	// If you add any values make sure you update the jibx-bindings.xml for mapping gatherResponse


	public GatherResponse() {
		this(0, null, 0, 0, 0, 0, null);
	}
	
	public GatherResponse(int errorCode,String errorMessage,int docCount, int nodeCount, int skipCount,int retryCount, String publishStatus ) {
		setErrorCode(errorCode);
		setErrorMessage(errorMessage);
		setDocCount(docCount);
		setNodeCount(nodeCount);
		setRetryCount(retryCount);
		setSkipCount(skipCount);
		setPublishStatus(publishStatus);
		
	}
	
	public GatherResponse(int errorCode,String errorMessage, int docCount, int retryCount, int expectedCount, String publishStatus ) {
		setErrorCode(errorCode);
		setErrorMessage(errorMessage);
		setDocCount(docCount);
		setRetryCount(retryCount);
		setExpectedCount(expectedCount);
		setPublishStatus(publishStatus);

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
	public int getDocCount() {
		return docCount;
	}

	public void setDocCount(int docCount) {
		this.docCount = docCount;
	}

	public int getNodeCount() {
		return nodeCount;
	}

	public void setNodeCount(int nodeCount) {
		this.nodeCount = nodeCount;
	}

	public int getSkipCount() {
		return skipCount;
	}

	public void setSkipCount(int skipCount) {
		this.skipCount = skipCount;
	}
	public int getRetryCount() {
		return retryCount;
	}

	public void setRetryCount(int retryCount) {
		this.retryCount = retryCount;
	}
	public int getExpectedCount() {
		return expectedCount;
	}

	public void setExpectedCount(int expectedCount) {
		this.expectedCount = expectedCount;
	}



	public String getPublishStatus() {
		return publishStatus;
	}

	public void setPublishStatus(String publishStatus) {
		this.publishStatus = publishStatus;
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
		if (docCount != other.docCount)
			return false;
		if (skipCount != other.skipCount)
			return false;
		if (nodeCount != other.nodeCount)
			return false;
		if (retryCount != other.retryCount)
			return false;
		if (expectedCount != other.expectedCount)
			return false;
		if (publishStatus == null) {
			if (other.publishStatus != null)
				return false;
		} else if (!publishStatus.equals(other.publishStatus))
			return false;
		return true;
	}
}
