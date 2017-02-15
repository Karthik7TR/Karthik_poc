package com.thomsonreuters.uscl.ereader.stats.service;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.thomsonreuters.uscl.ereader.StatsUpdateTypeEnum;
import com.thomsonreuters.uscl.ereader.core.book.domain.EbookAudit;
import com.thomsonreuters.uscl.ereader.stats.dao.PublishingStatsDao;
import com.thomsonreuters.uscl.ereader.stats.domain.PublishingStats;
import com.thomsonreuters.uscl.ereader.stats.domain.PublishingStatsFilter;
import com.thomsonreuters.uscl.ereader.stats.domain.PublishingStatsPK;
import com.thomsonreuters.uscl.ereader.stats.domain.PublishingStatsSort;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

public class PublishingStatsServiceImpl implements PublishingStatsService
{
    // private static final Logger LOG =
    // LogManager.getLogger(PublishingStatsServiceImpl.class);
    private PublishingStatsDao publishingStatsDAO;

    @Override
    @Transactional(readOnly = true)
    public PublishingStats findPublishingStatsByJobId(final Long JobId)
    {
        return publishingStatsDAO.findJobStatsByJobId(JobId);
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, String> findSubGroupByVersion(final Long boofDefnition)
    {
        return publishingStatsDAO.findSubGroupByVersion(boofDefnition);
    }

    @Override
    @Transactional(readOnly = true)
    public String findNameByBoofDefAndVersion(final Long boofDefnition, final String version)
    {
        return publishingStatsDAO.findNameByIdAndVersion(boofDefnition, version);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PublishingStats> getPubStatsByEbookDefSort(final Long EbookDefId)
    {
        return publishingStatsDAO.findPubStatsByEbookDefSort(EbookDefId);
    }

    @Override
    @Transactional(readOnly = true)
    public PublishingStats findStatsByLastUpdated(final Long jobId)
    {
        return publishingStatsDAO.findStatsByLastUpdated(jobId);
    }

    @Override
    @Transactional(readOnly = true)
    public PublishingStats findJobStatsByPubStatsPK(final PublishingStatsPK jobIdPK)
    {
        return publishingStatsDAO.findJobStatsByPubStatsPK(jobIdPK);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PublishingStats> findPublishingStatsByEbookDef(final Long EbookDefId)
    {
        return publishingStatsDAO.findPublishingStatsByEbookDef(EbookDefId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PublishingStats> findPublishingStats(final PublishingStatsFilter filter, final PublishingStatsSort sort)
    {
        return publishingStatsDAO.findPublishingStats(filter, sort);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PublishingStats> findPublishingStats(final PublishingStatsFilter filter)
    {
        return publishingStatsDAO.findPublishingStats(filter);
    }

    @Override
    @Transactional(readOnly = true)
    public int numberOfPublishingStats(final PublishingStatsFilter filter)
    {
        return publishingStatsDAO.numberOfPublishingStats(filter);
    }

    @Override
    @Transactional(readOnly = true)
    public EbookAudit findAuditInfoByJobId(final Long jobId)
    {
        return publishingStatsDAO.findAuditInfoByJobId(jobId);
    }

    @Override
    @Transactional
    public void savePublishingStats(final PublishingStats jobstats)
    {
        publishingStatsDAO.saveJobStats(jobstats);
    }

    @Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
    public int updatePublishingStats(final PublishingStats jobstats, final StatsUpdateTypeEnum updateType)
    {
        return publishingStatsDAO.updateJobStats(jobstats, updateType);
    }

    @Override
    @Transactional
    public void deleteJobStats(final PublishingStats jobStats)
    {
        publishingStatsDAO.deleteJobStats(jobStats);
    }

    @Override
    @Transactional
    public Long getMaxGroupVersionById(final Long ebookDefId)
    {
        return publishingStatsDAO.getMaxGroupVersionById(ebookDefId);
    }

    @Override
    @Transactional(readOnly = true)
    public Boolean hasIsbnBeenPublished(final String isbn, final String titleId)
    {
        String replacedIsbn = "";
        Boolean hasBeenPublished = false;

        if (StringUtils.isNotBlank(isbn))
        {
            replacedIsbn = isbn.replace("-", "");
        }

        final List<String> publishedIsbns = publishingStatsDAO.findSuccessfullyPublishedIsbnByTitleId(titleId);
        for (final String publishedIsbn : publishedIsbns)
        {
            if (StringUtils.isNotBlank(publishedIsbn))
            {
                final String replacedPublishedIsbn = publishedIsbn.replace("-", "");
                if (replacedPublishedIsbn.equalsIgnoreCase(replacedIsbn))
                {
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
    public Boolean hasSubGroupChanged(final String subGroupHeading, final Long ebookDefId)
    {
        Boolean hasSubGroupChanged = true;
        final List<String> previousSubGroupList = publishingStatsDAO.findSuccessfullyPublishedsubGroupById(ebookDefId);
        for (final String previousSubGroupHeading : previousSubGroupList)
        {
            // previousSubGroupHeading could be null as it may be single book in
            // previous version
            if (previousSubGroupHeading != null && previousSubGroupHeading.equalsIgnoreCase(subGroupHeading))
            {
                hasSubGroupChanged = false;
                break;
            }
        }
        return hasSubGroupChanged;
    }

    @Override
    @Transactional(readOnly = true)
    public Boolean hasBeenGrouped(final Long ebookDefId)
    {
        final Boolean hasBeenGrouped = true;
        final Long previousGroupBook = publishingStatsDAO.findSuccessfullyPublishedGroupBook(ebookDefId);
        if (previousGroupBook == null)
        {
            return false;
        }
        return hasBeenGrouped;
    }

    @Override
    @Transactional(readOnly = true)
    public EbookAudit findLastSuccessfulJobStatsAuditByEbookDef(final Long EbookDefId)
    {
        EbookAudit lastAuditSuccessful = null;
        PublishingStats lastSuccessfulPublishingStat = null;

        final List<PublishingStats> publishingStats = publishingStatsDAO.findPublishingStatsByEbookDef(EbookDefId);

        if (publishingStats != null && publishingStats.size() > 0)
        {
            lastSuccessfulPublishingStat = publishingStats.get(0);
            for (final PublishingStats publishingStat : publishingStats)
            {
                if (publishingStat.getJobInstanceId().longValue() >= lastSuccessfulPublishingStat.getJobInstanceId()
                    .longValue()
                    && (PublishingStats.SUCCESFULL_PUBLISH_STATUS.equalsIgnoreCase(publishingStat.getPublishStatus())
                        || PublishingStats.SEND_EMAIL_COMPLETE.equalsIgnoreCase(publishingStat.getPublishStatus())))
                {
                    lastSuccessfulPublishingStat = publishingStat;
                    lastAuditSuccessful = publishingStat.getAudit();
                }
            }
        }
        return lastAuditSuccessful;
    }

    @Override
    @Transactional(readOnly = true)
    public Date findLastPublishDateForBook(final Long EbookDefId)
    {
        Date lastPublishDate = null;
        final List<PublishingStats> publishingStats = publishingStatsDAO.findPublishingStatsByEbookDef(EbookDefId);
        PublishingStats lastPublishingStat = null;

        if (publishingStats != null && publishingStats.size() > 0)
        {
            lastPublishingStat = publishingStats.get(0);
            for (final PublishingStats publishingStat : publishingStats)
            {
                if (lastPublishingStat.getPublishEndTimestamp() == null)
                {
                    lastPublishingStat = publishingStat;
                }
                if (lastPublishingStat.getPublishEndTimestamp() != null
                    && publishingStat.getPublishEndTimestamp() != null
                    && (publishingStat.getPublishEndTimestamp() == lastPublishingStat.getPublishEndTimestamp()
                        || publishingStat.getPublishEndTimestamp().after(lastPublishingStat.getPublishEndTimestamp())))
                {
                    lastPublishingStat = publishingStat;
                }
            }
        }

        if (lastPublishingStat != null)
        {
            lastPublishDate = lastPublishingStat.getPublishEndTimestamp();
        }

        return lastPublishDate;
    }

    @Override
    @Transactional(readOnly = true)
    public List<PublishingStats> findAllPublishingStats()
    {
        return publishingStatsDAO.findAllPublishingStats();
    }

    @Required
    public void setPublishingStatsDAO(final PublishingStatsDao dao)
    {
        publishingStatsDAO = dao;
    }

    @Override
    @Transactional(readOnly = true)
    public EbookAudit getMaxAuditId(final Long eBookDefId)
    {
        return publishingStatsDAO.getMaxAuditId(eBookDefId);
    }

    @Transactional(readOnly = true)
    @Override
    public PublishingStats getPreviousPublishingStatsForSameBook(final long jobInstanceId)
    {
        return publishingStatsDAO.getPreviousPublishingStatsForSameBook(jobInstanceId);
    }
}
