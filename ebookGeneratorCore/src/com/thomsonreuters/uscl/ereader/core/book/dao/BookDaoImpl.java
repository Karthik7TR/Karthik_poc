/*
 * Copyright 2011: Thomson Reuters Global Resources. All Rights Reserved.
 * Proprietary and Confidential information of TRGR. Disclosure, Use or
 * Reproduction without the written authorization of TRGR is prohibited
 */
package com.thomsonreuters.uscl.ereader.core.book.dao;

import java.util.ArrayList;
import java.util.List;

import com.thomsonreuters.uscl.ereader.core.book.domain.Author;
import com.thomsonreuters.uscl.ereader.core.book.domain.KeywordTypeCode;
import com.thomsonreuters.uscl.ereader.core.book.domain.KeywordTypeValue;


public class BookDaoImpl implements BookDao {
	
	/**
	 * Get all the KeywordTypes from the KEYWORD_TYPE_VALUES and KEYWORD_TYPE_CODE
	 * @return a list of keywordType objects
	 */
	@Override
	public List<KeywordTypeCode> getKeywordsTypesAndValues() {
		List<KeywordTypeCode> keywordTypeCodes = new ArrayList<KeywordTypeCode>();
		
		// TODO: Get data from DB when made
		for(int i = 1; i < 41; i = i + 10) {
			KeywordTypeCode keywordTypeCode = new KeywordTypeCode();
			keywordTypeCode.setCodeId((long)i);
			String codeName = "";
			switch(i) {
				case 1:
					codeName = "Jurisdiction";
					break;
				case 21:
					codeName = "Publisher";
					break;
				case 31:
					codeName = "Subject";
					break;
				default:
					codeName = "Type";
					break;
			}
			keywordTypeCode.setCodeName(codeName);
			List<KeywordTypeValue> typeValues = new ArrayList<KeywordTypeValue>();
			
			for(int j=1; j < 8; j++){
				KeywordTypeValue kTV = new KeywordTypeValue();
				kTV.setKeywordTypeCode(keywordTypeCode);
				kTV.setValueId((long) i+j);
				kTV.setValueName(codeName + Integer.toString(j));
				typeValues.add(kTV);
			}
			keywordTypeCode.setValues(typeValues);
			
			
			keywordTypeCodes.add(keywordTypeCode);
		}
		
		return keywordTypeCodes;
	}
	
	/**
	 * Get all the authors from the AUTHOR table that match the EBOOK_DEFINITION_ID
	 * @param bookDefinitionId
	 * @return a list of author objects
	 */
	@Override
	public List<Author> getAuthors(int bookDefinitionId) {
		//TODO: update when DB is made- STUB for now
		List<Author> authors = new ArrayList<Author>();
		
		for(int i = 0; i < 4; i++) {
			Author author = new Author();
			author.setAuthorId(new Integer(i).longValue());
			author.setAuthorFirstName("Example" + Integer.toString(i));
			author.setAuthorLastName("Example" + Integer.toString(i));
			authors.add(author);
		}
		
		return authors;
	}

}
