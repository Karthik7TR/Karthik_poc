/*
 * Copyright 2012: Thomson Reuters Global Resources. All Rights Reserved.
 * Proprietary and Confidential information of TRGR. Disclosure, Use or
 * Reproduction without the written authorization of TRGR is prohibited
 */
package com.thomsonreuters.uscl.ereader.mgr.web.controller.booklibrary;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.thomsonreuters.uscl.ereader.orchestrate.core.BookDefinition;
import com.thomsonreuters.uscl.ereader.orchestrate.core.service.CoreService;

/**
 * Class to return paginated lists of book definition
 */
public class BookLibraryPaginatedList {
	private static final Logger log = Logger
			.getLogger(BookLibraryPaginatedList.class);

	private CoreService coreService;

	public List<BookDefinitionVdo> getPageItems(String sortProperty,
			boolean isAscending, int pageNumber, int itemsPerPage) {
		List<BookDefinitionVdo> pageItems = new ArrayList<BookDefinitionVdo>();

		List<BookDefinition> books = coreService.findAllBookDefinitions();

		for (BookDefinition book : books) {
			pageItems.add(new BookDefinitionVdo(book, false));
		}
		return pageItems;
	}

}
