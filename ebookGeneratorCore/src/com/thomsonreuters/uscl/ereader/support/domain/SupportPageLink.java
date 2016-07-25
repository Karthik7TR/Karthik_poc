/*
 * Copyright 2016: Thomson Reuters Global Resources. All Rights Reserved.
 * Proprietary and Confidential information of TRGR. Disclosure, Use or
 * Reproduction without the written authorization of TRGR is prohibited
 */
package com.thomsonreuters.uscl.ereader.support.domain;

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

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

@Entity
@Table(name = "SUPPORT_PAGE_LINK")
public class SupportPageLink implements Serializable {
	//private static Logger log = LogManager.getLogger(SupportPageLink.class);
	private static final long serialVersionUID = 1L;

	@Column(name = "SUPPORT_LINK_ID", nullable = false)
	@Id
	@GeneratedValue(generator = "SupportPageLinkSequence")
	@SequenceGenerator(name="SupportPageLinkSequence", sequenceName = "SUPPORT_LINK_ID_SEQ")	
	Long id;

	@Column(name = "LINK_DESCRIPTION", length = 512, nullable=false)
	String linkDescription;

	@Column(name = "LINK_ADDRESS", length = 1024, nullable=false)
	String linkAddress;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "LAST_UPDATED", nullable=false)
	Date lastUpdated;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getLinkDescription() {
		return linkDescription;
	}

	public void setLinkDescription(String linkDescription) {
		this.linkDescription = linkDescription;
	}

	public String getLinkAddress() {
		return linkAddress;
	}

	public void setLinkAddress(String linkAddress) {
		this.linkAddress = linkAddress;
	}

	public Date getLastUpdated() {
		return lastUpdated;
	}

	public void setLastUpdated(Date lastUpdated) {
		this.lastUpdated = lastUpdated;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result
				+ ((lastUpdated == null) ? 0 : lastUpdated.hashCode());
		result = prime * result
				+ ((linkAddress == null) ? 0 : linkAddress.hashCode());
		result = prime * result
				+ ((linkDescription == null) ? 0 : linkDescription.hashCode());
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
		SupportPageLink other = (SupportPageLink) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (lastUpdated == null) {
			if (other.lastUpdated != null)
				return false;
		} else if (!lastUpdated.equals(other.lastUpdated))
			return false;
		if (linkAddress == null) {
			if (other.linkAddress != null)
				return false;
		} else if (!linkAddress.equals(other.linkAddress))
			return false;
		if (linkDescription == null) {
			if (other.linkDescription != null)
				return false;
		} else if (!linkDescription.equals(other.linkDescription))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this, ToStringStyle.SIMPLE_STYLE);
	}
	
}
