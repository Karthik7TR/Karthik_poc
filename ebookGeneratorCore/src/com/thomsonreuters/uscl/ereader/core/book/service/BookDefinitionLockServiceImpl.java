package com.thomsonreuters.uscl.ereader.core.book.service;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import com.thomsonreuters.uscl.ereader.core.book.dao.BookDefinitionLockDao;
import com.thomsonreuters.uscl.ereader.core.book.domain.BookDefinition;
import com.thomsonreuters.uscl.ereader.core.book.domain.BookDefinitionLock;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.dao.DataAccessException;
import org.springframework.transaction.annotation.Transactional;

/**
 * DAO to manage BookDefinitionLock entities.
 *
 */

public class BookDefinitionLockServiceImpl implements BookDefinitionLockService
{
    private BookDefinitionLockDao dao;

    /**
     * Checks if book definition is locked.  If the book definition is locked, returns
     * BookDefinitionLock entity. Otherwise, null is returned
     */
    @Override
    @Transactional(readOnly = true)
    public BookDefinitionLock findBookLockByBookDefinition(final BookDefinition book) throws DataAccessException
    {
        // DAO returns BookDefinitionLocks in checkout_timestamp sorted by descending order
        final List<BookDefinitionLock> locks = dao.findLocksByBookDefinition(book);
        if (locks.size() > 0)
        {
            final BookDefinitionLock newestLock = locks.get(0);
            if (islockTimeoutSurpassed(newestLock.getCheckoutTimestamp()))
            {
                // Delete all locks for current Book Definition
                for (final BookDefinitionLock lock : locks)
                {
                    dao.removeLock(lock);
                }
            }
            else
            {
                // Book Definition is locked
                return newestLock;
            }
        }

        return null;
    }

    private boolean islockTimeoutSurpassed(final Date lockDate)
    {
        final long currentTime = Calendar.getInstance().getTimeInMillis();
        final Calendar lockCal = Calendar.getInstance();
        lockCal.setTime(lockDate);
        final long lockTime = lockCal.getTimeInMillis();

        final long difference = currentTime - lockTime;
        final int seconds = (int) (difference / (1000));

        return seconds >= BookDefinitionLock.LOCK_TIMEOUT_SEC;
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookDefinitionLock> findAllActiveLocks()
    {
        return dao.findAllActiveLocks();
    }

    @Override
    @Transactional(readOnly = true)
    public BookDefinitionLock findBookDefinitionLockByPrimaryKey(final Long primaryKey)
    {
        return dao.findBookDefinitionLockByPrimaryKey(primaryKey);
    }

    /**
     * Removes all locks that has expired
     */
    @Override
    @Transactional
    public void cleanExpiredLocks()
    {
        dao.cleanExpiredLocks();
    }

    @Override
    @Transactional
    public void removeLock(final BookDefinition book) throws DataAccessException
    {
        final List<BookDefinitionLock> locks = dao.findLocksByBookDefinition(book);

        for (final BookDefinitionLock lock : locks)
        {
            dao.removeLock(lock);
        }
    }

    @Override
    @Transactional
    public void lockBookDefinition(final BookDefinition bookDefinition, final String username, final String fullName)
    {
        final BookDefinitionLock lock = new BookDefinitionLock();
        lock.setEbookDefinition(bookDefinition);
        lock.setUsername(username);
        lock.setFullName(fullName);
        lock.setCheckoutTimestamp(new Date());

        dao.saveLock(lock);
    }

    @Required
    public void setBookDefinitionLockDao(final BookDefinitionLockDao dao)
    {
        this.dao = dao;
    }
}
