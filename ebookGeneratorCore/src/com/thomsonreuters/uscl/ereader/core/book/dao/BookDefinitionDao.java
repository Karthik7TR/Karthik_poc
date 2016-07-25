/*
 * Copyright 2016: Thomson Reuters Global Resources. All Rights Reserved.
 * Proprietary and Confidential information of TRGR. Disclosure, Use or
 * Reproduction without the written authorization of TRGR is prohibited
 */

package com.thomsonreuters.uscl.ereader.core.book.dao;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import org.springframework.dao.DataAccessException;

import com.thomsonreuters.uscl.ereader.core.book.domain.BookDefinition;
import com.thomsonreuters.uscl.ereader.core.book.domain.SplitDocument;
import com.thomsonreuters.uscl.ereader.core.book.domain.SplitNodeInfo;

public interface BookDefinitionDao {
	
	/**
	 * Returns all the current book definitions.
	 * @return a list of BookDefinition
	 */
	@Deprecated
	public List<BookDefinition> findAllBookDefinitions();
	
	/**
	 * Find a book definition by its primary key.
	 * @param key the primary key of the definition
	 * @return the found entity, or null if not found.
	 */
	public BookDefinition findBookDefinitionByTitle(String fullyQualifiedTitleId);
	
	
	/**
	 * Find a book definition by its primary key.
	 * @param key the primary key of the definition
	 * @return the found entity, or null if not found.
	 */
	public BookDefinition findBookDefinitionByEbookDefId(Long ebookDefId);

	
	/**
	 * Returns all the current book definitions based on the search criterion
	 * 
	 * @return a list of BookDefinition
	 */
	public List<BookDefinition> findBookDefinitions(String sortProperty, boolean isAscending, int pageNumber, int itemsPerPage);

	/**
	 * Returns all the book definitions based on Keyword Type Code 
	 * @return a list of BookDefinition
	 */
	public List<BookDefinition> findAllBookDefinitionsByKeywordCodeId(Long keywordTypeCodeId);
	
	/**
	 * Returns all the book definitions based on Keyword Type Value 
	 * @return a list of BookDefinition
	 */
	public List<BookDefinition> findAllBookDefinitionsByKeywordValueId(Long keywordTypeValueId);
	
	/**
	 * Returns a count of the current book definitions.
	 * @return an integer
	 */
	public long countNumberOfBookDefinitions();
	
	/**
	 * Removes a book definition.
	 * @param key the primary key of the definition
	 * @return void
	 */
	public void removeBookDefinition(Long bookDefId) throws DataAccessException;
	
	/**
	 * Saves a book definitions.
	 * @param a Book definition
	 * @return BookDefinition
	 */

	public BookDefinition saveBookDefinition(BookDefinition eBook);
	
	public BookDefinition saveSplitDocuments(Long ebookDefinitionId, Collection<SplitDocument> splitDocuments, int parts);

	List<BookDefinition> findBookDefinitions(String sortProperty,
			boolean isAscending, int pageNumber, int itemsPerPage,
			String proviewDisplayName, String fullyQualifiedTitleId, String isbn,
 String materialId, Date to, Date from, String status);
	
	public BookDefinition saveBookDefinition(Long ebookDefinitionId, Collection<SplitNodeInfo> splitNodeInfoList, String newVersion);
	
	public Integer getSplitPartsForEbook(Long ebookDefinitionId);
	
	public void removeSplitDocuments(Long bookId);	
	
	public List<SplitDocument> getSplitDocumentsforBook(Long bookId);	

}
