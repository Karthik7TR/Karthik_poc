/*
 * Copyright 2011: Thomson Reuters Global Resources. All Rights Reserved.
 * Proprietary and Confidential information of TRGR. Disclosure, Use or
 * Reproduction without the written authorization of TRGR is prohibited
 */
package com.thomsonreuters.uscl.ereader.core.book.service;

import java.util.Date;
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
	 * Save an existing Book Definition
	 * 
	 */
	@Transactional
	public BookDefinition saveBookDefinition(BookDefinition eBook) {
		BookDefinition existingBook = bookDefinitionDao.findBookDefinitionByTitle(eBook.getFullyQualifiedTitleId());
		
		if(existingBook != null) {
			eBook.setEbookDefinitionId(existingBook.getEbookDefinitionId());
			eBook.setPublishedOnceFlag(existingBook.IsPublishedOnceFlag());
			eBook.setIsDeletedFlag(existingBook.getIsDeletedFlag());
		}
		
		// Update Date
		eBook.setLastUpdated(new Date());
		
		eBook = bookDefinitionDao.saveBookDefinition(eBook);
		
		return eBook;
	}
	
}

