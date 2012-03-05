package com.thomsonreuters.uscl.ereader.core.book.domain;

import java.io.Serializable;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;

/**
 */
public class AuthorPK implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 */
	public AuthorPK() {
	}

	/**
	 */

	@Column(name = "AUTHOR_ID", nullable = false)
	@Basic(fetch = FetchType.EAGER)
	@Id
	@GeneratedValue(strategy=GenerationType.SEQUENCE, generator = "AuthorSequence")
	@SequenceGenerator(name="AuthorSequence", sequenceName = "AUTHOR_ID_SEQ")
	public Long authorId;
	/**
	 */

	@Column(name = "EBOOK_DEFINITION_ID", nullable = false)
	@Basic(fetch = FetchType.EAGER)
	@Id
	public Long ebookDefinitionId;

	/**
	 */
	public void setAuthorId(long authorId) {
		this.authorId = authorId;
	}

	/**
	 */
	public long getAuthorId() {
		return this.authorId;
	}

	/**
	 */
	public void setEbookDefinitionId(Long ebookDefinitionId) {
		this.ebookDefinitionId = ebookDefinitionId;
	}

	/**
	 */
	public Long getEbookDefinitionId() {
		return this.ebookDefinitionId;
	}

	/**
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = (int) (prime * result + ((authorId == null) ? 0 : authorId.hashCode()));
		result = (int) (prime * result + ((ebookDefinitionId == null) ? 0 : ebookDefinitionId.hashCode()));
		return result;
	}

	/**
	 */
	public boolean equals(Object obj) {
		if (obj == this)
			return true;
		if (!(obj instanceof AuthorPK))
			return false;
		AuthorPK equalCheck = (AuthorPK) obj;
		if ((authorId == null && equalCheck.authorId != null) || (authorId != null && equalCheck.authorId == null))
			return false;
		if (authorId != null && !authorId.equals(equalCheck.authorId))
			return false;
		if ((ebookDefinitionId == null && equalCheck.ebookDefinitionId != null) || (ebookDefinitionId != null && equalCheck.ebookDefinitionId == null))
			return false;
		if (ebookDefinitionId != null && !ebookDefinitionId.equals(equalCheck.ebookDefinitionId))
			return false;
		return true;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder("AuthorPK");
		sb.append(" authorId: ").append(getAuthorId());
		sb.append(" ebookDefinitionId: ").append(getEbookDefinitionId());
		return sb.toString();
	}
}
