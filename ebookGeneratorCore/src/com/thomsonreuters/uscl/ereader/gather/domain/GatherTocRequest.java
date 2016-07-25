/*
 * Copyright 2016: Thomson Reuters Global Resources. All Rights Reserved.
 * Proprietary and Confidential information of TRGR. Disclosure, Use or
 * Reproduction without the written authorization of TRGR is prohibited
 */

package com.thomsonreuters.uscl.ereader.gather.domain;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import com.thomsonreuters.uscl.ereader.core.book.domain.ExcludeDocument;
import com.thomsonreuters.uscl.ereader.core.book.domain.RenameTocEntry;

@XmlRootElement(name="gatherTocRequest", namespace="com.thomsonreuters.uscl.ereader.gather.domain")
public class GatherTocRequest {

	private String guid;
	private String collectionName;
	private File tocFile;
	private ArrayList<ExcludeDocument> excludeDocuments;
	private ArrayList<RenameTocEntry> renameTocEntries;
	private boolean isFinalStage;
	private ArrayList<String> splitTocGuidList;
	private int thresholdValue;

	public GatherTocRequest(){
		super();
	}
	
	public GatherTocRequest(String guid, String collectionName, File tocFile, ArrayList<ExcludeDocument> excludeDocuments, 
			ArrayList<RenameTocEntry> renameTocEntries, boolean isFinalStage, Collection<String> splitTocGuidList, int thresholdValue) {
		super();
		this.guid = guid;
		this.collectionName = collectionName;
		this.tocFile = tocFile;
		this.excludeDocuments = excludeDocuments;
		this.renameTocEntries = renameTocEntries;
		this.isFinalStage = isFinalStage;
		if ( splitTocGuidList != null){
			setSplitTocGuidList(splitTocGuidList);
		}
		this.thresholdValue = thresholdValue;
	}

	public int getThresholdValue() {
		return thresholdValue;
	}

	public void setThresholdValue(int thresholdValue) {
		this.thresholdValue = thresholdValue;
	}

	public ArrayList<String> getSplitTocGuidList() {
		return splitTocGuidList;
	}

	public void setSplitTocGuidList(Collection<String> splitTocGuidList) {
		this.splitTocGuidList = new ArrayList<String>(splitTocGuidList);
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

	@XmlElement(name="collectionName", required=true)
	public void setCollectionName(String collectionName) {
		this.collectionName = collectionName;
	}
	
	@XmlElement(name="guid", required=true)
	public void setGuid(String guid) {
		this.guid = guid;
	}
	
	@XmlElement(name="tocFile", required=true)
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
		result = prime * result + (isFinalStage ? 1231 : 1237);
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
		if (isFinalStage != other.isFinalStage)
			return false;
		if (tocFile == null) {
			if (other.tocFile != null)
				return false;
		} else if (!tocFile.equals(other.tocFile))
			return false;
		return true;
	}

	public ArrayList<ExcludeDocument> getExcludeDocuments() {
		return excludeDocuments;
	}
	
	@XmlElementWrapper(name="excludeDocument", required=false)
	public void setExcludeDocuments(ArrayList<ExcludeDocument> excludeDocuments) {
		this.excludeDocuments = excludeDocuments;
	}

	public ArrayList<RenameTocEntry> getRenameTocEntries() {
		return renameTocEntries;
	}

	@XmlElementWrapper(name="renameTocEntry", required=false)
	public void setRenameTocEntries(ArrayList<RenameTocEntry> renameTocEntries) {
		this.renameTocEntries = renameTocEntries;
	}
	
	public boolean isFinalStage() {
		return isFinalStage;
	}
	@XmlElement(name="isFinalStage", required=true)
	public void setFinalStage(boolean isFinalStage) {
		this.isFinalStage = isFinalStage;
	}
	
}
