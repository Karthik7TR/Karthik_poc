/*
 * Copyright 2011: Thomson Reuters Global Resources. All Rights Reserved.
 * Proprietary and Confidential information of TRGR. Disclosure, Use or
 * Reproduction without the written authorization of TRGR is prohibited
 */
package com.thomsonreuters.uscl.ereader.core.book.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

@Entity
@Table(name="KEYWORD_TYPE_CODES")
public class KeywordTypeCode implements Serializable, Comparable<KeywordTypeCode> {
	//private static final Logger log = Logger.getLogger(KeywordTypeCode.class);
	private static final long serialVersionUID = -6883749966331206015L;

	private Long id;
	private String name;
	private Date lastUpdated;
	private Collection<KeywordTypeValue> values;
	
	public KeywordTypeCode() {
		super();
		values = new ArrayList<KeywordTypeValue>();
	}
	
	
	@Id
	@Column(name="KEYWORD_TYPE_CODES_ID", unique = true, nullable = false)
	@SequenceGenerator(name="keywordTypeCodesIdSequence", sequenceName="KEYWORD_TYPE_CODES_ID_SEQ")
	@GeneratedValue(strategy=GenerationType.SEQUENCE, generator="keywordTypeCodesIdSequence")
	public Long getId() {
		return id;
	}


	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="LAST_UPDATED", nullable = false)
	public Date getLastUpdated() {
		return lastUpdated;
	}



	@Column(name="KEYWORD_TYPE_CODES_NAME", nullable = false, length = 1024)
	public String getName() {
		return name;
	}


	@OneToMany(mappedBy="keywordTypeCode", fetch=FetchType.EAGER)
	@Fetch(FetchMode.SELECT)
	@Cascade({CascadeType.ALL})
	public Collection<KeywordTypeValue> getValues() {
		return values;
	}


	public void setId(Long id) {
		this.id = id;
	}


	public void setLastUpdated(Date lastUpdated) {
		this.lastUpdated = lastUpdated;
	}


	public void setName(String name) {
		this.name = name;
	}


	public void setValues(Collection<KeywordTypeValue> values) {
		this.values = values;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		KeywordTypeCode other = (KeywordTypeCode) obj;
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
		result = prime * result
				+ ((lastUpdated == null) ? 0 : lastUpdated.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		return result;
	}

	@Override
	public String toString() {
		return ReflectionToStringBuilder.toString(this, ToStringStyle.SHORT_PREFIX_STYLE);
	}


	@Override
	public int compareTo(KeywordTypeCode arg0) {
		int result = 0;
		if (name != null) {
			result = (arg0 != null) ? name.compareTo(arg0.getName()) : 1;
		} else {  // str1 is null
			result = (arg0 != null) ? -1 : 0;
		}
		return result;
	}
	
}
