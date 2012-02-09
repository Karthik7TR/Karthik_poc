/*
 * Copyright 2012: Thomson Reuters Global Resources. All Rights Reserved.
 * Proprietary and Confidential information of TRGR. Disclosure, Use or
 * Reproduction without the written authorization of TRGR is prohibited
 */
package com.thomsonreuters.uscl.ereader.mgr.web.controller.booklibrary;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;

import com.thomsonreuters.uscl.ereader.orchestrate.core.BookDefinition;
import com.thomsonreuters.uscl.ereader.orchestrate.core.BookDefinitionKey;
import com.thomsonreuters.uscl.ereader.orchestrate.core.service.CoreService;

/**
 * Class that will provide book library service methods
 */
public class BookLibraryService {
	private static final Logger log = Logger
			.getLogger(BookLibraryService.class);

	private CoreService coreService;

	@Required
	public void setCoreService(CoreService coreService) {
		this.coreService = coreService;
	}

	/**
	 * 
	 * @param sortProperty
	 * @param isAscending
	 * @param pageNumber
	 * @param itemsPerPage
	 * @return
	 */
	public List<BookDefinitionVdo> getBooksOnPage(String sortProperty,
			boolean isAscending, int pageNumber, int itemsPerPage) {
		List<BookDefinitionVdo> pageItems = new ArrayList<BookDefinitionVdo>();

		List<BookDefinition> books = coreService.findBookDefinitions(
				sortProperty, isAscending, pageNumber, itemsPerPage);

		for (BookDefinition book : books) {
			pageItems.add(new BookDefinitionVdo(book, false));
		}
		return pageItems;
	}

	/**
	 * 
	 * @return
	 */
	public List<BookDefinitionVdo> getAllBooks() {
		List<BookDefinitionVdo> pageItems = new ArrayList<BookDefinitionVdo>();

		List<BookDefinition> books = coreService.findAllBookDefinitions();

		for (BookDefinition book : books) {
			pageItems.add(new BookDefinitionVdo(book, false));
		}
		return pageItems;
	}

	/**
	 * 
	 * @return
	 */
	public long getTotalBookCount() {
		return coreService.countNumberOfBookDefinitions();
	}

	/**
	 * 
	 * @param key
	 * @return
	 */

	public BookDefinitionVdo getSingleBook(BookDefinitionKey key) {
		BookDefinition book = coreService.findBookDefinition(key);
		return new BookDefinitionVdo(book, false);
	}

}
