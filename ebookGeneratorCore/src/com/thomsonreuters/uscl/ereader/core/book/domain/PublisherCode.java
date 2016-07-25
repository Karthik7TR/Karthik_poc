/*
 * Copyright 2016: Thomson Reuters Global Resources. All Rights Reserved.
 * Proprietary and Confidential information of TRGR. Disclosure, Use or
 * Reproduction without the written authorization of TRGR is prohibited
 */

package com.thomsonreuters.uscl.ereader.core.book.domain;

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
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

/**
 * A publisher code database table entity.
 * Represents all the publisher code id and names used for Book Definition
 */
@Entity
@Table(name="PUBLISHER_CODES")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(namespace = "ebookGenerator/com/thomsonreuters/uscl/ereader/core/book/domain", name = "PublisherCode")
public class PublisherCode implements Serializable {
	//private static final Logger log = LogManager.getLogger(PublisherCode.class);
	private static final long serialVersionUID = -2270804278406061488L;

	@Id
	@Column(name="PUBLISHER_CODES_ID")
	@SequenceGenerator(name="publisherCodesIdSequence", sequenceName="PUBLISHER_CODES_ID_SEQ")
	@GeneratedValue(generator="publisherCodesIdSequence")
	private Long id;
	
	@Column(name="PUBLISHER_NAME", nullable = false, length = 1024)
	private String name;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="LAST_UPDATED", nullable = false)
	private Date lastUpdatedTimeStampForPubCode;
	
	public PublisherCode() {
		super();
	}
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Date getLastUpdated() {
		return lastUpdatedTimeStampForPubCode;
	}

	public void setLastUpdated(Date lastUpdated) {
		this.lastUpdatedTimeStampForPubCode = lastUpdated;
	}

	@Override
	public String toString() {
		return ReflectionToStringBuilder.toString(this, ToStringStyle.SHORT_PREFIX_STYLE);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result
				+ ((lastUpdatedTimeStampForPubCode == null) ? 0 : lastUpdatedTimeStampForPubCode.hashCode());
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
		PublisherCode other = (PublisherCode) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (lastUpdatedTimeStampForPubCode == null) {
			if (other.lastUpdatedTimeStampForPubCode != null)
				return false;
		} else if (!lastUpdatedTimeStampForPubCode.equals(other.lastUpdatedTimeStampForPubCode))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}
	
}
