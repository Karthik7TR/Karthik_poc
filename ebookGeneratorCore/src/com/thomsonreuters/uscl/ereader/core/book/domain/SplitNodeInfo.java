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
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


@Entity
@Table(name = "SPLIT_NODE_INFO")
@IdClass(SplitNodeInfo.SplitNodeInfoPk.class)
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(namespace = "ebookGenerator/com/thomsonreuters/uscl/ereader/core/book/domain", name = "SplitNodeInfo")
public class SplitNodeInfo implements Serializable {
	private static final long serialVersionUID = 8698522630203083821L;

	@Id
	BookDefinition ebookDefinition;

	@Id
	String splitBookTitleId;
	
	@Id
	String bookVersionSubmitted;
	
	@Column(name = "SPLIT_NODE_GUID", length = 33, nullable=false)
	String splitNodeGuid;

	public BookDefinition getBookDefinition() {
		return ebookDefinition;
	}

	public void setBookDefinition(BookDefinition bookDefinition) {
		this.ebookDefinition = bookDefinition;
	}
	
	/**
	 */
	public void setBookVersionSubmitted(String bookVersionSubmitted) {
		this.bookVersionSubmitted = bookVersionSubmitted;
	}

	/**
	 */
	public String getBookVersionSubmitted() {
		return this.bookVersionSubmitted;
	}
	
	public String getSplitBookTitle() {
		return splitBookTitleId;
	}

	public void setSpitBookTitle(String splitBookTitle) {
		this.splitBookTitleId = splitBookTitle;
	}
	

	
	public String getSplitNodeGuid() {
		return splitNodeGuid;
	}

	public void setSplitNodeGuid(String splitNodeGuid) {
		this.splitNodeGuid = splitNodeGuid;
	}
	
	@Override
	public String toString() {
		StringBuilder buffer = new StringBuilder();
		buffer.append("SplitNodeInfo [");
		if(ebookDefinition != null) {
			buffer.append("bookDefinitionId=").append(ebookDefinition.getEbookDefinitionId()).append(", ");
		}
		buffer.append("splitBookTitleId=").append(splitBookTitleId).append(", ");
		buffer.append("bookVersionSubmitted=").append(bookVersionSubmitted).append(", ");
		buffer.append("splitNodeGuid=").append(splitNodeGuid).append("]");
		
		return buffer.toString();
	}


	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((bookVersionSubmitted == null) ? 0 : bookVersionSubmitted.hashCode());
		result = prime * result + ((ebookDefinition.getEbookDefinitionId() == null) ? 0 : ebookDefinition.getEbookDefinitionId().hashCode());
		result = prime * result + ((splitBookTitleId == null) ? 0 : splitBookTitleId.hashCode());
		result = prime * result + ((splitNodeGuid == null) ? 0 : splitNodeGuid.hashCode());
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
		SplitNodeInfo other = (SplitNodeInfo) obj;
		if (bookVersionSubmitted == null) {
			if (other.bookVersionSubmitted != null)
				return false;
		} else if (!bookVersionSubmitted.equals(other.bookVersionSubmitted))
			return false;
		if (ebookDefinition == null) {
			if (other.ebookDefinition != null)
				return false;
		} else if (!ebookDefinition.getEbookDefinitionId().equals(other.ebookDefinition.getEbookDefinitionId()))
			return false;
		if (splitBookTitleId == null) {
			if (other.splitBookTitleId != null)
				return false;
		} else if (!splitBookTitleId.equals(other.splitBookTitleId))
			return false;
		if (splitNodeGuid == null) {
			if (other.splitNodeGuid != null)
				return false;
		} else if (!splitNodeGuid.equals(other.splitNodeGuid))
			return false;
		return true;
	}

	@Embeddable
	public static class SplitNodeInfoPk implements Serializable{
		private static final long serialVersionUID = 3552710801579579685L;

		@ManyToOne(fetch = FetchType.LAZY)
		@JoinColumns({ @JoinColumn(name = "EBOOK_DEFINITION_ID", referencedColumnName = "EBOOK_DEFINITION_ID", nullable = false) })
		BookDefinition ebookDefinition;
		
		@Column(name = "SPLIT_BOOK_TITLE_ID", length = 64)
		@Basic(fetch = FetchType.EAGER)
		String splitBookTitleId;
		
		@Column(name = "BOOK_VERSION_SUBMITTED", length = 10)
		@Basic(fetch = FetchType.EAGER)
		@XmlElement
		String bookVersionSubmitted;

		public SplitNodeInfoPk() {
		}

		public BookDefinition getBookDefinition() {
			return ebookDefinition;
		}

		public void setBookDefinition(BookDefinition bookDefinition) {
			this.ebookDefinition = bookDefinition;
		}
		
		/**
		 */
		public void setBookVersionSubmitted(String bookVersionSubmitted) {
			this.bookVersionSubmitted = bookVersionSubmitted;
		}

		/**
		 */
		public String getBookVersionSubmitted() {
			return this.bookVersionSubmitted;
		}
		
		public String getSplitBookTitle() {
			return splitBookTitleId;
		}

		public void setSpitBookTitle(String splitBookTitle) {
			this.splitBookTitleId = splitBookTitle;
		}


		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime
					* result
					+ ((bookVersionSubmitted == null) ? 0 : bookVersionSubmitted.hashCode());
			result = prime * result + ((splitBookTitleId == null) ? 0 : splitBookTitleId.hashCode());
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
			SplitNodeInfoPk other = (SplitNodeInfoPk) obj;
			if (splitBookTitleId == null) {
				if (other.splitBookTitleId != null)
					return false;
			} else if (!splitBookTitleId.equals(other.splitBookTitleId))
				return false;
			if (bookVersionSubmitted == null) {
				if (other.bookVersionSubmitted != null)
					return false;
			} else if (!bookVersionSubmitted.equals(other.bookVersionSubmitted))
				return false;
			return true;
		}
   }

}

