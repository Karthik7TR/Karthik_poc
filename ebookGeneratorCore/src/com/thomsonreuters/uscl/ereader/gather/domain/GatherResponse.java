/*
 * Copyright 2016: Thomson Reuters Global Resources. All Rights Reserved.
 * Proprietary and Confidential information of TRGR. Disclosure, Use or
 * Reproduction without the written authorization of TRGR is prohibited
 */

package com.thomsonreuters.uscl.ereader.gather.domain;

import java.util.ArrayList;
import java.util.Collection;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import com.thomsonreuters.uscl.ereader.gather.util.ImgMetadataInfo;

@XmlRootElement(name="gatherResponse")
public class GatherResponse {
	
	public static final int CODE_SUCCESS= 0;
	public static final int CODE_NOVUS_ERROR = 1;
	public static final int CODE_FILE_ERROR = 2;
	public static final int CODE_UNHANDLED_ERROR = 3;
	public static final int CODE_DATA_ERROR = 4;	

	private String errorMessage;
	private int errorCode;
	private int docCount;
	private int docCount2;
	private int nodeCount;
	private int skipCount;
	private int retryCount;
	private int retryCount2;
	private int expectedCount;
	private String publishStatus;
	private int missingImgCount;

	private ArrayList<String> splitTocGuidList;
	private boolean findSplitsAgain = false;
	private ArrayList<String> duplicateTocGuids;
	private ArrayList<ImgMetadataInfo> imageMetadataList ;
	
	public GatherResponse() {
		this(0, null, 0, 0, 0, 0, 0, 0, null);
	}
	
	public GatherResponse(int errorCode,String errorMessage,int docCount,int docCount2, int nodeCount, int skipCount,int retryCount,int retryCount2, String publishStatus ) {
		setErrorCode(errorCode);
		setErrorMessage(errorMessage);
		setDocCount(docCount);
		setDocCount2(docCount2);
		setNodeCount(nodeCount);
		setRetryCount(retryCount);
		setRetryCount2(retryCount2);
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
	
	
	@XmlElement(name="errorMessage", required=false)
	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}
	public String getErrorMessage() {
		return errorMessage;
	}

	@XmlElement(name="errorCode", required=true)
	public void setErrorCode(int errorCode) {
		this.errorCode = errorCode;
	}
	public int getErrorCode() {
		return errorCode;
	}

	@XmlElement(name="docCount", required=false)
	public void setDocCount(int docCount) {
		this.docCount = docCount;
	}
	public int getDocCount() {
		return docCount;
	}

	@XmlElement(name="docCount2", required=false)
	public void setDocCount2(int docCount2) {
		this.docCount2 = docCount2;
	}
	public int getDocCount2() {
		return docCount2;
	}

	@XmlElement(name="nodeCount", required=false)
	public void setNodeCount(int nodeCount) {
		this.nodeCount = nodeCount;
	}
	public int getNodeCount() {
		return nodeCount;
	}

	@XmlElement(name="skipCount", required=false)
	public void setSkipCount(int skipCount) {
		this.skipCount = skipCount;
	}
	public int getSkipCount() {
		return skipCount;
	}

	@XmlElement(name="retryCount", required=false)
	public void setRetryCount(int retryCount) {
		this.retryCount = retryCount;
	}
	public int getRetryCount() {
		return retryCount;
	}

	@XmlElement(name="retryCount2", required=false)
	public void setRetryCount2(int retryCount2) {
		this.retryCount2 = retryCount2;
	}
	public int getRetryCount2() {
		return retryCount2;
	}

	@XmlElement(name="expectedCount", required=false)
	public void setExpectedCount(int expectedCount) {
		this.expectedCount = expectedCount;
	}
	public int getExpectedCount() {
		return expectedCount;
	}

	@XmlElement(name="publishStatus", required=false)
	public void setPublishStatus(String publishStatus) {
		this.publishStatus = publishStatus;
	}
	public String getPublishStatus() {
		return publishStatus;
	}

	@XmlElement(name="missingImgCount", required=false)
	public void setMissingImgCount(int missingImgCount) {
		this.missingImgCount = missingImgCount;
	}
	public int getMissingImgCount() {
		return missingImgCount;
	}

	
	@XmlElement(name="splitGuids", required=false)
	public void setSplitTocGuidList(Collection<String> splitTocGuidList) {
		if ( splitTocGuidList != null){
			this.splitTocGuidList = new ArrayList<String>(splitTocGuidList);
		}
	}
	public ArrayList<String> getSplitTocGuidList() {
		return splitTocGuidList;
	}
	
	@XmlElement(name="findSplitsAgain", required=false)
	public void setFindSplitsAgain(boolean findSplitsAgain) {
		this.findSplitsAgain = findSplitsAgain;
	}
	public boolean isFindSplitsAgain() {
		return findSplitsAgain;
	}

	@XmlElement(name="dupTocGuids", required=false)
	public void setDuplicateTocGuids(Collection<String> duplicateTocGuids) {
		if ( duplicateTocGuids != null){
			this.duplicateTocGuids = new ArrayList<String>(duplicateTocGuids);
		}
	}
	public ArrayList<String> getDuplicateTocGuids() {
		return duplicateTocGuids;
	}
	
	public ArrayList<ImgMetadataInfo> getImageMetadataList() {
		return imageMetadataList;
	}
	@XmlElement(name="imgMetadataInfo", required=false)
	public void setImageMetadataList(ArrayList<ImgMetadataInfo> imageMetadataList) {
		this.imageMetadataList = imageMetadataList;
	}
	
	
	@Override
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
		if (docCount2 != other.docCount2)
			return false;
		if (skipCount != other.skipCount)
			return false;
		if (nodeCount != other.nodeCount)
			return false;
		if (retryCount != other.retryCount)
			return false;
		if (retryCount2 != other.retryCount2)
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
