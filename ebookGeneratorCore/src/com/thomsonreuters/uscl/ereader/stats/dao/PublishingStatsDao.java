package com.thomsonreuters.uscl.ereader.stats.dao;

import java.util.List;

import com.thomsonreuters.uscl.ereader.StatsUpdateTypeEnum;
import com.thomsonreuters.uscl.ereader.core.book.domain.EbookAudit;
import com.thomsonreuters.uscl.ereader.stats.domain.PublishingStats;
import com.thomsonreuters.uscl.ereader.stats.domain.PublishingStatsFilter;
import com.thomsonreuters.uscl.ereader.stats.domain.PublishingStatsPK;
import com.thomsonreuters.uscl.ereader.stats.domain.PublishingStatsSort;

public interface PublishingStatsDao {
	
	/**
	 * Find Job Stats by Job Id
	 * @param JobId
	 * @return Stats for that JobId
	 */
	public PublishingStats findJobStatsByJobId(Long JobId);	
	
	public PublishingStats findStatsByLastUpdated(Long jobId);
	
	/**
	 * Find Publishing stats for ebook
	 * @param EbookDefId
	 * @return
	 */
	public List<PublishingStats> findPublishingStatsByEbookDef(Long EbookDefId);
	
	public List<String> findSuccessfullyPublishedIsbnByTitleId(String titleId);
	
	public List<String> findSuccessfullyPublishedsubGroupById(Long ebookDefId);
	
	/**
	 * Find Publishing stats
	 * @param filter
	 * @param sort
	 * @return
	 */
	public List<PublishingStats> findPublishingStats(PublishingStatsFilter filter, PublishingStatsSort sort);
	
	public List<PublishingStats> findPublishingStats(PublishingStatsFilter filter);
	
	public List<PublishingStats> findPubStatsByEbookDefSort(Long EbookDefId);
	
	public int numberOfPublishingStats(PublishingStatsFilter filter); 
	
	/**
	 * Save an Job Stats entry
	 * 
	 */
	public void saveJobStats(PublishingStats jobstats);
	
	/**
	 * Delete an Job Stats entry
	 * 
	 */
	public void deleteJobStats(PublishingStats jobstats);
	/**
	 * Get Maximum group version by book definition ID
	 * where status is  'sendEmailNotification : Completed'
	 * 
	 */
	public Long getMaxGroupVersionById(Long EbookDefId);
	
	/**
	 * Update an existing job stats entity
	 * @param  Stats
	 * @return update count
	 */
	public int updateJobStats(PublishingStats jobstats, StatsUpdateTypeEnum updateType);

	public PublishingStats findJobStatsByPubStatsPK(PublishingStatsPK jobIdPK);

	public EbookAudit findAuditInfoByJobId(Long jobId);
	
	public List<PublishingStats> findAllPublishingStats();
	
	public EbookAudit getMaxAuditId(Long eBookDefId);

}
