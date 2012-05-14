package com.thomsonreuters.uscl.ereader.core.book.domain;

import java.io.Serializable;
import java.util.Date;

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
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

import org.apache.commons.lang.StringUtils;

/**
 */
@Entity
@Table(name = "EXCLUDE_DOCUMENT")
@IdClass(ExcludeDocument.ExcludeDocumentPk.class)
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(namespace = "ebookGenerator/com/thomsonreuters/uscl/ereader/core/book/domain", name = "ExcludeDocument")
public class ExcludeDocument implements Serializable {
	private static final long serialVersionUID = 8698522630203083821L;

	@Id
	BookDefinition ebookDefinition;

	@Id
	String documentGuid;
	
	@Column(name = "NOTE", length = 512, nullable=false)
	String note;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "LAST_UPDATED", nullable=false)
	Date lastUpdated;
	
	public BookDefinition getBookDefinition() {
		return ebookDefinition;
	}

	public void setBookDefinition(BookDefinition bookDefinition) {
		this.ebookDefinition = bookDefinition;
	}

	public String getDocumentGuid() {
		return documentGuid;
	}

	public void setDocumentGuid(String documentGuid) {
		this.documentGuid = documentGuid;
	}

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}

	public Date getLastUpdated() {
		return lastUpdated;
	}

	public void setLastUpdated(Date lastUpdated) {
		this.lastUpdated = lastUpdated;
	}

	@Transient
	public boolean isEmpty() {
		return StringUtils.isBlank(this.note) &&
				StringUtils.isBlank(this.documentGuid);
	}
	
	@Override
	public String toString() {
		StringBuilder buffer = new StringBuilder();
		buffer.append("ExcludeDocument [");
		if(ebookDefinition != null) {
			buffer.append("bookDefinitionId=").append(ebookDefinition.getEbookDefinitionId()).append(", ");
		}
		buffer.append("documentGuid=").append(documentGuid).append(", ");
		buffer.append("note=").append(note).append(", ");
		buffer.append("lastUpdated=").append(lastUpdated).append("]");
		
		return buffer.toString();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((documentGuid == null) ? 0 : documentGuid.hashCode());
		result = prime * result
				+ ((lastUpdated == null) ? 0 : lastUpdated.hashCode());
		result = prime * result + ((note == null) ? 0 : note.hashCode());
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
		ExcludeDocument other = (ExcludeDocument) obj;
		if (documentGuid == null) {
			if (other.documentGuid != null)
				return false;
		} else if (!documentGuid.equals(other.documentGuid))
			return false;
		if (lastUpdated == null) {
			if (other.lastUpdated != null)
				return false;
		} else if (!lastUpdated.equals(other.lastUpdated))
			return false;
		if (note == null) {
			if (other.note != null)
				return false;
		} else if (!note.equals(other.note))
			return false;
		return true;
	}

	@Embeddable
	public static class ExcludeDocumentPk implements Serializable{
		private static final long serialVersionUID = 3552710801579579685L;

		@ManyToOne(fetch = FetchType.LAZY)
		@JoinColumns({ @JoinColumn(name = "EBOOK_DEFINITION_ID", referencedColumnName = "EBOOK_DEFINITION_ID", nullable = false) })
		BookDefinition ebookDefinition;
		
		@Column(name = "DOCUMENT_GUID", length = 33, nullable=false)
		private String documentGuid;

		public ExcludeDocumentPk() {
		}

		public BookDefinition getBookDefinition() {
			return ebookDefinition;
		}

		public void setBookDefinition(BookDefinition bookDefinition) {
			this.ebookDefinition = bookDefinition;
		}

		public String getDocumentGuid() {
			return documentGuid;
		}

		public void setDocumentGuid(String documentGuid) {
			this.documentGuid = documentGuid;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime
					* result
					+ ((documentGuid == null) ? 0 : documentGuid.hashCode());
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
			ExcludeDocumentPk other = (ExcludeDocumentPk) obj;
			if (documentGuid == null) {
				if (other.documentGuid != null)
					return false;
			} else if (!documentGuid.equals(other.documentGuid))
				return false;
			return true;
		}
   }
}
