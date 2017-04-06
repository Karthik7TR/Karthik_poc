package com.thomsonreuters.uscl.ereader.request.dao;

import java.util.List;

import com.thomsonreuters.uscl.ereader.request.domain.PrintComponent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;

public class PrintComponentDao
{
    private static final Logger log = LogManager.getLogger(PrintComponentDao.class);
    private SessionFactory sessionFactory;

    public PrintComponentDao(final SessionFactory sessionFactory)
    {
        this.sessionFactory = sessionFactory;
    }

    public Long saveRequest(final PrintComponent ebookRequest)
    {
        final Session session = sessionFactory.getCurrentSession();
        final long pk = (Long) session.save(ebookRequest);
        session.flush();
        return pk;
    }

    public PrintComponent findByPrimaryKey(final long printComponentId)
    {
        final Session session = sessionFactory.getCurrentSession();
        final PrintComponent request = (PrintComponent) session.get(PrintComponent.class, printComponentId);

        return request;
    }

    public void deleteComponent(final long printComponentId)
    {
        // TODO: Determine whether to log more human-readable information about the component being deleted. (requires a table read)
        log.warn(String.format("Removing Print Component %d from table.", printComponentId));
        final Session session = sessionFactory.getCurrentSession();
        session.delete(findByPrimaryKey(printComponentId));
        session.flush();
    }

    public PrintComponent findByComponentName(final String messageId)
    {
        final List<PrintComponent> printComponentList = sessionFactory.getCurrentSession()
            .createCriteria(PrintComponent.class)
            .add(Restrictions.eq("componentName", messageId))
            .list();

        if (printComponentList.size() > 0)
        {
            return printComponentList.get(0);
        }
        return null;
    }

    public PrintComponent findByMaterialNumber(final String materialNumber)
    {
        final List<PrintComponent> printComponentList = sessionFactory.getCurrentSession()
            .createCriteria(PrintComponent.class)
            .add(Restrictions.eq("materialNumber", materialNumber))
            .list();

        if (printComponentList.size() > 0)
        {
            return printComponentList.get(0);
        }
        return null;
    }

    public List<PrintComponent> findAllRequests()
    {
        final Criteria criteria = sessionFactory.getCurrentSession().createCriteria(PrintComponent.class);
        return criteria.list();
    }
}
