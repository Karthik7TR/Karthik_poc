/*
 * Copyright 2011: Thomson Reuters Global Resources. All Rights Reserved.
 * Proprietary and Confidential information of TRGR. Disclosure, Use or
 * Reproduction without the written authorization of TRGR is prohibited
 */
package com.thomsonreuters.uscl.ereader.core.book.service;

import java.util.List;

import com.thomsonreuters.uscl.ereader.core.book.domain.BookDefinition;

/**
 * Service methods that are common to both the Spring Batch generator engine and manager web applications.
 */
public interface BookDefinitionService {
	
	/**
	 * Returns all the current book definitions.
	 * @return a list of BookDefinition
	 */
	@Deprecated // Remove once the dashboard Create Book functionality is retired in favor of launching books from the manager application.
	public List<BookDefinition> findAllBookDefinitions();
	
	/**
	 * Find a book definition by its primary key.
	 * @param key the primary key of the definition
	 * @return the found entity, or null if not found.
	 */
	public BookDefinition findBookDefinitionByTitle(String titleId);
	
	public BookDefinition findBookDefinitionByEbookDefId(Long ebookDefId);
	
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
	 * Returns all the current book definitions based on the search criterion
	 * 
	 * @return a list of BookDefinitions
	 */

	public List<BookDefinition> findBookDefinitions(
			String sortProperty, boolean isAscending, int pageNumber,
			int itemsPerPage);

	/**
	 * Returns a count of all current book definitions.
	 * @return an integer
	 */
	public long countNumberOfBookDefinitions();	
	
	/**
	 * Update the published status of the book definition
	 * 
	 */
	public void updatePublishedStatus(Long bookId, boolean isPublishedOnce);
	
	/**
	 * Update the deleted status of the book definition
	 * 
	 */
	public void updateDeletedStatus(Long bookId, boolean isDeleted);
	
	/**
	 * Save or update the book definition
	 * 
	 */
	public BookDefinition saveBookDefinition(BookDefinition eBook);

	/**
	 * Delete the book definition
	 * 
	 */
	public void removeBookDefinition(Long ebookDefId);
}
