package com.thomsonreuters.uscl.ereader.core.book.dao;

import java.util.List;

import com.thomsonreuters.uscl.ereader.core.book.domain.PilotBook;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.dao.DataAccessException;
import org.springframework.transaction.annotation.Transactional;

public class PilotBookDaoImpl implements PilotBookDao {
    private SessionFactory sessionFactory;

    public PilotBookDaoImpl(final SessionFactory hibernateSessionFactory) {
        sessionFactory = hibernateSessionFactory;
    }

    /**
     * Query - findAuthorByPrimaryKey
     *
     */
    @Override
    @Transactional
    public PilotBook findPilotBookByTitleId(final String pilotBookTitleId) throws DataAccessException {
        final Session session = sessionFactory.getCurrentSession();
        return (PilotBook) session.get(PilotBook.class, pilotBookTitleId);
    }

    /**
     * Query - findAuthorsByEBookDefnId
     *
     */
    @Override
    public List<PilotBook> findPilotBooksByEBookDefnId(final Long eBookDefnId) throws DataAccessException {
        final Criteria criteria = sessionFactory.getCurrentSession().createCriteria(PilotBook.class);
        criteria.add(Restrictions.eq("ebookDefinition.ebookDefinitionId", eBookDefnId));
        return criteria.list();
    }

    @Override
    @Transactional
    public void remove(final PilotBook toRemove) throws DataAccessException {
        final PilotBook localToRemove = (PilotBook) sessionFactory.getCurrentSession().merge(toRemove);
        sessionFactory.getCurrentSession().delete(localToRemove);
        flush();
    }

    /*
     * (non-Javadoc)
     */
    @Transactional
    public void flush() {
        sessionFactory.getCurrentSession().flush();
    }

    @Override
    @Transactional
    public void savePilotBook(final PilotBook pilotBook) {
        final Session session = sessionFactory.getCurrentSession();
        session.save(pilotBook);
        session.flush();
    }
}
