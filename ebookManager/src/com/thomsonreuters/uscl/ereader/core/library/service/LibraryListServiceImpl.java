/*
 * Copyright 2011: Thomson Reuters Global Resources. All Rights Reserved.
 * Proprietary and Confidential information of TRGR. Disclosure, Use or
 * Reproduction without the written authorization of TRGR is prohibited
 */
package com.thomsonreuters.uscl.ereader.core.library.service;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpSession;

import org.apache.commons.lang.time.DateUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.transaction.annotation.Transactional;

import com.thomsonreuters.uscl.ereader.core.book.dao.BookDefinitionDao;
import com.thomsonreuters.uscl.ereader.core.book.domain.Author;
import com.thomsonreuters.uscl.ereader.core.book.domain.BookDefinition;
import com.thomsonreuters.uscl.ereader.core.book.service.AuthorService;
import com.thomsonreuters.uscl.ereader.core.library.dao.LibraryListDao;
import com.thomsonreuters.uscl.ereader.core.library.domain.LibraryList;
import com.thomsonreuters.uscl.ereader.deliver.exception.ProviewException;
import com.thomsonreuters.uscl.ereader.deliver.service.ProviewClient;
import com.thomsonreuters.uscl.ereader.deliver.service.ProviewTitleContainer;
import com.thomsonreuters.uscl.ereader.deliver.service.ProviewTitleInfo;
import com.thomsonreuters.uscl.ereader.mgr.web.WebConstants;
import com.thomsonreuters.uscl.ereader.stats.service.PublishingStatsService;

/**
 * Library list service
 */
public class LibraryListServiceImpl implements LibraryListService {
	public static final String PROVIEW_DATE_FORMAT_PATTERN = "yyyyMMdd";
	private static final Logger log = Logger
			.getLogger(LibraryListServiceImpl.class);
	private LibraryListDao libraryListDao;
	private BookDefinitionDao bookDefinitionDao;
	private AuthorService authorService;
	private PublishingStatsService publishingStatsService;
	private ProviewClient proviewClient;

	static final Comparator<LibraryList> PUBLISH_DATE_ASCENDING_ORDER = new Comparator<LibraryList>() {

		@Override
		public int compare(LibraryList l1, LibraryList l2) {
			int returnCode = 0;

			if (l1.getLastPublishDate() == null
					&& l2.getLastPublishDate() == null) {
				returnCode = 0;
			} else if (l1.getLastPublishDate() != null
					&& l2.getLastPublishDate() == null) {
				returnCode = 1;
			} else if (l1.getLastPublishDate() == null
					&& l2.getLastPublishDate() != null) {
				returnCode = -1;
			} else if (l1.getLastPublishDate().after(l2.getLastPublishDate())) {
				returnCode = 1;
			} else if (l1.getLastPublishDate().before(l2.getLastPublishDate())) {
				returnCode = -1;
			}
			return returnCode;
		}

	};

	static final Comparator<LibraryList> PUBLISH_DATE_DESCENDING_ORDER = new Comparator<LibraryList>() {

		@Override
		public int compare(LibraryList l1, LibraryList l2) {
			int returnCode = 0;

			if (l2.getLastPublishDate() == null
					&& l1.getLastPublishDate() == null) {
				returnCode = 0;
			} else if (l2.getLastPublishDate() != null
					&& l1.getLastPublishDate() == null) {
				returnCode = 1;
			} else if (l2.getLastPublishDate() == null
					&& l1.getLastPublishDate() != null) {
				returnCode = -1;
			} else if (l2.getLastPublishDate().after(l1.getLastPublishDate())) {
				returnCode = 1;
			} else if (l2.getLastPublishDate().before(l1.getLastPublishDate())) {
				returnCode = -1;
			}
			return returnCode;
		}

	};

	@Override
	@Transactional(readOnly = true)
	@Deprecated
	public List<LibraryList> findBookDefinitions(String sortProperty,
			boolean isAscending, int pageNumber, int itemsPerPage) {
		return libraryListDao.findBookDefinitions(sortProperty, isAscending,
				pageNumber, itemsPerPage);
	}

	@Override
	@Transactional(readOnly = true)
	@Deprecated
	public Long countNumberOfBookDefinitions() {
		return libraryListDao.countNumberOfBookDefinitions();
	}

	private Map<String, ProviewTitleContainer> fetchAllProviewTitleInfo(
			HttpSession httpSession) {
		Map<String, ProviewTitleContainer> allProviewTitleInfo = (Map<String, ProviewTitleContainer>) httpSession
				.getAttribute(WebConstants.KEY_ALL_PROVIEW_TITLES);
		return allProviewTitleInfo;
	}

	private void saveAllProviewTitleInfo(HttpSession httpSession,
			Map<String, ProviewTitleContainer> allProviewTitleInfo) {
		httpSession.setAttribute(WebConstants.KEY_ALL_PROVIEW_TITLES,
				allProviewTitleInfo);

	}

	@Override
	@Transactional(readOnly = true)
	public List<LibraryList> findBookDefinitions(String sortProperty,
			boolean isAscending, int pageNumber, int itemsPerPage,
			String proviewDisplayName, String titleID, String isbn,
			String materialID, Date to, Date from, String status,
			HttpSession httpSession) {

		List<BookDefinition> bookDefinitions = bookDefinitionDao
				.findBookDefinitions(sortProperty, isAscending, pageNumber,
						itemsPerPage, proviewDisplayName, titleID, isbn,
						materialID, to, from, status);

		Map<String, ProviewTitleContainer> allProviewTitleInfo = fetchAllProviewTitleInfo(httpSession);
		try {
			if (allProviewTitleInfo == null) {
				allProviewTitleInfo = proviewClient.getAllProviewTitleInfo();
				saveAllProviewTitleInfo(httpSession, allProviewTitleInfo);
			}
		} catch (ProviewException e) {
			log.debug(e);
		}

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

			String proviewVersion = null;
			Date lastProviewUpdateDate = null;

			if (allProviewTitleInfo != null) {
				ProviewTitleContainer proviewTitleContainer = allProviewTitleInfo
						.get(bookDefinition.getFullyQualifiedTitleId());

				if (proviewTitleContainer != null) {
					ProviewTitleInfo latestProviewTitleInfo = proviewTitleContainer
							.getLatestVersion();
					proviewVersion = latestProviewTitleInfo.getVersion();

					String[] parsePatterns = { PROVIEW_DATE_FORMAT_PATTERN };
					try {
						lastProviewUpdateDate = DateUtils.parseDate(
								latestProviewTitleInfo.getLastupdate(),
								parsePatterns);

					} catch (ParseException e) {

					}

				}
			}

			LibraryList libraryList = new LibraryList(
					bookDefinition.getEbookDefinitionId(),
					bookDefinition.getProviewDisplayName(),
					bookDefinition.getFullyQualifiedTitleId(),
					bookDefinition.getEbookDefinitionCompleteFlag() ? "Y" : "N",
					bookDefinition.isDeletedFlag() ? "Y" : "N", bookDefinition
							.getLastUpdated(), lastPublishDate, authors,
					proviewVersion, lastProviewUpdateDate);

			libraryLists.add(libraryList);

		}

		if (sortProperty.equals("publishEndTimestamp")) {
			if (isAscending) {
				Collections.sort(libraryLists, PUBLISH_DATE_ASCENDING_ORDER);
			} else {
				Collections.sort(libraryLists, PUBLISH_DATE_DESCENDING_ORDER);
			}
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

	public ProviewClient getProviewClient() {
		return proviewClient;
	}

	@Required
	public void setProviewClient(ProviewClient proviewClient) {
		this.proviewClient = proviewClient;
	}
}