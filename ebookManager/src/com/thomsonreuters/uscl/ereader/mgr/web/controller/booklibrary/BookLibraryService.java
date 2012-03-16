/*
 * Copyright 2012: Thomson Reuters Global Resources. All Rights Reserved.
 * Proprietary and Confidential information of TRGR. Disclosure, Use or
 * Reproduction without the written authorization of TRGR is prohibited
 */
package com.thomsonreuters.uscl.ereader.mgr.web.controller.booklibrary;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Required;

import com.thomsonreuters.uscl.ereader.core.book.domain.BookDefinition;
import com.thomsonreuters.uscl.ereader.core.book.service.BookDefinitionService;

/**
 * Class that will provide book library service methods
 */
public class BookLibraryService {
	//private static final Logger log = Logger.getLogger(BookLibraryService.class);

	private BookDefinitionService bookDefinitionService;

	@Required
	public void setBookDefinitionService(BookDefinitionService service) {
		this.bookDefinitionService = service;
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

		List<BookDefinition> books = bookDefinitionService.findBookDefinitions(
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
	public long getTotalBookCount() {
		return bookDefinitionService.countNumberOfBookDefinitions();
	}

	/**
	 * 
	 * @param key
	 * @return
	 */

	public BookDefinitionVdo getSingleBook(Long key) {
		BookDefinition book = bookDefinitionService.findBookDefinitionByEbookDefId(key);
		return new BookDefinitionVdo(book, false);
	}

}
