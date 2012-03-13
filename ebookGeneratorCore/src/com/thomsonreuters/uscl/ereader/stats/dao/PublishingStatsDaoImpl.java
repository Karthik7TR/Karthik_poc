package com.thomsonreuters.uscl.ereader.stats.dao;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.transaction.annotation.Transactional;

import com.thomsonreuters.uscl.ereader.StatsUpdateTypeEnum;
import com.thomsonreuters.uscl.ereader.core.book.domain.EbookAudit;
import com.thomsonreuters.uscl.ereader.stats.domain.PublishingStats;
import com.thomsonreuters.uscl.ereader.stats.domain.PublishingStatsPK;

public class PublishingStatsDaoImpl implements PublishingStatsDao {
	
	private static final Logger LOG = Logger.getLogger(PublishingStatsDaoImpl.class);
	private SessionFactory sessionFactory;

//	@Required
//	public void setSessionFactory(SessionFactory sessionfactory) {
//		this.sessionFactory = sessionfactory;
//	}


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

		PublishingStats pubStats = (PublishingStats) session.createCriteria(PublishingStats.class)
		.add( Restrictions.eq("jobInstanceId", JobId)).uniqueResult();
		
		return(pubStats);
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
	@Transactional
	public int updateJobStats(PublishingStats jobstats, StatsUpdateTypeEnum updateType) {
		StringBuffer hql = new StringBuffer("update PublishingStats set   ");
		if (updateType.equals(StatsUpdateTypeEnum.GATHERTOC))
		{
			hql.append("gatherTocNodeCount = " );
			hql.append(jobstats.getGatherTocNodeCount());
			hql.append(", gatherTocDocCount = " );
			hql.append(jobstats.getGatherTocDocCount());
			hql.append(", gatherTocRetryCount = " );
			hql.append(jobstats.getGatherTocRetryCount());
			hql.append(", gatherTocSkippedCount = " );
			hql.append(jobstats.getGatherTocSkippedCount());
		}
		else if (updateType.equals(StatsUpdateTypeEnum.GATHERDOC))
		{
			//TODO: update switches.
			hql.append("gatherDocExpectedCount = " );
			hql.append(jobstats.getGatherDocExpectedCount());
			hql.append(", gatherDocRetrievedCount = " );
			hql.append(jobstats.getGatherDocRetrievedCount());
			hql.append(", gatherDocRetryCount = " );
			hql.append(jobstats.getGatherDocRetryCount());

		}
		else
		{
			LOG.error("Unknown StatsUpdateTypeEnum");
//TODO: failure logic
		}			
		hql.append(", lastUpdated = sysdate ");

		hql.append(" where jobInstanceId =  "); //  WHERE clause
		hql.append(jobstats.getJobInstanceId()); 

		// Create query and populate it with where clause values
		Session session = sessionFactory.getCurrentSession();
		Query query = session.createQuery(hql.toString());

//		query.setLong(0, jobstats.getJobInstanceId());
		
		int result = query.executeUpdate();
		session.flush();

		return result;
	}

	@Override
	public EbookAudit findAuditInfoByJobId(Long jobId) {
		StringBuffer hql = new StringBuffer("select ea  from PublishingStats ps, EbookAudit ea ");

		hql.append(" where ps.jobInstanceId =  "); //  WHERE clause
		hql.append(jobId); 
//		hql.append(" and PublishingStats.ebookDefId = EbookAudit.ebookDefId " ); 
		hql.append(" and ps.auditId = ea.auditId " ); 

		// Create query and populate it with where clause values
		Session session = sessionFactory.getCurrentSession();
		Query query = session.createQuery(hql.toString());

		EbookAudit auditResult =  (EbookAudit) query.uniqueResult();
		return(auditResult);
		
		
	}

	@SuppressWarnings("unchecked")
	@Override
	@Transactional
	public List<EbookAudit> findJobStatsAuditByEbookDef(Long EbookDefId) {
		StringBuffer hql = new StringBuffer("select ea from PublishingStats ps, EbookAudit ea  ");

		hql.append(" where ps.ebookDefId =  "); //  WHERE clause
		hql.append(EbookDefId); 
//		hql.append(" and PublishingStats.ebookDefId = EbookAudit.ebookDefId " ); 
		hql.append(" and ps.auditId = ea.auditId " ); 

		// Create query and populate it with where clause values
		Session session = sessionFactory.getCurrentSession();
		Query query = session.createQuery(hql.toString());

		
		return (List<EbookAudit>) query.list();
		}





}

