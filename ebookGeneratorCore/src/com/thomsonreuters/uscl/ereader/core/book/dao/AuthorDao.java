/*
 * Copyright 2012: Thomson Reuters Global Resources. All Rights Reserved.
 * Proprietary and Confidential information of TRGR. Disclosure, Use or
 * Reproduction without the written authorization of TRGR is prohibited
 */

package com.thomsonreuters.uscl.ereader.core.book.dao;

import java.util.List;

import org.springframework.dao.DataAccessException;

import com.thomsonreuters.uscl.ereader.core.book.domain.Author;
import com.thomsonreuters.uscl.ereader.core.book.domain.AuthorPK;

/**
 * DAO to manage Author entities.
 * 
 */
public interface AuthorDao {

	/**
	 * Query - findAuthorByPrimaryKey
	 * 
	 */
	public Author findAuthorByPrimaryKey(AuthorPK authorPk)
			throws DataAccessException;

	/**
	 * Query - findAuthorsByEBookDefnId
	 * 
	 */
	public List<Author> findAuthorsByEBookDefnId(Long eBookDefnId)
			throws DataAccessException;


	public void remove(Author toRemove) throws DataAccessException;

	public void saveAuthor(Author author);;

}