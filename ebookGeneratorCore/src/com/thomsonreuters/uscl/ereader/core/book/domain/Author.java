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
import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

import org.apache.commons.lang.StringUtils;


/**
 */
@Entity
@NamedQueries({
		@NamedQuery(name = "findAuthorByEbookDefinitionId", query = "select myAuthor from Author myAuthor where myAuthor.ebookDefinition = :eBookDef")})
@Table(name = "AUTHOR")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(namespace = "ebookGenerator/com/thomsonreuters/uscl/ereader/core/book/domain", name = "Author")
public class Author implements Serializable, Comparable<Author> {
	private static final long serialVersionUID = 7962657038385328632L;
	/**
	 */
	@Column(name = "AUTHOR_ID", nullable = false)
	@Basic(fetch = FetchType.EAGER)
	@Id
	@GeneratedValue(strategy=GenerationType.SEQUENCE, generator = "AuthorSequence")
	@SequenceGenerator(name="AuthorSequence", sequenceName = "AUTHOR_ID_SEQ")
	Long authorId;
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
	
	@Column(name = "SEQUENCE_NUMBER")
	@Basic(fetch = FetchType.EAGER)
	Integer sequenceNum;

	/**
	 */
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumns({ @JoinColumn(name = "EBOOK_DEFINITION_ID", referencedColumnName = "EBOOK_DEFINITION_ID", nullable = false) })
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
	 * The full name of the author
	 * @return the concat of prefix, first Name, middle Name, Last Name and suffix
	 */
	@Transient
	public String getFullName() {
		StringBuilder buffer = new StringBuilder();

		if(!StringUtils.isBlank(authorNamePrefix))
			buffer.append(authorNamePrefix).append(" ");
		if(!StringUtils.isBlank(authorFirstName))
			buffer.append(authorFirstName).append(" ");
		if(!StringUtils.isBlank(authorMiddleName))
			buffer.append(authorMiddleName).append(" ");
		if(!StringUtils.isBlank(authorLastName))
			buffer.append(authorLastName).append(" ");
		if(!StringUtils.isBlank(authorNameSuffix))
			buffer.append(authorNameSuffix);

		return StringUtils.trim(buffer.toString());
	}

	@Override
	public String toString() {
		return "Author [authorId=" + authorId + ", authorNamePrefix="
				+ authorNamePrefix + ", authorNameSuffix=" + authorNameSuffix
				+ ", authorFirstName=" + authorFirstName
				+ ", authorMiddleName=" + authorMiddleName
				+ ", authorLastName=" + authorLastName + ", authorAddlText="
				+ authorAddlText + ", sequenceNum=" + sequenceNum + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((authorAddlText == null) ? 0 : authorAddlText.hashCode());
		result = prime * result
				+ ((authorFirstName == null) ? 0 : authorFirstName.hashCode());
		result = prime * result
				+ ((authorId == null) ? 0 : authorId.hashCode());
		result = prime * result
				+ ((authorLastName == null) ? 0 : authorLastName.hashCode());
		result = prime
				* result
				+ ((authorMiddleName == null) ? 0 : authorMiddleName.hashCode());
		result = prime
				* result
				+ ((authorNamePrefix == null) ? 0 : authorNamePrefix.hashCode());
		result = prime
				* result
				+ ((authorNameSuffix == null) ? 0 : authorNameSuffix.hashCode());
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
		Author other = (Author) obj;
		if (authorAddlText == null) {
			if (other.authorAddlText != null)
				return false;
		} else if (!authorAddlText.equals(other.authorAddlText))
			return false;
		if (authorFirstName == null) {
			if (other.authorFirstName != null)
				return false;
		} else if (!authorFirstName.equals(other.authorFirstName))
			return false;
		if (authorId == null) {
			if (other.authorId != null)
				return false;
		} else if (!authorId.equals(other.authorId))
			return false;
		if (authorLastName == null) {
			if (other.authorLastName != null)
				return false;
		} else if (!authorLastName.equals(other.authorLastName))
			return false;
		if (authorMiddleName == null) {
			if (other.authorMiddleName != null)
				return false;
		} else if (!authorMiddleName.equals(other.authorMiddleName))
			return false;
		if (authorNamePrefix == null) {
			if (other.authorNamePrefix != null)
				return false;
		} else if (!authorNamePrefix.equals(other.authorNamePrefix))
			return false;
		if (authorNameSuffix == null) {
			if (other.authorNameSuffix != null)
				return false;
		} else if (!authorNameSuffix.equals(other.authorNameSuffix))
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
	public int compareTo(Author o) {
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
