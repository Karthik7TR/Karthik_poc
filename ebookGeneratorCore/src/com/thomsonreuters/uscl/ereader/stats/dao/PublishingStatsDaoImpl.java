/*
 * Copyright 2012: Thomson Reuters Global Resources. All Rights Reserved.
 * Proprietary and Confidential information of TRGR. Disclosure, Use or
 * Reproduction without the written authorization of TRGR is prohibited
 */
package com.thomsonreuters.uscl.ereader.stats.dao;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.thomsonreuters.uscl.ereader.StatsUpdateTypeEnum;
import com.thomsonreuters.uscl.ereader.core.book.domain.EbookAudit;
import com.thomsonreuters.uscl.ereader.stats.domain.PublishingStats;
import com.thomsonreuters.uscl.ereader.stats.domain.PublishingStatsPK;

public class PublishingStatsDaoImpl implements PublishingStatsDao {

	private static final Logger LOG = Logger
			.getLogger(PublishingStatsDaoImpl.class);
	private SessionFactory sessionFactory;

	// @Required
	// public void setSessionFactory(SessionFactory sessionfactory) {
	// this.sessionFactory = sessionfactory;
	// }

	public PublishingStatsDaoImpl(SessionFactory hibernateSessionFactory) {
		this.sessionFactory = hibernateSessionFactory;
	}

	@Override
	@Transactional(readOnly = true)
	public PublishingStats findJobStatsByPubStatsPK(PublishingStatsPK jobIdPK) {

		Session session = sessionFactory.getCurrentSession();
		return (PublishingStats) session.get(PublishingStats.class, jobIdPK);
	}

	@Override
	@Transactional(readOnly = true)
	public PublishingStats findJobStatsByJobId(Long JobId) {

		Session session = sessionFactory.getCurrentSession();

		PublishingStats pubStats = (PublishingStats) session
				.createCriteria(PublishingStats.class)
				.add(Restrictions.eq("jobInstanceId", JobId)).uniqueResult();

		return (pubStats);
	}

	@Override
	@Transactional
	public void saveJobStats(PublishingStats jobstats) {
		Session session = sessionFactory.getCurrentSession();
		session.save(jobstats);
		session.flush();

	}

	@Transactional
	public void deleteJobStats(PublishingStats toRemove) {
		toRemove = (PublishingStats) sessionFactory.getCurrentSession().merge(
				toRemove);
		sessionFactory.getCurrentSession().delete(toRemove);
		sessionFactory.getCurrentSession().flush();
	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public int updateJobStats(PublishingStats jobstats,
			StatsUpdateTypeEnum updateType) {
		StringBuffer hql = new StringBuffer("update PublishingStats set   ");
		if (updateType.equals(StatsUpdateTypeEnum.GATHERTOC)) {
			hql.append("gatherTocNodeCount = ");
			hql.append(jobstats.getGatherTocNodeCount());
			hql.append(", gatherTocDocCount = ");
			hql.append(jobstats.getGatherTocDocCount());
			hql.append(", gatherTocRetryCount = ");
			hql.append(jobstats.getGatherTocRetryCount());
			hql.append(", gatherTocSkippedCount = ");
			hql.append(jobstats.getGatherTocSkippedCount());
		} else if (updateType.equals(StatsUpdateTypeEnum.GATHERDOC)) {
			hql.append("gatherDocExpectedCount = ");
			hql.append(jobstats.getGatherDocExpectedCount());
			hql.append(", gatherDocRetrievedCount = ");
			hql.append(jobstats.getGatherDocRetrievedCount());
			hql.append(", gatherDocRetryCount = ");
			hql.append(jobstats.getGatherDocRetryCount());
			hql.append(", gatherMetaExpectedCount = ");
			hql.append(jobstats.getGatherMetaExpectedCount());
			hql.append(", gatherMetaRetrievedCount = ");
			hql.append(jobstats.getGatherMetaRetrievedCount());
			hql.append(", gatherMetaRetryCount = ");
			hql.append(jobstats.getGatherMetaRetryCount());
		} else if (updateType.equals(StatsUpdateTypeEnum.GATHERIMAGE)) {
			hql.append("gatherImageExpectedCount = ");
			hql.append(jobstats.getGatherImageExpectedCount());
			hql.append(", gatherImageRetrievedCount = ");
			hql.append(jobstats.getGatherImageRetrievedCount());
			hql.append(", gatherImageRetryCount = ");
			hql.append(jobstats.getGatherImageRetryCount());
		} else if (updateType.equals(StatsUpdateTypeEnum.TITLEDOC)) {
			hql.append("titleDocCount = ");
			hql.append(jobstats.getTitleDocCount());
		} else if (updateType.equals(StatsUpdateTypeEnum.TITLEDUPDOCCOUNT)) {
			hql.append("titleDupDocCount = ");
			hql.append(jobstats.getTitleDupDocCount());
		} else if (updateType.equals(StatsUpdateTypeEnum.FORMATDOC)) {
			hql.append("formatDocCount = ");
			hql.append(jobstats.getFormatDocCount());
		} else if (updateType.equals(StatsUpdateTypeEnum.ASSEMBLEDOC)) {
			hql.append("bookSize = ");
			hql.append(jobstats.getBookSize());
			hql.append(", largestDocSize = ");
			hql.append(jobstats.getLargestDocSize());
			hql.append(", largestImageSize = ");
			hql.append(jobstats.getLargestImageSize());
			hql.append(", largestPdfSize = ");
			hql.append(jobstats.getLargestPdfSize());
		} else if (updateType.equals(StatsUpdateTypeEnum.FINALPUBLISH)) {
			hql.append("publishEndTimestamp = sysdate");
		} else if (updateType.equals(StatsUpdateTypeEnum.GENERAL)) {
			
		}
		else {
			LOG.error("Unknown StatsUpdateTypeEnum");
			// TODO: failure logic
		}
		if (StringUtils.isNotBlank(jobstats.getPublishStatus())) {
			if (updateType.equals(StatsUpdateTypeEnum.GENERAL))
			{
			  hql.append(String.format("publishStatus = '%s'", jobstats.getPublishStatus()));
			}
			else 
			{
		      hql.append(String.format(", publishStatus = '%s'", jobstats.getPublishStatus()));
			}
		} else {
			hql.append(", publishStatus = null");
		}
		
		hql.append(", lastUpdated = sysdate ");

		hql.append(" where jobInstanceId =  "); // WHERE clause
		hql.append(jobstats.getJobInstanceId());

		LOG.debug("hql.toString() ="+hql.toString());
		// Create query and populate it with where clause values
		Session session = sessionFactory.getCurrentSession();
		Query query = session.createQuery(hql.toString());

		// query.setLong(0, jobstats.getJobInstanceId());

		int result = 0;
		try {
			result = query.executeUpdate();
			session.flush();
		} catch (HibernateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return result;
	}

	@Override
	public EbookAudit findAuditInfoByJobId(Long jobId) {
		StringBuffer hql = new StringBuffer(
				"select ea  from PublishingStats ps, EbookAudit ea ");

		hql.append(" where ps.jobInstanceId =  "); // WHERE clause
		hql.append(jobId);
		// hql.append(" and PublishingStats.ebookDefId = EbookAudit.ebookDefId "
		// );
		hql.append(" and ps.auditId = ea.auditId ");

		// Create query and populate it with where clause values
		Session session = sessionFactory.getCurrentSession();
		Query query = session.createQuery(hql.toString());

		EbookAudit auditResult = (EbookAudit) query.uniqueResult();
		return (auditResult);

	}

	@SuppressWarnings("unchecked")
	@Override
	@Transactional
	public List<EbookAudit> findJobStatsAuditByEbookDef(Long EbookDefId) {
		StringBuffer hql = new StringBuffer(
				"select ea from PublishingStats ps, EbookAudit ea  ");

		hql.append(" where ps.ebookDefId =  "); // WHERE clause
		hql.append(EbookDefId);
		// hql.append(" and PublishingStats.ebookDefId = EbookAudit.ebookDefId "
		// );
		hql.append(" and ps.auditId = ea.auditId ");

		// Create query and populate it with where clause values
		Session session = sessionFactory.getCurrentSession();
		Query query = session.createQuery(hql.toString());

		return (List<EbookAudit>) query.list();
	}

	/**
	 * Find Publishing stats for ebook
	 * 
	 * @param EbookDefId
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@Override
	@Transactional
	public List<PublishingStats> findPublishingStatsByEbookDef(Long EbookDefId) {
		StringBuffer hql = new StringBuffer(
				"select ps from PublishingStats ps ");

		hql.append(" where ps.ebookDefId =  ");
		hql.append(EbookDefId);

		Session session = sessionFactory.getCurrentSession();
		Query query = session.createQuery(hql.toString());

		return (List<PublishingStats>) query.list();

	}

}
