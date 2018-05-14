package com.thomsonreuters.uscl.ereader.core.book.service;

import java.util.List;

import com.thomsonreuters.uscl.ereader.core.book.domain.KeywordTypeCode;

public interface KeywordTypeCodeSevice {
    /**
     * Get all the KeywordTypeCode codes from the KEYWORD_TYPE_CODES table
     * @return a list of KeywordTypeCode objects
     */
    List<KeywordTypeCode> getAllKeywordTypeCodes();

    /**
     * Get a KeywordTypeCode Code from the KEYWORD_TYPE_CODES table that match KEYWORD_TYPE_CODES_ID
     * @param keywordTypeCodeId
     * @return
     */
    KeywordTypeCode getKeywordTypeCodeById(Long keywordTypeCodeId);

    /**
     * Get a KeywordTypeCode Code from the KEYWORD_TYPE_CODES table that match KEYWORD_TYPE_CODES_NAME
     * @param keywordTypeCodeName
     * @return
     */
    KeywordTypeCode getKeywordTypeCodeByName(String keywordTypeCodeName);

    /**
     * Create or Update a KeywordTypeCode Code to the KEYWORD_TYPE_CODES table
     * @param keywordTypeCode
     * @return
     */
    void saveKeywordTypeCode(KeywordTypeCode keywordTypeCode);

    /**
     * Delete a KeywordTypeCode Code in the KEYWORD_TYPE_CODES table
     * @param id
     * @return
     */
    void deleteKeywordTypeCode(Long id);
}
