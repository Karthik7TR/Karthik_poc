package com.thomsonreuters.uscl.ereader.stats.dao;

import java.util.List;

import org.hibernate.SessionFactory;
import org.springframework.jdbc.core.JdbcTemplate;

import com.thomsonreuters.uscl.ereader.StatsUpdateTypeEnum;
import com.thomsonreuters.uscl.ereader.core.book.domain.EbookAudit;
import com.thomsonreuters.uscl.ereader.stats.domain.PublishingStats;
import com.thomsonreuters.uscl.ereader.stats.domain.PublishingStatsPK;

public interface PublishingStatsDao {
	
	/**
	 * Find Job Stats by Job Id
	 * @param JobId
	 * @return Stats for that JobId
	 */
	public PublishingStats findJobStatsByJobId(Long JobId);	
	
	/**
	 * Find Job Stats by Ebook Definition Id
	 * @param  Ebook Definition Id
	 * @return Stats for that Ebook Definition
	 */
	public List<EbookAudit> findJobStatsAuditByEbookDef(Long EbookDefId);	
	
	/**
	 * Find Publishing stats for ebook
	 * @param EbookDefId
	 * @return
	 */
	public List<PublishingStats> findPublishingStatsByEbookDef(Long EbookDefId);
	
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
	 * Update an existing job stats entity
	 * @param  Stats
	 * @return update count
	 */
	public int updateJobStats(PublishingStats jobstats, StatsUpdateTypeEnum updateType);

	public PublishingStats findJobStatsByPubStatsPK(PublishingStatsPK jobIdPK);

	public EbookAudit findAuditInfoByJobId(Long jobId);

}
