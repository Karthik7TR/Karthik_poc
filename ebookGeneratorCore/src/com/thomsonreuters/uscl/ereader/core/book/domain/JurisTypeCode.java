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
 * A Jurisdiction Type Code database table entity.
 * Represents all the Jurisdiction Type Code id and names used for Book Definition
 */
@Entity
@Table(name="JURIS_TYPE_CODES")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(namespace = "ebookGenerator/com/thomsonreuters/uscl/ereader/core/book/domain", name = "JurisTypeCode")
public class JurisTypeCode implements Serializable {
	//private static final Logger log = LogManager.getLogger(JurisTypeCode.class);
	private static final long serialVersionUID = -3118292918077264082L;

	@Id
	@Column(name="JURIS_TYPE_CODES_ID")
	@SequenceGenerator(name="jurisTypeCodesIdSequence", sequenceName="JURIS_TYPE_CODES_ID_SEQ")
	@GeneratedValue(generator="jurisTypeCodesIdSequence")
	private Long id;
	
	@Column(name="JURIS_TYPE_CODES_NAME", nullable = false, length = 1024)
	private String name;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="LAST_UPDATED", nullable = false)
	private Date lastUpdatedTimeStampForJurisCode;
	
	public JurisTypeCode() {
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
		return lastUpdatedTimeStampForJurisCode;
	}

	public void setLastUpdated(Date lastUpdated) {
		this.lastUpdatedTimeStampForJurisCode = lastUpdated;
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
				+ ((lastUpdatedTimeStampForJurisCode == null) ? 0 : lastUpdatedTimeStampForJurisCode.hashCode());
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
		JurisTypeCode other = (JurisTypeCode) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (lastUpdatedTimeStampForJurisCode == null) {
			if (other.lastUpdatedTimeStampForJurisCode != null)
				return false;
		} else if (!lastUpdatedTimeStampForJurisCode.equals(other.lastUpdatedTimeStampForJurisCode))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}
}
