package com.thomsonreuters.uscl.ereader.stats.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Required;

import com.thomsonreuters.uscl.ereader.StatsUpdateTypeEnum;
import com.thomsonreuters.uscl.ereader.core.book.domain.EbookAudit;
import com.thomsonreuters.uscl.ereader.stats.dao.PublishingStatsDao;
import com.thomsonreuters.uscl.ereader.stats.domain.PublishingStats;
import com.thomsonreuters.uscl.ereader.stats.domain.PublishingStatsPK;

public class PublishingStatsServiceImpl implements PublishingStatsService {
	
//	private static final Logger LOG = Logger.getLogger(PublishingStatsServiceImpl.class);

	private PublishingStatsDao publishingStatsDAO;

	@Override
	public PublishingStats findPublishingStatsByJobId(Long JobId) {
		return publishingStatsDAO.findJobStatsByJobId(JobId);
	}
	
	@Override
	public PublishingStats findJobStatsByPubStatsPK(PublishingStatsPK jobIdPK) {
		return publishingStatsDAO.findJobStatsByPubStatsPK(jobIdPK);
	}
	
	@Override
	public List<EbookAudit> findJobStatsAuditByEbookDef(Long EbookDefId)
	{
		return publishingStatsDAO.findJobStatsAuditByEbookDef(EbookDefId);
		}
	
	@Override
	public EbookAudit findAuditInfoByJobId(Long jobId)
	{
		return publishingStatsDAO.findAuditInfoByJobId(jobId);
		}


	@Override
	public void savePublishingStats(PublishingStats jobstats) {
		publishingStatsDAO.saveJobStats(jobstats);
		
		
	}

	@Override
	public int updatePublishingStats(PublishingStats jobstats, StatsUpdateTypeEnum updateType) {
		return publishingStatsDAO.updateJobStats(jobstats, updateType);
	}
	
	@Override
	public void deleteJobStats(PublishingStats jobStats)
	{
		publishingStatsDAO.deleteJobStats(jobStats);
}

	
	@Required
	public void setPublishingStatsDAO(PublishingStatsDao dao) {
		this.publishingStatsDAO = dao;
	}
}
