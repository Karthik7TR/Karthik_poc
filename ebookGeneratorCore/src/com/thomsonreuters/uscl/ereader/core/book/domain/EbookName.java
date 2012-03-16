package com.thomsonreuters.uscl.ereader.core.book.domain;

import java.io.Serializable;

import org.apache.commons.lang.StringUtils;

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


/**
 */

@Entity
@NamedQueries({
		@NamedQuery(name = "findAllEbookNames", query = "select myEbookName from EbookName myEbookName"),
		@NamedQuery(name = "findEbookNameByPrimaryKey", query = "select myEbookName from EbookName myEbookName where myEbookName.ebookNameId = ?1") })
@Table(schema = "EBOOK", name = "EBOOK_NAME")

public class EbookName implements Serializable {
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

	/**
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = (int) (prime * result + ((ebookNameId == null) ? 0 : ebookNameId.hashCode()));
		return result;
	}

	/**
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj == this)
			return true;
		if (!(obj instanceof EbookName))
			return false;
		EbookName equalCheck = (EbookName) obj;
		if ((ebookNameId == null && equalCheck.ebookNameId != null) || (ebookNameId != null && equalCheck.ebookNameId == null))
			return false;
		if (ebookNameId != null && !ebookNameId.equals(equalCheck.ebookNameId))
			return false;
		return true;
	}
}
