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
import lombok.Data;
import lombok.NoArgsConstructor;

@IdClass(com.thomsonreuters.uscl.ereader.stats.domain.PublishingStatsPK.class)
@Entity
@NamedNativeQuery(name = "getSysdate", query = "SELECT SYSDATE FROM DUAL")
@Table(name = "PUBLISHING_STATS")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(namespace = "ebookGenerator/com/thomsonreuters/uscl/ereader/core/job/domain", name = "PublishingStats")
@Data
@NoArgsConstructor
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

    @Column(name = "COMB_BOOK_DEFN_ID")
    @Basic(fetch = FetchType.EAGER)
    @XmlElement
    private Long combinedBookDefinitionId;

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

    public PublishingStats(final Long jobInstanceId) {
        this.jobInstanceId = jobInstanceId;
    }

    @Transient
    public String getBookSizeHumanReadable() {
        return humanReadableByteCount(bookSize);
    }

    @Transient
    public String getLargestDocSizeHumanReadable() {
        return humanReadableByteCount(largestDocSize);
    }

    @Transient
    public String getLargestImageSizeHumanReadable() {
        return humanReadableByteCount(largestImageSize);
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
    public static PublishingStats initStats(final Long jobInstanceId) {
        PublishingStats publishingStats = new PublishingStats();
        publishingStats.setGatherTocDocCount(0);
        publishingStats.setGatherTocNodeCount(0);
        publishingStats.setGatherTocSkippedCount(0);
        publishingStats.setGatherTocRetryCount(0);
        publishingStats.setGatherDocRetrievedCount(0);
        publishingStats.setGatherDocExpectedCount(0);
        publishingStats.setGatherDocRetryCount(0);
        publishingStats.setGatherMetaRetryCount(0);
        publishingStats.setGatherMetaRetrievedCount(0);
        publishingStats.setGatherMetaExpectedCount(0);
        publishingStats.setJobInstanceId(jobInstanceId);
        return publishingStats;
    }

    @Override
    public int compareTo(final PublishingStats o) {
        return jobInstanceId.compareTo(o.jobInstanceId);
    }
}
