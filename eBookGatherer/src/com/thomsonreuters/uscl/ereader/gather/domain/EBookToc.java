/*
* Copyright 2011: Thomson Reuters Global Resources. All Rights Reserved.
* Proprietary and Confidential information of TRGR. Disclosure, Use or
* Reproduction without the written authorization of TRGR is prohibited
*/

package com.thomsonreuters.uscl.ereader.gather.domain;

import java.util.List;


public class EBookToc {
	
	String name;
	String docGuid;
	List<EBookToc> childrenList;
	int childrenCount;
	
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((childrenList == null) ? 0 : childrenList.hashCode());
		result = prime * result + childrenCount;
		result = prime * result + ((docGuid == null) ? 0 : docGuid.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());

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
		EBookToc other = (EBookToc) obj;
		if (childrenList == null) {
			if (other.childrenList != null)
				return false;
		} else if (!childrenList.equals(other.childrenList))
			return false;
		if (childrenCount != other.childrenCount)
			return false;
		if (docGuid == null) {
			if (other.docGuid != null)
				return false;
		} else if (!docGuid.equals(other.docGuid))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		
		return true;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getDocGuid() {
		return docGuid;
	}
	public void setDocGuid(String docGuid) {
		this.docGuid = docGuid;
	}
	public List<EBookToc> getChildren() {
		return childrenList;
	}
	public void setChildren(List<EBookToc> children) {
		this.childrenList = children;
	}
	public int getChildrenCount() {
		return childrenCount;
	}
	public void setChildrenCount(int childrenCount) {
		this.childrenCount = childrenCount;
	}

	public String toString()
	{
		return new StringBuilder().
		append("  Name:").append(name).
		append(" ,docGuid:").append(docGuid).
		append(" ,childrenCount:").append(childrenCount).
		append(" ,children:").append(childrenList).
		toString();
	}

}
