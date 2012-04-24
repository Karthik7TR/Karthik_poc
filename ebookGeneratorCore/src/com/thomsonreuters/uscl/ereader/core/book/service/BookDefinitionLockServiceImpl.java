/*
 * Copyright 2011: Thomson Reuters Global Resources. All Rights Reserved.
 * Proprietary and Confidential information of TRGR. Disclosure, Use or
 * Reproduction without the written authorization of TRGR is prohibited
 */

package com.thomsonreuters.uscl.ereader.core.book.service;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Required;
import org.springframework.dao.DataAccessException;
import org.springframework.transaction.annotation.Transactional;

import com.thomsonreuters.uscl.ereader.core.book.dao.BookDefinitionLockDao;
import com.thomsonreuters.uscl.ereader.core.book.domain.BookDefinition;
import com.thomsonreuters.uscl.ereader.core.book.domain.BookDefinitionLock;


/**
 * DAO to manage BookDefinitionLock entities.
 * 
 */

public class BookDefinitionLockServiceImpl implements BookDefinitionLockService {
	private BookDefinitionLockDao dao;

	/**
	 * Checks if book definition is locked.  If the book definition is locked, returns
	 * BookDefinitionLock entity. Otherwise, null is returned
	 */
	@Transactional(readOnly=true)
	public BookDefinitionLock findBookLockByBookDefinition(BookDefinition book)
			throws DataAccessException {
		
		// DAO returns BookDefinitionLocks in checkout_timestamp sorted by descending order 
		List<BookDefinitionLock> locks = dao.findLocksByBookDefinition(book);
		if(locks.size() > 0) {
			BookDefinitionLock newestLock = locks.get(0);
			if(islockTimeoutSurpassed(newestLock.getCheckoutTimestamp())) {
				// Delete all locks for current Book Definition
				for(BookDefinitionLock lock : locks) {
					dao.removeLock(lock);
				}
			} else {
				// Book Definition is locked
				return newestLock;
			}
		} 
		
		return null;
	}
	
	private boolean islockTimeoutSurpassed(Date lockDate) {
		long currentTime = Calendar.getInstance().getTimeInMillis();
		Calendar lockCal = Calendar.getInstance();
		lockCal.setTime(lockDate);
		long lockTime = lockCal.getTimeInMillis();
		
		long difference = currentTime - lockTime;
		int seconds = (int) (difference/(1000));
		
		if(seconds >= BookDefinitionLock.LOCK_TIMEOUT_SEC ) {
			return true;
		} else {
			return false;
		}
	}
	
	@Transactional(readOnly=true)
	public List<BookDefinitionLock> findAllActiveLocks() {
		return dao.findAllActiveLocks();
	}
	
	@Transactional(readOnly=true)
	public BookDefinitionLock findBookDefinitionLockByPrimaryKey(Long primaryKey) {
		return dao.findBookDefinitionLockByPrimaryKey(primaryKey);
	}

	@Transactional
	public void removeLock(BookDefinition book) throws DataAccessException {
		List<BookDefinitionLock> locks = dao.findLocksByBookDefinition(book);
		
		for(BookDefinitionLock lock : locks) {
			dao.removeLock(lock);
		}
	}

	@Transactional
	public void lockBookDefinition(BookDefinition bookDefinition, String username, String fullName) {
		BookDefinitionLock lock = new BookDefinitionLock();
		lock.setEbookDefinition(bookDefinition);
		lock.setUsername(username);
		lock.setFullName(fullName);
		lock.setCheckoutTimestamp(new Date());
		
		dao.saveLock(lock);
	}
	
	@Required
	public void setBookDefinitionLockDao(BookDefinitionLockDao dao) {
		this.dao = dao;
	}
}
