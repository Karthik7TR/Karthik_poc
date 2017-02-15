package com.thomsonreuters.uscl.ereader.core.book.dao;

import java.util.List;

import com.thomsonreuters.uscl.ereader.core.book.domain.BookDefinition;
import com.thomsonreuters.uscl.ereader.core.book.domain.BookDefinitionLock;
import org.springframework.dao.DataAccessException;

/**
 * DAO to manage BookDefinitionLock entities.
 *
 */
public interface BookDefinitionLockDao
{
    /**
     * Returns a list of book definition locks that are currently active
     * @return
     */
    List<BookDefinitionLock> findAllActiveLocks();

    List<BookDefinitionLock> findLocksByBookDefinition(BookDefinition book) throws DataAccessException;

    BookDefinitionLock findBookDefinitionLockByPrimaryKey(Long primaryKey);

    void removeLock(BookDefinitionLock bookDefinitionLock) throws DataAccessException;

    /**
     * Removes all locks that has expired
     */
    void cleanExpiredLocks();

    void saveLock(BookDefinitionLock bookDefinitionLock);
}
