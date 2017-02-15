package com.thomsonreuters.uscl.ereader.stats.dao;

import java.util.List;
import java.util.Map;

import com.thomsonreuters.uscl.ereader.StatsUpdateTypeEnum;
import com.thomsonreuters.uscl.ereader.core.book.domain.EbookAudit;
import com.thomsonreuters.uscl.ereader.stats.domain.PublishingStats;
import com.thomsonreuters.uscl.ereader.stats.domain.PublishingStatsFilter;
import com.thomsonreuters.uscl.ereader.stats.domain.PublishingStatsPK;
import com.thomsonreuters.uscl.ereader.stats.domain.PublishingStatsSort;

public interface PublishingStatsDao
{
    /**
     * Find Job Stats by Job Id
     *
     * @param JobId
     * @return Stats for that JobId
     */
    PublishingStats findJobStatsByJobId(Long JobId);

    PublishingStats findStatsByLastUpdated(Long jobId);

    Long findSuccessfullyPublishedGroupBook(Long ebookDefId);

    /**
     * Find Publishing stats for ebook
     *
     * @param EbookDefId
     * @return
     */
    List<PublishingStats> findPublishingStatsByEbookDef(Long EbookDefId);

    List<String> findSuccessfullyPublishedIsbnByTitleId(String titleId);

    List<String> findSuccessfullyPublishedsubGroupById(Long ebookDefId);

    Map<String, String> findSubGroupByVersion(Long boofDefnition);

    String findNameByIdAndVersion(Long boofDefnition, String version);

    /**
     * Find Publishing stats
     *
     * @param filter
     * @param sort
     * @return
     */
    List<PublishingStats> findPublishingStats(PublishingStatsFilter filter, PublishingStatsSort sort);

    List<PublishingStats> findPublishingStats(PublishingStatsFilter filter);

    List<PublishingStats> findPubStatsByEbookDefSort(Long EbookDefId);

    int numberOfPublishingStats(PublishingStatsFilter filter);

    /**
     * Save an Job Stats entry
     *
     */
    void saveJobStats(PublishingStats jobstats);

    /**
     * Delete an Job Stats entry
     *
     */
    void deleteJobStats(PublishingStats jobstats);

    /**
     * Get Maximum group version by book definition ID where status is
     * 'sendEmailNotification : Completed'
     *
     */
    Long getMaxGroupVersionById(Long EbookDefId);

    /**
     * Update an existing job stats entity
     *
     * @param Stats
     * @return update count
     */
    int updateJobStats(PublishingStats jobstats, StatsUpdateTypeEnum updateType);

    PublishingStats findJobStatsByPubStatsPK(PublishingStatsPK jobIdPK);

    EbookAudit findAuditInfoByJobId(Long jobId);

    List<PublishingStats> findAllPublishingStats();

    EbookAudit getMaxAuditId(Long eBookDefId);

    /**
     * Returns publishing stats of previous to jobInstanceId successful book
     * generation, where book definition is the same as used in specified job
     * instance
     *
     * @param jobInstanceId
     *            id of job instance
     * @return previous publishing stats or null if no stats found
     */
    PublishingStats getPreviousPublishingStatsForSameBook(long jobInstanceId);
}
