package com.thomsonreuters.uscl.ereader.gather.metadata.domain;

import java.io.Serializable;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

/**
 * @author <a href="mailto:ravi.nandikolla@thomsonreuters.com">Ravi Nandikolla</a> c139353
 *
 */
@Entity
@Table(name = "PACE_METADATA")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(namespace = "ebookGenerator/com/thomsonreuters/uscl/ereader/gather/metadata/domain", name = "PaceMetadata")
public class PaceMetadata implements Serializable {
    private static final long serialVersionUID = 1L;
    private Long publicationId;
    private Long publicationCode;
    private String primaryCategory;
    private String secondaryCategory;
    private String specificCategory;
    private String authorityName;
    private String publicationName;
    private String westPubFlag;
    private String stdPubName;
    private String longPubName;
    private String type;
    private Long auditId;
    private String active;

    /**
     *
     * @return the active
     */
    @Column(name = "ACTIVE", length = 64)
    @Basic(fetch = FetchType.EAGER)
    @XmlElement
    public String getActive() {
        return active;
    }

    /**
     *
     * @return the auditId
     */
    @Column(name = "AUDIT_ID")
    @Basic(fetch = FetchType.EAGER)
    @XmlElement
    public Long getAuditId() {
        return auditId;
    }

    /**
     *
     * @return the authorityName
     */
    @Column(name = "AUTHORITY_NAME", length = 100)
    @Basic(fetch = FetchType.EAGER)
    @XmlElement
    public String getAuthorityName() {
        return authorityName;
    }

    /**
     *
     * @return the longPubName
     */
    @Column(name = "LONG_PUB_NAME", length = 500)
    @Basic(fetch = FetchType.EAGER)
    @XmlElement
    public String getLongPubName() {
        return longPubName;
    }

    /**
     *
     * @return the primaryCategory
     */
    @Column(name = "PRIMARY_CATEGORY", length = 10)
    @Basic(fetch = FetchType.EAGER)
    @XmlElement
    public String getPrimaryCategory() {
        return primaryCategory;
    }

    /**
     *
     * @return the publicationCode
     */
    @Column(name = "PUBLICATION_CODE", nullable = false)
    @Basic(fetch = FetchType.EAGER)
    @XmlElement
    public Long getPublicationCode() {
        return publicationCode;
    }

    /**
     *
     * @return the publicationId
     */
    @Column(name = "PUBLICATION_ID", nullable = false)
    @Basic(fetch = FetchType.EAGER)
    @Id
    @XmlElement
    public Long getPublicationId() {
        return publicationId;
    }

    /**
     *
     * @return the publicationName
     */
    @Column(name = "PUBLICATION_NAME", length = 255, nullable = false)
    @Basic(fetch = FetchType.EAGER)
    @XmlElement
    public String getPublicationName() {
        return publicationName;
    }

    /**
     *
     * @return the secondaryCategory
     */
    @Column(name = "SECONDARY_CATEGORY", length = 64)
    @Basic(fetch = FetchType.EAGER)
    @XmlElement
    public String getSecondaryCategory() {
        return secondaryCategory;
    }

    /**
     *
     * @return the specificCategory
     */
    @Column(name = "SPECIFIC_CATEGORY", length = 64)
    @Basic(fetch = FetchType.EAGER)
    @XmlElement
    public String getSpecificCategory() {
        return specificCategory;
    }

    /**
     *
     * @return the stdPubName
     */
    @Column(name = "STD_PUB_NAME", length = 255)
    @Basic(fetch = FetchType.EAGER)
    @XmlElement
    public String getStdPubName() {
        return stdPubName;
    }

    /**
     *
     * @return the type
     */
    @Column(name = "TYPE", length = 64)
    @Basic(fetch = FetchType.EAGER)
    @XmlElement
    public String getType() {
        return type;
    }

    /**
     *
     * @return the westPubFlag
     */
    @Column(name = "WESTPUB_FLAG", length = 64)
    @Basic(fetch = FetchType.EAGER)
    @XmlElement
    public String getWestPubFlag() {
        return westPubFlag;
    }

    /**
     *
     * @param active the active to set
     */
    public void setActive(final String active) {
        this.active = active;
    }

    /**
     *
     * @param auditId the auditId to set
     */
    public void setAuditId(final Long auditId) {
        this.auditId = auditId;
    }

    /**
     *
     * @param authorityName the authorityName to set
     */
    public void setAuthorityName(final String authorityName) {
        this.authorityName = authorityName;
    }

    /**
     *
     * @param longPubName the longPubName to set
     */
    public void setLongPubName(final String longPubName) {
        this.longPubName = longPubName;
    }

    /**
     *
     * @param primaryCategory the primaryCategory to set
     */
    public void setPrimaryCategory(final String primaryCategory) {
        this.primaryCategory = primaryCategory;
    }

    /**
     *
     * @param publicationCode the publicationCode to set
     */
    public void setPublicationCode(final Long publicationCode) {
        this.publicationCode = publicationCode;
    }

    /**
     *
     * @param publicationId the publicationId to set
     */
    public void setPublicationId(final Long publicationId) {
        this.publicationId = publicationId;
    }

    /**
     *
     * @param publicationName the publicationName to set
     */
    public void setPublicationName(final String publicationName) {
        this.publicationName = publicationName;
    }

    /**
     *
     * @param secondaryCategory the secondaryCategory to set
     */
    public void setSecondaryCategory(final String secondaryCategory) {
        this.secondaryCategory = secondaryCategory;
    }

    /**
     *
     * @param specificCategory the specificCategory to set
     */
    public void setSpecificCategory(final String specificCategory) {
        this.specificCategory = specificCategory;
    }

    /**
     *
     * @param stdPubName the stdPubName to set
     */
    public void setStdPubName(final String stdPubName) {
        this.stdPubName = stdPubName;
    }

    /**
     *
     * @param type the type to set
     */
    public void setType(final String type) {
        this.type = type;
    }

    /**
     *
     * @param westPubFlag the westPubFlag to set
     */
    public void setWestPubFlag(final String westPubFlag) {
        this.westPubFlag = westPubFlag;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((publicationId == null) ? 0 : publicationId.hashCode());
        return result;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(final Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        final PaceMetadata other = (PaceMetadata) obj;

        if (publicationId == null) {
            if (other.publicationId != null)
                return false;
        } else if (!publicationId.equals(other.publicationId)) {
            return false;
        }

        return true;
    }
}
