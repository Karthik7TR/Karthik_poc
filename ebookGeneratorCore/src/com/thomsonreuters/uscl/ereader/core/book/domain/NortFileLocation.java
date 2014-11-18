/*
 * Copyright 2014: Thomson Reuters Global Resources. All Rights Reserved.
 * Proprietary and Confidential information of TRGR. Disclosure, Use or
 * Reproduction without the written authorization of TRGR is prohibited
 */
package com.thomsonreuters.uscl.ereader.core.book.domain;

import java.io.Serializable;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

import org.apache.commons.lang.StringUtils;


/**
 */
@Entity
@Table(name = "NORT_FILE_LOCATION")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(namespace = "ebookGenerator/com/thomsonreuters/uscl/ereader/core/book/domain", name = "NortFileLocation")
public class NortFileLocation implements Serializable, Comparable<NortFileLocation> {
	private static final long serialVersionUID = 249868448548819700L;

	@Column(name = "NORT_FILE_LOCATION_ID", nullable = false)
	@Basic(fetch = FetchType.EAGER)
	@Id
	@GeneratedValue(generator = "NortFileLocationSequence")
	@SequenceGenerator(name="NortFileLocationSequence", sequenceName = "NORT_FILE_LOCATION_ID_SEQ")
	Long nortFileLocationId;
	/**
	 */

	@Column(name = "LOCATION_NAME", length = 1024)
	@Basic(fetch = FetchType.EAGER)
	String locationName;
	/**
	 */

	
	@Column(name = "SEQUENCE_NUMBER")
	@Basic(fetch = FetchType.EAGER)
	Integer sequenceNum;
	/**
	 */
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns({ @JoinColumn(name = "EBOOK_DEFINITION_ID", referencedColumnName = "EBOOK_DEFINITION_ID", nullable = false) })
	BookDefinition ebookDefinition;


	public Long getNortFileLocationId() {
		return nortFileLocationId;
	}

	public void setNortFileLocationId(Long nortFileLocationId) {
		this.nortFileLocationId = nortFileLocationId;
	}

	public String getLocationName() {
		return locationName;
	}

	public void setLocationName(String locationName) {
		this.locationName = locationName;
	}

	public Integer getSequenceNum() {
		return sequenceNum;
	}

	public void setSequenceNum(Integer sequenceNum) {
		this.sequenceNum = sequenceNum;
	}
	
	/**
	 */
	public void setEbookDefinition(BookDefinition ebookDefinition) {
		this.ebookDefinition = ebookDefinition;
	}

	/**
	 */
	public BookDefinition getEbookDefinition() {
		return ebookDefinition;
	}

	/**
	 */
	public NortFileLocation() {
	}

	/**
	 * Copies the contents of the specified bean into this bean.
	 *
	 */
	public void copy(NortFileLocation that) {
		setNortFileLocationId(that.getNortFileLocationId());
		setLocationName(that.getLocationName());
		setSequenceNum(that.getSequenceNum());
		setEbookDefinition(that.getEbookDefinition());
	}
	
	@Transient
	public boolean isEmpty() {
		return StringUtils.isBlank(this.locationName);
	}
	

	@Override
	public String toString() {
		StringBuilder buffer = new StringBuilder();
		buffer.append("nortFileLocationId=[").append(nortFileLocationId).append("] ");
		buffer.append("locationName=[").append(locationName).append("] ");
		buffer.append("sequenceNum=[").append(sequenceNum).append("] ");
		
		return buffer.toString();
	}

	
	/**
	 * For sorting the name components into sequence order (1...n).
	 */
	@Override
	public int compareTo(NortFileLocation o) {
		int result = 0;
		if (sequenceNum != null) {
			if(o != null) {
				Integer i = o.getSequenceNum();
				result = (i != null) ? sequenceNum.compareTo(i) : 1;
			} else {
				result = 1;
			}
		} else {  // int1 is null
			result = (o != null) ? -1 : 0;
		}
		return result;
	}
}
