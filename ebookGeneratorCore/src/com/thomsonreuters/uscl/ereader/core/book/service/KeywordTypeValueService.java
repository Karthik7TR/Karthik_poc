package com.thomsonreuters.uscl.ereader.core.book.service;

import java.util.List;

import com.thomsonreuters.uscl.ereader.core.book.domain.KeywordTypeValue;

public interface KeywordTypeValueService {
    /**
     * Get all the KeywordTypeValue codes from the KEYWORD_TYPE_VALUES table
     * @return a list of KeywordTypeValue objects
     */
    List<KeywordTypeValue> getAllKeywordTypeValues();

    /**
     * Get all the KeywordTypeValue codes from the KEYWORD_TYPE_VALUES table
     * that has keywordTypeCodeId
     * @return a list of KeywordTypeValue objects
     */
    List<KeywordTypeValue> getAllKeywordTypeValues(Long keywordTypeCodeId);

    /**
     * Get a KeywordTypeValue Value from the KEYWORD_TYPE_VALUES table that match KEYWORD_TYPE_VALUES_ID
     * @param keywordTypeValueId
     * @return
     */
    KeywordTypeValue getKeywordTypeValueById(Long keywordTypeValueId);

    /**
     * Create or Update a KeywordTypeValue Value to the KEYWORD_TYPE_VALUES table
     * @param keywordTypeValue
     * @return
     */
    void saveKeywordTypeValue(KeywordTypeValue keywordTypeValue);

    /**
     * Delete a KeywordTypeValue Value in the KEYWORD_TYPE_VALUES table
     * @param keywordTypeValue
     * @return
     */
    void deleteKeywordTypeValue(Long id);
}
