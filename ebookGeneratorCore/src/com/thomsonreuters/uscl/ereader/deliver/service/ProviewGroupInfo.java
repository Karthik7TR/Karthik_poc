package com.thomsonreuters.uscl.ereader.deliver.service;

import java.io.Serializable;
import java.util.List;

public class ProviewGroupInfo implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4229230493652304110L;
	private String groupName;
	private String subGroupName;
	private String version;
	private String proviewName;
	private String status;
	private String titleId;
	private String groupIdentification;
	private List<String> splitTitles;
	private String id;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public List<String> getSplitTitles() {
		return splitTitles;
	}

	public void setSplitTitles(List<String> splitTitles) {
		this.splitTitles = splitTitles;
	}

	public String getGroupIdentification() {
		return groupIdentification;
	}

	public void setGroupIdentification(String groupIdentification) {
		this.groupIdentification = groupIdentification;
	}

	public String getProviewName() {
		return proviewName;
	}

	public void setProviewName(String proviewName) {
		this.proviewName = proviewName;
	}	
	
	public String getGroupName() {
		return groupName;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

	public String getSubGroupName() {
		return subGroupName;
	}

	public void setSubGroupName(String subGroupName) {
		this.subGroupName = subGroupName;
	}

	
	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
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
		result = prime * result + ((status == null) ? 0 : status.hashCode());
		result = prime * result + ((titleId == null) ? 0 : titleId.hashCode());
		result = prime * result + ((version == null) ? 0 : version.hashCode());
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
		ProviewGroupInfo other = (ProviewGroupInfo) obj;		
		if (status == null) {
			if (other.status != null)
				return false;
		} else if (!status.equals(other.status))
			return false;
		if (titleId == null) {
			if (other.titleId != null)
				return false;
		} else if (!titleId.equals(other.titleId))
			return false;		
		if (version == null) {
			if (other.version != null)
				return false;
		} else if (!version.equals(other.version))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "ProviewGroupInfo [version=" + version
				+ ", group=" + groupName + ", subGroupName=" + subGroupName
				+ ", status=" + status + ", title=" + titleId + "]";
	}

}

