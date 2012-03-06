package com.thomsonreuters.uscl.ereader.core.book.domain;

import java.io.Serializable;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.apache.commons.lang.StringUtils;

import com.thomsonreuters.uscl.ereader.orchestrate.core.BookDefinition;

/**
 */
@IdClass(com.thomsonreuters.uscl.ereader.core.book.domain.AuthorPK.class)
@Entity
@NamedQueries({
		@NamedQuery(name = "findAuthorByEbookDefinitionId", query = "select myAuthor from Author myAuthor where myAuthor.ebookDefinitionId = :eBookDefId")})
@Table(schema = "EBOOK", name = "AUTHOR")
public class Author implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 */

	@Column(name = "AUTHOR_ID", nullable = false)
	@Basic(fetch = FetchType.EAGER)
	@Id
	Long authorId;
	/**
	 */

	@Column(name = "EBOOK_DEFINITION_ID", nullable = false)
	@Basic(fetch = FetchType.EAGER)
	@Id
	Long ebookDefinitionId;
	/**
	 */

	@Column(name = "AUTHOR_NAME_PREFIX", length = 40)
	@Basic(fetch = FetchType.EAGER)
	String authorNamePrefix;
	/**
	 */

	@Column(name = "AUTHOR_NAME_SUFFIX", length = 40)
	@Basic(fetch = FetchType.EAGER)
	String authorNameSuffix;
	/**
	 */

	@Column(name = "AUTHOR_FIRST_NAME", length = 1024)
	@Basic(fetch = FetchType.EAGER)
	String authorFirstName;
	/**
	 */

	@Column(name = "AUTHOR_MIDDLE_NAME", length = 1024)
	@Basic(fetch = FetchType.EAGER)

	String authorMiddleName;
	/**
	 */

	@Column(name = "AUTHOR_LAST_NAME", length = 1024, nullable = false)
	@Basic(fetch = FetchType.EAGER)
	String authorLastName;
	/**
	 */

	@Column(name = "AUTHOR_ADDL_TEXT", length = 1024)
	@Basic(fetch = FetchType.EAGER)
	String authorAddlText;

	/**
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns({ @JoinColumn(name = "EBOOK_DEFINITION_ID", referencedColumnName = "EBOOK_DEFINITION_ID", nullable = false, insertable = false, updatable = false) })
	BookDefinition ebookDefinition;

	/**
	 */
	public void setAuthorId(Long authorId) {
		this.authorId = authorId;
	}

	/**
	 */
	public Long getAuthorId() {
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
	public void setAuthorNamePrefix(String authorNamePrefix) {
		this.authorNamePrefix = authorNamePrefix;
	}

	/**
	 */
	public String getAuthorNamePrefix() {
		return this.authorNamePrefix;
	}

	/**
	 */
	public void setAuthorNameSuffix(String authorNameSuffix) {
		this.authorNameSuffix = authorNameSuffix;
	}

	/**
	 */
	public String getAuthorNameSuffix() {
		return this.authorNameSuffix;
	}

	/**
	 */
	public void setAuthorFirstName(String authorFirstName) {
		this.authorFirstName = authorFirstName;
	}

	/**
	 */
	public String getAuthorFirstName() {
		return this.authorFirstName;
	}

	/**
	 */
	public void setAuthorMiddleName(String authorMiddleName) {
		this.authorMiddleName = authorMiddleName;
	}

	/**
	 */
	public String getAuthorMiddleName() {
		return this.authorMiddleName;
	}

	/**
	 */
	public void setAuthorLastName(String authorLastName) {
		this.authorLastName = authorLastName;
	}

	/**
	 */
	public String getAuthorLastName() {
		return this.authorLastName;
	}

	/**
	 */
	public void setAuthorAddlText(String authorAddlText) {
		this.authorAddlText = authorAddlText;
	}

	/**
	 */
	public String getAuthorAddlText() {
		return this.authorAddlText;
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
	public Author() {
	}

	/**
	 * Copies the contents of the specified bean into this bean.
	 *
	 */
	public void copy(Author that) {
		setAuthorId(that.getAuthorId());
		setEbookDefinitionId(that.getEbookDefinitionId());
		setAuthorNamePrefix(that.getAuthorNamePrefix());
		setAuthorNameSuffix(that.getAuthorNameSuffix());
		setAuthorFirstName(that.getAuthorFirstName());
		setAuthorMiddleName(that.getAuthorMiddleName());
		setAuthorLastName(that.getAuthorLastName());
		setAuthorAddlText(that.getAuthorAddlText());
		setEbookDefinition(that.getEbookDefinition());
	}
	
	/**
	 * Returns boolean whether all the name fields are empty.
	 * Used in EditBookDefinitionForm to delete List of authors.
	 * @return
	 */
	@Transient
	public boolean isNameEmpty () {
		return StringUtils.isBlank(this.authorFirstName) &&
				StringUtils.isBlank(this.authorMiddleName) &&
				StringUtils.isBlank(this.authorLastName) &&
				StringUtils.isBlank(this.authorNamePrefix) &&
				StringUtils.isBlank(this.authorNameSuffix) &&
				StringUtils.isBlank(this.authorAddlText);
	}

	/**
	 * Returns a textual representation of a bean.
	 *
	 */
	public String toString() {

		StringBuilder buffer = new StringBuilder();

		buffer.append("authorId=[").append(authorId).append("] ");
		buffer.append("ebookDefinitionId=[").append(ebookDefinitionId).append("] ");
		buffer.append("authorNamePrefix=[").append(authorNamePrefix).append("] ");
		buffer.append("authorNameSuffix=[").append(authorNameSuffix).append("] ");
		buffer.append("authorFirstName=[").append(authorFirstName).append("] ");
		buffer.append("authorMiddleName=[").append(authorMiddleName).append("] ");
		buffer.append("authorLastName=[").append(authorLastName).append("] ");
		buffer.append("authorAddlText=[").append(authorAddlText).append("] ");

		return buffer.toString();
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
		if (!(obj instanceof Author))
			return false;
		Author equalCheck = (Author) obj;
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
}
