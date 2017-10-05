package com.thomsonreuters.uscl.ereader.core.book.domain;

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

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * A DocumentType code database table entity.
 * Represents all the DocumentType code id and names used for Book Definition
 */
@Entity
@Table(name = "DOCUMENT_TYPE_CODES")
public class DocumentTypeCode implements Serializable {
    //private static final Logger log = LogManager.getLogger(DocumentTypeCode.class);
    private static final long serialVersionUID = -401472676661960713L;
    public static final Long ANALYTICAL = Long.valueOf(1);

    @Id
    @Column(name = "DOCUMENT_TYPE_CODES_ID")
    @SequenceGenerator(name = "documentTypeCodesIdSequence", sequenceName = "DOC_TYPE_CODES_ID_SEQ")
    @GeneratedValue(generator = "documentTypeCodesIdSequence")
    private Long id;

    @Column(name = "DOCUMENT_TYPE_CODES_NAME", nullable = false, length = 1024)
    private String name;

    @Column(name = "DOCUMENT_TYPE_CODES_ABBRV", nullable = false, length = 32)
    private String abbreviation;

    @Column(name = "USE_PUBLISH_CUTOFF_DATE_FLAG", nullable = false, length = 1)
    private String usePublishCutoffDateFlag;

    @Column(name = "THRESHOLD_VALUE")
    private Integer thresholdValue;

    @Column(name = "THRESHOLD_PERCENT")
    private Integer thresholdPercent;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "LAST_UPDATED", nullable = false)
    private Date lastUpdatedTimeStampForDocType;

    public DocumentTypeCode() {
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

    public String getAbbreviation() {
        return abbreviation;
    }

    public void setAbbreviation(final String abbreviation) {
        this.abbreviation = abbreviation;
    }

    public boolean getUsePublishCutoffDateFlag() {
        return (usePublishCutoffDateFlag.equalsIgnoreCase("Y") ? true : false);
    }

    public void setUsePublishCutoffDateFlag(final boolean usePublishCutoffDateFlag) {
        this.usePublishCutoffDateFlag = ((usePublishCutoffDateFlag) ? "Y" : "N");
    }

    public Date getLastUpdated() {
        return lastUpdatedTimeStampForDocType;
    }

    public void setLastUpdated(final Date lastUpdated) {
        lastUpdatedTimeStampForDocType = lastUpdated;
    }

    public Integer getThresholdValue() {
        return thresholdValue;
    }

    public void setThresholdValue(final Integer thresholdValue) {
        this.thresholdValue = thresholdValue;
    }

    public Integer getThresholdPercent() {
        return thresholdPercent;
    }

    public void setThresholdPercent(final Integer thresholdPercent) {
        this.thresholdPercent = thresholdPercent;
    }

    @Override
    public String toString() {
        return ReflectionToStringBuilder.toString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((abbreviation == null) ? 0 : abbreviation.hashCode());
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        result =
            prime * result + ((lastUpdatedTimeStampForDocType == null) ? 0 : lastUpdatedTimeStampForDocType.hashCode());
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
        final DocumentTypeCode other = (DocumentTypeCode) obj;
        if (abbreviation == null) {
            if (other.abbreviation != null)
                return false;
        } else if (!abbreviation.equals(other.abbreviation))
            return false;
        if (usePublishCutoffDateFlag == null) {
            if (other.usePublishCutoffDateFlag != null)
                return false;
        } else if (!usePublishCutoffDateFlag.equals(other.usePublishCutoffDateFlag))
            return false;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        if (lastUpdatedTimeStampForDocType == null) {
            if (other.lastUpdatedTimeStampForDocType != null)
                return false;
        } else if (!lastUpdatedTimeStampForDocType.equals(other.lastUpdatedTimeStampForDocType))
            return false;
        if (name == null) {
            if (other.name != null)
                return false;
        } else if (!name.equals(other.name))
            return false;
        return true;
    }
}
