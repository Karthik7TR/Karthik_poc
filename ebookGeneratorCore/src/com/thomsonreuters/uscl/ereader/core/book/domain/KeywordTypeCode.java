package com.thomsonreuters.uscl.ereader.core.book.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

@Entity
@Table(name = "KEYWORD_TYPE_CODES")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(namespace = "ebookGenerator/com/thomsonreuters/uscl/ereader/core/book/domain", name = "KeywordTypeCode")
public class KeywordTypeCode implements Serializable, Comparable<KeywordTypeCode> {
    //private static final Logger log = LogManager.getLogger(KeywordTypeCode.class);
    private static final long serialVersionUID = -6883749966331206015L;

    @Id
    @Column(name = "KEYWORD_TYPE_CODES_ID", unique = true, nullable = false)
    @SequenceGenerator(name = "keywordTypeCodesIdSequence", sequenceName = "KEYWORD_TYPE_CODES_ID_SEQ")
    @GeneratedValue(generator = "keywordTypeCodesIdSequence")
    private Long id;

    @Column(name = "KEYWORD_TYPE_CODES_NAME", nullable = false, length = 1024)
    private String name;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "LAST_UPDATED", nullable = false)
    private Date lastUpdatedTimeStampForKeyWordCode;

    @Column(name = "IS_REQUIRED", nullable = false, length = 1)
    private String isRequired;

    @OneToMany(mappedBy = "keywordTypeCode", fetch = FetchType.EAGER)
    @Fetch(FetchMode.SELECT)
    @Cascade(CascadeType.ALL)
    private Collection<KeywordTypeValue> values;

    public KeywordTypeCode() {
        super();
        values = new ArrayList<>();
    }

    public Long getId() {
        return id;
    }

    public Date getLastUpdated() {
        return lastUpdatedTimeStampForKeyWordCode;
    }

    public String getName() {
        return name;
    }

    public boolean getIsRequired() {
        return ((isRequired.equalsIgnoreCase("Y") ? true : false));
    }

    public Collection<KeywordTypeValue> getValues() {
        return values;
    }

    public void setId(final Long id) {
        this.id = id;
    }

    public void setLastUpdated(final Date lastUpdated) {
        lastUpdatedTimeStampForKeyWordCode = lastUpdated;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public void setIsRequired(final boolean isRequired) {
        this.isRequired = ((isRequired) ? "Y" : "N");
    }

    public void setValues(final Collection<KeywordTypeValue> values) {
        this.values = values;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        final KeywordTypeCode other = (KeywordTypeCode) obj;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        if (lastUpdatedTimeStampForKeyWordCode == null) {
            if (other.lastUpdatedTimeStampForKeyWordCode != null)
                return false;
        } else if (!lastUpdatedTimeStampForKeyWordCode.equals(other.lastUpdatedTimeStampForKeyWordCode))
            return false;
        if (name == null) {
            if (other.name != null)
                return false;
        } else if (!name.equals(other.name))
            return false;
        return true;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        result = prime * result
            + ((lastUpdatedTimeStampForKeyWordCode == null) ? 0 : lastUpdatedTimeStampForKeyWordCode.hashCode());
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        return result;
    }

    @Override
    public String toString() {
        return ReflectionToStringBuilder.toString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }

    @Override
    public int compareTo(final KeywordTypeCode arg0) {
        int result = 0;
        if (name != null) {
            result = (arg0 != null) ? name.compareTo(arg0.getName()) : 1;
        } else { // str1 is null
            result = (arg0 != null) ? -1 : 0;
        }
        return result;
    }
}
