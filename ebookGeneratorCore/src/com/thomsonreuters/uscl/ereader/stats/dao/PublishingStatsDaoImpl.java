/*
 * Copyright 2016: Thomson Reuters Global Resources. All Rights Reserved.
 * Proprietary and Confidential information of TRGR. Disclosure, Use or
 * Reproduction without the written authorization of TRGR is prohibited
 */

package com.thomsonreuters.uscl.ereader.stats.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.FetchMode;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

import com.thomsonreuters.uscl.ereader.StatsUpdateTypeEnum;
import com.thomsonreuters.uscl.ereader.core.book.domain.EbookAudit;
import com.thomsonreuters.uscl.ereader.stats.domain.PublishingStats;
import com.thomsonreuters.uscl.ereader.stats.domain.PublishingStatsFilter;
import com.thomsonreuters.uscl.ereader.stats.domain.PublishingStatsPK;
import com.thomsonreuters.uscl.ereader.stats.domain.PublishingStatsSort;
import com.thomsonreuters.uscl.ereader.stats.domain.PublishingStatsSort.SortProperty;

public class PublishingStatsDaoImpl implements PublishingStatsDao {

	private static final Logger LOG = LogManager.getLogger(PublishingStatsDaoImpl.class);
	private SessionFactory sessionFactory;

	// @Required
	// public void setSessionFactory(SessionFactory sessionfactory) {
	// this.sessionFactory = sessionfactory;
	// }

	public PublishingStatsDaoImpl(SessionFactory hibernateSessionFactory) {
		this.sessionFactory = hibernateSessionFactory;
	}

	@Override
	public PublishingStats findStatsByLastUpdated(Long jobId) {
		StringBuffer hql = new StringBuffer(
				"select ps from PublishingStats ps where ps.lastUpdated = (select max(ps2.lastUpdated) from PublishingStats ps2 where ps.ebookDefId = ps2.ebookDefId ");
		hql.append(" and ps2.gatherTocNodeCount is not null)  ");
		hql.append(" and ps.ebookDefId =  ");
		hql.append(jobId);
		// Create query and populate it with where clause values
		Session session = sessionFactory.getCurrentSession();
		Query query = session.createQuery(hql.toString());

		PublishingStats pubStats = (PublishingStats) query.uniqueResult();
		return (pubStats);
	}

	@Override
	public PublishingStats findJobStatsByPubStatsPK(PublishingStatsPK jobIdPK) {

		Session session = sessionFactory.getCurrentSession();
		return (PublishingStats) session.get(PublishingStats.class, jobIdPK);
	}

	@Override
	public PublishingStats findJobStatsByJobId(Long JobId) {

		Session session = sessionFactory.getCurrentSession();

		PublishingStats pubStats = (PublishingStats) session.createCriteria(PublishingStats.class)
				.setFetchMode("audit", FetchMode.JOIN).add(Restrictions.eq("jobInstanceId", JobId)).uniqueResult();

		return (pubStats);
	}

	@Override
	public Map<String, String> findSubGroupByVersion(Long boofDefnition) {

		StringBuffer hql = new StringBuffer(
				"select ps.BOOK_VERSION_SUBMITTED, a.SUBGROUP_HEADING from PUBLISHING_STATS ps inner join(");
		hql.append(
				" select BOOK_VERSION_SUBMITTED,max(last_updated) as maxt from PUBLISHING_STATS where ebook_definition_id=");
		hql.append(boofDefnition);
		hql.append(" group by BOOK_VERSION_SUBMITTED) b on b.maxt = ps.last_updated");
		hql.append(" join ebook_audit a on a.AUDIT_ID = ps.AUDIT_ID order by ps.BOOK_VERSION_SUBMITTED ");

		Session session = sessionFactory.getCurrentSession();
		Query query = session.createSQLQuery(hql.toString());
		@SuppressWarnings("unchecked")
		List<Object[]> objectList = query.list();

		Map<String, String> eBookSubgroupVersionMap = new HashMap<String, String>();

		for (Object[] arr : objectList) {
			String bookVersionSubmitted;
			String subGroupHeading;
			bookVersionSubmitted = "v" + arr[0].toString();
			if (arr[1] != null) {
				subGroupHeading = arr[1].toString();
				eBookSubgroupVersionMap.put(bookVersionSubmitted, subGroupHeading);
			}

		}
		return eBookSubgroupVersionMap;

	}

	public String findNameByIdAndVersion(Long boofDefnition, String version) {
		StringBuffer hql = new StringBuffer("select a.PROVIEW_DISPLAY_NAME from PUBLISHING_STATS ps inner join(");
		hql.append(
				" select BOOK_VERSION_SUBMITTED,max(last_updated) as maxt from PUBLISHING_STATS where ebook_definition_id=");
		hql.append(boofDefnition);
		hql.append(" and BOOK_VERSION_SUBMITTED= '");
		hql.append(StringUtils.substringAfter(version, "v") + "'");
		hql.append(" group by BOOK_VERSION_SUBMITTED) b on b.maxt = ps.last_updated");
		hql.append(" join ebook_audit a on a.AUDIT_ID = ps.AUDIT_ID and a.ebook_definition_id= ");
		hql.append(boofDefnition);

		Session session = sessionFactory.getCurrentSession();
		Query query = session.createSQLQuery(hql.toString());
		String proviewDisplayName = (String) query.uniqueResult();
		return proviewDisplayName;
	}

	@Override
	public void saveJobStats(PublishingStats jobstats) {
		Session session = sessionFactory.getCurrentSession();
		session.save(jobstats);
		session.flush();

	}

	public void deleteJobStats(PublishingStats toRemove) {
		toRemove = (PublishingStats) sessionFactory.getCurrentSession().merge(toRemove);
		sessionFactory.getCurrentSession().delete(toRemove);
		sessionFactory.getCurrentSession().flush();
	}

	@Override
	public Long getMaxGroupVersionById(Long eBookDefId) {
		Session session = sessionFactory.getCurrentSession();

		Query query = session
				.createQuery("select max(groupVersion) from PublishingStats " + "where ebookDefId = :ebookDefId ");
		query.setParameter("ebookDefId", eBookDefId);
		return (Long) query.uniqueResult();
	}

	@Override
	public EbookAudit getMaxAuditId(Long eBookDefId) {
		Session session = sessionFactory.getCurrentSession();

		Query query = session
				.createQuery("select max(audit) from PublishingStats " + "where ebookDefId = :ebookDefId ");
		query.setParameter("ebookDefId", eBookDefId);
		return (EbookAudit) query.uniqueResult();
	}

	@Override
	public int updateJobStats(PublishingStats jobstats, StatsUpdateTypeEnum updateType) {
		StringBuffer hql = new StringBuffer("update PublishingStats set   ");
		if (updateType.equals(StatsUpdateTypeEnum.GATHERTOC) || updateType.equals(StatsUpdateTypeEnum.GENERATETOC)) {
			hql.append("gatherTocNodeCount = ");
			hql.append(jobstats.getGatherTocNodeCount());
			hql.append(", gatherTocDocCount = ");
			hql.append(jobstats.getGatherTocDocCount());
			hql.append(", gatherTocRetryCount = ");
			hql.append(jobstats.getGatherTocRetryCount());
			hql.append(", gatherTocSkippedCount = ");
			hql.append(jobstats.getGatherTocSkippedCount());
		} else if (updateType.equals(StatsUpdateTypeEnum.GATHERDOC)
				|| updateType.equals(StatsUpdateTypeEnum.GENERATEDOC)) {
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

		} else if (updateType.equals(StatsUpdateTypeEnum.GROUPEBOOK)) {
			hql.append("groupVersion = ");
			hql.append(jobstats.getGroupVersion());
		}

		else {
			LOG.error("Unknown StatsUpdateTypeEnum");
			// TODO: failure logic
		}
		if (StringUtils.isNotBlank(jobstats.getPublishStatus())) {
			if (updateType.equals(StatsUpdateTypeEnum.GENERAL)) {
				hql.append(String.format("publishStatus = '%s'", jobstats.getPublishStatus()));
			} else {
				hql.append(String.format(", publishStatus = '%s'", jobstats.getPublishStatus()));
			}
		} else {
			hql.append(", publishStatus = null");
		}

		hql.append(", lastUpdated = sysdate ");

		hql.append(" where jobInstanceId =  "); // WHERE clause
		hql.append(jobstats.getJobInstanceId());

		LOG.debug("hql.toString() =" + hql.toString());
		// Create query and populate it with where clause values
		Session session = sessionFactory.getCurrentSession();
		Query query = session.createQuery(hql.toString());

		// query.setLong(0, jobstats.getJobInstanceId());

		int result = 0;
		try {
			result = query.executeUpdate();
			session.flush();
		} catch (HibernateException e) {
			e.printStackTrace();
		}

		return result;
	}

	@Override
	public EbookAudit findAuditInfoByJobId(Long jobId) {
		StringBuffer hql = new StringBuffer("select ea  from PublishingStats ps, EbookAudit ea ");

		hql.append(" where ps.jobInstanceId =  "); // WHERE clause
		hql.append(jobId);
		// hql.append(" and PublishingStats.ebookDefId = EbookAudit.ebookDefId "
		// );
		hql.append(" and ps.audit.auditId = ea.auditId ");

		// Create query and populate it with where clause values
		Session session = sessionFactory.getCurrentSession();
		Query query = session.createQuery(hql.toString());

		EbookAudit auditResult = (EbookAudit) query.uniqueResult();
		return (auditResult);

	}

	/**
	 * Find Publishing stats for ebook
	 * 
	 * @param EbookDefId
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<PublishingStats> findPublishingStatsByEbookDef(Long EbookDefId) {
		Session session = sessionFactory.getCurrentSession();

		Criteria criteria = session.createCriteria(PublishingStats.class).createAlias("audit", "book")
				.setFetchMode("audit", FetchMode.JOIN).add(Restrictions.eq("ebookDefId", EbookDefId));

		return criteria.list();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<PublishingStats> findPubStatsByEbookDefSort(Long EbookDefId) {
		Session session = sessionFactory.getCurrentSession();

		Criteria criteria = session.createCriteria(PublishingStats.class).add(Restrictions.eq("ebookDefId", EbookDefId))
				.addOrder(Order.desc("jobInstanceId"));
		return criteria.list();
	}

	@SuppressWarnings("unchecked")
	public List<String> findSuccessfullyPublishedIsbnByTitleId(String titleId) {
		Session session = sessionFactory.getCurrentSession();

		Criteria criteria = session.createCriteria(PublishingStats.class).createAlias("audit", "book")
				.setFetchMode("audit", FetchMode.JOIN).add(Restrictions.eq("book.titleId", titleId))
				.add(Restrictions.in("publishStatus",
						new String[] { PublishingStats.SEND_EMAIL_COMPLETE,
								PublishingStats.SUCCESFULL_PUBLISH_STATUS }))
				.setProjection(Projections.distinct(Projections.property("book.isbn")));
		return criteria.list();
	}

	@SuppressWarnings("unchecked")
	public List<String> findSuccessfullyPublishedsubGroupById(Long ebookDefId) {
		Session session = sessionFactory.getCurrentSession();

		Criteria criteria = session.createCriteria(PublishingStats.class).createAlias("audit", "book")
				.setFetchMode("audit", FetchMode.JOIN).add(Restrictions.eq("book.ebookDefinitionId", ebookDefId))
				.add(Restrictions.in("publishStatus",
						new String[] { PublishingStats.SEND_EMAIL_COMPLETE,
								PublishingStats.SUCCESFULL_PUBLISH_STATUS }))
				.setProjection(Projections.distinct(Projections.property("book.subGroupHeading")));
		return criteria.list();
	}

	public Long findSuccessfullyPublishedGroupBook(Long ebookDefId) {
		Session session = sessionFactory.getCurrentSession();

		Criteria criteria = session.createCriteria(PublishingStats.class).createAlias("audit", "book")
				.setFetchMode("audit", FetchMode.JOIN).add(Restrictions.eq("book.ebookDefinitionId", ebookDefId))
				.add(Restrictions.in("publishStatus",
						new String[] { PublishingStats.SEND_EMAIL_COMPLETE,
								PublishingStats.SUCCESFULL_PUBLISH_STATUS }))
				.add(Restrictions.isNotNull("book.groupName"))
				.setProjection(Projections.distinct(Projections.property("book.ebookDefinitionId")));
		return (Long) criteria.uniqueResult();
	}

	@SuppressWarnings("unchecked")
	public List<PublishingStats> findPublishingStats(PublishingStatsFilter filter, PublishingStatsSort sort) {

		Criteria criteria = addFilters(filter);

		String orderByColumn = getOrderByColumnName(sort.getSortProperty());
		if (sort.isAscending()) {
			criteria.addOrder(Order.asc(orderByColumn));
		} else {
			criteria.addOrder(Order.desc(orderByColumn));
		}

		int itemsPerPage = sort.getItemsPerPage();
		criteria.setFirstResult((sort.getPageNumber() - 1) * (itemsPerPage));
		criteria.setMaxResults(itemsPerPage);

		return criteria.list();
	}

	@SuppressWarnings("unchecked")
	public List<PublishingStats> findPublishingStats(PublishingStatsFilter filter) {
		Criteria criteria = addFilters(filter);

		return criteria.list();
	}

	public int numberOfPublishingStats(PublishingStatsFilter filter) {
		Criteria criteria = addFilters(filter);

		Number rows = (Number) criteria.setProjection(Projections.rowCount()).uniqueResult();

		return rows.intValue();
	}

	/**
	 * Map the sort column enumeration into the actual column identifier used in
	 * the HQL query.
	 * 
	 * @param sortProperty
	 *            enumerated value that reflects the database table sort column
	 *            to sort on.
	 */
	private String getOrderByColumnName(SortProperty sortProperty) {
		switch (sortProperty) {
		case AUDIT_ID:
			return "book.auditId";
		case BOOK_SIZE:
			return "bookSize";
		case BOOK_VERSION:
			return "bookVersionSubmitted";
		case EBOOK_DEFINITION_ID:
			return "ebookDefId";
		case JOB_INSTANCE_ID:
			return "jobInstanceId";
		case JOB_SUBMIT_TIMESTAMP:
			return "jobSubmitTimestamp";
		case JOB_SUBMITTER:
			return "jobSubmitterName";
		case LARGEST_DOC_SIZE:
			return "largestDocSize";
		case LARGEST_IMAGE_SIZE:
			return "largestImageSize";
		case LARGEST_PDF_SIZE:
			return "largestPdfSize";
		case PUBLISH_STATUS:
			return "publishStatus";
		case PROVIEW_DISPLAY_NAME:
			return "book.proviewDisplayName";
		case TITLE_ID:
			return "book.titleId";
		default:
			throw new IllegalArgumentException("Unexpected sort property: " + sortProperty);
		}
	}

	private Criteria addFilters(PublishingStatsFilter filter) {
		Session session = sessionFactory.getCurrentSession();

		Criteria criteria = session.createCriteria(PublishingStats.class).createAlias("audit", "book")
				.setFetchMode("audit", FetchMode.JOIN);

		if (filter.getFrom() != null) {
			criteria.add(Restrictions.ge("jobSubmitTimestamp", filter.getFrom()));
		}
		if (filter.getTo() != null) {
			criteria.add(Restrictions.le("jobSubmitTimestamp", filter.getTo()));
		}
		if (StringUtils.isNotBlank(filter.getBookName())) {
			criteria.add(Restrictions.like("book.proviewDisplayName", filter.getBookName()).ignoreCase());
		}
		if (StringUtils.isNotBlank(filter.getTitleId())) {
			criteria.add(Restrictions.like("book.titleId", filter.getTitleId()).ignoreCase());
		}
		if (filter.getBookDefinitionId() != null) {
			criteria.add(Restrictions.eq("ebookDefId", filter.getBookDefinitionId()));
		}
		if (StringUtils.isNotBlank(filter.getIsbn())) {
			criteria.add(Restrictions.like("book.isbn", filter.getIsbn()).ignoreCase());
		}

		return criteria;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<PublishingStats> findAllPublishingStats() {
		Session session = sessionFactory.getCurrentSession();
		Query query = session.createQuery("from PublishingStats");
		return query.list();
	}

}
