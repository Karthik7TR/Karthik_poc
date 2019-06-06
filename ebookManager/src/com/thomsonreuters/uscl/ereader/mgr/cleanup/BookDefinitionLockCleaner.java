package com.thomsonreuters.uscl.ereader.mgr.cleanup;

import static com.thomsonreuters.uscl.ereader.core.book.domain.BookDefinitionLock.LOCK_TIMEOUT_SEC;

import com.thomsonreuters.uscl.ereader.mgr.web.service.book.BookDefinitionLockService;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.scheduling.annotation.Scheduled;

/**
 * A regularly scheduled task to clean up the BOOK_DEFINITION_LOCK table.
 * Removes locks that has expired
 */
public class BookDefinitionLockCleaner {
    private BookDefinitionLockService lockService;

    /**
     * Clean up the BOOK_DEFINITION_LOCK table every 2 minutes.
     */
    @Scheduled(fixedRate = LOCK_TIMEOUT_SEC * 1000)
    public void cleanBookDefinitionLockTable() {
        lockService.cleanExpiredLocks();
    }

    @Required
    public void setBookDefinitionLockService(final BookDefinitionLockService service) {
        lockService = service;
    }
}
