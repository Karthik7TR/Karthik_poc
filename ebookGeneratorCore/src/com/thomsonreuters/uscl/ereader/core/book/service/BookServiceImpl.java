/*
 * Copyright 2011: Thomson Reuters Global Resources. All Rights Reserved.
 * Proprietary and Confidential information of TRGR. Disclosure, Use or
 * Reproduction without the written authorization of TRGR is prohibited
 */
package com.thomsonreuters.uscl.ereader.core.book.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Required;

import com.thomsonreuters.uscl.ereader.core.book.dao.BookDao;
import com.thomsonreuters.uscl.ereader.core.book.domain.Author;
import com.thomsonreuters.uscl.ereader.core.book.domain.KeywordTypeCode;


public class BookServiceImpl implements BookService {
	private BookDao dao;

	@Override
	public List<KeywordTypeCode> getKeywordsTypesAndValues() {
		return this.dao.getKeywordsTypesAndValues();
	}

	@Override
	public List<Author> getAuthors(int bookDefinitionId) {
		return this.dao.getAuthors(bookDefinitionId);
	}
	
	@Required
	public void setBookDao(BookDao dao) {
		this.dao = dao;
	}

}
