/*
 * Copyright 2011: Thomson Reuters Global Resources. All Rights Reserved.
 * Proprietary and Confidential information of TRGR. Disclosure, Use or
 * Reproduction without the written authorization of TRGR is prohibited
 */
package com.thomsonreuters.uscl.ereader.core.book.dao;

import java.util.List;
import com.thomsonreuters.uscl.ereader.core.book.domain.Author;
import com.thomsonreuters.uscl.ereader.core.book.domain.KeywordTypeCode;


public interface BookDao {
	
	/**
	 * Get all the KeywordTypes from the KEYWORD_TYPE_VALUES and KEYWORD_TYPE_CODE
	 * @return a list of keywordType objects
	 */
	public List<KeywordTypeCode> getKeywordsTypesAndValues();
	
	/**
	 * Get all the authors from the AUTHOR table that match the EBOOK_DEFINITION_ID
	 * @param bookDefinitionId
	 * @return a list of author objects
	 */
	public List<Author> getAuthors(int bookDefinitionId);

}
