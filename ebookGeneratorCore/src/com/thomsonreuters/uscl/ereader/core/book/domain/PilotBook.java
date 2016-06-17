package com.thomsonreuters.uscl.ereader.core.book.domain;

import java.io.Serializable;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

@Entity
@Table(name = "PILOT_BOOK")
@IdClass(PilotBook.PilotBookPk.class)
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(namespace = "ebookGenerator/com/thomsonreuters/uscl/ereader/core/book/domain", name = "PilotBook")
public class PilotBook implements Serializable, Comparable<PilotBook> {
	private static final long serialVersionUID = 7962657038385328632L;
	
	
	@Id
	String pilotBookTitleId;
	
	@Id
	BookDefinition ebookDefinition;
		
	
	@Column(name = "SEQUENCE_NUMBER")
	@Basic(fetch = FetchType.EAGER)
	Integer sequenceNum;
	
	@Column(name = "NOTE", length = 512)
	String note;

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
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
	
	public String getPilotBookTitleId() {
		return pilotBookTitleId;
	}

	public void setPilotBookTitleId(String pilotBookTitleId) {
		this.pilotBookTitleId = pilotBookTitleId;
	}

	/**
	 */
	public PilotBook() {
	}

	/**
	 * Copies the contents of the specified bean into this bean.
	 *
	 */
	public void copy(PilotBook that) {
		setPilotBookTitleId(that.getPilotBookTitleId());
		setSequenceNum(that.getSequenceNum());
		setEbookDefinition(that.getEbookDefinition());
	}
	
	public boolean isEmpty() {
		return pilotBookTitleId == null || pilotBookTitleId.equals("");
	}
	


	@Override
	public String toString() {
		StringBuilder buffer = new StringBuilder();
		buffer.append("pilotBookTitleId=[").append(pilotBookTitleId).append("] ");		
		buffer.append("sequenceNum=[").append(sequenceNum).append("] ");
		buffer.append("note=").append(note).append("]");
		return buffer.toString();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		
		result = prime * result
				+ ((pilotBookTitleId == null) ? 0 : pilotBookTitleId.hashCode());		
		result = prime * result
				+ ((ebookDefinition == null) ? 0 : ebookDefinition.hashCode());
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
		PilotBook other = (PilotBook) obj;
		if (pilotBookTitleId == null) {
			if (other.pilotBookTitleId != null)
				return false;
		} else if (!pilotBookTitleId.equals(other.pilotBookTitleId))
			return false;
		
		if (ebookDefinition == null) {
			if (other.ebookDefinition != null)
				return false;
		} else if (!ebookDefinition.equals(other.ebookDefinition))
			return false;
		return true;
	}
	
	/**
	 * For sorting the name components into sequence order (1...n).
	 */
	@Override
	public int compareTo(PilotBook o) {
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
	
	@Embeddable
	public static class PilotBookPk implements Serializable{
		private static final long serialVersionUID = 3552710801579579685L;

		@ManyToOne(fetch = FetchType.LAZY)
		@JoinColumns({ @JoinColumn(name = "EBOOK_DEFINITION_ID", referencedColumnName = "EBOOK_DEFINITION_ID", nullable = false) })
		BookDefinition ebookDefinition;
		
		@Column(name = "PILOT_BOOK_TITLE_ID", nullable = false)
		String pilotBookTitleId;	

		public PilotBookPk() {
		}

		public BookDefinition getBookDefinition() {
			return ebookDefinition;
		}

		public void setBookDefinition(BookDefinition bookDefinition) {
			this.ebookDefinition = bookDefinition;
		}

		

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime
					* result
					+ ((pilotBookTitleId == null) ? 0 : pilotBookTitleId.hashCode());
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
			PilotBookPk other = (PilotBookPk) obj;
			if (pilotBookTitleId == null) {
				if (other.pilotBookTitleId != null)
					return false;
			} else if (!pilotBookTitleId.equals(other.pilotBookTitleId))
				return false;
			return true;
		}
   }

}

