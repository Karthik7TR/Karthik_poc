/*
 * Copyright 2011: Thomson Reuters Global Resources. All Rights Reserved.
 * Proprietary and Confidential information of TRGR. Disclosure, Use or
 * Reproduction without the written authorization of TRGR is prohibited
 */
package com.thomsonreuters.uscl.ereader.orchestrate.core.service;

import java.util.List;

import com.thomsonreuters.uscl.ereader.orchestrate.core.BookDefinition;
import com.thomsonreuters.uscl.ereader.orchestrate.core.BookDefinitionKey;

/**
 * Service methods that are common to both the Spring Batch generator engine and manager web applications.
 */
public interface CoreService {
	
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
	public BookDefinition findBookDefinition(BookDefinitionKey key);
	
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

}
