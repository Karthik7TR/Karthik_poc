package com.thomsonreuters.uscl.ereader.stats.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Required;
import org.springframework.transaction.annotation.Transactional;

import com.thomsonreuters.uscl.ereader.StatsUpdateTypeEnum;
import com.thomsonreuters.uscl.ereader.core.book.domain.EbookAudit;
import com.thomsonreuters.uscl.ereader.stats.dao.PublishingStatsDao;
import com.thomsonreuters.uscl.ereader.stats.domain.PublishingStats;
import com.thomsonreuters.uscl.ereader.stats.domain.PublishingStatsPK;

public class PublishingStatsServiceImpl implements PublishingStatsService {

	// private static final Logger LOG =
	// Logger.getLogger(PublishingStatsServiceImpl.class);

	private PublishingStatsDao publishingStatsDAO;
	private static final String SUCCESFULL_PUBLISH_STATUS = "Publish Step Completed";

	@Override
	@Transactional(readOnly = true)
	public PublishingStats findPublishingStatsByJobId(Long JobId) {
		return publishingStatsDAO.findJobStatsByJobId(JobId);
	}

	@Override
	@Transactional(readOnly = true)
	public PublishingStats findJobStatsByPubStatsPK(PublishingStatsPK jobIdPK) {
		return publishingStatsDAO.findJobStatsByPubStatsPK(jobIdPK);
	}

	@Override
	@Transactional(readOnly = true)
	public List<EbookAudit> findJobStatsAuditByEbookDef(Long EbookDefId) {
		return publishingStatsDAO.findJobStatsAuditByEbookDef(EbookDefId);
	}

	@Override
	@Transactional(readOnly = true)
	public List<PublishingStats> findPublishingStatsByEbookDef(Long EbookDefId) {
		return publishingStatsDAO.findPublishingStatsByEbookDef(EbookDefId);
	}

	@Override
	@Transactional(readOnly = true)
	public EbookAudit findAuditInfoByJobId(Long jobId) {
		return publishingStatsDAO.findAuditInfoByJobId(jobId);
	}

	@Override
	public void savePublishingStats(PublishingStats jobstats) {
		publishingStatsDAO.saveJobStats(jobstats);

	}

	@Override
	public int updatePublishingStats(PublishingStats jobstats,
			StatsUpdateTypeEnum updateType) {
		return publishingStatsDAO.updateJobStats(jobstats, updateType);
	}

	@Override
	public void deleteJobStats(PublishingStats jobStats) {
		publishingStatsDAO.deleteJobStats(jobStats);
	}

	@Required
	public void setPublishingStatsDAO(PublishingStatsDao dao) {
		this.publishingStatsDAO = dao;
	}

	@Override
	public EbookAudit findLastSuccessfulJobStatsAuditByEbookDef(Long EbookDefId) {

		EbookAudit lastAudit = null;

		List<EbookAudit> ebookAudits = findJobStatsAuditByEbookDef(EbookDefId);

		if (ebookAudits != null && ebookAudits.size() > 0) {
			lastAudit = ebookAudits.get(0);
			for (EbookAudit ebookAudit : ebookAudits) {
				if (ebookAudit.getAuditId() >= lastAudit.getAuditId()) {
					lastAudit = ebookAudit;
				}
			}
		}

		PublishingStats lastSuccessfulPublishingStat = null;
		List<PublishingStats> publishingStats = findPublishingStatsByEbookDef(EbookDefId);

		if (publishingStats != null && publishingStats.size() > 0) {
			lastSuccessfulPublishingStat = publishingStats.get(0);
			for (PublishingStats publishingStat : publishingStats) {
				if (publishingStat.getAuditId() == lastAudit.getAuditId()
						&& publishingStat.getJobInstanceId() >= lastSuccessfulPublishingStat
								.getJobInstanceId()
						&& SUCCESFULL_PUBLISH_STATUS
								.equalsIgnoreCase(publishingStat
										.getPublishStatus())) {
					lastSuccessfulPublishingStat = publishingStat;
				}
			}
		}

		if (!SUCCESFULL_PUBLISH_STATUS
				.equalsIgnoreCase(lastSuccessfulPublishingStat
						.getPublishStatus())) {
			lastAudit = null;
		}

		return lastAudit;

	}

}
