package com.thomsonreuters.uscl.ereader.userpreference.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.apache.commons.lang.StringUtils;

@Entity
@Table(name = "USER_PREFERENCE")
public class UserPreference implements Serializable {
	private static final long serialVersionUID = 1L;

	@Column(name = "USER_NAME", length = 1024, nullable = false)
	@Id
	String userName;

	@Column(name = "EMAIL_LIST", length = 2048)
	String emails;

	@Column(name = "LIBRARY_PROVIEW_NAME_FILTER", length = 1024)
	String libraryProviewName;
	
	@Column(name = "LIBRARY_TITLE_ID_FILTER", length = 1024)
	String libraryTitleId;
	
	@Column(name = "AUDIT_PROVIEW_NAME_FILTER", length = 1024)
	String auditProviewName;
	
	@Column(name = "AUDIT_TITLE_ID_FILTER", length = 1024)
	String auditTitleId;
	
	@Column(name = "JOB_SUM_PROVIEW_NAME_FILTER", length = 1024)
	String jobSummaryProviewName;
	
	@Column(name = "JOB_SUM_TITLE_ID_FILTER", length = 1024)
	String jobSummaryTitleId;
	
	@Column(name = "START_PAGE", length = 64)
	String startPage;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "LAST_UPDATED", nullable=false)
	Date lastUpdated;

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getEmails() {
		return emails;
	}

	public void setEmails(String emails) {
		this.emails = emails;
	}

	public String getLibraryProviewName() {
		return libraryProviewName;
	}

	public void setLibraryProviewName(String libraryProviewName) {
		this.libraryProviewName = libraryProviewName;
	}

	public String getLibraryTitleId() {
		return libraryTitleId;
	}

	public void setLibraryTitleId(String libraryTitleId) {
		this.libraryTitleId = libraryTitleId;
	}

	public String getAuditProviewName() {
		return auditProviewName;
	}

	public void setAuditProviewName(String auditProviewName) {
		this.auditProviewName = auditProviewName;
	}

	public String getAuditTitleId() {
		return auditTitleId;
	}

	public void setAuditTitleId(String auditTitleId) {
		this.auditTitleId = auditTitleId;
	}

	public String getJobSummaryProviewName() {
		return jobSummaryProviewName;
	}

	public void setJobSummaryProviewName(String jobSummaryProviewName) {
		this.jobSummaryProviewName = jobSummaryProviewName;
	}

	public String getJobSummaryTitleId() {
		return jobSummaryTitleId;
	}

	public void setJobSummaryTitleId(String jobSummaryTitleId) {
		this.jobSummaryTitleId = jobSummaryTitleId;
	}

	public String getStartPage() {
		return startPage;
	}

	public void setStartPage(String startPage) {
		this.startPage = startPage;
	}
	
	public Date getLastUpdated() {
		return lastUpdated;
	}

	public void setLastUpdated(Date lastUpdated) {
		this.lastUpdated = lastUpdated;
	}

	@Transient
	public List<String> getEmailList() {
		String[] emailArray = StringUtils.split(emails, ",");

		if(emailArray != null) {
			return Arrays.asList(emailArray);
		} else {
			return new ArrayList<String>();
		}
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((userName == null) ? 0 : userName.hashCode());
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
		UserPreference other = (UserPreference) obj;
		if (userName == null) {
			if (other.userName != null)
				return false;
		} else if (!userName.equals(other.userName))
			return false;
		return true;
	}

}
