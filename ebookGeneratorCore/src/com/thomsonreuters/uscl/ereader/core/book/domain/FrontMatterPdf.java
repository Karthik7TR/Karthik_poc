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

import org.apache.commons.lang.StringUtils;


/**
 */
@Entity
@Table(name = "FRONT_MATTER_PDF")
public class FrontMatterPdf implements Serializable, Comparable<FrontMatterPdf> {
	private static final long serialVersionUID = -8713934748505263533L;
	/**
	 */
	@Column(name = "FRONT_MATTER_PDF_ID", nullable = false)
	@Basic(fetch = FetchType.EAGER)
	@Id
	@GeneratedValue(generator = "FrontMatterPdfSequence")
	@SequenceGenerator(name="FrontMatterPdfSequence", sequenceName = "FRONT_MATTER_PDF_ID_SEQ")
	Long id;
	/**
	 */
	
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumns({ @JoinColumn(name = "FRONT_MATTER_SECTION_ID", referencedColumnName = "FRONT_MATTER_SECTION_ID", nullable = false) })
	FrontMatterSection section;
	/**
	 */

	@Column(name = "PDF_LINK_TEXT", length = 1024)
	@Basic(fetch = FetchType.EAGER)
	String pdfLinkText;
	/**
	 */

	@Column(name = "PDF_FILENAME", length = 1024)
	@Basic(fetch = FetchType.EAGER)
	String pdfFilename;
	
	@Column(name = "SEQUENCE_NUMBER", nullable = false)
	@Basic(fetch = FetchType.EAGER)
	Integer sequenceNum;
	

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public FrontMatterSection getSection() {
		return section;
	}

	public void setSection(FrontMatterSection section) {
		this.section = section;
	}

	public String getPdfLinkText() {
		return pdfLinkText;
	}

	public void setPdfLinkText(String pdfLinkText) {
		this.pdfLinkText = pdfLinkText;
	}

	public String getPdfFilename() {
		return pdfFilename;
	}

	public void setPdfFilename(String pdfFilename) {
		this.pdfFilename = pdfFilename;
	}
	
	public Integer getSequenceNum() {
		return sequenceNum;
	}

	public void setSequenceNum(Integer sequenceNum) {
		this.sequenceNum = sequenceNum;
	}

	@Transient
	public boolean isEmpty() {
		return (StringUtils.isBlank(pdfFilename) && StringUtils.isBlank(pdfLinkText) && (sequenceNum == null));
	}

	@Override
	public String toString() {
		StringBuilder buffer = new StringBuilder();
		buffer.append("FrontMatterPdf [pdfLinkText=").append(pdfLinkText).append(", ");
		buffer.append("pdfFilename=").append(pdfFilename).append(", ");
		buffer.append("]");
		
		return buffer.toString();
	}
	
	/**
	 * For sorting the name components into sequence order (1...n).
	 */
	@Override
	public int compareTo(FrontMatterPdf o) {
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