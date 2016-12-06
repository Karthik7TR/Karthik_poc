/*
 * Copyright 2016: Thomson Reuters Global Resources. All Rights Reserved.
 * Proprietary and Confidential information of TRGR. Disclosure, Use or
 * Reproduction without the written authorization of TRGR is prohibited
 */

package com.thomsonreuters.uscl.ereader.deliver.service;

import java.io.Serializable;

import org.apache.commons.lang.StringUtils;

/**
 * Placeholder for proview title info
 * 
 * @author U0057241
 * 
 */
public class ProviewTitleInfo implements TitleInfo, Serializable, Comparable<ProviewTitleInfo> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4229230493652304110L;
	private String titleId;
	private String version;

	private String publisher;
	private String lastupdate;
	private String status;
	private String title;
	private Integer totalNumberOfVersions;

	public Integer getTotalNumberOfVersions() {
		return totalNumberOfVersions;
	}

	public void setTotalNumberOfVersions(Integer totalNumberOfVersions) {
		this.totalNumberOfVersions = totalNumberOfVersions;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}
	
	public Integer getMajorVersion() {
		Integer majorVersion = null;
		String version = StringUtils.substringAfter(this.version, "v");
		String number = StringUtils.substringBefore(version, ".");
		try {
			if(StringUtils.isNotBlank(number)) {
				majorVersion = Integer.valueOf(number);
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
		return majorVersion;
	}
	
	public Integer getMinorVersion() {
		Integer minorVersion = null;
		String number = StringUtils.substringAfter(this.version, ".");
		try {
			if(StringUtils.isNotBlank(number)) {
				minorVersion = Integer.valueOf(number);
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
		return minorVersion;
	}

	public String getTitleId() {
		return titleId;
	}

	public void setTitleId(String titleId) {
		this.titleId = titleId;
	}

	public String getPublisher() {
		return publisher;
	}

	public void setPublisher(String publisher) {
		this.publisher = publisher;
	}

	public String getLastupdate() {
		return lastupdate;
	}

	public void setLastupdate(String lastupdate) {
		this.lastupdate = lastupdate;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((lastupdate == null) ? 0 : lastupdate.hashCode());
		result = prime * result
				+ ((publisher == null) ? 0 : publisher.hashCode());
		result = prime * result + ((status == null) ? 0 : status.hashCode());
		result = prime * result + ((title == null) ? 0 : title.hashCode());
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
		ProviewTitleInfo other = (ProviewTitleInfo) obj;
		if (lastupdate == null) {
			if (other.lastupdate != null)
				return false;
		} else if (!lastupdate.equals(other.lastupdate))
			return false;
		if (publisher == null) {
			if (other.publisher != null)
				return false;
		} else if (!publisher.equals(other.publisher))
			return false;
		if (status == null) {
			if (other.status != null)
				return false;
		} else if (!status.equals(other.status))
			return false;
		if (title == null) {
			if (other.title != null)
				return false;
		} else if (!title.equals(other.title))
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
		return "ProviewTitleInfo [titleId=" + titleId + ", vesrion=" + version
				+ ", publisher=" + publisher + ", lastupdate=" + lastupdate
				+ ", status=" + status + ", title=" + title + "]";
	}

	@Override
	public int compareTo(ProviewTitleInfo info) {
		int version  = info.getMajorVersion().compareTo(this.getMajorVersion());
		
		if(version == 0) {
			return this.getTitleId().compareToIgnoreCase(info.getTitleId());
		} 
		return version;
	}

}
