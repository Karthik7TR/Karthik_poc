package com.thomsonreuters.uscl.ereader.stats.service;

import static java.util.Comparator.comparingLong;

import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import com.thomsonreuters.uscl.ereader.StatsUpdateTypeEnum;
import com.thomsonreuters.uscl.ereader.core.book.domain.EbookAudit;
import com.thomsonreuters.uscl.ereader.core.book.model.Version;
import com.thomsonreuters.uscl.ereader.gather.domain.GatherResponse;
import com.thomsonreuters.uscl.ereader.stats.dao.PublishingStatsDao;
import com.thomsonreuters.uscl.ereader.stats.domain.PublishingStats;
import com.thomsonreuters.uscl.ereader.stats.domain.PublishingStatsFilter;
import com.thomsonreuters.uscl.ereader.stats.domain.PublishingStatsSort;
import com.thomsonreuters.uscl.ereader.stats.util.PublishingStatsUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service("publishingStatsService")
@Slf4j
public class PublishingStatsServiceImpl implements PublishingStatsService {

    private final PublishingStatsDao publishingStatsDAO;
    private final PublishingStatsUtil publishingStatsUtil;

    @Autowired
    public PublishingStatsServiceImpl(final PublishingStatsDao publishingStatsDAO, final PublishingStatsUtil publishingStatsUtil) {
        this.publishingStatsDAO = publishingStatsDAO;
        this.publishingStatsUtil = publishingStatsUtil;
    }

    @Override
    @Transactional(readOnly = true)
    public Date getSysDate() {
        return publishingStatsDAO.getSysDate();
    }

    @Override
    @Transactional(readOnly = true)
    public PublishingStats findPublishingStatsByJobId(final Long JobId) {
        return publishingStatsDAO.findJobStatsByJobId(JobId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PublishingStats> findPublishingStatsByEbookDef(final Long EbookDefId) {
        return publishingStatsDAO.findPublishingStatsByEbookDef(EbookDefId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PublishingStats> findPublishingStats(
        final PublishingStatsFilter filter,
        final PublishingStatsSort sort) {
        return publishingStatsDAO.findPublishingStats(filter, sort);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PublishingStats> findPublishingStatsForExcelReport(PublishingStatsFilter filter,
         PublishingStatsSort sort, int maxExcelRowCount) {
        return publishingStatsDAO.findPublishingStatsForExcelReport(filter, sort, maxExcelRowCount);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PublishingStats> findPublishingStats(final PublishingStatsFilter filter) {
        return publishingStatsDAO.findPublishingStats(filter);
    }

    @Override
    @Transactional(readOnly = true)
    public int numberOfPublishingStats(final PublishingStatsFilter filter) {
        return publishingStatsDAO.numberOfPublishingStats(filter);
    }

    @Override
    @Transactional(readOnly = true)
    public EbookAudit findAuditInfoByJobId(final Long jobId) {
        return publishingStatsDAO.findAuditInfoByJobId(jobId);
    }

    @Override
    @Transactional
    public void savePublishingStats(final PublishingStats jobstats) {
        publishingStatsDAO.saveJobStats(jobstats);
    }

    @Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
    public void updatePublishingStats(final PublishingStats newstats, final StatsUpdateTypeEnum updateType) {
        PublishingStats stats = publishingStatsDAO.findJobStatsByJobId(newstats.getJobInstanceId());
        final Date rightNow = publishingStatsDAO.getSysDate();
        if (stats == null) {
            stats = newstats;
        } else {
            switch (updateType) {
            case PREPARE_SOURCES:
                copyTocStats(newstats, stats);
                copyDocsStats(newstats, stats);
                break;
            case GATHERTOC:
            case GENERATETOC:
                copyTocStats(newstats, stats);
                break;
            case GATHERDOC:
            case GENERATEDOC:
                copyDocsStats(newstats, stats);
                break;
            case GATHERIMAGE:
                stats.setGatherImageExpectedCount(newstats.getGatherImageExpectedCount());
                stats.setGatherImageRetrievedCount(newstats.getGatherImageRetrievedCount());
                stats.setGatherImageRetryCount(newstats.getGatherImageRetryCount());
                break;
            case TITLEDOC:
                stats.setTitleDocCount(newstats.getTitleDocCount());
                break;
            case TITLEDUPDOCCOUNT:
                stats.setTitleDupDocCount(newstats.getTitleDupDocCount());
                break;
            case FORMATDOC:
                stats.setFormatDocCount(newstats.getFormatDocCount());
                break;
            case ASSEMBLEDOC:
                stats.setBookSize(newstats.getBookSize());
                stats.setLargestDocSize(newstats.getLargestDocSize());
                stats.setLargestImageSize(newstats.getLargestImageSize());
                stats.setLargestPdfSize(newstats.getLargestPdfSize());
                stats.setAssembleDocCount(newstats.getAssembleDocCount());
                break;
            case FINALPUBLISH:
                stats.setPublishEndTimestamp(rightNow);
                break;
            case GENERAL:
                break;
            case GROUPEBOOK:
                stats.setGroupVersion(newstats.getGroupVersion());
                break;
            default:
                log.error("Unknown StatsUpdateTypeEnum");
                // TODO: failure logic
            }
            stats.setPublishStatus(newstats.getPublishStatus());
        }
        stats.setLastUpdated(rightNow);
        publishingStatsDAO.saveJobStats(stats);
    }

    @Override
    @Transactional
    public void deleteJobStats(final PublishingStats jobStats) {
        publishingStatsDAO.deleteJobStats(jobStats);
    }

    @Override
    @Transactional(readOnly = true)
    public Boolean hasBeenGrouped(final Long ebookDefId) {
        final Long previousGroupBook = publishingStatsDAO.findSuccessfullyPublishedGroupBook(ebookDefId);
        return previousGroupBook != null;
    }

    @Override
    @Transactional(readOnly = true)
    public EbookAudit findLastSuccessfulJobStatsAuditByEbookDef(final Long EbookDefId) {
        EbookAudit lastAuditSuccessful = null;

        final List<PublishingStats> publishingStats = publishingStatsDAO.findPublishingStatsByEbookDef(EbookDefId);

        if (publishingStats != null && !publishingStats.isEmpty()) {
            lastAuditSuccessful = publishingStats.stream()
                    .filter(stats -> publishingStatsUtil.isPublishedSuccessfully(stats.getPublishStatus()))
                    .max(comparingLong(PublishingStats::getJobInstanceId))
                    .map(PublishingStats::getAudit)
                    .orElse(null);
        }
        return lastAuditSuccessful;
    }

    @Override
    @Transactional(readOnly = true)
    public Date findLastPublishDateForBook(final Long EbookDefId) {
        Date lastPublishDate = null;
        final List<PublishingStats> publishingStats = publishingStatsDAO.findPublishingStatsByEbookDef(EbookDefId);

        if (publishingStats != null) {
            lastPublishDate = publishingStats.stream()
                .map(PublishingStats::getPublishEndTimestamp)
                .filter(Objects::nonNull)
                .min(Comparator.reverseOrder())
                .orElse(null);
        }

        return lastPublishDate;
    }

    @Override
    @Transactional(readOnly = true)
    public List<PublishingStats> findAllPublishingStats() {
        return publishingStatsDAO.findAllPublishingStats();
    }

    @Transactional(readOnly = true)
    @Override
    public PublishingStats getPreviousPublishingStatsForSameBook(final long jobInstanceId) {
        return publishingStatsDAO.getPreviousPublishingStatsForSameBook(jobInstanceId);
    }

    @Transactional(readOnly = true)
    @Override
    public String getIsbnByTitleAndVersion(final String title, final String fullVersion) {
        final String version = new Version(fullVersion).getVersionWithoutPrefix();
        return publishingStatsDAO.findSuccessfullyPublishedIsbnByTitleIdAndVersion(title, version);
    }

    @Override
    public void addDocsAndMetadataStats(final PublishingStats docsAndMetadataStatsSum, final GatherResponse docAndMetadataResponse) {
        docsAndMetadataStatsSum.setGatherDocRetrievedCount(docsAndMetadataStatsSum.getGatherDocRetrievedCount() + docAndMetadataResponse.getDocCount());
        docsAndMetadataStatsSum.setGatherDocExpectedCount(docsAndMetadataStatsSum.getGatherDocExpectedCount() + docAndMetadataResponse.getNodeCount());
        docsAndMetadataStatsSum.setGatherDocRetryCount(docsAndMetadataStatsSum.getGatherDocRetryCount() + docAndMetadataResponse.getRetryCount());
        docsAndMetadataStatsSum.setGatherMetaRetryCount(docsAndMetadataStatsSum.getGatherMetaRetryCount() + docAndMetadataResponse.getRetryCount2());
        docsAndMetadataStatsSum.setGatherMetaRetrievedCount(docsAndMetadataStatsSum.getGatherMetaRetrievedCount() + docAndMetadataResponse.getDocCount2());
        docsAndMetadataStatsSum.setGatherMetaExpectedCount(docsAndMetadataStatsSum.getGatherMetaExpectedCount() + docAndMetadataResponse.getNodeCount());
    }

    @Override
    public void addTocStats(final PublishingStats tocStatsSum, final GatherResponse tocResponse) {
        tocStatsSum.setGatherTocDocCount(tocStatsSum.getGatherTocDocCount() + tocResponse.getDocCount());
        tocStatsSum.setGatherTocNodeCount(tocStatsSum.getGatherTocNodeCount() + tocResponse.getNodeCount());
        tocStatsSum.setGatherTocSkippedCount(tocStatsSum.getGatherTocSkippedCount() + tocResponse.getSkipCount());
        tocStatsSum.setGatherTocRetryCount(tocStatsSum.getGatherTocRetryCount() + tocResponse.getRetryCount());
    }

    private void copyDocsStats(final PublishingStats newStats, final PublishingStats stats) {
        stats.setGatherDocExpectedCount(newStats.getGatherDocExpectedCount());
        stats.setGatherDocRetrievedCount(newStats.getGatherDocRetrievedCount());
        stats.setGatherDocRetryCount(newStats.getGatherDocRetryCount());
        stats.setGatherMetaExpectedCount(newStats.getGatherMetaExpectedCount());
        stats.setGatherMetaRetrievedCount(newStats.getGatherMetaRetrievedCount());
        stats.setGatherMetaRetryCount(newStats.getGatherMetaRetryCount());
    }

    private void copyTocStats(final PublishingStats newStats, final PublishingStats stats) {
        stats.setGatherTocNodeCount(newStats.getGatherTocNodeCount());
        stats.setGatherTocDocCount(newStats.getGatherTocDocCount());
        stats.setGatherTocRetryCount(newStats.getGatherTocRetryCount());
        stats.setGatherTocSkippedCount(newStats.getGatherTocSkippedCount());
    }
}
