package com.thomsonreuters.uscl.ereader.request.dao;

import java.util.List;

import com.thomsonreuters.uscl.ereader.request.EBookRequest;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;

public class BundleArchiveDaoImpl implements BundleArchiveDao
{
    private static final Logger log = LogManager.getLogger(BundleArchiveDaoImpl.class);
    private SessionFactory sessionFactory;

    public BundleArchiveDaoImpl(final SessionFactory sessionFactory)
    {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public Long saveRequest(final EBookRequest ebookRequest)
    {
        final Session session = sessionFactory.getCurrentSession();
        final long pk = (Long) session.save(ebookRequest);
        session.flush();
        return pk;
    }

    @Override
    public EBookRequest findByPrimaryKey(final long ebookRequestId)
    {
        final Session session = sessionFactory.getCurrentSession();
        final EBookRequest request = (EBookRequest) session.get(EBookRequest.class, ebookRequestId);

        return request;
    }

    @Override
    public void deleteRequest(final long ebookRequestId)
    {
        // possible TODO: log more human-readable information about the request being deleted. (requires a table read)
        log.warn(String.format("Removing request %d from the Bundle Archive.", ebookRequestId));
        final Session session = sessionFactory.getCurrentSession();
        session.delete(findByPrimaryKey(ebookRequestId));
        session.flush();
    }

    @Override
    public EBookRequest findByRequestId(final String messageId)
    {
        final List<EBookRequest> eBookRequestList = sessionFactory.getCurrentSession()
            .createCriteria(EBookRequest.class)
            .add(Restrictions.eq("messageId", messageId))
            .list();

        if (eBookRequestList.size() > 0)
        {
            return eBookRequestList.get(0);
        }
        return null;
    }

    @Override
    public List<EBookRequest> findAllRequests()
    {
        final Criteria criteria = sessionFactory.getCurrentSession().createCriteria(EBookRequest.class);
        return criteria.list();
    }
}
