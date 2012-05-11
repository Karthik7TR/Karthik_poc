/*
 * Copyright 2011: Thomson Reuters Global Resources. All Rights Reserved.
 * Proprietary and Confidential information of TRGR. Disclosure, Use or
 * Reproduction without the written authorization of TRGR is prohibited
 */
package com.thomsonreuters.uscl.ereader.mgr.library.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Required;
import org.springframework.transaction.annotation.Transactional;

import com.thomsonreuters.uscl.ereader.mgr.library.dao.LibraryListDao;
import com.thomsonreuters.uscl.ereader.mgr.library.vdo.LibraryList;
import com.thomsonreuters.uscl.ereader.mgr.library.vdo.LibraryListFilter;
import com.thomsonreuters.uscl.ereader.mgr.library.vdo.LibraryListSort;

/**
 * Library list service
 */
public class LibraryListServiceImpl implements LibraryListService {
	//private static final Logger log = Logger.getLogger(LibraryListServiceImpl.class);

	private LibraryListDao libraryListDao;
	
	/**
	 * Returns all the current book definitions based on the search criterion
	 * 
	 * @return a list of LibraryList
	 */
	@Transactional(readOnly = true)
	public List<LibraryList> findBookDefinitions(LibraryListFilter filter, LibraryListSort sort) {
		return libraryListDao.findBookDefinitions(filter, sort);
	}

	/**
	 * Returns a count of the current book definitions.
	 * @return an long
	 */
	@Transactional(readOnly = true)
	public Integer numberOfBookDefinitions(LibraryListFilter filter) {
		return libraryListDao.numberOfBookDefinitions(filter);
	}

	@Required
	public void setLibraryListDao(LibraryListDao dao) {
		this.libraryListDao = dao;
	}
}
