/*
* Copyright 2011: Thomson Reuters Global Resources. All Rights Reserved.
* Proprietary and Confidential information of TRGR. Disclosure, Use or
* Reproduction without the written authorization of TRGR is prohibited
*/

package com.thomsonreuters.uscl.ereader.gather.domain;

import java.util.List;


public class EBookToc {
	
	String name;
	String guid;
	String rootGuid;
	String parentGuid;
	String metadata;
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
		result = prime * result + ((guid == null) ? 0 : guid.hashCode());
		result = prime * result
				+ ((metadata == null) ? 0 : metadata.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result
				+ ((parentGuid == null) ? 0 : parentGuid.hashCode());
		result = prime * result
				+ ((rootGuid == null) ? 0 : rootGuid.hashCode());
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
		if (guid == null) {
			if (other.guid != null)
				return false;
		} else if (!guid.equals(other.guid))
			return false;
		if (metadata == null) {
			if (other.metadata != null)
				return false;
		} else if (!metadata.equals(other.metadata))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (parentGuid == null) {
			if (other.parentGuid != null)
				return false;
		} else if (!parentGuid.equals(other.parentGuid))
			return false;
		if (rootGuid == null) {
			if (other.rootGuid != null)
				return false;
		} else if (!rootGuid.equals(other.rootGuid))
			return false;
		return true;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getGuid() {
		return guid;
	}
	public void setGuid(String guid) {
		this.guid = guid;
	}
	public String getRootGuid() {
		return rootGuid;
	}
	public void setRootGuid(String rootGuid) {
		this.rootGuid = rootGuid;
	}
	public String getParentGuid() {
		return parentGuid;
	}
	public void setParentGuid(String parentGuid) {
		this.parentGuid = parentGuid;
	}
	public String getMetadata() {
		return metadata;
	}
	public void setMetadata(String metadata) {
		this.metadata = metadata;
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
		append(" ,guid:").append(guid).
		append(" ,rootGuid:").append(rootGuid).
		append(" ,parentGuid:").append(parentGuid).
		append(" ,metadata:").append(metadata).
		append(" ,docGuid:").append(docGuid).
		append(" ,childrenCount:").append(childrenCount).
		append(" ,children:").append(childrenList).
		toString();
	}

}
