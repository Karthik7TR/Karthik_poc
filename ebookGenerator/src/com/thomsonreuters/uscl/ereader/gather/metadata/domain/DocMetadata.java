package com.thomsonreuters.uscl.ereader.gather.metadata.domain;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 */
@IdClass(com.thomsonreuters.uscl.ereader.gather.metadata.domain.DocMetadataPK.class)
@NamedQueries({
    @NamedQuery(
        name = "findDocumentMetaDataByCiteAndJobId",
        query = "select docM from DocMetadata docM where docM.jobInstanceId = :jobInstaneId "
            + "and docM.normalizedFirstlineCite like :normalizedCite ")})
@Entity
@Table(name = "DOCUMENT_METADATA")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(namespace = "ebookGenerator/com/thomsonreuters/uscl/ereader/gather/metadata/domain", name = "DocMetadata")
public class DocMetadata implements Serializable {
    private static final long serialVersionUID = 1L;
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");

    @Column(name = "TITLE_ID", length = 64, nullable = false)
    @Basic(fetch = FetchType.EAGER)
    @Id
    @XmlElement
    private String titleId;

    @Column(name = "JOB_INSTANCE_ID", nullable = false)
    @Basic(fetch = FetchType.EAGER)
    @Id
    @XmlElement
    private Long jobInstanceId;

    @Column(name = "DOC_UUID", length = 42, nullable = false)
    @Basic(fetch = FetchType.EAGER)
    @Id
    @XmlElement
    private String docUuid;

    @Column(name = "DOC_FAMILY_UUID", length = 36, nullable = false)
    @Basic(fetch = FetchType.EAGER)
    @XmlElement
    private String docFamilyUuid;

    @Column(name = "DOC_TYPE", length = 10)
    @Basic(fetch = FetchType.EAGER)
    @XmlElement
    private String docType;

    @Column(name = "NORMALIZED_FIRSTLINE_CITE", length = 256)
    @Basic(fetch = FetchType.EAGER)
    @XmlElement
    private String normalizedFirstlineCite;

    @Column(name = "FIRSTLINE_CITE", length = 128)
    @Basic(fetch = FetchType.EAGER)
    @XmlElement
    private String firstlineCite;

    @Column(name = "SECONDLINE_CITE", length = 128)
    @Basic(fetch = FetchType.EAGER)
    @XmlElement
    private String secondlineCite;

    @Column(name = "THIRDLINE_CITE", length = 512)
    @Basic(fetch = FetchType.EAGER)
    @XmlElement
    private String thirdlineCite;

    @Column(name = "FIRSTLINE_CITE_PUBPAGE", length = 512)
    @Basic(fetch = FetchType.EAGER)
    @XmlElement
    private String firstlineCitePubpage;

    @Column(name = "FIRSTLINE_CITE_PUB_ID")
    @Basic(fetch = FetchType.EAGER)
    @XmlElement
    private Long firstlineCitePubId;

    @Column(name = "SECONDLINE_CITE_PUB_ID")
    @Basic(fetch = FetchType.EAGER)
    @XmlElement
    private Long secondlineCitePubId;

    @Column(name = "THIRDLINE_CITE_PUB_ID")
    @Basic(fetch = FetchType.EAGER)
    @XmlElement
    private Long thirdlineCitePubId;

    @Column(name = "FIND_ORIG", length = 256)
    @Basic(fetch = FetchType.EAGER)
    @XmlElement
    private String findOrig;

    @Column(name = "SERIAL_NUMBER")
    @Basic(fetch = FetchType.EAGER)
    @XmlElement
    private Long serialNumber;

    @Column(name = "COLLECTION_NAME", length = 36, nullable = false)
    @Basic(fetch = FetchType.EAGER)
    @XmlElement
    private String collectionName;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "LAST_UPDATED", nullable = false)
    @Basic(fetch = FetchType.EAGER)
    @XmlElement
    private Date lastUpdated;

    @Column(name = "PROVIEW_FAMILY_UUID_DEDUP")
    @Basic(fetch = FetchType.EAGER)
    private Integer proviewFamilyUUIDDedup;

    @Column(name = "DOC_SIZE")
    @Basic(fetch = FetchType.EAGER)
    private Long docSize;

    @Column(name = "SPLIT_BOOK_TITLE_ID", length = 64)
    @Basic(fetch = FetchType.EAGER)
    private String splitBookTitleId;

    @Column(name = "START_EFFECTIVE_DATE")
    @XmlElement
    private String startEffectiveDate;

    @Column(name = "END_EFFECTIVE_DATE")
    @XmlElement
    private String endEffectiveDate;

    @Getter
    @Setter
    @Column(name = "CURRENCY_DEFAULT")
    @XmlElement
    private String currencyDefault;

    @Getter
    @Setter
    @OneToMany(mappedBy = "docMetadata", orphanRemoval = true, cascade = CascadeType.ALL)
    @Basic(fetch = FetchType.LAZY)
    @XmlElement
    private List<CanadianDigest> canadianDigests = new ArrayList<>();

    @Getter
    @Setter
    @OneToMany(mappedBy = "docMetadata", orphanRemoval = true, cascade = CascadeType.ALL)
    @Basic(fetch = FetchType.LAZY)
    @XmlElement
    private List<CanadianTopicCode> canadianTopicCodes = new ArrayList<>();

    public DocMetadata() {
    }

    public void setTitleId(final String titleId) {
        this.titleId = titleId;
    }

    public String getTitleId() {
        return titleId;
    }

    public void setJobInstanceId(final Long jobInstanceId) {
        this.jobInstanceId = jobInstanceId;
    }

    public Long getJobInstanceId() {
        return jobInstanceId;
    }

    public void setDocUuid(final String docUuid) {
        this.docUuid = docUuid;
    }

    public String getDocUuid() {
        return docUuid;
    }

    public void setDocFamilyUuid(final String docFamilyUuid) {
        this.docFamilyUuid = docFamilyUuid;
    }

    public String getDocFamilyUuid() {
        return docFamilyUuid;
    }

    public void setDocType(final String docType) {
        this.docType = docType;
    }

    public String getDocType() {
        return docType;
    }

    public void setNormalizedFirstlineCite(final String normalizedFirstlineCite) {
        this.normalizedFirstlineCite = normalizedFirstlineCite;
    }

    public String getNormalizedFirstlineCite() {
        return normalizedFirstlineCite;
    }

    public String getFirstlineCite() {
        return firstlineCite;
    }

    public void setFirstlineCite(final String firstlineCite) {
        this.firstlineCite = firstlineCite;
    }

    public String getSecondlineCite() {
        return secondlineCite;
    }

    public void setSecondlineCite(final String secondlineCite) {
        this.secondlineCite = secondlineCite;
    }

    public String getThirdlineCite() {
        return thirdlineCite;
    }

    public void setThirdlineCite(final String thirdlineCite) {
        this.thirdlineCite = thirdlineCite;
    }

    public String getFirstlineCitePubpage() {
        return firstlineCitePubpage;
    }

    public void setFirstlineCitePubpage(final String firstlineCitePubpage) {
        this.firstlineCitePubpage = firstlineCitePubpage;
    }

    public Long getFirstlineCitePubId() {
        return firstlineCitePubId;
    }

    public void setFirstlineCitePubId(final Long firstlineCitePubId) {
        this.firstlineCitePubId = firstlineCitePubId;
    }

    public Long getSecondlineCitePubId() {
        return secondlineCitePubId;
    }

    public void setSecondlineCitePubId(final Long secondlineCitePubId) {
        this.secondlineCitePubId = secondlineCitePubId;
    }

    public Long getThirdlineCitePubId() {
        return thirdlineCitePubId;
    }

    public void setThirdlineCitePubId(final Long thirdlineCitePubId) {
        this.thirdlineCitePubId = thirdlineCitePubId;
    }

    public void setFindOrig(final String findOrig) {
        this.findOrig = findOrig;
    }

    public String getFindOrig() {
        return findOrig;
    }

    public void setSerialNumber(final Long serialNumber) {
        this.serialNumber = serialNumber;
    }

    public Long getSerialNumber() {
        return serialNumber;
    }

    public void setCollectionName(final String collectionName) {
        this.collectionName = collectionName;
    }

    public String getCollectionName() {
        return collectionName;
    }

    public void setLastUpdated(final Date lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    public Date getLastUpdated() {
        return lastUpdated;
    }

    public Integer getProviewFamilyUUIDDedup() {
        return proviewFamilyUUIDDedup;
    }

    public void setProviewFamilyUUIDDedup(final Integer proviewFamilyUUIDDedup) {
        this.proviewFamilyUUIDDedup = proviewFamilyUUIDDedup;
    }

    public Long getDocSize() {
        return docSize;
    }

    public void setDocSize(final Long docSize) {
        this.docSize = docSize;
    }

    public String getSplitBookTitle() {
        return splitBookTitleId;
    }

    public void setSpitBookTitle(final String splitBookTitle) {
        splitBookTitleId = splitBookTitle;
    }

    public String getStartEffectiveDate() {
        return startEffectiveDate;
    }

    public void setStartEffectiveDate(final String startEffectiveDate) {
        this.startEffectiveDate = startEffectiveDate;
    }

    public String getEndEffectiveDate() {
        return endEffectiveDate;
    }

    public void setEndEffectiveDate(final String endEffectiveDate) {
        this.endEffectiveDate = endEffectiveDate;
    }

    public void addDigest(final CanadianDigest digest) {
        digest.setDocMetadata(this);
        canadianDigests.add(digest);
    }
    /**
     * Returns unique id for each document, in most cases this will be the Document Family GUID but
     * in some cases it will be a deduped Document Family GUID with the dedup value appended after the
     * Document Family GUID.
     *
     * @return unique identifier that will be used by ProView for this document
     */
    public String getProViewId() {
        if (docFamilyUuid != null && proviewFamilyUUIDDedup != null) {
            return String.join("_", docFamilyUuid, proviewFamilyUUIDDedup.toString());
        }
        return docFamilyUuid;
    }

    public boolean isDocumentEffective() {
        final LocalDateTime now = LocalDateTime.now();
        return startEffectiveDate != null
            && endEffectiveDate != null
            && now.isAfter(LocalDateTime.parse(startEffectiveDate, FORMATTER))
            && now.isBefore(LocalDateTime.parse(endEffectiveDate, FORMATTER));
    }

    public boolean isFirstSplitTitle() {
        return getSplitBookTitle() == null || getSplitBookTitle().endsWith(getTitleId());
    }

    public void addTopicCode(final CanadianTopicCode canadianTopicCode) {
        canadianTopicCode.setDocMetadata(this);
        canadianTopicCodes.add(canadianTopicCode);
    }

    /**
     * Returns a textual representation of a bean.
     *
     */
    @Override
    public String toString() {
        final StringBuilder buffer = new StringBuilder();

        buffer.append("titleId=[").append(titleId).append("] ");
        buffer.append("jobInstanceId=[").append(jobInstanceId).append("] ");
        buffer.append("docUuid=[").append(docUuid).append("] ");
        buffer.append("docFamilyUuid=[").append(docFamilyUuid).append("] ");
        buffer.append("docType=[").append(docType).append("] ");
        buffer.append("normalizedFirstlineCite=[").append(normalizedFirstlineCite).append("] ");
        buffer.append("FirstlineCite=[").append(firstlineCite).append("] ");
        buffer.append("firstlineCitePubpage=[").append(firstlineCitePubpage).append("] ");
        buffer.append("findOrig=[").append(findOrig).append("] ");
        buffer.append("serialNumber=[").append(serialNumber).append("] ");
        buffer.append("collectionName=[").append(collectionName).append("] ");
        buffer.append("lastUpdated=[").append(lastUpdated).append("] ");
        buffer.append("proviewFamilyUUIDDedup=[").append(proviewFamilyUUIDDedup).append("] ");

        return buffer.toString();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder()
        .append(titleId)
        .append(jobInstanceId)
        .append(docUuid)
        .toHashCode();
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof DocMetadata)) {
            return false;
        }
        final DocMetadata equalCheck = (DocMetadata) obj;
        return new EqualsBuilder().append(titleId, equalCheck.titleId)
        .append(jobInstanceId, equalCheck.jobInstanceId)
        .append(docUuid, equalCheck.docUuid)
        .isEquals();
    }
}
