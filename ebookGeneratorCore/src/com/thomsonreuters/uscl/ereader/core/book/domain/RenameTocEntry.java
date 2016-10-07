/*
 * Copyright 2016: Thomson Reuters Global Resources. All Rights Reserved.
 * Proprietary and Confidential information of TRGR. Disclosure, Use or
 * Reproduction without the written authorization of TRGR is prohibited
 */
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
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.apache.commons.lang.StringUtils;

import jaxb.adapter.BookDefinitionAdapter;

/**
 */
@Entity
@Table(name = "RENAME_TOC_ENTRY")
@IdClass(RenameTocEntry.RenameTocEntryPk.class)
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(namespace = "ebookGenerator/com/thomsonreuters/uscl/ereader/core/book/domain", name = "RenameTocEntry")
public class RenameTocEntry implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@XmlJavaTypeAdapter(BookDefinitionAdapter.class)
	BookDefinition ebookDefinition;

	@Id
	@XmlElement(name="tocGuid", required=true)
	String tocGuid;
	
	@Column(name = "OLD_LABEL", length = 1024, nullable=false)
	@XmlElement(name="oldLabel", required=true)
	String oldLabel;
	
	@Column(name = "NEW_LABEL", length = 1024, nullable=false)
	@XmlElement(name="newLabel", required=true)
	String newLabel;
	
	@Column(name = "NOTE", length = 512, nullable=false)
	@XmlElement(name="note", required=true)
	String note;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "LAST_UPDATED", nullable=false)
	@XmlElement(name="lastUpdated", required=true)
	Date lastUpdated;
	
	public BookDefinition getBookDefinition() {
		return ebookDefinition;
	}

	public void setBookDefinition(BookDefinition bookDefinition) {
		this.ebookDefinition = bookDefinition;
	}

	public void setTocGuid(String tocGuid) {
		this.tocGuid = tocGuid;
	}
	public String getTocGuid() {
		return tocGuid;
	}

	public void setOldLabel(String oldLabel) {
		this.oldLabel = oldLabel;
	}
	public String getOldLabel() {
		return oldLabel;
	}

	public void setNewLabel(String newLabel) {
		this.newLabel = newLabel;
	}
	public String getNewLabel() {
		return newLabel;
	}

	public void setNote(String note) {
		this.note = note;
	}
	public String getNote() {
		return note;
	}

	public void setLastUpdated(Date lastUpdated) {
		this.lastUpdated = lastUpdated;
	}
	public Date getLastUpdated() {
		return lastUpdated;
	}

	@Transient
	public boolean isEmpty() {
		return StringUtils.isBlank(this.oldLabel) &&
				StringUtils.isBlank(this.newLabel) &&
				StringUtils.isBlank(this.tocGuid) &&
				StringUtils.isBlank(this.note);
	}
	
	@Override
	public String toString() {
		StringBuilder buffer = new StringBuilder();
		buffer.append("ExcludeToc [");
		if(ebookDefinition != null) {
			buffer.append("bookDefinitionId=").append(ebookDefinition.getEbookDefinitionId()).append(", ");
		}
		buffer.append("tocGuid=").append(tocGuid).append(", ");
		buffer.append("oldLabel=").append(oldLabel).append(", ");
		buffer.append("newLabel=").append(newLabel).append(", ");
		buffer.append("note=").append(note).append(", ");
		buffer.append("lastUpdated=").append(lastUpdated).append("]");
		
		return buffer.toString();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((tocGuid == null) ? 0 : tocGuid.hashCode());
		result = prime * result
				+ ((lastUpdated == null) ? 0 : lastUpdated.hashCode());
		result = prime * result
				+ ((newLabel == null) ? 0 : newLabel.hashCode());
		result = prime * result
				+ ((oldLabel == null) ? 0 : oldLabel.hashCode());
		result = prime * result
				+ ((note == null) ? 0 : note.hashCode());
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
		RenameTocEntry other = (RenameTocEntry) obj;
		if (tocGuid == null) {
			if (other.tocGuid != null)
				return false;
		} else if (!tocGuid.equals(other.tocGuid))
			return false;
		if (lastUpdated == null) {
			if (other.lastUpdated != null)
				return false;
		} else if (!lastUpdated.equals(other.lastUpdated))
			return false;
		if (newLabel == null) {
			if (other.newLabel != null)
				return false;
		} else if (!newLabel.equals(other.newLabel))
			return false;
		if (oldLabel == null) {
			if (other.oldLabel != null)
				return false;
		} else if (!oldLabel.equals(other.oldLabel))
			return false;
		if (note == null) {
			if (other.note != null)
				return false;
		} else if (!note.equals(other.note))
			return false;
		return true;
	}



	@Embeddable
	public static class RenameTocEntryPk implements Serializable{
		private static final long serialVersionUID = 1L;

		@ManyToOne(fetch = FetchType.LAZY)
		@JoinColumns({ @JoinColumn(name = "EBOOK_DEFINITION_ID", referencedColumnName = "EBOOK_DEFINITION_ID", nullable = false) })
		BookDefinition ebookDefinition;
		
		@Column(name = "TOC_GUID", length = 33, nullable=false)
		private String tocGuid;

		public RenameTocEntryPk() {
		}

		public BookDefinition getBookDefinition() {
			return ebookDefinition;
		}

		public void setBookDefinition(BookDefinition bookDefinition) {
			this.ebookDefinition = bookDefinition;
		}

		public String getTocGuid() {
			return tocGuid;
		}

		public void setTocGuid(String tocGuid) {
			this.tocGuid = tocGuid;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime
					* result
					+ ((tocGuid == null) ? 0 : tocGuid.hashCode());
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
			RenameTocEntryPk other = (RenameTocEntryPk) obj;
			if (tocGuid == null) {
				if (other.tocGuid != null)
					return false;
			} else if (!tocGuid.equals(other.tocGuid))
				return false;
			return true;
		}
   }
}
