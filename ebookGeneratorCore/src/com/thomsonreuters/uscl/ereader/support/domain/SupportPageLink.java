package com.thomsonreuters.uscl.ereader.support.domain;

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

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Table(name = "SUPPORT_PAGE_LINK")
@EntityListeners(AuditingEntityListener.class)
public class SupportPageLink implements Serializable {
    //private static Logger log = LogManager.getLogger(SupportPageLink.class);
    private static final long serialVersionUID = 1L;

    @Column(name = "SUPPORT_LINK_ID", nullable = false)
    @Id
    @GeneratedValue(generator = "SupportPageLinkSequence")
    @SequenceGenerator(name = "SupportPageLinkSequence", sequenceName = "SUPPORT_LINK_ID_SEQ")
    private Long id;

    @Column(name = "LINK_DESCRIPTION", length = 512, nullable = false)
    private String linkDescription;

    @Column(name = "LINK_ADDRESS", length = 1024, nullable = false)
    private String linkAddress;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "LAST_UPDATED", nullable = false)
    @LastModifiedDate
    private Date lastUpdated;

    public Long getId() {
        return id;
    }

    public void setId(final Long id) {
        this.id = id;
    }

    public String getLinkDescription() {
        return linkDescription;
    }

    public void setLinkDescription(final String linkDescription) {
        this.linkDescription = linkDescription;
    }

    public String getLinkAddress() {
        return linkAddress;
    }

    public void setLinkAddress(final String linkAddress) {
        this.linkAddress = linkAddress;
    }

    public Date getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(final Date lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        result = prime * result + ((lastUpdated == null) ? 0 : lastUpdated.hashCode());
        result = prime * result + ((linkAddress == null) ? 0 : linkAddress.hashCode());
        result = prime * result + ((linkDescription == null) ? 0 : linkDescription.hashCode());
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
        final SupportPageLink other = (SupportPageLink) obj;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        if (lastUpdated == null) {
            if (other.lastUpdated != null)
                return false;
        } else if (!lastUpdated.equals(other.lastUpdated))
            return false;
        if (linkAddress == null) {
            if (other.linkAddress != null)
                return false;
        } else if (!linkAddress.equals(other.linkAddress))
            return false;
        if (linkDescription == null) {
            if (other.linkDescription != null)
                return false;
        } else if (!linkDescription.equals(other.linkDescription))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SIMPLE_STYLE);
    }
}
