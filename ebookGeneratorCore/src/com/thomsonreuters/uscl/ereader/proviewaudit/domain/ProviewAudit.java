/*
 * Copyright 2016: Thomson Reuters Global Resources. All Rights Reserved.
 * Proprietary and Confidential information of TRGR. Disclosure, Use or
 * Reproduction without the written authorization of TRGR is prohibited
 */
package com.thomsonreuters.uscl.ereader.proviewaudit.domain;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;


@Entity
@Table(name = "PROVIEW_AUDIT")
public class ProviewAudit implements Serializable {
	//private static final Logger log = LogManager.getLogger(ProviewAudit.class);
	private static final long serialVersionUID = 1L;
	
	public static enum ProviewRequest {DELETE, REMOVE, PROMOTE};

	@Column(name = "PROVIEW_AUDIT_ID", nullable = false)
	@Id
	@GeneratedValue(generator = "ProviewAuditSequence")
	@SequenceGenerator(name="ProviewAuditSequence", sequenceName = "PROVIEW_AUDIT_ID_SEQ")	
	Long id;
	/**
	 */

	@Column(name = "TITLE_ID", length = 40, nullable = false)
	String titleId;
	/**
	 */
	
	@Column(name = "BOOK_VERSION", length = 10, nullable = false)
	String bookVersion;
	/**
	 */
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "BOOK_LAST_UPDATED", nullable = false)
	Date bookLastUpdated;
	/**
	 */
	
	@Column(name = "USER_NAME", length = 1024, nullable = false)
	String username;
	/**
	 */
	
	@Column(name = "PROVIEW_REQUEST", length = 128, nullable = false)
	String proviewRequest;
	/**
	 */
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "REQUEST_DATE", nullable = false)
	Date requestDate;
	/**
	 */
	
	@Column(name = "AUDIT_NOTE", length = 1024, nullable = false)
	String auditNote;
	/**
	 */
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getTitleId() {
		return titleId;
	}
	public void setTitleId(String titleId) {
		this.titleId = titleId;
	}
	public String getBookVersion() {
		return bookVersion;
	}
	public void setBookVersion(String bookVersion) {
		this.bookVersion = bookVersion;
	}
	public Date getBookLastUpdated() {
		return bookLastUpdated;
	}
	public void setBookLastUpdated(Date bookLastUpdated) {
		this.bookLastUpdated = bookLastUpdated;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getProviewRequest() {
		return proviewRequest;
	}
	public void setProviewRequest(String proviewRequest) {
		this.proviewRequest = proviewRequest;
	}
	public Date getRequestDate() {
		return requestDate;
	}
	public void setRequestDate(Date requestDate) {
		this.requestDate = requestDate;
	}
	public String getAuditNote() {
		return auditNote;
	}
	public void setAuditNote(String auditNote) {
		this.auditNote = auditNote;
	}
}
