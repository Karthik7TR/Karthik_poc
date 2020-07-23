package com.thomsonreuters.uscl.ereader.mgr.web.service.book;

import java.util.List;

import com.thomsonreuters.uscl.ereader.core.book.domain.BookDefinition;
import com.thomsonreuters.uscl.ereader.core.book.domain.BookDefinitionLock;
import org.springframework.dao.DataAccessException;

/**
 * Service to manage BookDefinitionLock entities.
 *
 */
public interface BookDefinitionLockService {
    /**
     * Checks if book definition is locked.  If the book definition is locked, returns
     * BookDefinitionLock entity. Otherwise, null is returned.
     */
    BookDefinitionLock findActiveBookLock(BookDefinition book);

    List<BookDefinitionLock> findAllActiveLocks();

    BookDefinitionLock findBookDefinitionLockByPrimaryKey(Long primaryKey);

    /**
     * Removes all locks that has expired
     */
    void cleanExpiredLocks();

    void extendLock(BookDefinition book);

    /**
     * Remove all BookDefinitionLocks for a given bookDefinition
     * @param book
     * @throws DataAccessException
     */
    void removeLock(BookDefinition book);

    void lockBookDefinition(BookDefinition bookDefinition, String username, String fullName);
}