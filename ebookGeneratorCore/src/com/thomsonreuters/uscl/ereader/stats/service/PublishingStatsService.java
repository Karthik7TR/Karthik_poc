package com.thomsonreuters.uscl.ereader.stats.service;

import java.util.Date;
import java.util.List;

import com.thomsonreuters.uscl.ereader.StatsUpdateTypeEnum;
import com.thomsonreuters.uscl.ereader.core.book.domain.EbookAudit;
import com.thomsonreuters.uscl.ereader.stats.domain.PublishingStats;
import com.thomsonreuters.uscl.ereader.stats.domain.PublishingStatsFilter;
import com.thomsonreuters.uscl.ereader.stats.domain.PublishingStatsSort;
import org.jetbrains.annotations.Nullable;

public interface PublishingStatsService {
    Date getSysDate();

    /**
     * Find Job Stats by Job Id
     *
     * @param JobId
     * @return List of Stats for that JobId
     */
    PublishingStats findPublishingStatsByJobId(Long JobId);

    /**
     * Save an Job Stats entry
     *
     */
    void savePublishingStats(PublishingStats jobstats);

    Boolean hasBeenGrouped(Long ebookDefId);

    /**
     * Save an existing DocMetadata entity
     *
     * @param StatsUpdateTypeEnum
     *
     */
    void updatePublishingStats(PublishingStats jobstats, StatsUpdateTypeEnum updateType);

    List<PublishingStats> findPublishingStatsByEbookDef(Long EbookDefId);

    List<PublishingStats> findPublishingStats(PublishingStatsFilter filter, PublishingStatsSort sort);

    List<PublishingStats> findPublishingStats(PublishingStatsFilter filter);

    int numberOfPublishingStats(PublishingStatsFilter filter);

    EbookAudit findLastSuccessfulJobStatsAuditByEbookDef(Long EbookDefId);

    void deleteJobStats(PublishingStats jobStats);

    EbookAudit findAuditInfoByJobId(Long jobId);

    Date findLastPublishDateForBook(Long EbookDefId);

    List<PublishingStats> findAllPublishingStats();

    /**
     * Returns publishing stats of previous to jobInstanceId successful book
     * generation, where book definition is the same as used in specified job
     * instance
     *
     * @param jobInstanceId
     *            id of job instance
     * @return previous publishing stats or null if no stats found
     */
    @Nullable
    PublishingStats getPreviousPublishingStatsForSameBook(long jobInstanceId);

    String getIsbnByTitleAndVersion(String title, String version);
}
