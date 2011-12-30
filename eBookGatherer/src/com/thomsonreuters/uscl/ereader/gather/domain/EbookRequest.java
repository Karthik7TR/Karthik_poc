/*
* Copyright 2011: Thomson Reuters Global Resources. All Rights Reserved.
* Proprietary and Confidential information of TRGR. Disclosure, Use or
* Reproduction without the written authorization of TRGR is prohibited
*/


package com.thomsonreuters.uscl.ereader.gather.domain;

public class EbookRequest {
	String contentType;
	String guid;
	String collection;
	String tocFilePath;
	String docFilePath;
	
	public String getTocFilePath() {
		return tocFilePath;
	}
	public void setTocFilePath(String tocFilePath) {
		this.tocFilePath = tocFilePath;
	}
	public String getDocFilePath() {
		return docFilePath;
	}
	public void setDocFilePath(String docFilePath) {
		this.docFilePath = docFilePath;
	}
	public String getContentType() {
		return contentType;
	}
	public void setContentType(String contentType) {
		this.contentType = contentType;
	}
	public String getGuid() {
		return guid;
	}
	public void setGuid(String guid) {
		this.guid = guid;
	}
	public String getCollection() {
		return collection;
	}
	public void setCollection(String collection) {
		this.collection = collection;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((collection == null) ? 0 : collection.hashCode());
		result = prime * result
				+ ((contentType == null) ? 0 : contentType.hashCode());
		result = prime * result
				+ ((docFilePath == null) ? 0 : docFilePath.hashCode());
		result = prime * result + ((guid == null) ? 0 : guid.hashCode());
		result = prime * result
				+ ((tocFilePath == null) ? 0 : tocFilePath.hashCode());
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
		EbookRequest other = (EbookRequest) obj;
		if (collection == null) {
			if (other.collection != null)
				return false;
		} else if (!collection.equals(other.collection))
			return false;
		if (contentType == null) {
			if (other.contentType != null)
				return false;
		} else if (!contentType.equals(other.contentType))
			return false;
		if (docFilePath == null) {
			if (other.docFilePath != null)
				return false;
		} else if (!docFilePath.equals(other.docFilePath))
			return false;
		if (guid == null) {
			if (other.guid != null)
				return false;
		} else if (!guid.equals(other.guid))
			return false;
		if (tocFilePath == null) {
			if (other.tocFilePath != null)
				return false;
		} else if (!tocFilePath.equals(other.tocFilePath))
			return false;
		return true;
	}
	

	
}
