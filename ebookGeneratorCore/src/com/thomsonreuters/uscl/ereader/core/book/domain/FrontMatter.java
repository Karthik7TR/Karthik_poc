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
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.apache.commons.lang.StringUtils;


/**
 */

@Entity
@NamedQueries({
		@NamedQuery(name = "findAllFrontMatters", query = "select myFrontMatter from FrontMatter myFrontMatter"),
		@NamedQuery(name = "findFrontMatterByPrimaryKey", query = "select myFrontMatter from FrontMatter myFrontMatter where myFrontMatter.frontMatterId = ?1") })
@Table(schema = "EBOOK", name = "FRONT_MATTER")
public class FrontMatter implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 */

	@Column(name = "FRONT_MATTER_ID", nullable = false)
	@Basic(fetch = FetchType.EAGER)
	@Id
	@GeneratedValue(strategy=GenerationType.SEQUENCE, generator = "FrontMatterSequence")
	@SequenceGenerator(name="FrontMatterSequence", sequenceName = "FRONT_MATTER_ID_SEQ")	
	Integer frontMatterId;
	/**
	 */

	@Column(name = "BOOK_NAME_TEXT", length = 1024)
	@Basic(fetch = FetchType.EAGER)
	String additionalFrontMatterText;
	/**
	 */

	@Column(name = "SEQUENCE_NUM")
	@Basic(fetch = FetchType.EAGER)
	Integer sequenceNum;

	/**
	 */
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumns({ @JoinColumn(name = "EBOOK_DEFINITION_ID", referencedColumnName = "EBOOK_DEFINITION_ID", nullable = false) })
	BookDefinition ebookDefinition;

	/**
	 */
	public void setFrontMatterId(Integer frontMatterId) {
		this.frontMatterId = frontMatterId;
	}

	/**
	 */
	public Integer getFrontMatterId() {
		return this.frontMatterId;
	}

	/**
	 */
	public void setAdditionalFrontMatterText(String bookNameText) {
		this.additionalFrontMatterText = bookNameText;
	}

	/**
	 */
	public String getAdditionalFrontMatterText() {
		return this.additionalFrontMatterText;
	}

	/**
	 */
	public void setSequenceNum(Integer sequenceNum) {
		this.sequenceNum = sequenceNum;
	}

	/**
	 */
	public Integer getSequenceNum() {
		return this.sequenceNum;
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
	public FrontMatter() {
	}

	/**
	 * Copies the contents of the specified bean into this bean.
	 *
	 */
	public void copy(FrontMatter that) {
		setFrontMatterId(that.getFrontMatterId());
		setAdditionalFrontMatterText(that.getAdditionalFrontMatterText());
		setSequenceNum(that.getSequenceNum());
		setEbookDefinition(that.getEbookDefinition());
	}

	/**
	 * Returns a textual representation of a bean.
	 *
	 */
	public String toString() {

		StringBuilder buffer = new StringBuilder();

		buffer.append("frontMatterId=[").append(frontMatterId).append("] ");
		buffer.append("bookNameText=[").append(additionalFrontMatterText).append("] ");
		buffer.append("sequenceNum=[").append(sequenceNum).append("] ");

		return buffer.toString();
	}

	
	/**
	 */
	public boolean isEmpty() {
		return StringUtils.isBlank(this.additionalFrontMatterText) & this.sequenceNum == null;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime
				* result
				+ ((additionalFrontMatterText == null) ? 0
						: additionalFrontMatterText.hashCode());
		result = prime * result
				+ ((ebookDefinition == null) ? 0 : ebookDefinition.hashCode());
		result = prime * result
				+ ((frontMatterId == null) ? 0 : frontMatterId.hashCode());
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
		FrontMatter other = (FrontMatter) obj;
		if (additionalFrontMatterText == null) {
			if (other.additionalFrontMatterText != null)
				return false;
		} else if (!additionalFrontMatterText
				.equals(other.additionalFrontMatterText))
			return false;
		if (ebookDefinition == null) {
			if (other.ebookDefinition != null)
				return false;
		} else if (!ebookDefinition.equals(other.ebookDefinition))
			return false;
		if (frontMatterId == null) {
			if (other.frontMatterId != null)
				return false;
		} else if (!frontMatterId.equals(other.frontMatterId))
			return false;
		if (sequenceNum == null) {
			if (other.sequenceNum != null)
				return false;
		} else if (!sequenceNum.equals(other.sequenceNum))
			return false;
		return true;
	}


}
