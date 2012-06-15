/*
 * Copyright 2011: Thomson Reuters Global Resources. All Rights Reserved.
 * Proprietary and Confidential information of TRGR. Disclosure, Use or
 * Reproduction without the written authorization of TRGR is prohibited
 */
package com.thomsonreuters.uscl.ereader.mgr.cleanup;

//import org.apache.log4j.Logger;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.scheduling.annotation.Scheduled;

import com.thomsonreuters.uscl.ereader.core.book.service.BookDefinitionLockService;

/**
 * A regularly scheduled task to clean up the BOOK_DEFINITION_LOCK table.
 * Removes locks that has expired
 */
public class BookDefinitionLockCleaner {
	private static final Logger log = Logger.getLogger(BookDefinitionLockCleaner.class);

	private BookDefinitionLockService lockService;

	/**
	 * Clean up the BOOK_DEFINITION_LOCK table every day at 2 AM.
	 */
	@Scheduled(cron="0 0 2 * * *")
	public void cleanBookDefinitionLockTable() {
		lockService.cleanExpiredLocks();
		log.debug("Clean the table!");
	}

	@Required
	public void setBookDefinitionLockService(BookDefinitionLockService service) {
		this.lockService = service;
	}
}
