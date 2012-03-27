package com.thomsonreuters.uscl.ereader.core.book.domain;

import java.io.Serializable;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.hibernate.annotations.OnDelete;


/**
 */
@Entity
@Table(name = "FRONT_MATTER_PDF")
public class FrontMatterPdf implements Serializable {
	private static final long serialVersionUID = -8713934748505263533L;
	/**
	 */
	@Column(name = "FRONT_MATTER_PDF_ID", nullable = false)
	@Basic(fetch = FetchType.EAGER)
	@Id
	@GeneratedValue(strategy=GenerationType.SEQUENCE, generator = "FrontMatterPdfSequence")
	@SequenceGenerator(name="FrontMatterPdfSequence", sequenceName = "FRONT_MATTER_PDF_ID_SEQ")
	Long id;
	/**
	 */
	
	@OneToOne(fetch = FetchType.EAGER)
	@JoinColumns({ @JoinColumn(name = "FRONT_MATTER_SECTION_ID", referencedColumnName = "FRONT_MATTER_SECTION_ID", nullable = false) })
	@OnDelete(action = org.hibernate.annotations.OnDeleteAction.CASCADE)
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
}