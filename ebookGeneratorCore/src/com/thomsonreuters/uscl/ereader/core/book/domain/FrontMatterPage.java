package com.thomsonreuters.uscl.ereader.core.book.domain;

import java.io.Serializable;
import java.util.ArrayList;
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

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.springframework.util.AutoPopulatingList;

/**
 */
@Entity
@Table(name = "FRONT_MATTER_PAGE")
public class FrontMatterPage implements Serializable, Comparable<FrontMatterPage> {
	private static final long serialVersionUID = 6894572296330551335L;
	/**
	 */
	
	@Column(name = "FRONT_MATTER_PAGE_ID", nullable = false)
	@Basic(fetch = FetchType.EAGER)
	@Id
	@GeneratedValue(generator = "FrontMatterPageSequence")
	@SequenceGenerator(name="FrontMatterPageSequence", sequenceName = "FRONT_MATTER_PAGE_ID_SEQ")
	Long id;
	/**
	 */
	
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumns({ @JoinColumn(name = "EBOOK_DEFINITION_ID", referencedColumnName = "EBOOK_DEFINITION_ID", nullable = false) })
	BookDefinition ebookDefinition;
	/**
	 */
	
	@Column(name = "PAGE_TOC_LABEL", nullable = false, length = 1024)
	@Basic(fetch = FetchType.EAGER)
	String pageTocLabel;
	/**
	 */

	@Column(name = "PAGE_HEADING_LABEL", length = 1024)
	@Basic(fetch = FetchType.EAGER)
	String pageHeadingLabel;
	/**
	 */
	
	@Column(name = "SEQUENCE_NUM", nullable = false)
	@Basic(fetch = FetchType.EAGER)
	Integer sequenceNum;
	
	/**
	 */
	@OneToMany(mappedBy = "frontMatterPage", fetch = FetchType.EAGER, orphanRemoval = true)
	@Cascade({CascadeType.ALL})
	@Fetch(FetchMode.SELECT)
	Collection<FrontMatterSection> frontMatterSections;
	
	public FrontMatterPage() {
		super();
		frontMatterSections = new AutoPopulatingList<FrontMatterSection>(FrontMatterSection.class);
	}
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public BookDefinition getEbookDefinition() {
		return ebookDefinition;
	}
	public void setEbookDefinition(BookDefinition ebookDefinition) {
		this.ebookDefinition = ebookDefinition;
	}
	public String getPageTocLabel() {
		return pageTocLabel;
	}
	public void setPageTocLabel(String pageTocLabel) {
		this.pageTocLabel = pageTocLabel;
	}
	public String getPageHeadingLabel() {
		return pageHeadingLabel;
	}
	public void setPageHeadingLabel(String pageHeadingLabel) {
		this.pageHeadingLabel = pageHeadingLabel;
	}
	public Integer getSequenceNum() {
		return sequenceNum;
	}
	public void setSequenceNum(Integer sequenceNum) {
		this.sequenceNum = sequenceNum;
	}
	public Collection<FrontMatterSection> getFrontMatterSections() {
		if(this.frontMatterSections == null){
			this.frontMatterSections = new ArrayList<FrontMatterSection>();
		}
		
		return frontMatterSections;
	}
	public void setFrontMatterSections(
			Collection<FrontMatterSection> frontMatterSections) {
		this.frontMatterSections = frontMatterSections;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((ebookDefinition == null) ? 0 : ebookDefinition.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime
				* result
				+ ((pageHeadingLabel == null) ? 0 : pageHeadingLabel.hashCode());
		result = prime * result
				+ ((pageTocLabel == null) ? 0 : pageTocLabel.hashCode());
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
		FrontMatterPage other = (FrontMatterPage) obj;
		if (ebookDefinition == null) {
			if (other.ebookDefinition != null)
				return false;
		} else if (!ebookDefinition.equals(other.ebookDefinition))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (pageHeadingLabel == null) {
			if (other.pageHeadingLabel != null)
				return false;
		} else if (!pageHeadingLabel.equals(other.pageHeadingLabel))
			return false;
		if (pageTocLabel == null) {
			if (other.pageTocLabel != null)
				return false;
		} else if (!pageTocLabel.equals(other.pageTocLabel))
			return false;
		if (sequenceNum == null) {
			if (other.sequenceNum != null)
				return false;
		} else if (!sequenceNum.equals(other.sequenceNum))
			return false;
		return true;
	}
	
	@Override
	public String toString() {
		StringBuilder buffer = new StringBuilder();
		buffer.append("FrontMatterPage [pageTocLabel=").append(pageTocLabel).append(", ");
		buffer.append("pageHeadingLabel=").append(pageHeadingLabel).append(", ");
		buffer.append("sequenceNum=").append(sequenceNum).append(", ");
		buffer.append("frontMatterSections=");
		for(FrontMatterSection section : frontMatterSections) {
			buffer.append(section.toString());
		}
		buffer.append("]");
		
		return buffer.toString();
	}

	/**
	 * For sorting the name components into sequence order (1...n).
	 */
	@Override
	public int compareTo(FrontMatterPage o) {
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
