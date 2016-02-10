package com.thomsonreuters.uscl.ereader.deliver.service;

import java.io.Serializable;

public class ProviewGroup  implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4229230493652304110L;
	private String proviewName;
	private String titleId;
	private String groupName;
	private String bookVersion;
	private Integer groupVersion;
	private String groupId;
	private String bookDefId;

	public String getBookDefId() {
		return bookDefId;
	}

	public void setBookDefId(String bookDefId) {
		this.bookDefId = bookDefId;
	}

	public String getGroupId() {
		return groupId;
	}

	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}

	public Integer getGroupVersion() {
		return groupVersion;
	}

	public void setGroupVersion(Integer groupVersion) {
		this.groupVersion = groupVersion;
	}

	public String getGroupName() {
		return groupName;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}	

	public String getProviewName() {
		return proviewName;
	}

	public void setProviewName(String proviewName) {
		this.proviewName = proviewName;
	}
	
	public String getBookVersion() {
		return bookVersion;
	}

	public void setBookVersion(String bookVersion) {
		this.bookVersion = bookVersion;
	}

	public String getTitleId() {
		return titleId;
	}

	public void setTitleId(String titleId) {
		this.titleId = titleId;
	}


	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((groupName == null) ? 0 : groupName.hashCode());
		result = prime * result + ((proviewName == null) ? 0 : proviewName.hashCode());
		result = prime * result + ((titleId == null) ? 0 : titleId.hashCode());
		result = prime * result + ((bookVersion == null) ? 0 : bookVersion.hashCode());
		result = prime * result + ((groupVersion == null) ? 0 : groupVersion.hashCode());
		result = prime * result + ((groupId == null) ? 0 : groupId.hashCode());
		result = prime * result + ((bookDefId == null) ? 0 : bookDefId.hashCode());
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
		ProviewGroup other = (ProviewGroup) obj;
		if (groupName == null) {
			if (other.groupName != null)
				return false;
		} else if (!groupName.equals(other.groupName))
			return false;
		if (proviewName == null) {
			if (other.proviewName != null)
				return false;
		} else if (!proviewName.equals(proviewName))
			return false;
		if (titleId == null) {
			if (other.titleId != null)
				return false;
		} else if (!titleId.equals(other.titleId))
			return false;
		if (bookVersion == null) {
			if (other.bookVersion != null)
				return false;
		} else if (!bookVersion.equals(other.bookVersion))
			return false;
		if (groupVersion == null) {
			if (other.groupVersion != null)
				return false;
		} else if (!groupVersion.equals(other.groupVersion))
			return false;
		if (groupId == null) {
			if (other.groupId != null)
				return false;
		} else if (!groupId.equals(other.groupId))
			return false;
		if (bookDefId == null) {
			if (other.bookDefId != null)
				return false;
		} else if (!bookDefId.equals(other.groupId))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "ProviewGroup [titleId=" + titleId + ", bookVersion=" + bookVersion + ", bookDefId=" + bookDefId
				+ ", groupName=" + groupName + ", groupVersion=" + groupVersion + ",proviewTitle=" + proviewName + "]";
	}

}

