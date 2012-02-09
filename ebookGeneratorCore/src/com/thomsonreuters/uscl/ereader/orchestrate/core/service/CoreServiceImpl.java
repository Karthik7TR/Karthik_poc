/*
 * Copyright 2011: Thomson Reuters Global Resources. All Rights Reserved.
 * Proprietary and Confidential information of TRGR. Disclosure, Use or
 * Reproduction without the written authorization of TRGR is prohibited
 */
package com.thomsonreuters.uscl.ereader.orchestrate.core.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Required;
import org.springframework.transaction.annotation.Transactional;

import com.thomsonreuters.uscl.ereader.orchestrate.core.BookDefinition;
import com.thomsonreuters.uscl.ereader.orchestrate.core.BookDefinitionKey;
import com.thomsonreuters.uscl.ereader.orchestrate.core.dao.CoreDao;

/**
 * Shared service methods used in both the Spring Batch engine and the dashboard web apps.
 */
public class CoreServiceImpl implements CoreService {
	//private static final Logger log = Logger.getLogger(CoreDaoImpl.class);
	private CoreDao coreDao;
	
	@Override
	@Transactional(readOnly = true)
	public BookDefinition findBookDefinition(BookDefinitionKey key) {
		return coreDao.findBookDefinition(key);
	}
	
	@Override
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
	
		BookDefinitionKey existingBookPk = new BookDefinitionKey();
		existingBookPk.setFullyQualifiedTitleId(eBook.getPrimaryKey().getFullyQualifiedTitleId());
	
		BookDefinition existingBook = coreDao.findBookDefinition(existingBookPk);
	
		if (existingBook != null) {
			if (existingBook != eBook) {
				existingBook.setAuthorInfo(eBook.getAuthorInfo());
				existingBook.setBookName(eBook.getBookName());
				existingBook.setContentSubtype(eBook.getContentSubtype());
				existingBook.setContentType(eBook.getContentType());
				existingBook.setCopyright(eBook.getCopyright());
				existingBook.setCoverImage(eBook.getCoverImage());
				existingBook.setDocCollectionName(eBook.getDocCollectionName());
				existingBook.setIsbn(eBook.getIsbn());
				existingBook.setMajorVersion(eBook.getMajorVersion());
				existingBook.setMaterialId(eBook.getMaterialId());
				existingBook.setMaterialIdEmbeddedInDocText(eBook.getMaterialIdEmbeddedInDocText());
				existingBook.setMinorVersion(eBook.getMinorVersion());
				existingBook.setNortDomain(eBook.getNortDomain());
				existingBook.setNortFilterView(eBook.getNortFilterView());
				existingBook.setRootTocGuid(eBook.getRootTocGuid());
				existingBook.setTocCollectionName(eBook.getTocCollectionName());
			}
			coreDao.saveBookDefinition(existingBook);
		} else {
			coreDao.saveBookDefinition(eBook);
		}
	}
	
}

