package com.thomsonreuters.uscl.ereader.support.dao;

import java.util.Date;
import java.util.List;

import com.thomsonreuters.uscl.ereader.support.domain.SupportPageLink;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;

/**
 * DAO to manage SupportPageLink entities.
 *
 */

public class SupportPageLinkDaoImpl implements SupportPageLinkDao
{
    //private static Logger log = LogManager.getLogger(UserPreferenceDaoImpl.class);
    private SessionFactory sessionFactory;

    public SupportPageLinkDaoImpl(final SessionFactory hibernateSessionFactory)
    {
        sessionFactory = hibernateSessionFactory;
    }

    @Override
    public void save(final SupportPageLink spl)
    {
        final Session session = sessionFactory.getCurrentSession();

        spl.setLastUpdated(new Date());
        session.saveOrUpdate(spl);
        session.flush();
    }

    @Override
    public void delete(final SupportPageLink spl)
    {
        final Session session = sessionFactory.getCurrentSession();
        session.delete(spl);
        session.flush();
    }

    @Override
    public SupportPageLink findByPrimaryKey(final Long id)
    {
        final Session session = sessionFactory.getCurrentSession();
        return (SupportPageLink) session.get(SupportPageLink.class, id);
    }

    @Override
    public List<SupportPageLink> findAllSupportPageLink()
    {
        final Session session = sessionFactory.getCurrentSession();
        return session.createCriteria(SupportPageLink.class).addOrder(Order.desc("linkDescription")).list();
    }
}
