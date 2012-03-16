/*
 * Copyright 2011: Thomson Reuters Global Resources. All Rights Reserved.
 * Proprietary and Confidential information of TRGR. Disclosure, Use or
 * Reproduction without the written authorization of TRGR is prohibited
 */
package com.thomsonreuters.uscl.ereader.core.book.dao;

import java.util.List;

import org.springframework.dao.DataAccessException;

import com.thomsonreuters.uscl.ereader.core.book.domain.BookDefinition;

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
	 * Returns a count of the current book definitions.
	 * @return an integer
	 */
	public long countNumberOfBookDefinitions();
	
	/**
	 * Removes a book definition.
	 * @param key the primary key of the definition
	 * @return void
	 */
	public void removeBookDefinition(String fullyQualifiedTitleId) throws DataAccessException;
	
	/**
	 * Saves a book definitions.
	 * @param a Book definition
	 * @return void
	 */

	public void saveBookDefinition(BookDefinition eBook);

}
