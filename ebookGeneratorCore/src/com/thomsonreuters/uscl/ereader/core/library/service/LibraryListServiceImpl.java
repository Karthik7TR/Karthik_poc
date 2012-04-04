/*
 * Copyright 2011: Thomson Reuters Global Resources. All Rights Reserved.
 * Proprietary and Confidential information of TRGR. Disclosure, Use or
 * Reproduction without the written authorization of TRGR is prohibited
 */
package com.thomsonreuters.uscl.ereader.core.library.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Required;
import org.springframework.transaction.annotation.Transactional;

import com.thomsonreuters.uscl.ereader.core.book.dao.BookDefinitionDao;
import com.thomsonreuters.uscl.ereader.core.book.domain.Author;
import com.thomsonreuters.uscl.ereader.core.book.domain.BookDefinition;
import com.thomsonreuters.uscl.ereader.core.book.service.AuthorService;
import com.thomsonreuters.uscl.ereader.core.library.dao.LibraryListDao;
import com.thomsonreuters.uscl.ereader.core.library.domain.LibraryList;
import com.thomsonreuters.uscl.ereader.stats.service.PublishingStatsService;

/**
 * Shared service methods used in both the Spring Batch engine and the dashboard
 * web apps.
 */
public class LibraryListServiceImpl implements LibraryListService {
	// private static final Logger log =
	// Logger.getLogger(BookDefinitionServiceImpl.class);
	private LibraryListDao libraryListDao;
	private BookDefinitionDao bookDefinitionDao;
	private AuthorService authorService;
	private PublishingStatsService publishingStatsService;

	@Override
	@Transactional(readOnly = true)
	public List<LibraryList> findBookDefinitions(String sortProperty,
			boolean isAscending, int pageNumber, int itemsPerPage) {
		return libraryListDao.findBookDefinitions(sortProperty, isAscending,
				pageNumber, itemsPerPage);
	}

	@Override
	@Transactional(readOnly = true)
	public Long countNumberOfBookDefinitions() {
		return libraryListDao.countNumberOfBookDefinitions();
	}

	@Override
	@Transactional(readOnly = true)
	public List<LibraryList> findBookDefinitions(String sortProperty,
			boolean isAscending, int pageNumber, int itemsPerPage,
			String proviewDisplayName, String titleID, String isbn,
			String materialID, String to, String from, String status) {

		List<BookDefinition> bookDefinitions = bookDefinitionDao.findBookDefinitions(sortProperty, isAscending, pageNumber,
						itemsPerPage, proviewDisplayName, titleID, isbn,
						materialID, to, from, status);

		List<LibraryList> libraryLists = new ArrayList<LibraryList>();

		for (BookDefinition bookDefinition : bookDefinitions) {

			// Add the author information
			Set<Author> authors = new HashSet<Author>();
			authors.addAll(authorService
					.findAuthorsByEBookDefnId(bookDefinition
							.getEbookDefinitionId()));
			Date lastPublishDate = publishingStatsService
					.findLastPublishDateForBook(bookDefinition
							.getEbookDefinitionId());

			LibraryList libraryList = new LibraryList(
					bookDefinition.getEbookDefinitionId(),
					bookDefinition.getProviewDisplayName(),
					bookDefinition.getFullyQualifiedTitleId(),
					bookDefinition.getEbookDefinitionCompleteFlag() ? "Y" : "N",
					bookDefinition.isDeletedFlag() ? "Y" : "N", bookDefinition
							.getLastUpdated(), lastPublishDate, authors);

			libraryLists.add(libraryList);

		}

		return libraryLists;
	}

	public BookDefinitionDao getBookDefinitionDao() {
		return bookDefinitionDao;
	}

	@Required
	public void setBookDefinitionDao(BookDefinitionDao bookDefinitionDao) {
		this.bookDefinitionDao = bookDefinitionDao;
	}

	public AuthorService getAuthorService() {
		return authorService;
	}

	@Required
	public void setAuthorService(AuthorService authorService) {
		this.authorService = authorService;
	}

	public LibraryListDao getLibraryListDao() {
		return libraryListDao;
	}

	@Required
	public void setLibraryListDao(LibraryListDao dao) {
		this.libraryListDao = dao;
	}

	public PublishingStatsService getPublishingStatsService() {
		return publishingStatsService;
	}

	@Required
	public void setPublishingStatsService(
			PublishingStatsService publishingStatsService) {
		this.publishingStatsService = publishingStatsService;
	}

}
