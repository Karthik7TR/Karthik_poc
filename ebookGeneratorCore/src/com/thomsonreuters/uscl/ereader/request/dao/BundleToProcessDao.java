package com.thomsonreuters.uscl.ereader.request.dao;

import java.util.List;

import com.thomsonreuters.uscl.ereader.request.BundleToProcess;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;

public class BundleToProcessDao
{
    private static final Logger log = LogManager.getLogger(BundleToProcessDao.class);
    private SessionFactory sessionFactory;

    public BundleToProcessDao(final SessionFactory sessionFactory)
    {
        this.sessionFactory = sessionFactory;
    }

    public Long save(final BundleToProcess bundleJob)
    {
        final Session session = sessionFactory.getCurrentSession();
        final long pk = (Long) session.save(bundleJob);
        session.flush();
        return pk;
    }

    public List<BundleToProcess> findAll()
    {
        final Criteria criteria = sessionFactory.getCurrentSession().createCriteria(BundleToProcess.class);
        return criteria.list();
    }

    public BundleToProcess findByPrimaryKey(final long ebookRequestId)
    {
        final Session session = sessionFactory.getCurrentSession();
        final BundleToProcess request = (BundleToProcess) session.get(BundleToProcess.class, ebookRequestId);
        return request;
    }

    public List<BundleToProcess> findByProductTitle(final String productTitle)
    {
        final Criteria criteria = sessionFactory.getCurrentSession()
            .createCriteria(BundleToProcess.class)
            .add(Restrictions.eq("productTitle", productTitle));
        return criteria.list();
    }

    public List<BundleToProcess> findByProductType(final String productType)
    {
        final Criteria criteria = sessionFactory.getCurrentSession()
            .createCriteria(BundleToProcess.class)
            .add(Restrictions.eq("productType", productType));
        return criteria.list();
    }

    public void delete(final long bundleToProcessId)
    {
        // TODO: Determine whether to log more human-readable information about the request being deleted. (requires a table read)
        log.warn(String.format("Removing bundle processing request %d from the queue.", bundleToProcessId));
        final Session session = sessionFactory.getCurrentSession();
        session.delete(findByPrimaryKey(bundleToProcessId));
        session.flush();
    }
}
