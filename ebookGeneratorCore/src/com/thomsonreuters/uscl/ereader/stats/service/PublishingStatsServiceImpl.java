package com.thomsonreuters.uscl.ereader.stats.service;

import java.util.Date;
import java.util.List;

import com.thomsonreuters.uscl.ereader.StatsUpdateTypeEnum;
import com.thomsonreuters.uscl.ereader.core.book.domain.EbookAudit;
import com.thomsonreuters.uscl.ereader.stats.dao.PublishingStatsDao;
import com.thomsonreuters.uscl.ereader.stats.domain.PublishingStats;
import com.thomsonreuters.uscl.ereader.stats.domain.PublishingStatsFilter;
import com.thomsonreuters.uscl.ereader.stats.domain.PublishingStatsSort;
import com.thomsonreuters.uscl.ereader.stats.util.PublishingStatsUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

public class PublishingStatsServiceImpl implements PublishingStatsService {
    private static final Logger LOG = LogManager.getLogger(PublishingStatsServiceImpl.class);
    private PublishingStatsDao publishingStatsDAO;
    private PublishingStatsUtil publishingStatsUtil;

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
            case GATHERTOC:
            case GENERATETOC:
                stats.setGatherTocNodeCount(newstats.getGatherTocNodeCount());
                stats.setGatherTocDocCount(newstats.getGatherTocDocCount());
                stats.setGatherTocRetryCount(newstats.getGatherTocRetryCount());
                stats.setGatherTocSkippedCount(newstats.getGatherTocSkippedCount());
                break;
            case GATHERDOC:
            case GENERATEDOC:
                stats.setGatherDocExpectedCount(newstats.getGatherDocExpectedCount());
                stats.setGatherDocRetrievedCount(newstats.getGatherDocRetrievedCount());
                stats.setGatherDocRetryCount(newstats.getGatherDocRetryCount());
                stats.setGatherMetaExpectedCount(newstats.getGatherMetaExpectedCount());
                stats.setGatherMetaRetrievedCount(newstats.getGatherMetaRetrievedCount());
                stats.setGatherMetaRetryCount(newstats.getGatherMetaRetryCount());
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
                LOG.error("Unknown StatsUpdateTypeEnum");
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
    public Boolean hasIsbnBeenPublished(final String isbn, final String titleId) {
        String replacedIsbn = "";
        Boolean hasBeenPublished = false;

        if (StringUtils.isNotBlank(isbn)) {
            replacedIsbn = isbn.replace("-", "");
        }

        final List<String> publishedIsbns = publishingStatsDAO.findSuccessfullyPublishedIsbnByTitleId(titleId);
        for (final String publishedIsbn : publishedIsbns) {
            if (StringUtils.isNotBlank(publishedIsbn)) {
                final String replacedPublishedIsbn = publishedIsbn.replace("-", "");
                if (replacedPublishedIsbn.equalsIgnoreCase(replacedIsbn)) {
                    // ISBN has been published
                    hasBeenPublished = true;
                    break;
                }
            }
        }
        return hasBeenPublished;
    }

    @Override
    @Transactional(readOnly = true)
    public Boolean hasBeenGrouped(final Long ebookDefId) {
        final Boolean hasBeenGrouped = true;
        final Long previousGroupBook = publishingStatsDAO.findSuccessfullyPublishedGroupBook(ebookDefId);
        if (previousGroupBook == null) {
            return false;
        }
        return hasBeenGrouped;
    }

    @Override
    @Transactional(readOnly = true)
    public EbookAudit findLastSuccessfulJobStatsAuditByEbookDef(final Long EbookDefId) {
        EbookAudit lastAuditSuccessful = null;
        PublishingStats lastSuccessfulPublishingStat = null;

        final List<PublishingStats> publishingStats = publishingStatsDAO.findPublishingStatsByEbookDef(EbookDefId);

        if (publishingStats != null && !publishingStats.isEmpty()) {
            lastSuccessfulPublishingStat = publishingStats.get(0);
            for (final PublishingStats publishingStat : publishingStats) {
                if (publishingStat.getJobInstanceId().longValue() >= lastSuccessfulPublishingStat.getJobInstanceId()
                    .longValue() && publishingStatsUtil.isPublishedSuccessfully(publishingStat.getPublishStatus())) {
                    lastSuccessfulPublishingStat = publishingStat;
                    lastAuditSuccessful = publishingStat.getAudit();
                }
            }
        }
        return lastAuditSuccessful;
    }

    @Override
    @Transactional(readOnly = true)
    public Date findLastPublishDateForBook(final Long EbookDefId) {
        Date lastPublishDate = null;
        final List<PublishingStats> publishingStats = publishingStatsDAO.findPublishingStatsByEbookDef(EbookDefId);

        if (publishingStats != null && !publishingStats.isEmpty()) {
            for (final PublishingStats publishingStat : publishingStats) {
                if (publishingStat.getPublishEndTimestamp() != null) {
                    if (lastPublishDate == null) {
                        lastPublishDate = publishingStat.getPublishEndTimestamp();
                    } else if (publishingStat.getPublishEndTimestamp().equals(lastPublishDate)
                        || publishingStat.getPublishEndTimestamp().after(lastPublishDate)) {
                        lastPublishDate = publishingStat.getPublishEndTimestamp();
                    }
                }
            }
        }

        return lastPublishDate;
    }

    @Override
    @Transactional(readOnly = true)
    public List<PublishingStats> findAllPublishingStats() {
        return publishingStatsDAO.findAllPublishingStats();
    }

    @Required
    public void setPublishingStatsDAO(final PublishingStatsDao dao) {
        publishingStatsDAO = dao;
    }

    @Required
    public void setPublishingStatsUtil(final PublishingStatsUtil publishingStatsUtil) {
        this.publishingStatsUtil = publishingStatsUtil;
    }

    @Transactional(readOnly = true)
    @Override
    public PublishingStats getPreviousPublishingStatsForSameBook(final long jobInstanceId) {
        return publishingStatsDAO.getPreviousPublishingStatsForSameBook(jobInstanceId);
    }
}
