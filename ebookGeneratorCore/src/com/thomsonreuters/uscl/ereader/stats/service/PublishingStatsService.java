package com.thomsonreuters.uscl.ereader.stats.service;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.thomsonreuters.uscl.ereader.StatsUpdateTypeEnum;
import com.thomsonreuters.uscl.ereader.core.book.domain.EbookAudit;
import com.thomsonreuters.uscl.ereader.stats.domain.PublishingStats;
import com.thomsonreuters.uscl.ereader.stats.domain.PublishingStatsFilter;
import com.thomsonreuters.uscl.ereader.stats.domain.PublishingStatsPK;
import com.thomsonreuters.uscl.ereader.stats.domain.PublishingStatsSort;
import org.jetbrains.annotations.Nullable;

public interface PublishingStatsService
{
    /**
     * Find Job Stats by Job Id
     *
     * @param JobId
     * @return List of Stats for that JobId
     */
    PublishingStats findPublishingStatsByJobId(Long JobId);

    Map<String, String> findSubGroupByVersion(Long boofDefnition);

    String findNameByBoofDefAndVersion(Long boofDefnition, String version);

    /**
     * Save an Job Stats entry
     *
     */
    void savePublishingStats(PublishingStats jobstats);

    PublishingStats findStatsByLastUpdated(Long jobId);

    Boolean hasBeenGrouped(Long ebookDefId);

    /**
     * Save an existing DocMetadata entity
     *
     * @param StatsUpdateTypeEnum
     *
     */
    int updatePublishingStats(PublishingStats jobstats, StatsUpdateTypeEnum updateType);

    List<PublishingStats> findPublishingStatsByEbookDef(Long EbookDefId);

    List<PublishingStats> getPubStatsByEbookDefSort(Long EbookDefId);

    Boolean hasIsbnBeenPublished(String isbn, String titleId);

    /**
     * To verify if subgroupheading has been updated with major version
     *
     * @param subGroupHeading
     * @param Long
     *            ebookDefId
     * @return
     */
    Boolean hasSubGroupChanged(String subGroupHeading, Long ebookDefId);

    List<PublishingStats> findPublishingStats(PublishingStatsFilter filter, PublishingStatsSort sort);

    List<PublishingStats> findPublishingStats(PublishingStatsFilter filter);

    int numberOfPublishingStats(PublishingStatsFilter filter);

    EbookAudit findLastSuccessfulJobStatsAuditByEbookDef(Long EbookDefId);

    PublishingStats findJobStatsByPubStatsPK(PublishingStatsPK jobIdPK);

    void deleteJobStats(PublishingStats jobStats);

    EbookAudit findAuditInfoByJobId(Long jobId);

    Date findLastPublishDateForBook(Long EbookDefId);

    List<PublishingStats> findAllPublishingStats();

    Long getMaxGroupVersionById(Long ebookDefId);

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
    @Nullable
    PublishingStats getPreviousPublishingStatsForSameBook(long jobInstanceId);
}
