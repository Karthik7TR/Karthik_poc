/*
 * Copyright 2016: Thomson Reuters Global Resources. All Rights Reserved.
 * Proprietary and Confidential information of TRGR. Disclosure, Use or
 * Reproduction without the written authorization of TRGR is prohibited
 */
package com.thomsonreuters.uscl.ereader.stats.service;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Required;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.thomsonreuters.uscl.ereader.StatsUpdateTypeEnum;
import com.thomsonreuters.uscl.ereader.core.book.domain.EbookAudit;
import com.thomsonreuters.uscl.ereader.stats.dao.PublishingStatsDao;
import com.thomsonreuters.uscl.ereader.stats.domain.PublishingStats;
import com.thomsonreuters.uscl.ereader.stats.domain.PublishingStatsFilter;
import com.thomsonreuters.uscl.ereader.stats.domain.PublishingStatsPK;
import com.thomsonreuters.uscl.ereader.stats.domain.PublishingStatsSort;

import org.apache.commons.lang.StringUtils;

public class PublishingStatsServiceImpl implements PublishingStatsService {

	// private static final Logger LOG =
	// LogManager.getLogger(PublishingStatsServiceImpl.class);
	private PublishingStatsDao publishingStatsDAO;

	@Override
	@Transactional(readOnly = true)
	public PublishingStats findPublishingStatsByJobId(Long JobId) {
		return publishingStatsDAO.findJobStatsByJobId(JobId);
	}

	@Override
	@Transactional(readOnly = true)
	public Map<String, String> findSubGroupByVersion(Long boofDefnition) {
		return publishingStatsDAO.findSubGroupByVersion(boofDefnition);
	}

	@Override
	@Transactional(readOnly = true)
	public String findNameByBoofDefAndVersion(Long boofDefnition, String version) {
		return publishingStatsDAO.findNameByIdAndVersion(boofDefnition, version);
	}

	@Override
	@Transactional(readOnly = true)
	public List<PublishingStats> getPubStatsByEbookDefSort(Long EbookDefId) {
		return publishingStatsDAO.findPubStatsByEbookDefSort(EbookDefId);
	}

	@Override
	@Transactional(readOnly = true)
	public PublishingStats findStatsByLastUpdated(Long jobId) {
		return publishingStatsDAO.findStatsByLastUpdated(jobId);
	}

	@Override
	@Transactional(readOnly = true)
	public PublishingStats findJobStatsByPubStatsPK(PublishingStatsPK jobIdPK) {
		return publishingStatsDAO.findJobStatsByPubStatsPK(jobIdPK);
	}

	@Override
	@Transactional(readOnly = true)
	public List<PublishingStats> findPublishingStatsByEbookDef(Long EbookDefId) {
		return publishingStatsDAO.findPublishingStatsByEbookDef(EbookDefId);
	}

	@Override
	@Transactional(readOnly = true)
	public List<PublishingStats> findPublishingStats(PublishingStatsFilter filter, PublishingStatsSort sort) {
		return publishingStatsDAO.findPublishingStats(filter, sort);
	}

	@Override
	@Transactional(readOnly = true)
	public List<PublishingStats> findPublishingStats(PublishingStatsFilter filter) {
		return publishingStatsDAO.findPublishingStats(filter);
	}

	@Override
	@Transactional(readOnly = true)
	public int numberOfPublishingStats(PublishingStatsFilter filter) {
		return publishingStatsDAO.numberOfPublishingStats(filter);
	}

	@Override
	@Transactional(readOnly = true)
	public EbookAudit findAuditInfoByJobId(Long jobId) {
		return publishingStatsDAO.findAuditInfoByJobId(jobId);
	}

	@Override
	@Transactional
	public void savePublishingStats(PublishingStats jobstats) {
		publishingStatsDAO.saveJobStats(jobstats);

	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public int updatePublishingStats(PublishingStats jobstats, StatsUpdateTypeEnum updateType) {
		return publishingStatsDAO.updateJobStats(jobstats, updateType);
	}

	@Override
	@Transactional
	public void deleteJobStats(PublishingStats jobStats) {
		publishingStatsDAO.deleteJobStats(jobStats);
	}

	@Override
	@Transactional
	public Long getMaxGroupVersionById(Long ebookDefId) {
		return publishingStatsDAO.getMaxGroupVersionById(ebookDefId);
	}

	@Override
	@Transactional(readOnly = true)
	public Boolean hasIsbnBeenPublished(String isbn, String titleId) {
		String replacedIsbn = "";
		Boolean hasBeenPublished = false;

		if (StringUtils.isNotBlank(isbn)) {
			replacedIsbn = isbn.replace("-", "");
		}

		List<String> publishedIsbns = publishingStatsDAO.findSuccessfullyPublishedIsbnByTitleId(titleId);
		for (String publishedIsbn : publishedIsbns) {
			if (StringUtils.isNotBlank(publishedIsbn)) {
				String replacedPublishedIsbn = publishedIsbn.replace("-", "");
				if (replacedPublishedIsbn.equalsIgnoreCase(replacedIsbn)) {
					// ISBN has been published
					hasBeenPublished = true;
					break;
				}
			}
		}
		return hasBeenPublished;
	}

	@Override
	@Transactional(readOnly = true)
	public Boolean hasSubGroupChanged(String subGroupHeading, Long ebookDefId) {
		Boolean hasSubGroupChanged = true;
		List<String> previousSubGroupList = publishingStatsDAO.findSuccessfullyPublishedsubGroupById(ebookDefId);
		for (String previousSubGroupHeading : previousSubGroupList) {
			// previousSubGroupHeading could be null as it may be single book in
			// previous version
			if (previousSubGroupHeading != null && previousSubGroupHeading.equalsIgnoreCase(subGroupHeading)) {
				hasSubGroupChanged = false;
				break;
			}
		}
		return hasSubGroupChanged;
	}

	@Override
	@Transactional(readOnly = true)
	public Boolean hasBeenGrouped(Long ebookDefId) {
		Boolean hasBeenGrouped = true;
		Long previousGroupBook = publishingStatsDAO.findSuccessfullyPublishedGroupBook(ebookDefId);
		if (previousGroupBook == null) {
			return false;
		}
		return hasBeenGrouped;
	}

	@Override
	@Transactional(readOnly = true)
	public EbookAudit findLastSuccessfulJobStatsAuditByEbookDef(Long EbookDefId) {

		EbookAudit lastAuditSuccessful = null;
		PublishingStats lastSuccessfulPublishingStat = null;

		List<PublishingStats> publishingStats = publishingStatsDAO.findPublishingStatsByEbookDef(EbookDefId);

		if (publishingStats != null && publishingStats.size() > 0) {
			lastSuccessfulPublishingStat = publishingStats.get(0);
			for (PublishingStats publishingStat : publishingStats) {
				if (publishingStat.getJobInstanceId().longValue() >= lastSuccessfulPublishingStat.getJobInstanceId().longValue()
						&& (PublishingStats.SUCCESFULL_PUBLISH_STATUS.equalsIgnoreCase(publishingStat.getPublishStatus())
								|| PublishingStats.SEND_EMAIL_COMPLETE.equalsIgnoreCase(publishingStat.getPublishStatus()))) {
					lastSuccessfulPublishingStat = publishingStat;
					lastAuditSuccessful = publishingStat.getAudit();
				}
			}
		}
		return lastAuditSuccessful;

	}

	@Transactional(readOnly = true)
	public Date findLastPublishDateForBook(Long EbookDefId) {

		Date lastPublishDate = null;
		List<PublishingStats> publishingStats = publishingStatsDAO.findPublishingStatsByEbookDef(EbookDefId);
		PublishingStats lastPublishingStat = null;

		if (publishingStats != null && publishingStats.size() > 0) {
			lastPublishingStat = publishingStats.get(0);
			for (PublishingStats publishingStat : publishingStats) {
				if (lastPublishingStat.getPublishEndTimestamp() == null) {
					lastPublishingStat = publishingStat;
				}
				if (lastPublishingStat.getPublishEndTimestamp() != null
						&& publishingStat.getPublishEndTimestamp() != null
						&& (publishingStat.getPublishEndTimestamp() == lastPublishingStat.getPublishEndTimestamp()
								|| publishingStat.getPublishEndTimestamp().after(lastPublishingStat.getPublishEndTimestamp()))) {
					lastPublishingStat = publishingStat;
				}
			}
		}

		if (lastPublishingStat != null) {
			lastPublishDate = lastPublishingStat.getPublishEndTimestamp();
		}

		return lastPublishDate;
	}

	@Transactional(readOnly = true)
	public List<PublishingStats> findAllPublishingStats() {
		return publishingStatsDAO.findAllPublishingStats();
	}

	@Required
	public void setPublishingStatsDAO(PublishingStatsDao dao) {
		this.publishingStatsDAO = dao;
	}

	@Transactional(readOnly = true)
	public EbookAudit getMaxAuditId(Long eBookDefId) {
		return publishingStatsDAO.getMaxAuditId(eBookDefId);
	}

	@Transactional(readOnly = true)
	@Override
	public PublishingStats getPreviousPublishingStatsForSameBook(long jobInstanceId) {
		return publishingStatsDAO.getPreviousPublishingStatsForSameBook(jobInstanceId);
	}
}
