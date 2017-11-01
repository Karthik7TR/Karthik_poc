package com.thomsonreuters.uscl.ereader.mgr.cleanup;

import com.thomsonreuters.uscl.ereader.mgr.web.service.book.BookDefinitionLockService;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.scheduling.annotation.Scheduled;

/**
 * A regularly scheduled task to clean up the BOOK_DEFINITION_LOCK table.
 * Removes locks that has expired
 */
public class BookDefinitionLockCleaner {
    private static final Logger log = LogManager.getLogger(BookDefinitionLockCleaner.class);

    private BookDefinitionLockService lockService;

    /**
     * Clean up the BOOK_DEFINITION_LOCK table every day at 2 AM.
     */
    @Scheduled(cron = "0 0 2 * * *")
    public void cleanBookDefinitionLockTable() {
        lockService.cleanExpiredLocks();
        log.debug("Clean the table!");
    }

    @Required
    public void setBookDefinitionLockService(final BookDefinitionLockService service) {
        lockService = service;
    }
}
