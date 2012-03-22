/*
 * Copyright 2011: Thomson Reuters Global Resources. All Rights Reserved.
 * Proprietary and Confidential information of TRGR. Disclosure, Use or
 * Reproduction without the written authorization of TRGR is prohibited
 */
package com.thomsonreuters.uscl.ereader.core.library.service;

import java.util.List;

import com.thomsonreuters.uscl.ereader.core.library.domain.LibraryList;

/**
 * Service methods that are common to both the Spring Batch generator engine and manager web applications.
 */
public interface LibraryListService {
	

	public List<LibraryList> findBookDefinitions(
			String sortProperty, boolean isAscending, int pageNumber,
			int itemsPerPage);

	/**
	 * Returns a count of all current book definitions.
	 * @return an integer
	 */
	public Long countNumberOfBookDefinitions();	
	



}
