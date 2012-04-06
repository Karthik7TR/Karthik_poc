/*
 * Copyright 2011: Thomson Reuters Global Resources. All Rights Reserved.
 * Proprietary and Confidential information of TRGR. Disclosure, Use or
 * Reproduction without the written authorization of TRGR is prohibited
 */
package com.thomsonreuters.uscl.ereader.core.book.domain;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
@Table(name = "KEYWORD_TYPE_VALUES")
public class KeywordTypeValue implements Serializable, Comparable<KeywordTypeValue> {
	// private static final Logger log =
	// Logger.getLogger(KeywordTypeValue.class);
	private static final long serialVersionUID = 8698248929292091625L;

	private Long id;
	private KeywordTypeCode keywordTypeCode;
	private String name;
	private Date lastUpdated;

	public KeywordTypeValue() {
		super();
	}

	@Id
	@Column(name = "KEYWORD_TYPE_VALUES_ID", unique = true, nullable = false)
	@SequenceGenerator(name = "keywordTypeValuesIdSequence", sequenceName = "KEYWORD_TYPE_VALUES_ID_SEQ")
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "keywordTypeValuesIdSequence")
	public Long getId() {
		return id;
	}

	@ManyToOne
	@JoinColumn(name = "KEYWORD_TYPE_CODES_ID")
	public KeywordTypeCode getKeywordTypeCode() {
		return keywordTypeCode;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "LAST_UPDATED", nullable = false)
	public Date getLastUpdated() {
		return lastUpdated;
	}

	@Column(name = "KEYWORD_TYPE_VALUES_NAME", nullable = false, length = 1024)
	public String getName() {
		return name;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public void setKeywordTypeCode(KeywordTypeCode keywordTypeCode) {
		this.keywordTypeCode = keywordTypeCode;
	}

	public void setLastUpdated(Date lastUpdated) {
		this.lastUpdated = lastUpdated;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		StringBuilder buffer = new StringBuilder();

		if(keywordTypeCode != null) {
			buffer.append("keywordTypeCode=[").append(keywordTypeCode.getName()).append("] ");
		} else {
			buffer.append("keywordTypeCode=[").append(keywordTypeCode).append("] ");
		}
		buffer.append("KeywordTypeValue=[").append(name).append("] ");

		return buffer.toString();
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		KeywordTypeValue other = (KeywordTypeValue) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		return result;
	}
	
	@Override
	public int compareTo(KeywordTypeValue arg0) {
		int result = 0;
		if (name != null) {
			result = (arg0 != null) ? name.compareTo(arg0.getName()) : 1;
		} else {  // str1 is null
			result = (arg0 != null) ? -1 : 0;
		}
		return result;
	}

}
