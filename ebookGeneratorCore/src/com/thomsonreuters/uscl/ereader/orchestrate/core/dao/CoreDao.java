/*
 * Copyright 2011: Thomson Reuters Global Resources. All Rights Reserved.
 * Proprietary and Confidential information of TRGR. Disclosure, Use or
 * Reproduction without the written authorization of TRGR is prohibited
 */
package com.thomsonreuters.uscl.ereader.orchestrate.core.dao;

import java.util.List;

import com.thomsonreuters.uscl.ereader.orchestrate.core.BookDefinition;
import com.thomsonreuters.uscl.ereader.orchestrate.core.BookDefinitionKey;

public interface CoreDao {
	
	/**
	 * Returns all the current book definitions.
	 * @return a list of BookDefinition
	 */
	public List<BookDefinition> findAllBookDefinitions();
	
	/**
	 * Find a book definition by its primary key.
	 * @param key the primary key of the definition
	 * @return the found entity, or null if not found.
	 */
	public BookDefinition findBookDefinition(BookDefinitionKey key);
	
	
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
	public int countNumberOfBookDefinitions();

}
