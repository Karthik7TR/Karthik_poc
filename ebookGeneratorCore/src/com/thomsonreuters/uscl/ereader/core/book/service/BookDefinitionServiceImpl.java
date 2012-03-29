/*
 * Copyright 2011: Thomson Reuters Global Resources. All Rights Reserved.
 * Proprietary and Confidential information of TRGR. Disclosure, Use or
 * Reproduction without the written authorization of TRGR is prohibited
 */
package com.thomsonreuters.uscl.ereader.core.book.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Required;
import org.springframework.transaction.annotation.Transactional;

import com.thomsonreuters.uscl.ereader.core.book.dao.BookDefinitionDao;
import com.thomsonreuters.uscl.ereader.core.book.domain.BookDefinition;

/**
 * Shared service methods used in both the Spring Batch engine and the dashboard web apps.
 */
public class BookDefinitionServiceImpl implements BookDefinitionService {
	//private static final Logger log = Logger.getLogger(BookDefinitionServiceImpl.class);
	private BookDefinitionDao bookDefinitionDao;
	
	@Override
	@Transactional(readOnly = true)
	public BookDefinition findBookDefinitionByTitle(String titleId) {
		return bookDefinitionDao.findBookDefinitionByTitle(titleId);
	}
	
	@Override
	@Transactional(readOnly = true)
	public BookDefinition findBookDefinitionByEbookDefId(Long ebookDefId) {
		return bookDefinitionDao.findBookDefinitionByEbookDefId(ebookDefId);
	}
	
	@Override
	@Deprecated
	@Transactional(readOnly = true)
	public List<BookDefinition> findAllBookDefinitions() {
		return bookDefinitionDao.findAllBookDefinitions();
	}

	@Override
	@Transactional(readOnly = true)
	public List<BookDefinition> findBookDefinitions(String sortProperty, boolean isAscending, int pageNumber, int itemsPerPage) {
		return bookDefinitionDao.findBookDefinitions(sortProperty, isAscending, pageNumber, itemsPerPage);
	}
	
	@Required
	public void setBookDefinitionDao(BookDefinitionDao dao) {
		this.bookDefinitionDao = dao;
	}

	@Override
	@Transactional(readOnly = true)
	public long countNumberOfBookDefinitions() {
		return bookDefinitionDao.countNumberOfBookDefinitions();
	}

	/**
	 * Instantiates a new DocMetadataServiceImpl.
	 * 
	 */
	/*
	 * public DocMetadataServiceImpl() { docMetaXMLParser = new
	 * DocMetaDataXMLParser(); }
	 */
	
	/**
	 * Update the published status of the book definition
	 * 
	 */
	@Transactional
	public void updatePublishedStatus(Long bookId, boolean isPublishedOnce) {
		BookDefinition book = findBookDefinitionByEbookDefId(bookId);
		if(book != null) {
			book.setPublishedOnceFlag(isPublishedOnce);
			bookDefinitionDao.saveBookDefinition(book);
		}
	}
	
	/**
	 * Update the deleted status of the book definition
	 * 
	 */
	@Transactional
	public void updateDeletedStatus(Long bookId, boolean isDeleted) {
		BookDefinition book = findBookDefinitionByEbookDefId(bookId);
		if(book != null) {
			book.setIsDeletedFlag(isDeleted);
			bookDefinitionDao.saveBookDefinition(book);
		}
	}


	/**
	 * Save an existing Book Definition
	 * 
	 */
	@Transactional
	public BookDefinition saveBookDefinition(BookDefinition eBook) {
		BookDefinition existingBook = bookDefinitionDao.findBookDefinitionByTitle(eBook.getFullyQualifiedTitleId());
		
		// Prevent publishedOnce and isDeleted from being updated through save.
		// updateDeletedStatus() and updatePublishedStatus() are used to update those columns
		// Prevent user from updating these columns by injecting a form to create/update.
		if(existingBook != null) {
			eBook.setEbookDefinitionId(existingBook.getEbookDefinitionId());
			eBook.setPublishedOnceFlag(existingBook.getPublishedOnceFlag());
			eBook.setIsDeletedFlag(existingBook.isDeletedFlag());
		}
		
		eBook = bookDefinitionDao.saveBookDefinition(eBook);
		
		return eBook;
	}
	
}

