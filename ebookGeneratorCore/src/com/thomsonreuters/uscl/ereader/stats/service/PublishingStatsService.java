package com.thomsonreuters.uscl.ereader.stats.service;

import java.util.List;

import com.thomsonreuters.uscl.ereader.StatsUpdateTypeEnum;
import com.thomsonreuters.uscl.ereader.core.book.domain.EbookAudit;
import com.thomsonreuters.uscl.ereader.stats.dao.PublishingStatsDao;
import com.thomsonreuters.uscl.ereader.stats.domain.PublishingStats;
import com.thomsonreuters.uscl.ereader.stats.domain.PublishingStatsPK;

public interface PublishingStatsService {
	
	/**
	 * Find Job Stats by Job Id
	 * @param JobId
	 * @return List of Stats for that JobId
	 */
	public PublishingStats findPublishingStatsByJobId(Long JobId);
	
	
	/**
	 * Save an Job Stats entry
	 * 
	 */
	public void savePublishingStats(PublishingStats jobstats);
	
	/**
	 * Save an existing DocMetadata entity
	 * @param StatsUpdateTypeEnum 
	 * 
	 */
	public int updatePublishingStats(PublishingStats jobstats, StatsUpdateTypeEnum updateType);


	public List<EbookAudit> findJobStatsAuditByEbookDef(Long ebookDefId);
	
	public List<PublishingStats> findPublishingStatsByEbookDef(Long EbookDefId);


	public PublishingStats findJobStatsByPubStatsPK(PublishingStatsPK jobIdPK);


	public void deleteJobStats(PublishingStats jobStats);

	public EbookAudit findAuditInfoByJobId(Long jobId);
	
	
}
