package com.thomsonreuters.uscl.ereader.core.book.domain;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

/**
 * A Pub Type Code database table entity.
 * Represents all the Pub Type Code id and names used for Book Definition
 */
@Entity
@Table(name = "PUB_TYPE_CODES")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(namespace = "ebookGenerator/com/thomsonreuters/uscl/ereader/core/book/domain", name = "PubTypeCode")
@EntityListeners(AuditingEntityListener.class)
public class PubTypeCode implements Serializable {
    //private static final Logger log = LogManager.getLogger(PubTypeCode.class);
    private static final long serialVersionUID = -4932734236359244870L;

    @Id
    @Column(name = "PUB_TYPE_CODES_ID")
    @SequenceGenerator(name = "pubTypeCodesIdSequence", sequenceName = "PUB_TYPE_CODES_ID_SEQ")
    @GeneratedValue(generator = "pubTypeCodesIdSequence")
    private Long id;

    @Column(name = "PUB_TYPE_CODES_NAME", nullable = false, length = 1024)
    private String name;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "LAST_UPDATED", nullable = false)
    @LastModifiedDate
    private Date lastUpdatedTimeStampForPubTypeCode;

    public PubTypeCode() {
        super();
    }

    public Long getId() {
        return id;
    }

    public void setId(final Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public Date getLastUpdated() {
        return lastUpdatedTimeStampForPubTypeCode;
    }

    public void setLastUpdated(final Date lastUpdated) {
        lastUpdatedTimeStampForPubTypeCode = lastUpdated;
    }

    @Override
    public String toString() {
        return ReflectionToStringBuilder.toString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        result = prime * result
            + ((lastUpdatedTimeStampForPubTypeCode == null) ? 0 : lastUpdatedTimeStampForPubTypeCode.hashCode());
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        return result;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        final PubTypeCode other = (PubTypeCode) obj;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        if (lastUpdatedTimeStampForPubTypeCode == null) {
            if (other.lastUpdatedTimeStampForPubTypeCode != null)
                return false;
        } else if (!lastUpdatedTimeStampForPubTypeCode.equals(other.lastUpdatedTimeStampForPubTypeCode))
            return false;
        if (name == null) {
            if (other.name != null)
                return false;
        } else if (!name.equals(other.name))
            return false;
        return true;
    }
}
