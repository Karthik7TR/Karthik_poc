package com.thomsonreuters.uscl.ereader.request.dao;

import java.util.Collections;
import java.util.List;

import com.thomsonreuters.uscl.ereader.request.domain.XppBundleArchive;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

public class XppBundleArchiveDaoImpl implements XppBundleArchiveDao {
    private static final Logger log = LogManager.getLogger(XppBundleArchiveDaoImpl.class);
    private SessionFactory sessionFactory;

    public XppBundleArchiveDaoImpl(final SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public Long saveRequest(final XppBundleArchive ebookRequest) {
        final Session session = sessionFactory.getCurrentSession();
        final long pk = (Long) session.save(ebookRequest);
        session.flush();
        return pk;
    }

    @Override
    public XppBundleArchive findByPrimaryKey(final long ebookRequestId) {
        final Session session = sessionFactory.getCurrentSession();
        final XppBundleArchive request = (XppBundleArchive) session.get(XppBundleArchive.class, ebookRequestId);

        return request;
    }

    @Override
    public void deleteRequest(final long ebookRequestId) {
        // TODO: Determine whether to log more human-readable information about the request being deleted. (requires a table read)
        log.warn(String.format("Removing request %d from the Bundle Archive.", ebookRequestId));
        final Session session = sessionFactory.getCurrentSession();
        session.delete(findByPrimaryKey(ebookRequestId));
        session.flush();
    }

    @Override
    public XppBundleArchive findByRequestId(final String messageId) {
        final List<XppBundleArchive> eBookRequestList = sessionFactory.getCurrentSession()
            .createCriteria(XppBundleArchive.class)
            .add(Restrictions.eq("messageId", messageId))
            .list();

        if (eBookRequestList.size() > 0) {
            return eBookRequestList.get(0);
        }
        return null;
    }

    @Override
    public XppBundleArchive findByMaterialNumber(final String materialNumber) {
        final List<XppBundleArchive> archive = sessionFactory.getCurrentSession()
            .createCriteria(XppBundleArchive.class)
            .add(Restrictions.eq("materialNumber", materialNumber))
            .addOrder(Order.desc("dateTime"))
            .list();

        if (archive.size() > 0) {
            return archive.get(0);
        }
        return null;
    }

    @Override
    public List<XppBundleArchive> findAllRequests() {
        final Criteria criteria = sessionFactory.getCurrentSession().createCriteria(XppBundleArchive.class);
        return criteria.list();
    }

    @Override
    public List<XppBundleArchive> findByMaterialNumberList(final List<String> sourceMaterialNumberList) {
        final Criteria criteria = sessionFactory.getCurrentSession().createCriteria(XppBundleArchive.class);
        if (sourceMaterialNumberList != null && !sourceMaterialNumberList.isEmpty()) {
            criteria.add(Restrictions.in("materialNumber", sourceMaterialNumberList));
            return criteria.list();
        } else {
            return Collections.EMPTY_LIST;
        }
    }
}
