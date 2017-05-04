package com.thomsonreuters.uscl.ereader.stats.dao;

import java.util.Date;
import java.util.List;

import com.thomsonreuters.uscl.ereader.core.book.domain.EbookAudit;
import com.thomsonreuters.uscl.ereader.stats.domain.PublishingStats;
import com.thomsonreuters.uscl.ereader.stats.domain.PublishingStatsFilter;
import com.thomsonreuters.uscl.ereader.stats.domain.PublishingStatsSort;

public interface PublishingStatsDao
{
    Date getSysDate();

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

    List<PublishingStats> findAllPublishingStats();

    /**
     * Find Job Stats by Job Id
     *
     * @param JobId
     * @return Stats for that JobId
     */
    PublishingStats findJobStatsByJobId(Long JobId);

    /**
     * Returns publishing stats of previous to jobInstanceId successful book
     * generation, where book definition is the same as used in specified job
     * instance
     *
     * @param jobInstanceId
     *            id of job instance
     * @return previous publishing stats or null if no stats found
     */
    PublishingStats getPreviousPublishingStatsForSameBook(long jobId);

    EbookAudit findAuditInfoByJobId(Long jobId);

    /**
     * Find Publishing stats for ebook
     *
     * @param EbookDefId
     * @return
     */
    List<PublishingStats> findPublishingStatsByEbookDef(Long ebookDefId);

    Long findSuccessfullyPublishedGroupBook(Long ebookDefId);

    List<String> findSuccessfullyPublishedIsbnByTitleId(String titleId);

    /**
     * Find Publishing stats
     *
     * @param filter
     * @param sort
     * @return
     */
    List<PublishingStats> findPublishingStats(PublishingStatsFilter filter, PublishingStatsSort sort);

    List<PublishingStats> findPublishingStats(PublishingStatsFilter filter);

    int numberOfPublishingStats(PublishingStatsFilter filter);
}
