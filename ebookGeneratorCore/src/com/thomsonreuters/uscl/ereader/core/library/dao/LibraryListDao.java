/*
 * Copyright 2011: Thomson Reuters Global Resources. All Rights Reserved.
 * Proprietary and Confidential information of TRGR. Disclosure, Use or
 * Reproduction without the written authorization of TRGR is prohibited
 */
package com.thomsonreuters.uscl.ereader.core.library.dao;

import java.util.List;

import com.thomsonreuters.uscl.ereader.core.library.domain.LibraryList;

public interface LibraryListDao {

	/**
	 * Returns all the current book definitions based on the search criterion
	 * 
	 * @return a list of BookDefinition
	 */
	public List<LibraryList> findBookDefinitions(String sortProperty, boolean isAscending, int pageNumber, int itemsPerPage);

	/**
	 * Returns a count of the current book definitions.
	 * @return an long
	 */
	public Long countNumberOfBookDefinitions();
	

}
