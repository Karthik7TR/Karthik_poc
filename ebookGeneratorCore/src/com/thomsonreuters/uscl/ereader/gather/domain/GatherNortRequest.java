/*
 * Copyright 2011: Thomson Reuters Global Resources. All Rights Reserved.
 * Proprietary and Confidential information of TRGR. Disclosure, Use or
 * Reproduction without the written authorization of TRGR is prohibited
 */

package com.thomsonreuters.uscl.ereader.gather.domain;

import java.io.File;

import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

public class GatherNortRequest {

	private String domainName;
	private String expressionFilter;
	private File nortFile;

	public GatherNortRequest(){
		super();
	}
	
	public GatherNortRequest(String domainName, String expressionFilter, File nortFile) {
		super();
		this.domainName = domainName;
		this.expressionFilter = expressionFilter;
		this.nortFile = nortFile;
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

	public void setDomainName(String domainName) {
		this.domainName = domainName;
	}

	public void setExpressionFilter(String expressionFilter) {
		this.expressionFilter = expressionFilter;
	}
	public void setNortFile(File nortFile) {
		this.nortFile = nortFile;
	}

	public String toString() {
		return ReflectionToStringBuilder.toString(this,ToStringStyle.SHORT_PREFIX_STYLE);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((domainName == null) ? 0 : domainName.hashCode());
		result = prime * result + ((expressionFilter == null) ? 0 : expressionFilter.hashCode());
		result = prime * result + ((nortFile == null) ? 0 : nortFile.hashCode());
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
		if (domainName == null) {
			if (other.domainName != null)
				return false;
		} else if (!domainName.equals(other.domainName))
			return false;
		if (expressionFilter == null) {
			if (other.expressionFilter != null)
				return false;
		} else if (!expressionFilter.equals(other.expressionFilter))
			return false;
		if (nortFile == null) {
			if (other.nortFile != null)
				return false;
		} else if (!nortFile.equals(other.nortFile))
			return false;
		return true;
	}
}
