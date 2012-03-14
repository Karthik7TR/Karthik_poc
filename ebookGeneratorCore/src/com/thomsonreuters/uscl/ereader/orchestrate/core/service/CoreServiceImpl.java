/*
 * Copyright 2011: Thomson Reuters Global Resources. All Rights Reserved.
 * Proprietary and Confidential information of TRGR. Disclosure, Use or
 * Reproduction without the written authorization of TRGR is prohibited
 */
package com.thomsonreuters.uscl.ereader.orchestrate.core.service;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Required;
import org.springframework.transaction.annotation.Transactional;

import com.thomsonreuters.uscl.ereader.orchestrate.core.BookDefinition;
import com.thomsonreuters.uscl.ereader.orchestrate.core.dao.CoreDao;

/**
 * Shared service methods used in both the Spring Batch engine and the dashboard web apps.
 */
public class CoreServiceImpl implements CoreService {
	//private static final Logger log = Logger.getLogger(CoreDaoImpl.class);
	private CoreDao coreDao;
	
	@Override
	@Transactional(readOnly = true)
	public BookDefinition findBookDefinitionByTitle(String titleId) {
		return coreDao.findBookDefinitionByTitle(titleId);
	}
	
	@Override
	@Transactional(readOnly = true)
	public BookDefinition findBookDefinitionByEbookDefId(Long ebookDefId) {
		return coreDao.findBookDefinitionByEbookDefId(ebookDefId);
	}
	
	@Override
	@Deprecated
	@Transactional(readOnly = true)
	public List<BookDefinition> findAllBookDefinitions() {
		return coreDao.findAllBookDefinitions();
	}

	@Override
	@Transactional(readOnly = true)
	public List<BookDefinition> findBookDefinitions(String sortProperty, boolean isAscending, int pageNumber, int itemsPerPage) {
		return coreDao.findBookDefinitions(sortProperty, isAscending, pageNumber, itemsPerPage);
	}
	
	@Required
	public void setCoreDao(CoreDao dao) {
		this.coreDao = dao;
	}

	@Override
	@Transactional(readOnly = true)
	public long countNumberOfBookDefinitions() {
		return coreDao.countNumberOfBookDefinitions();
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
	public void saveBookDefinition(BookDefinition eBook) {
		BookDefinition existingBook = coreDao.findBookDefinitionByTitle(eBook.getFullyQualifiedTitleId());
		
		if(existingBook != null) {
			eBook.setEbookDefinitionId(existingBook.getEbookDefinitionId());
			eBook.setPublishedOnceFlag(existingBook.IsPublishedOnceFlag());
			eBook.setIsDeletedFlag(existingBook.getIsDeletedFlag());
		}
		
		// Update Date
		eBook.setLastUpdated(new Date());
		
		coreDao.saveBookDefinition(eBook);
	}
	
}

