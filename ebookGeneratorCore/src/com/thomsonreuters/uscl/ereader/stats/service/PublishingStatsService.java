package com.thomsonreuters.uscl.ereader.stats.service;

import java.util.Date;
import java.util.List;

import org.apache.poi.ss.usermodel.Workbook;

import com.thomsonreuters.uscl.ereader.StatsUpdateTypeEnum;
import com.thomsonreuters.uscl.ereader.core.book.domain.EbookAudit;
import com.thomsonreuters.uscl.ereader.stats.domain.PublishingStats;
import com.thomsonreuters.uscl.ereader.stats.domain.PublishingStatsFilter;
import com.thomsonreuters.uscl.ereader.stats.domain.PublishingStatsPK;
import com.thomsonreuters.uscl.ereader.stats.domain.PublishingStatsSort;

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
	
	public List<PublishingStats> findPublishingStatsByEbookDef(Long EbookDefId);
	
	public List<PublishingStats> getPubStatsByEbookDefSort(Long EbookDefId);
	
	public Boolean hasIsbnBeenPublished(String isbn, String titleId);
	
	/**
	 * To verify if subgroupheading has been updated with major version
	 * @param subGroupHeading
	 * @param Long ebookDefId
	 * @return
	 */
	public Boolean hasSubGroupChanged(String subGroupHeading, Long ebookDefId);
	
	public List<PublishingStats> findPublishingStats(PublishingStatsFilter filter, PublishingStatsSort sort);
	
	public List<PublishingStats> findPublishingStats(PublishingStatsFilter filter);
	
	public int numberOfPublishingStats(PublishingStatsFilter filter); 
	
	public EbookAudit findLastSuccessfulJobStatsAuditByEbookDef(Long EbookDefId);
	
	public PublishingStats findJobStatsByPubStatsPK(PublishingStatsPK jobIdPK);

	public void deleteJobStats(PublishingStats jobStats);

	public EbookAudit findAuditInfoByJobId(Long jobId);
	
	public Date findLastPublishDateForBook(Long EbookDefId);
	
	public List<PublishingStats> findAllPublishingStats();
	
	public Workbook createExcelDocument(PublishingStatsFilter filter);
	
	public Long getMaxGroupVersionById(Long ebookDefId);
	
}
