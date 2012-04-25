/*
 * Copyright 2012: Thomson Reuters Global Resources. All Rights Reserved.
 * Proprietary and Confidential information of TRGR. Disclosure, Use or
 * Reproduction without the written authorization of TRGR is prohibited
 */

package com.thomsonreuters.uscl.ereader.core.book.service;

import java.util.List;

import org.springframework.dao.DataAccessException;

import com.thomsonreuters.uscl.ereader.core.book.domain.BookDefinition;
import com.thomsonreuters.uscl.ereader.core.book.domain.BookDefinitionLock;

/**
 * Service to manage BookDefinitionLock entities.
 * 
 */
public interface BookDefinitionLockService {

	/**
	 * Checks if book definition is locked.  If the book definition is locked, returns
	 * BookDefinitionLock entity. Otherwise, null is returned. 
	 */
	public BookDefinitionLock findBookLockByBookDefinition(BookDefinition book)
			throws DataAccessException;
	
	public List<BookDefinitionLock> findAllActiveLocks();

	public BookDefinitionLock findBookDefinitionLockByPrimaryKey(Long primaryKey);
	
	/**
	 * Removes all locks that has expired
	 */
	public void cleanExpiredLocks();

	/**
	 * Remove all BookDefinitionLocks for a given bookDefinition
	 * @param book
	 * @throws DataAccessException
	 */
	public void removeLock(BookDefinition book) throws DataAccessException;

	public void lockBookDefinition(BookDefinition bookDefinition, String username, String fullName);

}