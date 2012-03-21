/*
 * Copyright 2011: Thomson Reuters Global Resources. All Rights Reserved.
 * Proprietary and Confidential information of TRGR. Disclosure, Use or
 * Reproduction without the written authorization of TRGR is prohibited
 */

package com.thomsonreuters.uscl.ereader.gather.domain;

import java.io.File;

import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

public class GatherTocRequest {

	private String guid;
	private String collectionName;
	private File tocFile;

	public GatherTocRequest(){
		super();
	}
	
	public GatherTocRequest(String guid, String collectionName, File tocFile) {
		super();
		this.guid = guid;
		this.collectionName = collectionName;
		this.tocFile = tocFile;
	}

	public String getCollectionName() {
		return collectionName;
	}
	public String getGuid() {
		return guid;
	}
	public File getTocFile() {
		return tocFile;
	}


	public void setCollectionName(String collectionName) {
		this.collectionName = collectionName;
	}

	public void setGuid(String guid) {
		this.guid = guid;
	}
	public void setTocFile(File tocFile) {
		this.tocFile = tocFile;
	}

	public String toString() {
		return ReflectionToStringBuilder.toString(this,ToStringStyle.SHORT_PREFIX_STYLE);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((collectionName == null) ? 0 : collectionName.hashCode());
		result = prime * result + ((guid == null) ? 0 : guid.hashCode());
		result = prime * result + ((tocFile == null) ? 0 : tocFile.hashCode());
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
		GatherTocRequest other = (GatherTocRequest) obj;
		if (collectionName == null) {
			if (other.collectionName != null)
				return false;
		} else if (!collectionName.equals(other.collectionName))
			return false;
		if (guid == null) {
			if (other.guid != null)
				return false;
		} else if (!guid.equals(other.guid))
			return false;
		if (tocFile == null) {
			if (other.tocFile != null)
				return false;
		} else if (!tocFile.equals(other.tocFile))
			return false;
		return true;
	}

	


}
