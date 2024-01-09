package com.thomsonreuters.uscl.ereader.core.book.statecode;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
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

/**
 * A state code database table entity.
 * Represents all the state code id and names used for Book Definition
 */
@Entity
@Table(name = "STATE_CODES")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(namespace = "ebookGenerator/com/thomsonreuters/uscl/ereader/core/book/domain", name = "StateCode")
public class StateCode implements Serializable {
    private static final long serialVersionUID = -6419698127062095582L;

    @Id
    @Column(name = "STATE_CODES_ID")
    @SequenceGenerator(name = "stateCodesIdSequence", sequenceName = "STATE_CODES_ID_SEQ")
    @GeneratedValue(generator = "stateCodesIdSequence")
    private Long id;

    @Column(name = "STATE_CODES_NAME", nullable = false, length = 1024)
    private String name;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "LAST_UPDATED", nullable = false)
    private Date lastUpdatedTimeStampForStateCode;

    public StateCode() {
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
        return lastUpdatedTimeStampForStateCode;
    }

    public void setLastUpdated(final Date lastUpdated) {
        lastUpdatedTimeStampForStateCode = lastUpdated;
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
            + ((lastUpdatedTimeStampForStateCode == null) ? 0 : lastUpdatedTimeStampForStateCode.hashCode());
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
        final StateCode other = (StateCode) obj;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        if (lastUpdatedTimeStampForStateCode == null) {
            if (other.lastUpdatedTimeStampForStateCode != null)
                return false;
        } else if (!lastUpdatedTimeStampForStateCode.equals(other.lastUpdatedTimeStampForStateCode))
            return false;
        if (name == null) {
            if (other.name != null)
                return false;
        } else if (!name.equals(other.name))
            return false;
        return true;
    }
}
