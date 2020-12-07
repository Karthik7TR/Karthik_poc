package com.thomsonreuters.uscl.ereader.stats.domain;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
import javax.persistence.NamedNativeQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import com.thomsonreuters.uscl.ereader.core.book.domain.EbookAudit;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

@IdClass(com.thomsonreuters.uscl.ereader.stats.domain.PublishingStatsPK.class)
@Entity
@NamedNativeQuery(name = "getSysdate", query = "SELECT SYSDATE FROM DUAL")
@Table(name = "PUBLISHING_STATS")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(namespace = "ebookGenerator/com/thomsonreuters/uscl/ereader/core/job/domain", name = "PublishingStats")
public class PublishingStats implements Serializable, Comparable<PublishingStats> {
    private static final long serialVersionUID = 1L;

    public static final String SUCCESFULL_PUBLISH_STATUS = "Publish Step Completed";
    public static final String SEND_EMAIL_COMPLETE = "sendEmailNotification : Completed";
    public static final String SEND_EMAIL_COMPLETE_XPP = "sendEmailNotificationXppStep : COMPLETED";

    @Column(name = "JOB_INSTANCE_ID", nullable = false)
    @Basic(fetch = FetchType.EAGER)
    @Id
    @XmlElement
    private Long jobInstanceId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumns({@JoinColumn(name = "AUDIT_ID", referencedColumnName = "AUDIT_ID", nullable = false)})
    @XmlElement
    private EbookAudit audit;

    @Column(name = "EBOOK_DEFINITION_ID", nullable = false)
    @Basic(fetch = FetchType.EAGER)
    @XmlElement
    private Long ebookDefId;

    @Column(name = "JOB_SUBMITTER_NAME", length = 1024)
    @Basic(fetch = FetchType.EAGER)
    @XmlElement
    private String jobSubmitterName;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "JOB_SUBMIT_TIMESTAMP", nullable = false)
    @Basic(fetch = FetchType.EAGER)
    @XmlElement
    private Date jobSubmitTimestamp;

    @Column(name = "BOOK_VERSION_SUBMITTED")
    @Basic(fetch = FetchType.EAGER)
    @XmlElement
    private String bookVersionSubmitted;

    @Column(name = "JOB_HOST_NAME", length = 1024)
    @Basic(fetch = FetchType.EAGER)
    @XmlElement
    private String jobHostName;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "PUBLISH_START_TIMESTAMP", nullable = false)
    @Basic(fetch = FetchType.EAGER)
    @XmlElement
    private Date publishStartTimestamp;

    @Column(name = "GATHER_TOC_NODE_COUNT")
    @Basic(fetch = FetchType.EAGER)
    @XmlElement
    private Integer gatherTocNodeCount;

    @Column(name = "GATHER_TOC_SKIPPED_COUNT")
    @Basic(fetch = FetchType.EAGER)
    @XmlElement
    private Integer gatherTocSkippedCount;

    @Column(name = "GATHER_TOC_DOC_COUNT")
    @Basic(fetch = FetchType.EAGER)
    @XmlElement
    private Integer gatherTocDocCount;

    @Column(name = "GATHER_TOC_RETRY_COUNT")
    @Basic(fetch = FetchType.EAGER)
    @XmlElement
    private Integer gatherTocRetryCount;

    @Column(name = "GATHER_DOC_EXPECTED_COUNT")
    @Basic(fetch = FetchType.EAGER)
    @XmlElement
    private Integer gatherDocExpectedCount;

    @Column(name = "GATHER_DOC_RETRY_COUNT")
    @Basic(fetch = FetchType.EAGER)
    @XmlElement
    private Integer gatherDocRetryCount;

    @Column(name = "GATHER_DOC_RETRIEVED_COUNT")
    @Basic(fetch = FetchType.EAGER)
    @XmlElement
    private Integer gatherDocRetrievedCount;

    @Column(name = "GATHER_META_EXPECTED_COUNT")
    @Basic(fetch = FetchType.EAGER)
    @XmlElement
    private Integer gatherMetaExpectedCount;

    @Column(name = "GATHER_META_RETRY_COUNT")
    @Basic(fetch = FetchType.EAGER)
    @XmlElement
    private Integer gatherMetaRetryCount;

    @Column(name = "GATHER_META_RETRIEVED_COUNT")
    @Basic(fetch = FetchType.EAGER)
    @XmlElement
    private Integer gatherMetaRetrievedCount;

    @Column(name = "GATHER_IMAGE_EXPECTED_COUNT")
    @Basic(fetch = FetchType.EAGER)
    @XmlElement
    private Integer gatherImageExpectedCount;

    @Column(name = "GATHER_IMAGE_RETRY_COUNT")
    @Basic(fetch = FetchType.EAGER)
    @XmlElement
    private Integer gatherImageRetryCount;

    @Column(name = "GATHER_IMAGE_RETRIEVED_COUNT")
    @Basic(fetch = FetchType.EAGER)
    @XmlElement
    private Integer gatherImageRetrievedCount;

    @Column(name = "FORMAT_DOC_COUNT")
    @Basic(fetch = FetchType.EAGER)
    @XmlElement
    private Integer formatDocCount;

    @Column(name = "ASSEMBLE_DOC_COUNT")
    @Basic(fetch = FetchType.EAGER)
    @XmlElement
    private Integer assembleDocCount;

    @Column(name = "TITLE_DOC_COUNT")
    @Basic(fetch = FetchType.EAGER)
    @XmlElement
    private Integer titleDocCount;

    @Column(name = "TITLE_DUP_DOC_COUNT")
    @Basic(fetch = FetchType.EAGER)
    @XmlElement
    private Integer titleDupDocCount;

    @Column(name = "PUBLISH_STATUS", length = 1024)
    @Basic(fetch = FetchType.EAGER)
    @XmlElement
    private String publishStatus;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "PUBLISH_END_TIMESTAMP")
    @Basic(fetch = FetchType.EAGER)
    @XmlElement
    private Date publishEndTimestamp;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "LAST_UPDATED", nullable = false)
    @Basic(fetch = FetchType.EAGER)
    @XmlElement
    private Date lastUpdated;

    @Column(name = "BOOK_SIZE")
    @Basic(fetch = FetchType.EAGER)
    @XmlElement
    private Long bookSize;

    @Column(name = "LARGEST_DOC_SIZE")
    @Basic(fetch = FetchType.EAGER)
    @XmlElement
    private Long largestDocSize;

    @Column(name = "LARGEST_IMAGE_SIZE")
    @Basic(fetch = FetchType.EAGER)
    @XmlElement
    private Long largestImageSize;

    @Column(name = "LARGEST_PDF_SIZE")
    @Basic(fetch = FetchType.EAGER)
    @XmlElement
    private Long largestPdfSize;

    @Column(name = "GROUP_VERSION")
    @Basic(fetch = FetchType.EAGER)
    @XmlElement
    private Long groupVersion;

    public Date getPublishStartTimestamp() {
        return publishStartTimestamp;
    }

    public void setPublishStartTimestamp(final Date publishStartTimestamp) {
        this.publishStartTimestamp = publishStartTimestamp;
    }

    public Integer getGatherTocNodeCount() {
        return gatherTocNodeCount;
    }

    public void setGatherTocNodeCount(final Integer gatherTocNodeCount) {
        this.gatherTocNodeCount = gatherTocNodeCount;
    }

    public Integer getGatherTocSkippedCount() {
        return gatherTocSkippedCount;
    }

    public void setGatherTocSkippedCount(final Integer gatherTocSkippedCount) {
        this.gatherTocSkippedCount = gatherTocSkippedCount;
    }

    public Integer getGatherTocDocCount() {
        return gatherTocDocCount;
    }

    public void setGatherTocDocCount(final Integer gatherTocDocCount) {
        this.gatherTocDocCount = gatherTocDocCount;
    }

    public Integer getGatherTocRetryCount() {
        return gatherTocRetryCount;
    }

    public void setGatherTocRetryCount(final Integer gatherTocRetryCount) {
        this.gatherTocRetryCount = gatherTocRetryCount;
    }

    public Integer getGatherDocExpectedCount() {
        return gatherDocExpectedCount;
    }

    public void setGatherDocExpectedCount(final Integer gatherDocExpectedCount) {
        this.gatherDocExpectedCount = gatherDocExpectedCount;
    }

    public Integer getGatherDocRetryCount() {
        return gatherDocRetryCount;
    }

    public void setGatherDocRetryCount(final Integer gatherDocRetryCount) {
        this.gatherDocRetryCount = gatherDocRetryCount;
    }

    public Integer getGatherDocRetrievedCount() {
        return gatherDocRetrievedCount;
    }

    public void setGatherDocRetrievedCount(final Integer gatherDocRetrievedCount) {
        this.gatherDocRetrievedCount = gatherDocRetrievedCount;
    }

    public Integer getGatherMetaExpectedCount() {
        return gatherMetaExpectedCount;
    }

    public void setGatherMetaExpectedCount(final Integer gatherMetaExpectedCount) {
        this.gatherMetaExpectedCount = gatherMetaExpectedCount;
    }

    public Integer getGatherMetaRetryCount() {
        return gatherMetaRetryCount;
    }

    public void setGatherMetaRetryCount(final Integer gatherMetaRetryCount) {
        this.gatherMetaRetryCount = gatherMetaRetryCount;
    }

    public Integer getGatherMetaRetrievedCount() {
        return gatherMetaRetrievedCount;
    }

    public void setGatherMetaRetrievedCount(final Integer gatherMetaRetrievedCount) {
        this.gatherMetaRetrievedCount = gatherMetaRetrievedCount;
    }

    public Integer getGatherImageExpectedCount() {
        return gatherImageExpectedCount;
    }

    public void setGatherImageExpectedCount(final Integer gatherImageExpectedCount) {
        this.gatherImageExpectedCount = gatherImageExpectedCount;
    }

    public Integer getGatherImageRetryCount() {
        return gatherImageRetryCount;
    }

    public void setGatherImageRetryCount(final Integer gatherImageRetryCount) {
        this.gatherImageRetryCount = gatherImageRetryCount;
    }

    public Integer getGatherImageRetrievedCount() {
        return gatherImageRetrievedCount;
    }

    public void setGatherImageRetrievedCount(final Integer gatherImageRetrievedCount) {
        this.gatherImageRetrievedCount = gatherImageRetrievedCount;
    }

    public Integer getFormatDocCount() {
        return formatDocCount;
    }

    public void setFormatDocCount(final Integer formatDocCount) {
        this.formatDocCount = formatDocCount;
    }

    public Integer getAssembleDocCount() {
        return assembleDocCount;
    }

    public void setAssembleDocCount(final Integer assembleDocCount) {
        this.assembleDocCount = assembleDocCount;
    }

    public Integer getTitleDocCount() {
        return titleDocCount;
    }

    public void setTitleDocCount(final Integer titleDocCount) {
        this.titleDocCount = titleDocCount;
    }

    public Integer getTitleDupDocCount() {
        return titleDupDocCount;
    }

    public void setTitleDupDocCount(final Integer titleDupDocCount) {
        this.titleDupDocCount = titleDupDocCount;
    }

    public String getPublishStatus() {
        return publishStatus;
    }

    /** Automatically set to sysdate
     *
     * */
    public void setPublishStatus(final String publishStatus) {
        this.publishStatus = publishStatus;
    }

    public Date getPublishEndTimestamp() {
        return publishEndTimestamp;
    }

    public void setPublishEndTimestamp(final Date publishEndTimestamp) {
        this.publishEndTimestamp = publishEndTimestamp;
    }

    public void setEbookDefId(final Long ebookDefId) {
        this.ebookDefId = ebookDefId;
    }

    public Long getEbookDefId() {
        return ebookDefId;
    }

    public void setJobInstanceId(final Long jobInstanceId) {
        this.jobInstanceId = jobInstanceId;
    }

    public Long getJobInstanceId() {
        return jobInstanceId;
    }

    public void setAudit(final EbookAudit audit) {
        this.audit = audit;
    }

    public EbookAudit getAudit() {
        return audit;
    }

    public void setJobSubmitterName(final String jobSubmitterName) {
        this.jobSubmitterName = jobSubmitterName;
    }

    public String getJobSubmitterName() {
        return jobSubmitterName;
    }

    public void setBookVersionSubmitted(final String bookVersionSubmitted) {
        this.bookVersionSubmitted = bookVersionSubmitted;
    }

    public String getBookVersionSubmitted() {
        return bookVersionSubmitted;
    }

    public void setJobHostName(final String jobHostName) {
        this.jobHostName = jobHostName;
    }

    public String getJobHostName() {
        return jobHostName;
    }

    public void setJobSubmitTimestamp(final Date jobSubmitTimestamp) {
        this.jobSubmitTimestamp = jobSubmitTimestamp;
    }

    public Date getJobSubmitTimestamp() {
        return jobSubmitTimestamp;
    }

    /**  Automatically set to sysdate
     *
     */
    public void setLastUpdated(final Date lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    public Date getLastUpdated() {
        return lastUpdated;
    }

    public PublishingStats() {
    }

    public PublishingStats(final Long jobInstanceId) {
        this.jobInstanceId = jobInstanceId;
    }

    /**
     * Returns a textual representation of a bean.
     *
     */
    public String toStringNew() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE);
    }

    public void setBookSize(final Long bookSize) {
        this.bookSize = bookSize;
    }

    public void setLargestDocSize(final Long largestDocSize) {
        this.largestDocSize = largestDocSize;
    }

    public void setLargestImageSize(final Long largestImageSize) {
        this.largestImageSize = largestImageSize;
    }

    public void setLargestPdfSize(final Long largestPdfSize) {
        this.largestPdfSize = largestPdfSize;
    }

    public Long getBookSize() {
        return bookSize;
    }

    @Transient
    public String getBookSizeHumanReadable() {
        return humanReadableByteCount(bookSize);
    }

    public Long getLargestDocSize() {
        return largestDocSize;
    }

    @Transient
    public String getLargestDocSizeHumanReadable() {
        return humanReadableByteCount(largestDocSize);
    }

    public Long getLargestImageSize() {
        return largestImageSize;
    }

    @Transient
    public String getLargestImageSizeHumanReadable() {
        return humanReadableByteCount(largestImageSize);
    }

    public Long getLargestPdfSize() {
        return largestPdfSize;
    }

    @Transient
    public String getLargestPdfSizeHumanReadable() {
        return humanReadableByteCount(largestPdfSize);
    }

    @Transient
    private String humanReadableByteCount(final Long bytes) {
        if (bytes != null) {
            final int unit = 1024;
            if (bytes < unit)
                return bytes + " B";
            final int exp = (int) (Math.log(bytes) / Math.log(unit));
            final String pre = ("KMGTPE").charAt(exp - 1) + "i";
            return String.format("%.1f %sB", bytes / Math.pow(unit, exp), pre);
        } else {
            return null;
        }
    }

    public Long getGroupVersion() {
        return groupVersion;
    }

    public void setGroupVersion(final Long groupVersion) {
        this.groupVersion = groupVersion;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((assembleDocCount == null) ? 0 : assembleDocCount.hashCode());
        result = prime * result + ((audit == null) ? 0 : audit.hashCode());
        result = prime * result + ((bookSize == null) ? 0 : bookSize.hashCode());
        result = prime * result + ((bookVersionSubmitted == null) ? 0 : bookVersionSubmitted.hashCode());
        result = prime * result + ((ebookDefId == null) ? 0 : ebookDefId.hashCode());
        result = prime * result + ((formatDocCount == null) ? 0 : formatDocCount.hashCode());
        result = prime * result + ((gatherDocExpectedCount == null) ? 0 : gatherDocExpectedCount.hashCode());
        result = prime * result + ((gatherDocRetrievedCount == null) ? 0 : gatherDocRetrievedCount.hashCode());
        result = prime * result + ((gatherDocRetryCount == null) ? 0 : gatherDocRetryCount.hashCode());
        result = prime * result + ((gatherImageExpectedCount == null) ? 0 : gatherImageExpectedCount.hashCode());
        result = prime * result + ((gatherImageRetrievedCount == null) ? 0 : gatherImageRetrievedCount.hashCode());
        result = prime * result + ((gatherImageRetryCount == null) ? 0 : gatherImageRetryCount.hashCode());
        result = prime * result + ((gatherMetaExpectedCount == null) ? 0 : gatherMetaExpectedCount.hashCode());
        result = prime * result + ((gatherMetaRetrievedCount == null) ? 0 : gatherMetaRetrievedCount.hashCode());
        result = prime * result + ((gatherMetaRetryCount == null) ? 0 : gatherMetaRetryCount.hashCode());
        result = prime * result + ((gatherTocDocCount == null) ? 0 : gatherTocDocCount.hashCode());
        result = prime * result + ((gatherTocNodeCount == null) ? 0 : gatherTocNodeCount.hashCode());
        result = prime * result + ((gatherTocRetryCount == null) ? 0 : gatherTocRetryCount.hashCode());
        result = prime * result + ((gatherTocSkippedCount == null) ? 0 : gatherTocSkippedCount.hashCode());
        result = prime * result + ((jobHostName == null) ? 0 : jobHostName.hashCode());
        result = prime * result + ((jobInstanceId == null) ? 0 : jobInstanceId.hashCode());
        result = prime * result + ((jobSubmitTimestamp == null) ? 0 : jobSubmitTimestamp.hashCode());
        result = prime * result + ((jobSubmitterName == null) ? 0 : jobSubmitterName.hashCode());
        result = prime * result + ((largestDocSize == null) ? 0 : largestDocSize.hashCode());
        result = prime * result + ((largestImageSize == null) ? 0 : largestImageSize.hashCode());
        result = prime * result + ((largestPdfSize == null) ? 0 : largestPdfSize.hashCode());
        result = prime * result + ((lastUpdated == null) ? 0 : lastUpdated.hashCode());
        result = prime * result + ((publishEndTimestamp == null) ? 0 : publishEndTimestamp.hashCode());
        result = prime * result + ((publishStartTimestamp == null) ? 0 : publishStartTimestamp.hashCode());
        result = prime * result + ((publishStatus == null) ? 0 : publishStatus.hashCode());
        result = prime * result + ((titleDocCount == null) ? 0 : titleDocCount.hashCode());
        result = prime * result + ((titleDupDocCount == null) ? 0 : titleDupDocCount.hashCode());
        result = prime * result + ((groupVersion == null) ? 0 : groupVersion.hashCode());
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
        final PublishingStats other = (PublishingStats) obj;
        if (assembleDocCount == null) {
            if (other.assembleDocCount != null)
                return false;
        } else if (!assembleDocCount.equals(other.assembleDocCount))
            return false;
        if (audit == null) {
            if (other.audit != null)
                return false;
        } else if (!audit.equals(other.audit))
            return false;
        if (bookSize == null) {
            if (other.bookSize != null)
                return false;
        } else if (!bookSize.equals(other.bookSize))
            return false;
        if (bookVersionSubmitted == null) {
            if (other.bookVersionSubmitted != null)
                return false;
        } else if (!bookVersionSubmitted.equals(other.bookVersionSubmitted))
            return false;
        if (ebookDefId == null) {
            if (other.ebookDefId != null)
                return false;
        } else if (!ebookDefId.equals(other.ebookDefId))
            return false;
        if (formatDocCount == null) {
            if (other.formatDocCount != null)
                return false;
        } else if (!formatDocCount.equals(other.formatDocCount))
            return false;
        if (gatherDocExpectedCount == null) {
            if (other.gatherDocExpectedCount != null)
                return false;
        } else if (!gatherDocExpectedCount.equals(other.gatherDocExpectedCount))
            return false;
        if (gatherDocRetrievedCount == null) {
            if (other.gatherDocRetrievedCount != null)
                return false;
        } else if (!gatherDocRetrievedCount.equals(other.gatherDocRetrievedCount))
            return false;
        if (gatherDocRetryCount == null) {
            if (other.gatherDocRetryCount != null)
                return false;
        } else if (!gatherDocRetryCount.equals(other.gatherDocRetryCount))
            return false;
        if (gatherImageExpectedCount == null) {
            if (other.gatherImageExpectedCount != null)
                return false;
        } else if (!gatherImageExpectedCount.equals(other.gatherImageExpectedCount))
            return false;
        if (gatherImageRetrievedCount == null) {
            if (other.gatherImageRetrievedCount != null)
                return false;
        } else if (!gatherImageRetrievedCount.equals(other.gatherImageRetrievedCount))
            return false;
        if (gatherImageRetryCount == null) {
            if (other.gatherImageRetryCount != null)
                return false;
        } else if (!gatherImageRetryCount.equals(other.gatherImageRetryCount))
            return false;
        if (gatherMetaExpectedCount == null) {
            if (other.gatherMetaExpectedCount != null)
                return false;
        } else if (!gatherMetaExpectedCount.equals(other.gatherMetaExpectedCount))
            return false;
        if (gatherMetaRetrievedCount == null) {
            if (other.gatherMetaRetrievedCount != null)
                return false;
        } else if (!gatherMetaRetrievedCount.equals(other.gatherMetaRetrievedCount))
            return false;
        if (gatherMetaRetryCount == null) {
            if (other.gatherMetaRetryCount != null)
                return false;
        } else if (!gatherMetaRetryCount.equals(other.gatherMetaRetryCount))
            return false;
        if (gatherTocDocCount == null) {
            if (other.gatherTocDocCount != null)
                return false;
        } else if (!gatherTocDocCount.equals(other.gatherTocDocCount))
            return false;
        if (gatherTocNodeCount == null) {
            if (other.gatherTocNodeCount != null)
                return false;
        } else if (!gatherTocNodeCount.equals(other.gatherTocNodeCount))
            return false;
        if (gatherTocRetryCount == null) {
            if (other.gatherTocRetryCount != null)
                return false;
        } else if (!gatherTocRetryCount.equals(other.gatherTocRetryCount))
            return false;
        if (gatherTocSkippedCount == null) {
            if (other.gatherTocSkippedCount != null)
                return false;
        } else if (!gatherTocSkippedCount.equals(other.gatherTocSkippedCount))
            return false;
        if (jobHostName == null) {
            if (other.jobHostName != null)
                return false;
        } else if (!jobHostName.equals(other.jobHostName))
            return false;
        if (jobInstanceId == null) {
            if (other.jobInstanceId != null)
                return false;
        } else if (!jobInstanceId.equals(other.jobInstanceId))
            return false;
        if (jobSubmitTimestamp == null) {
            if (other.jobSubmitTimestamp != null)
                return false;
        } else if (!jobSubmitTimestamp.equals(other.jobSubmitTimestamp))
            return false;
        if (jobSubmitterName == null) {
            if (other.jobSubmitterName != null)
                return false;
        } else if (!jobSubmitterName.equals(other.jobSubmitterName))
            return false;
        if (largestDocSize == null) {
            if (other.largestDocSize != null)
                return false;
        } else if (!largestDocSize.equals(other.largestDocSize))
            return false;
        if (largestImageSize == null) {
            if (other.largestImageSize != null)
                return false;
        } else if (!largestImageSize.equals(other.largestImageSize))
            return false;
        if (largestPdfSize == null) {
            if (other.largestPdfSize != null)
                return false;
        } else if (!largestPdfSize.equals(other.largestPdfSize))
            return false;
        if (lastUpdated == null) {
            if (other.lastUpdated != null)
                return false;
        } else if (!lastUpdated.equals(other.lastUpdated))
            return false;
        if (publishEndTimestamp == null) {
            if (other.publishEndTimestamp != null)
                return false;
        } else if (!publishEndTimestamp.equals(other.publishEndTimestamp))
            return false;
        if (publishStartTimestamp == null) {
            if (other.publishStartTimestamp != null)
                return false;
        } else if (!publishStartTimestamp.equals(other.publishStartTimestamp))
            return false;
        if (publishStatus == null) {
            if (other.publishStatus != null)
                return false;
        } else if (!publishStatus.equals(other.publishStatus))
            return false;
        if (titleDocCount == null) {
            if (other.titleDocCount != null)
                return false;
        } else if (!titleDocCount.equals(other.titleDocCount))
            return false;
        if (titleDupDocCount == null) {
            if (other.titleDupDocCount != null)
                return false;
        } else if (!titleDupDocCount.equals(other.titleDupDocCount))
            return false;
        if (groupVersion == null) {
            if (other.groupVersion != null)
                return false;
        } else if (!groupVersion.equals(other.groupVersion))
            return false;
        return true;
    }

    @Override
    public int compareTo(final PublishingStats o) {
        return jobInstanceId.compareTo(o.jobInstanceId);
    }
}
