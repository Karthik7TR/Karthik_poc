/*
 * Copyright 2011: Thomson Reuters Global Resources. All Rights Reserved.
 * Proprietary and Confidential information of TRGR. Disclosure, Use or
 * Reproduction without the written authorization of TRGR is prohibited
 */
package com.thomsonreuters.uscl.ereader.core.book.service;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Required;
import org.springframework.transaction.annotation.Transactional;

import com.thomsonreuters.uscl.ereader.core.book.dao.BookDefinitionDao;
import com.thomsonreuters.uscl.ereader.core.book.domain.BookDefinition;
import com.thomsonreuters.uscl.ereader.core.book.domain.SplitDocument;
import com.thomsonreuters.uscl.ereader.core.book.domain.SplitNodeInfo;

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
	
	/**
	 * Returns all the book definitions based on Keyword Type Code 
	 * @return a list of BookDefinition
	 */
	@Override
	@Transactional(readOnly = true)
	public List<BookDefinition> findAllBookDefinitionsByKeywordCodeId(Long keywordTypeCodeId) {
		return bookDefinitionDao.findAllBookDefinitionsByKeywordCodeId(keywordTypeCodeId);
	}
	
	/**
	 * Returns all the book definitions based on Keyword Type Value 
	 * @return a list of BookDefinition
	 */
	@Override
	@Transactional(readOnly = true)
	public List<BookDefinition> findAllBookDefinitionsByKeywordValueId(Long keywordTypeValueId){
		return bookDefinitionDao.findAllBookDefinitionsByKeywordValueId(keywordTypeValueId);
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
	 * Update the book definition
	 * 
	 */
	@Transactional
	public void updateSplitNodeInfoSet(Long bookId, Collection<SplitNodeInfo> splitNodeInfoList, String version) {
		bookDefinitionDao.saveBookDefinition(bookId,splitNodeInfoList,version);
	}


	/**
	 * Save an existing Book Definition
	 * 
	 */
	@Transactional
	public BookDefinition saveBookDefinition(BookDefinition eBook) {
		eBook = bookDefinitionDao.saveBookDefinition(eBook);
		
		return eBook;
	}
	
	/**
	 * Delete the book definition
	 * 
	 */
	@Transactional
	public void removeBookDefinition(Long ebookDefId) {
		bookDefinitionDao.removeBookDefinition(ebookDefId);
	}
	
	@Transactional
	public BookDefinition saveSplitDocumentsforEBook(Long bookId, Collection<SplitDocument> splitDocuments,int parts){
		return bookDefinitionDao.saveSplitDocuments(bookId,splitDocuments,parts);
	}
	
	@Transactional(readOnly = true)
	public Integer getSplitPartsForEbook(Long bookId){
		return bookDefinitionDao.getSplitPartsForEbook(bookId);
	}
	
	@Transactional
	public void deleteSplitDocuments(Long bookId){
		bookDefinitionDao.removeSplitDocuments(bookId);		
	}
	
	@Transactional(readOnly = true)
	public List<SplitDocument> findSplitDocuments(Long bookId){
		return bookDefinitionDao.getSplitDocumentsforBook(bookId);	
	}
}

