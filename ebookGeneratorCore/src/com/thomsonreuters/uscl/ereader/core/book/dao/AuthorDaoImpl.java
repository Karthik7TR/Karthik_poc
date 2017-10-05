package com.thomsonreuters.uscl.ereader.core.book.dao;

import java.util.List;

import com.thomsonreuters.uscl.ereader.core.book.domain.Author;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.dao.DataAccessException;
import org.springframework.transaction.annotation.Transactional;

/**
 * DAO to manage DocMetadata entities.
 *
 */

public class AuthorDaoImpl implements AuthorDao {
    private SessionFactory sessionFactory;

    public AuthorDaoImpl(final SessionFactory hibernateSessionFactory) {
        sessionFactory = hibernateSessionFactory;
    }

    /**
     * Used to determine whether or not to merge the entity or persist the
     * entity when calling Store
     *
     * @see store
     *
     *
     */
    public boolean canBeMerged(final Author entity) {
        return true;
    }

    /*
     * (non-Javadoc)
     */
    @Transactional
    public Query createNamedQuery(final String queryName) {
        final Query query = sessionFactory.getCurrentSession().getNamedQuery(queryName);
        return query;
    }

    /*
     * (non-Javadoc)
     */
    @Transactional
    public Author persist(final Author toPersist) {
        sessionFactory.getCurrentSession().save(toPersist);
        flush();
        return toPersist;
    }

    /*
     * (non-Javadoc)
     */
    @Override
    @Transactional
    public void remove(Author toRemove) {
        toRemove = (Author) sessionFactory.getCurrentSession().merge(toRemove);
        sessionFactory.getCurrentSession().delete(toRemove);
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
    public List<Author> findAuthorsByEBookDefnId(final Long eBookDefnId) {
//		Query query = createNamedQuery("findAuthorByEbookDefinitionId");
//		query.setLong("eBookDefId", eBookDefnId);
//		return query.list();

        final Criteria criteria = sessionFactory.getCurrentSession().createCriteria(Author.class);
        criteria.add(Restrictions.eq("ebookDefinition.ebookDefinitionId", eBookDefnId));
        return criteria.list();
    }

    @Override
    @Transactional
    public void saveAuthor(final Author author) {
        final Session session = sessionFactory.getCurrentSession();
        session.save(author);
        session.flush();
    }

    @Override
    public Author findAuthorById(final Long authorId) throws DataAccessException {
        final Session session = sessionFactory.getCurrentSession();
        return (Author) session.get(Author.class, authorId);
    }
}
