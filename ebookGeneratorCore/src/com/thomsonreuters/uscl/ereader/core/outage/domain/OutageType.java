package com.thomsonreuters.uscl.ereader.core.outage.domain;

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

@Entity
@Table(name = "OUTAGE_TYPE")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(namespace = "ebookGenerator/com/thomsonreuters/uscl/ereader/core/outage/domain", name = "OutageType")
public class OutageType implements Serializable
{
    private static final long serialVersionUID = 1L;

    @Column(name = "OUTAGE_TYPE_ID", nullable = false)
    @Id
    @SequenceGenerator(name = "outageTypeIdSequence", sequenceName = "OUTAGE_TYPE_ID_SEQ")
    @GeneratedValue(generator = "outageTypeIdSequence")
    private Long id;

    @Column(name = "SYSTEM", nullable = false, length = 128)
    private String system;

    @Column(name = "SUB_SYSTEM", nullable = false, length = 128)
    private String subSystem;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "LAST_UPDATED", nullable = false)
    private Date lastUpdated;

    public Long getId()
    {
        return id;
    }

    public void setId(final Long id)
    {
        this.id = id;
    }

    public String getSystem()
    {
        return system;
    }

    public void setSystem(final String system)
    {
        this.system = system;
    }

    public String getSubSystem()
    {
        return subSystem;
    }

    public void setSubSystem(final String subSystem)
    {
        this.subSystem = subSystem;
    }

    public Date getLastUpdated()
    {
        return lastUpdated;
    }

    public void setLastUpdated(final Date lastUpdated)
    {
        this.lastUpdated = lastUpdated;
    }

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        return result;
    }

    /*
     * A more extensive .equals method is nested in the outageEquals(..) method
     * in JAXBMarshallingTest.java
     */
    @Override
    public boolean equals(final Object obj)
    {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        final OutageType other = (OutageType) obj;
        if (id == null)
        {
            if (other.id != null)
                return false;
        }
        else if (!id.equals(other.id))
            return false;
        return true;
    }
}
