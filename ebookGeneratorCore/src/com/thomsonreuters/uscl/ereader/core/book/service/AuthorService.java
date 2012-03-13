/*
 * Copyright 2012: Thomson Reuters Global Resources. All Rights Reserved.
 * Proprietary and Confidential information of TRGR. Disclosure, Use or
 * Reproduction without the written authorization of TRGR is prohibited
 */

package com.thomsonreuters.uscl.ereader.core.book.service;

import java.util.List;

import com.thomsonreuters.uscl.ereader.core.book.domain.Author;

/**
 * Spring service that handles CRUD requests for Author entities
 * 
 */
public interface AuthorService {

	/**
	 * Save an existing Author entity
	 * 
	 */
	public void saveAuthor(Author author);

	/**
	 * Delete an existing Author entity
	 * 
	 */
	public void deleteAuthor(Author author);

	/**
	 */
	public Author findAuthorById(Long authorId);
	/**
	 */
	public List<Author> findAuthorsByEBookDefnId(Long eBookDefnId);
}