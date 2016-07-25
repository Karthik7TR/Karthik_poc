/*
 * Copyright 2016: Thomson Reuters Global Resources. All Rights Reserved.
 * Proprietary and Confidential information of TRGR. Disclosure, Use or
 * Reproduction without the written authorization of TRGR is prohibited
 */

package com.thomsonreuters.uscl.ereader.gather.domain;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import com.thomsonreuters.uscl.ereader.core.book.domain.ExcludeDocument;
import com.thomsonreuters.uscl.ereader.core.book.domain.RenameTocEntry;

@XmlRootElement(name="gatherNortRequest")
public class GatherNortRequest {

	private Date cutoffDate;
	private String domainName;
	private String expressionFilter;
	private File nortFile;
	private ArrayList<ExcludeDocument> excludeDocuments;
	private ArrayList<RenameTocEntry> renameTocEntries;
	private boolean isFinalStage;
	private boolean useReloadContent;
	private ArrayList<String> splitTocGuidList;
	private int thresholdValue;

	public GatherNortRequest(){
		super();
	}
	
	public GatherNortRequest(String domainName, String expressionFilter, File nortFile, Date cutoffDate, 
			ArrayList<ExcludeDocument> excludeDocuments, ArrayList<RenameTocEntry> renameTocEntries, 
			boolean isFinalStage, boolean useReloadContent, Collection<String> splitTocGuidList, int thresholdValue) {
		super();
		this.domainName = domainName;
		this.expressionFilter = expressionFilter;
		this.nortFile = nortFile;
		this.cutoffDate = cutoffDate;
		this.excludeDocuments = excludeDocuments;
		this.renameTocEntries = renameTocEntries;
		this.isFinalStage = isFinalStage;
		this.useReloadContent = useReloadContent;
		if ( splitTocGuidList != null){
			setSplitTocGuidList(splitTocGuidList);
		}
		this.thresholdValue = thresholdValue;
	}


	@XmlElement(name="cutoffDate", required=false)
	public void setCutoffDate(Date cutoffDate) {
		this.cutoffDate = cutoffDate;
	}
	public Date getCutoffDate() {
		return cutoffDate;
	}
	
	@XmlElement(name="domainName", required=true)
	public void setDomainName(String domainName) {
		this.domainName = domainName;
	}
	public String getDomainName() {
		return domainName;
	}

	@XmlElement(name="expressionFilter", required=true)
	public void setExpressionFilter(String expressionFilter) {
		this.expressionFilter = expressionFilter;
	}
	public String getExpressionFilter() {
		return expressionFilter;
	}

	@XmlElement(name="nortFile", required=true)
	public void setNortFile(File nortFile) {
		this.nortFile = nortFile;
	}
	public File getNortFile() {
		return nortFile;
	}
	
	@XmlElementWrapper(name="excludeDocuments", required=false)
	public void setExcludeDocuments(ArrayList<ExcludeDocument> excludeDocuments) {
		this.excludeDocuments = excludeDocuments;
	}
	public ArrayList<ExcludeDocument> getExcludeDocuments() {
		return excludeDocuments;
	}

	@XmlElementWrapper(name="renameTocEntry", required=false)
	public void setRenameTocEntries(ArrayList<RenameTocEntry> renameTocEntries) {
		this.renameTocEntries = renameTocEntries;
	}
	public ArrayList<RenameTocEntry> getRenameTocEntries() {
		return renameTocEntries;
	}

	@XmlElement(name="isFinalStage", required=true)
	public void setFinalStage(boolean isFinalStage) {
		this.isFinalStage = isFinalStage;
	}
	public boolean isFinalStage() {
		return isFinalStage;
	}

	@XmlElement(name="useReloadContent", required=true)
	public void setUseReloadContent(boolean useReloadContent) {
		this.useReloadContent = useReloadContent;
	}
	public boolean getUseReloadContent() {
		return useReloadContent;
	}

	@XmlElementWrapper(name="splitGuids", required=false)
	public void setSplitTocGuidList(Collection<String> splitTocGuidList) {
		this.splitTocGuidList = new ArrayList<String>(splitTocGuidList);
	}
	public List<String> getSplitTocGuidList() {
		return this.splitTocGuidList;
	}
	
	@XmlElement(name="thresholdValue", required=false)
	public void setThresholdValue(int thresholdValue) {
		this.thresholdValue = thresholdValue;
	}
	public int getThresholdValue() {
		return thresholdValue;
	}
	
	@Override
	public String toString() {
		return ReflectionToStringBuilder.toString(this,ToStringStyle.SHORT_PREFIX_STYLE);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((cutoffDate == null) ? 0 : cutoffDate.hashCode());
		result = prime * result
				+ ((domainName == null) ? 0 : domainName.hashCode());
		result = prime
				* result
				+ ((excludeDocuments == null) ? 0 : excludeDocuments.hashCode());
		result = prime
				* result
				+ ((expressionFilter == null) ? 0 : expressionFilter.hashCode());
		result = prime * result + (isFinalStage ? 1231 : 1237);
		result = prime * result
				+ ((nortFile == null) ? 0 : nortFile.hashCode());
		result = prime
				* result
				+ ((renameTocEntries == null) ? 0 : renameTocEntries.hashCode());
		result = prime * result + (useReloadContent ? 1231 : 1237);
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
		GatherNortRequest other = (GatherNortRequest) obj;
		if (cutoffDate == null) {
			if (other.cutoffDate != null)
				return false;
		} else if (!cutoffDate.equals(other.cutoffDate))
			return false;
		if (domainName == null) {
			if (other.domainName != null)
				return false;
		} else if (!domainName.equals(other.domainName))
			return false;
		if (excludeDocuments == null) {
			if (other.excludeDocuments != null)
				return false;
		} else if (!excludeDocuments.equals(other.excludeDocuments))
			return false;
		if (expressionFilter == null) {
			if (other.expressionFilter != null)
				return false;
		} else if (!expressionFilter.equals(other.expressionFilter))
			return false;
		if (isFinalStage != other.isFinalStage)
			return false;
		if (nortFile == null) {
			if (other.nortFile != null)
				return false;
		} else if (!nortFile.equals(other.nortFile))
			return false;
		if (renameTocEntries == null) {
			if (other.renameTocEntries != null)
				return false;
		} else if (!renameTocEntries.equals(other.renameTocEntries))
			return false;
		if (useReloadContent != other.useReloadContent)
			return false;
		return true;
	}

//	public void setJobInstance(Long jobInstance) {
//		this.jobInstance = jobInstance;
//	}
//
//	public Long getJobInstance() {
//		return jobInstance;
//	}
}
