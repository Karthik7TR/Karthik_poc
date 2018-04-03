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
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.apache.commons.lang3.StringUtils;

@Getter
@Setter
@EqualsAndHashCode(
    of = {
        "authorAddlText",
        "authorFirstName",
        "authorId",
        "authorLastName",
        "authorMiddleName",
        "authorNamePrefix",
        "authorNameSuffix",
        "ebookDefinition"})
@ToString

@Entity
@Table(name = "AUTHOR")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(namespace = "ebookGenerator/com/thomsonreuters/uscl/ereader/core/book/domain", name = "Author")
public class Author implements Serializable, Comparable<Author> {
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

    public boolean getUseCommaBeforeSuffix() {
        if (StringUtils.isBlank(useCommaBeforeSuffix)) {
            return false;
        } else {
            return "Y".equalsIgnoreCase(useCommaBeforeSuffix);
        }
    }

    public void setUseCommaBeforeSuffix(final boolean useCommaBeforeSuffix) {
        this.useCommaBeforeSuffix = useCommaBeforeSuffix ? "Y" : "N";
    }

    /**
     * Copies the contents of the specified bean into this bean.
     *
     */
    public void copy(final Author that) {
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
    public boolean isNameEmpty() {
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
    public String getFullName() {
        final StringBuilder buffer = new StringBuilder();

        if (StringUtils.isNotBlank(authorNamePrefix))
            buffer.append(authorNamePrefix).append(" ");
        if (StringUtils.isNotBlank(authorFirstName))
            buffer.append(authorFirstName).append(" ");
        if (StringUtils.isNotBlank(authorMiddleName))
            buffer.append(authorMiddleName).append(" ");
        if (StringUtils.isNotBlank(authorLastName))
            buffer.append(authorLastName);
        if (StringUtils.isNotBlank(authorNameSuffix)) {
            if (getUseCommaBeforeSuffix()) {
                buffer.append(", ");
            } else {
                buffer.append(" ");
            }
            buffer.append(authorNameSuffix);
        }

        return StringUtils.trim(buffer.toString());
    }

    /**
     * For sorting the name components into sequence order (1...n).
     */
    @Override
    public int compareTo(final Author o) {
        int result = 0;
        if (sequenceNum != null) {
            if (o != null) {
                final Integer i = o.getSequenceNum();
                result = (i != null) ? sequenceNum.compareTo(i) : 1;
            } else {
                result = 1;
            }
        } else { // int1 is null
            result = (o != null) ? -1 : 0;
        }
        return result;
    }
}
