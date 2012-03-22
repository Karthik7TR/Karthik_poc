/*
 * Copyright 2011: Thomson Reuters Global Resources. All Rights Reserved.
 * Proprietary and Confidential information of TRGR. Disclosure, Use or
 * Reproduction without the written authorization of TRGR is prohibited
 */
package com.thomsonreuters.uscl.ereader.core.library.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Required;
import org.springframework.transaction.annotation.Transactional;

import com.thomsonreuters.uscl.ereader.core.library.dao.LibraryListDao;
import com.thomsonreuters.uscl.ereader.core.library.domain.LibraryList;

/**
 * Shared service methods used in both the Spring Batch engine and the dashboard web apps.
 */
public class LibraryListServiceImpl implements LibraryListService {
	//private static final Logger log = Logger.getLogger(BookDefinitionServiceImpl.class);
	private LibraryListDao libraryListDao;
	
	@Override
	@Transactional(readOnly = true)
	public List<LibraryList> findBookDefinitions(String sortProperty, boolean isAscending, int pageNumber, int itemsPerPage) {
		return libraryListDao.findBookDefinitions(sortProperty, isAscending, pageNumber, itemsPerPage);
	}
	
	@Override
	@Transactional(readOnly = true)
	public Long countNumberOfBookDefinitions() {
		return libraryListDao.countNumberOfBookDefinitions();
	}

	@Required
	public void setLibraryListDao(LibraryListDao dao) {
		this.libraryListDao = dao;
	}
}

