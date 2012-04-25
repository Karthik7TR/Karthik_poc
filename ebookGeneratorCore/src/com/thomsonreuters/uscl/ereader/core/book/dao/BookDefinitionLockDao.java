/*
 * Copyright 2012: Thomson Reuters Global Resources. All Rights Reserved.
 * Proprietary and Confidential information of TRGR. Disclosure, Use or
 * Reproduction without the written authorization of TRGR is prohibited
 */

package com.thomsonreuters.uscl.ereader.core.book.dao;

import java.util.List;

import org.springframework.dao.DataAccessException;

import com.thomsonreuters.uscl.ereader.core.book.domain.BookDefinition;
import com.thomsonreuters.uscl.ereader.core.book.domain.BookDefinitionLock;

/**
 * DAO to manage BookDefinitionLock entities.
 * 
 */
public interface BookDefinitionLockDao {

	/**
	 * Returns a list of book definition locks that are currently active
	 * @return
	 */
	public List<BookDefinitionLock> findAllActiveLocks();
	
	public List<BookDefinitionLock> findLocksByBookDefinition(BookDefinition book)
			throws DataAccessException;
	
	public BookDefinitionLock findBookDefinitionLockByPrimaryKey(Long primaryKey);

	public void removeLock(BookDefinitionLock bookDefinitionLock) throws DataAccessException;
	
	/**
	 * Removes all locks that has expired
	 */
	public void cleanExpiredLocks();

	public void saveLock(BookDefinitionLock bookDefinitionLock);

}