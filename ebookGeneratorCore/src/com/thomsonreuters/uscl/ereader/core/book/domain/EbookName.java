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
		@NamedQuery(name = "findAllEbookNames", query = "select myEbookName from EbookName myEbookName"),
		@NamedQuery(name = "findEbookNameByPrimaryKey", query = "select myEbookName from EbookName myEbookName where myEbookName.ebookNameId = ?1") })
@Table(name = "EBOOK_NAME")

public class EbookName implements Serializable, Comparable<EbookName> {
	private static final long serialVersionUID = 1L;

	/**
	 */

	@Column(name = "EBOOK_NAME_ID", nullable = false)
	@Basic(fetch = FetchType.EAGER)
	@Id
	@GeneratedValue(strategy=GenerationType.SEQUENCE, generator = "BookNameSequence")
	@SequenceGenerator(name="BookNameSequence", sequenceName = "EBOOK_NAME_ID_SEQ")	
	Integer ebookNameId;
	/**
	 */

	@Column(name = "BOOK_NAME_TEXT", length = 1024)
	@Basic(fetch = FetchType.EAGER)
	String bookNameText;
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
	public void setEbookNameId(Integer ebookNameId) {
		this.ebookNameId = ebookNameId;
	}

	/**
	 */
	public Integer getEbookNameId() {
		return this.ebookNameId;
	}

	/**
	 */
	public void setBookNameText(String bookNameText) {
		this.bookNameText = bookNameText;
	}

	/**
	 */
	public String getBookNameText() {
		return this.bookNameText;
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
	public EbookName() {
	}

	/**
	 * Copies the contents of the specified bean into this bean.
	 *
	 */
	public void copy(EbookName that) {
		setEbookNameId(that.getEbookNameId());
		setBookNameText(that.getBookNameText());
		setSequenceNum(that.getSequenceNum());
		setEbookDefinition(that.getEbookDefinition());
	}
	
	public boolean isEmpty() {
		return StringUtils.isBlank(this.bookNameText) & this.sequenceNum == null;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((bookNameText == null) ? 0 : bookNameText.hashCode());
		result = prime * result
				+ ((ebookDefinition == null) ? 0 : ebookDefinition.hashCode());
		result = prime * result
				+ ((ebookNameId == null) ? 0 : ebookNameId.hashCode());
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
		EbookName other = (EbookName) obj;
		if (bookNameText == null) {
			if (other.bookNameText != null)
				return false;
		} else if (!bookNameText.equals(other.bookNameText))
			return false;
		if (ebookDefinition == null) {
			if (other.ebookDefinition != null)
				return false;
		} else if (!ebookDefinition.equals(other.ebookDefinition))
			return false;
		if (ebookNameId == null) {
			if (other.ebookNameId != null)
				return false;
		} else if (!ebookNameId.equals(other.ebookNameId))
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

		buffer.append("ebookNameId=[").append(ebookNameId).append("] ");
		buffer.append("bookNameText=[").append(bookNameText).append("] ");
		buffer.append("sequenceNum=[").append(sequenceNum).append("] ");

		return buffer.toString();
	}
	
	/**
	 * For sorting the name components into sequence order (1...n).
	 */
	@Override
	public int compareTo(EbookName o) {
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



