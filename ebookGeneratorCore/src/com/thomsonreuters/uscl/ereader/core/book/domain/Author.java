package com.thomsonreuters.uscl.ereader.core.book.domain;

import java.io.Serializable;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
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

import org.apache.commons.lang3.StringUtils;

@Entity
@NamedQueries({
    @NamedQuery(
        name = "findAuthorByEbookDefinitionId",
        query = "select myAuthor from Author myAuthor where myAuthor.ebookDefinition = :eBookDef")})
@Table(name = "AUTHOR")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(namespace = "ebookGenerator/com/thomsonreuters/uscl/ereader/core/book/domain", name = "Author")
public class Author implements Serializable, Comparable<Author>
{
    private static final long serialVersionUID = 7962657038385328632L;

    @Column(name = "AUTHOR_ID", nullable = false)
    @Basic(fetch = FetchType.EAGER)
    @Id
    @GeneratedValue(generator = "AuthorSequence")
    @SequenceGenerator(name = "AuthorSequence", sequenceName = "AUTHOR_ID_SEQ")
    private Long authorId;

    @Column(name = "AUTHOR_NAME_PREFIX", length = 40)
    @Basic(fetch = FetchType.EAGER)
    private String authorNamePrefix;

    @Column(name = "AUTHOR_NAME_SUFFIX", length = 40)
    @Basic(fetch = FetchType.EAGER)
    private String authorNameSuffix;

    @Column(name = "AUTHOR_FIRST_NAME", length = 1024)
    @Basic(fetch = FetchType.EAGER)
    private String authorFirstName;

    @Column(name = "AUTHOR_MIDDLE_NAME", length = 1024)
    @Basic(fetch = FetchType.EAGER)

    private String authorMiddleName;

    @Column(name = "AUTHOR_LAST_NAME", length = 1024, nullable = false)
    @Basic(fetch = FetchType.EAGER)
    private String authorLastName;

    @Column(name = "AUTHOR_ADDL_TEXT", length = 2048)
    @Basic(fetch = FetchType.EAGER)
    private String authorAddlText;

    @Column(name = "SEQUENCE_NUMBER")
    @Basic(fetch = FetchType.EAGER)
    private Integer sequenceNum;

    @Column(name = "USE_COMMA_BEFORE_SUFFIX", length = 1)
    @Basic(fetch = FetchType.EAGER)
    private String useCommaBeforeSuffix;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumns({
        @JoinColumn(name = "EBOOK_DEFINITION_ID", referencedColumnName = "EBOOK_DEFINITION_ID", nullable = false)})
    private BookDefinition ebookDefinition;

    public void setAuthorId(final Long authorId)
    {
        this.authorId = authorId;
    }

    public Long getAuthorId()
    {
        return authorId;
    }

    public void setAuthorNamePrefix(final String authorNamePrefix)
    {
        this.authorNamePrefix = authorNamePrefix;
    }

    public String getAuthorNamePrefix()
    {
        return authorNamePrefix;
    }

    public void setAuthorNameSuffix(final String authorNameSuffix)
    {
        this.authorNameSuffix = authorNameSuffix;
    }

    public String getAuthorNameSuffix()
    {
        return authorNameSuffix;
    }

    public void setAuthorFirstName(final String authorFirstName)
    {
        this.authorFirstName = authorFirstName;
    }

    public String getAuthorFirstName()
    {
        return authorFirstName;
    }

    public void setAuthorMiddleName(final String authorMiddleName)
    {
        this.authorMiddleName = authorMiddleName;
    }

    public String getAuthorMiddleName()
    {
        return authorMiddleName;
    }

    public void setAuthorLastName(final String authorLastName)
    {
        this.authorLastName = authorLastName;
    }

    public String getAuthorLastName()
    {
        return authorLastName;
    }

    public void setAuthorAddlText(final String authorAddlText)
    {
        this.authorAddlText = authorAddlText;
    }

    public String getAuthorAddlText()
    {
        return authorAddlText;
    }

    public Integer getSequenceNum()
    {
        return sequenceNum;
    }

    public void setSequenceNum(final Integer sequenceNum)
    {
        this.sequenceNum = sequenceNum;
    }

    public boolean getUseCommaBeforeSuffix()
    {
        if (StringUtils.isBlank(useCommaBeforeSuffix))
        {
            return false;
        }
        else
        {
            return ((useCommaBeforeSuffix.equalsIgnoreCase("Y") ? true : false));
        }
    }

    public void setUseCommaBeforeSuffix(final boolean useCommaBeforeSuffix)
    {
        this.useCommaBeforeSuffix = ((useCommaBeforeSuffix) ? "Y" : "N");
    }

    public void setEbookDefinition(final BookDefinition ebookDefinition)
    {
        this.ebookDefinition = ebookDefinition;
    }

    public BookDefinition getEbookDefinition()
    {
        return ebookDefinition;
    }

    public Author()
    {
    }

    /**
     * Copies the contents of the specified bean into this bean.
     *
     */
    public void copy(final Author that)
    {
        setAuthorId(that.getAuthorId());
        setAuthorNamePrefix(that.getAuthorNamePrefix());
        setAuthorNameSuffix(that.getAuthorNameSuffix());
        setAuthorFirstName(that.getAuthorFirstName());
        setAuthorMiddleName(that.getAuthorMiddleName());
        setAuthorLastName(that.getAuthorLastName());
        setAuthorAddlText(that.getAuthorAddlText());
        setSequenceNum(that.getSequenceNum());
        setUseCommaBeforeSuffix(that.getUseCommaBeforeSuffix());
        setEbookDefinition(that.getEbookDefinition());
    }

    /**
     * Returns boolean whether all the name fields are empty.
     * Used in EditBookDefinitionForm to delete List of authors.
     * @return
     */
    @Transient
    public boolean isNameEmpty()
    {
        return StringUtils.isBlank(authorFirstName)
            && StringUtils.isBlank(authorMiddleName)
            && StringUtils.isBlank(authorLastName)
            && StringUtils.isBlank(authorNamePrefix)
            && StringUtils.isBlank(authorNameSuffix)
            && StringUtils.isBlank(authorAddlText);
    }

    /**
     * The full name of the author
     * @return the concat of prefix, first Name, middle Name, Last Name and suffix
     */
    @Transient
    public String getFullName()
    {
        final StringBuilder buffer = new StringBuilder();

        if (StringUtils.isNotBlank(authorNamePrefix))
            buffer.append(authorNamePrefix).append(" ");
        if (StringUtils.isNotBlank(authorFirstName))
            buffer.append(authorFirstName).append(" ");
        if (StringUtils.isNotBlank(authorMiddleName))
            buffer.append(authorMiddleName).append(" ");
        if (StringUtils.isNotBlank(authorLastName))
            buffer.append(authorLastName);
        if (StringUtils.isNotBlank(authorNameSuffix))
        {
            if (getUseCommaBeforeSuffix())
            {
                buffer.append(", ");
            }
            else
            {
                buffer.append(" ");
            }
            buffer.append(authorNameSuffix);
        }

        return StringUtils.trim(buffer.toString());
    }

    @Override
    public String toString()
    {
        final StringBuilder buffer = new StringBuilder();
        buffer.append("authorId=[").append(authorId).append("] ");
        buffer.append("authorNamePrefix=[").append(authorNamePrefix).append("] ");
        buffer.append("authorFirstName=[").append(authorFirstName).append("] ");
        buffer.append("authorMiddleName=[").append(authorMiddleName).append("] ");
        buffer.append("authorLastName=[").append(authorLastName).append("] ");
        buffer.append("authorNameSuffix=[").append(authorNameSuffix).append("] ");
        buffer.append("authorAddlText=[").append(authorAddlText).append("] ");
        buffer.append("sequenceNum=[").append(sequenceNum).append("] ");
        buffer.append("useCommaBeforeSuffix=[").append(useCommaBeforeSuffix).append("] ");

        return buffer.toString();
    }

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((authorAddlText == null) ? 0 : authorAddlText.hashCode());
        result = prime * result + ((authorFirstName == null) ? 0 : authorFirstName.hashCode());
        result = prime * result + ((authorId == null) ? 0 : authorId.hashCode());
        result = prime * result + ((authorLastName == null) ? 0 : authorLastName.hashCode());
        result = prime * result + ((authorMiddleName == null) ? 0 : authorMiddleName.hashCode());
        result = prime * result + ((authorNamePrefix == null) ? 0 : authorNamePrefix.hashCode());
        result = prime * result + ((authorNameSuffix == null) ? 0 : authorNameSuffix.hashCode());
        result = prime * result + ((ebookDefinition == null) ? 0 : ebookDefinition.hashCode());
        return result;
    }

    @Override
    public boolean equals(final Object obj)
    {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        final Author other = (Author) obj;
        if (authorAddlText == null)
        {
            if (other.authorAddlText != null)
                return false;
        }
        else if (!authorAddlText.equals(other.authorAddlText))
            return false;
        if (authorFirstName == null)
        {
            if (other.authorFirstName != null)
                return false;
        }
        else if (!authorFirstName.equals(other.authorFirstName))
            return false;
        if (authorId == null)
        {
            if (other.authorId != null)
                return false;
        }
        else if (!authorId.equals(other.authorId))
            return false;
        if (authorLastName == null)
        {
            if (other.authorLastName != null)
                return false;
        }
        else if (!authorLastName.equals(other.authorLastName))
            return false;
        if (authorMiddleName == null)
        {
            if (other.authorMiddleName != null)
                return false;
        }
        else if (!authorMiddleName.equals(other.authorMiddleName))
            return false;
        if (authorNamePrefix == null)
        {
            if (other.authorNamePrefix != null)
                return false;
        }
        else if (!authorNamePrefix.equals(other.authorNamePrefix))
            return false;
        if (authorNameSuffix == null)
        {
            if (other.authorNameSuffix != null)
                return false;
        }
        else if (!authorNameSuffix.equals(other.authorNameSuffix))
            return false;
        if (ebookDefinition == null)
        {
            if (other.ebookDefinition != null)
                return false;
        }
        else if (!ebookDefinition.equals(other.ebookDefinition))
            return false;
        return true;
    }

    /**
     * For sorting the name components into sequence order (1...n).
     */
    @Override
    public int compareTo(final Author o)
    {
        int result = 0;
        if (sequenceNum != null)
        {
            if (o != null)
            {
                final Integer i = o.getSequenceNum();
                result = (i != null) ? sequenceNum.compareTo(i) : 1;
            }
            else
            {
                result = 1;
            }
        }
        else
        { // int1 is null
            result = (o != null) ? -1 : 0;
        }
        return result;
    }
}
