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

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * A DocumentType code database table entity.
 * Represents all the DocumentType code id and names used for Book Definition
 */
@Getter
@Setter
@EqualsAndHashCode(of = {"abbreviation", "usePublishCutoffDateFlag", "id", "lastUpdatedTimeStampForDocType", "name"})
@ToString

@Entity
@Table(name = "DOCUMENT_TYPE_CODES")
public class DocumentTypeCode implements Serializable {
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
    private Date lastUpdatedDocTypeCode;

    public boolean getUsePublishCutoffDateFlag() {
        return "Y".equalsIgnoreCase(usePublishCutoffDateFlag);
    }

    public void setUsePublishCutoffDateFlag(final boolean usePublishCutoffDateFlag) {
        this.usePublishCutoffDateFlag = usePublishCutoffDateFlag ? "Y" : "N";
    }
}
