package com.thomsonreuters.uscl.ereader.mgr.web.service.book;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import com.thomsonreuters.uscl.ereader.core.book.dao.BookDefinitionLockDao;
import com.thomsonreuters.uscl.ereader.core.book.domain.BookDefinition;
import com.thomsonreuters.uscl.ereader.core.book.domain.BookDefinitionLock;
import org.jetbrains.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * DAO to manage BookDefinitionLock entities.
 *
 */
@Service("bookDefintionLockService")
public class BookDefinitionLockServiceImpl implements BookDefinitionLockService {
    private final BookDefinitionLockDao dao;

    @Autowired
    public BookDefinitionLockServiceImpl(final BookDefinitionLockDao dao) {
        this.dao = dao;
    }

    /**
     * Checks if book definition is locked.  If the book definition is locked, returns
     * BookDefinitionLock entity. Otherwise, null is returned
     */
    @Nullable
    @Override
    @Transactional
    public BookDefinitionLock findActiveBookLock(final BookDefinition book) {
        // DAO returns BookDefinitionLocks in checkout_timestamp sorted by descending order
        final List<BookDefinitionLock> locks = dao.findLocksByBookDefinition(book);
        if (locks.isEmpty())
            return null;

        final BookDefinitionLock newestLock = locks.get(0);
        if (islockTimeoutSurpassed(newestLock.getCheckoutTimestamp())) {
            locks.forEach(dao::removeLock);
            return null;
        } else {
            return newestLock;
        }
    }

    private boolean islockTimeoutSurpassed(final Date lockDate) {
        final LocalDateTime lockDateTime = lockDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
        return Duration.between(lockDateTime, LocalDateTime.now()).getSeconds() >= BookDefinitionLock.LOCK_TIMEOUT_SEC;
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookDefinitionLock> findAllActiveLocks() {
        return dao.findAllActiveLocks();
    }

    @Override
    @Transactional(readOnly = true)
    public BookDefinitionLock findBookDefinitionLockByPrimaryKey(final Long primaryKey) {
        return dao.findBookDefinitionLockByPrimaryKey(primaryKey);
    }

    /**
     * Removes all locks that has expired
     */
    @Override
    @Transactional
    public void cleanExpiredLocks() {
        dao.cleanExpiredLocks();
    }

    @Override
    @Transactional
    public void extendLock(final BookDefinition book) {
        Optional.ofNullable(findActiveBookLock(book)).ifPresent(dao::extendLock);
    }

    @Override
    @Transactional
    public void removeLock(final BookDefinition book) {
        dao.findLocksByBookDefinition(book).forEach(dao::removeLock);
    }

    @Override
    @Transactional
    public void lockBookDefinition(final BookDefinition bookDefinition, final String username, final String fullName) {
        final BookDefinitionLock lock = new BookDefinitionLock();
        lock.setEbookDefinition(bookDefinition);
        lock.setUsername(username);
        lock.setFullName(fullName);
        lock.setCheckoutTimestamp(new Date());
        dao.saveLock(lock);
    }
}
