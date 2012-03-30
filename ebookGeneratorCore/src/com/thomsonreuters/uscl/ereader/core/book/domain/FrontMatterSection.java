package com.thomsonreuters.uscl.ereader.core.book.domain;

import java.io.Serializable;
import java.util.Collection;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.springframework.util.AutoPopulatingList;

/**
 */
@Entity
@Table(name = "FRONT_MATTER_SECTION")
public class FrontMatterSection implements Serializable {
	private static final long serialVersionUID = -7248785950042234491L;
	/**
	 */
	@Column(name = "FRONT_MATTER_SECTION_ID", nullable = false)
	@Basic(fetch = FetchType.EAGER)
	@Id
	@GeneratedValue(strategy=GenerationType.SEQUENCE, generator = "FrontMatterSectionSequence")
	@SequenceGenerator(name="FrontMatterSectionSequence", sequenceName = "FRONT_MATTER_SECTION_ID_SEQ")
	Long id;
	/**
	 */
	
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumns({ @JoinColumn(name = "FRONT_MATTER_PAGE_ID", referencedColumnName = "FRONT_MATTER_PAGE_ID", nullable = false) })
	FrontMatterPage frontMatterPage;
	/**
	 */
	
	@Column(name = "SECTION_HEADING", length = 1024)
	@Basic(fetch = FetchType.EAGER)
	String sectionHeading;
	/**
	 */

	@Column(name = "SECTION_TEXT", nullable = false)
	@Basic(fetch = FetchType.EAGER)
	String sectionText;
	/**
	 */
	
	@Column(name = "SEQUENCE_NUM", nullable = false)
	@Basic(fetch = FetchType.EAGER)
	Integer sequenceNum;
	
	@OneToMany(mappedBy = "section", fetch = FetchType.EAGER, orphanRemoval = true)
	@Cascade({CascadeType.ALL})
	@Fetch(FetchMode.SELECT)
	Collection<FrontMatterPdf> pdf;
	/**
	 */
	
	public FrontMatterSection() {
		pdf = new AutoPopulatingList<FrontMatterPdf>(FrontMatterPdf.class);
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public FrontMatterPage getFrontMatterPage() {
		return frontMatterPage;
	}

	public void setFrontMatterPage(FrontMatterPage frontMatterPage) {
		this.frontMatterPage = frontMatterPage;
	}

	public String getSectionHeading() {
		return sectionHeading;
	}

	public void setSectionHeading(String sectionHeading) {
		this.sectionHeading = sectionHeading;
	}

	public String getSectionText() {
		return sectionText;
	}

	public void setSectionText(String sectionText) {
		this.sectionText = sectionText;
	}

	public Integer getSequenceNum() {
		return sequenceNum;
	}

	public void setSequenceNum(Integer sequenceNum) {
		this.sequenceNum = sequenceNum;
	}

	public Collection<FrontMatterPdf> getPdf() {
		return pdf;
	}

	public void setPdf(Collection<FrontMatterPdf> pdf) {
		this.pdf = pdf;
	}
	
	@Transient
	public int getPdfSize() {
		return pdf.size();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result
				+ ((sectionHeading == null) ? 0 : sectionHeading.hashCode());
		result = prime * result
				+ ((sectionText == null) ? 0 : sectionText.hashCode());
		result = prime * result
				+ ((sequenceNum == null) ? 0 : sequenceNum.hashCode());
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
		FrontMatterSection other = (FrontMatterSection) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (sectionHeading == null) {
			if (other.sectionHeading != null)
				return false;
		} else if (!sectionHeading.equals(other.sectionHeading))
			return false;
		if (sectionText == null) {
			if (other.sectionText != null)
				return false;
		} else if (!sectionText.equals(other.sectionText))
			return false;
		if (sequenceNum == null) {
			if (other.sequenceNum != null)
				return false;
		} else if (!sequenceNum.equals(other.sequenceNum))
			return false;
		return true;
	}

}
