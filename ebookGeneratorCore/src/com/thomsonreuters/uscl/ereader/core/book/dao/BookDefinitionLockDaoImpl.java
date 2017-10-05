package com.thomsonreuters.uscl.ereader.core.book.dao;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.thomsonreuters.uscl.ereader.core.book.domain.BookDefinition;
import com.thomsonreuters.uscl.ereader.core.book.domain.BookDefinitionLock;
import org.hibernate.Criteria;
import org.hibernate.FetchMode;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.CriteriaSpecification;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.dao.DataAccessException;

/**
 * DAO to manage BookDefinitionLock entities.
 *
 */

public class BookDefinitionLockDaoImpl implements BookDefinitionLockDao {
    //private static final Logger log = LogManager.getLogger(BookDefinitionLockDaoImpl.class);

    private SessionFactory sessionFactory;

    public BookDefinitionLockDaoImpl(final SessionFactory hibernateSessionFactory) {
        sessionFactory = hibernateSessionFactory;
    }

    @Override
    public List<BookDefinitionLock> findAllActiveLocks() {
        // Set session timeout
        final Calendar cal = Calendar.getInstance();
        cal.add(Calendar.SECOND, -(BookDefinitionLock.LOCK_TIMEOUT_SEC));

        final Criteria criteria = sessionFactory.getCurrentSession()
            .createCriteria(BookDefinitionLock.class)
            .createAlias("ebookDefinition", "book")
            .addOrder(Order.desc("checkoutTimestamp"))
            .add(Restrictions.ge("checkoutTimestamp", cal.getTime()))
            .setResultTransformer(CriteriaSpecification.DISTINCT_ROOT_ENTITY);

        // Create HashMap to find the newest timestamp for each book definition id
        final Map<Long, BookDefinitionLock> bookDefinitionMap = new HashMap<>();
        for (final BookDefinitionLock lock : (List<BookDefinitionLock>) criteria.list()) {
            final BookDefinitionLock previousLock =
                bookDefinitionMap.get(lock.getEbookDefinition().getEbookDefinitionId());
            if (previousLock != null) {
                if (lock.getCheckoutTimestamp().after(previousLock.getCheckoutTimestamp())) {
                    bookDefinitionMap.put(lock.getEbookDefinition().getEbookDefinitionId(), lock);
                }
            } else {
                bookDefinitionMap.put(lock.getEbookDefinition().getEbookDefinitionId(), lock);
            }
        }

        return new ArrayList<>(bookDefinitionMap.values());
    }

    @Override
    public List<BookDefinitionLock> findLocksByBookDefinition(final BookDefinition book) throws DataAccessException {
        final Criteria criteria = sessionFactory.getCurrentSession()
            .createCriteria(BookDefinitionLock.class)
            .add(Restrictions.eq("ebookDefinition", book))
            .addOrder(Order.desc("checkoutTimestamp"));
        return criteria.list();
    }

    @Override
    public BookDefinitionLock findBookDefinitionLockByPrimaryKey(final Long primaryKey) {
        return (BookDefinitionLock) sessionFactory.getCurrentSession()
            .createCriteria(BookDefinitionLock.class)
            .add(Restrictions.eq("ebookDefinitionLockId", primaryKey))
            .setFetchMode("ebookDefinition", FetchMode.JOIN)
            .uniqueResult();
    }

    @Override
    public void removeLock(BookDefinitionLock bookDefinitionLock) throws DataAccessException {
        final Session session = sessionFactory.getCurrentSession();
        bookDefinitionLock = (BookDefinitionLock) session.merge(bookDefinitionLock);
        session.delete(bookDefinitionLock);
        session.flush();
    }

    /**
     * Removes all locks that has expired
     */
    @Override
    public void cleanExpiredLocks() {
        final Session session = sessionFactory.getCurrentSession();
        // Set session timeout
        final Calendar cal = Calendar.getInstance();
        cal.add(Calendar.SECOND, -(BookDefinitionLock.LOCK_TIMEOUT_SEC));

        final Criteria criteria =
            session.createCriteria(BookDefinitionLock.class).add(Restrictions.le("checkoutTimestamp", cal.getTime()));

        final List<BookDefinitionLock> expiredLocks = criteria.list();

        for (final BookDefinitionLock lock : expiredLocks) {
            session.delete(lock);
        }

        session.flush();
    }

    @Override
    public void saveLock(final BookDefinitionLock bookDefinitionLock) {
        final Session session = sessionFactory.getCurrentSession();
        session.save(bookDefinitionLock);
        session.flush();
    }
}
