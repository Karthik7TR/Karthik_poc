/*
 * Copyright 2011: Thomson Reuters Global Resources. All Rights Reserved.
 * Proprietary and Confidential information of TRGR. Disclosure, Use or
 * Reproduction without the written authorization of TRGR is prohibited
 */

package com.thomsonreuters.uscl.ereader.gather.domain;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;

import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import com.thomsonreuters.uscl.ereader.core.book.domain.ExcludeDocument;
import com.thomsonreuters.uscl.ereader.core.book.domain.RenameTocEntry;

public class GatherNortRequest {

	private Date cutoffDate;
	private String domainName;
	private String expressionFilter;
	private File nortFile;
	private ArrayList<ExcludeDocument> excludeDocuments;
	private ArrayList<RenameTocEntry> renameTocEntries;
	private boolean isFinalStage;
	private boolean useReloadContent;

	public GatherNortRequest(){
		super();
	}
	
	public GatherNortRequest(String domainName, String expressionFilter, File nortFile, Date cutoffDate, 
			ArrayList<ExcludeDocument> excludeDocuments, ArrayList<RenameTocEntry> renameTocEntries, 
			boolean isFinalStage, boolean useReloadContent) {
		super();
		this.domainName = domainName;
		this.expressionFilter = expressionFilter;
		this.nortFile = nortFile;
		this.cutoffDate = cutoffDate;
		this.excludeDocuments = excludeDocuments;
		this.renameTocEntries = renameTocEntries;
		this.isFinalStage = isFinalStage;
		this.useReloadContent = useReloadContent;
	}
	
	public String getDomainName() {
		return domainName;
	}
	public String getExpressionFilter() {
		return expressionFilter;
	}
	public File getNortFile() {
		return nortFile;
	}
	public Date getCutoffDate() {
		return cutoffDate;
	}

	public void setDomainName(String domainName) {
		this.domainName = domainName;
	}

	public void setExpressionFilter(String expressionFilter) {
		this.expressionFilter = expressionFilter;
	}

	public void setNortFile(File nortFile) {
		this.nortFile = nortFile;
	}
	public ArrayList<ExcludeDocument> getExcludeDocuments() {
		return excludeDocuments;
	}
	
	public void setExcludeDocuments(ArrayList<ExcludeDocument> excludeDocuments) {
		this.excludeDocuments = excludeDocuments;
	}
	
	public ArrayList<RenameTocEntry> getRenameTocEntries() {
		return renameTocEntries;
	}

	public void setRenameTocEntries(ArrayList<RenameTocEntry> renameTocEntries) {
		this.renameTocEntries = renameTocEntries;
	}

	public boolean isFinalStage() {
		return isFinalStage;
	}

	public void setFinalStage(boolean isFinalStage) {
		this.isFinalStage = isFinalStage;
	}

	public boolean getUseReloadContent() {
		return useReloadContent;
	}

	public void setUseReloadContent(boolean useReloadContent) {
		this.useReloadContent = useReloadContent;
	}
	
	public void setCutoffDate(Date cutoffDate) {
		this.cutoffDate = cutoffDate;
	}
	
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
