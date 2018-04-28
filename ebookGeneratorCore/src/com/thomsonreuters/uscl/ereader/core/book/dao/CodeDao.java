package com.thomsonreuters.uscl.ereader.core.book.dao;

import java.util.List;

import com.thomsonreuters.uscl.ereader.core.book.domain.KeywordTypeCode;
import com.thomsonreuters.uscl.ereader.core.book.domain.KeywordTypeValue;
import com.thomsonreuters.uscl.ereader.core.book.domain.PubTypeCode;

public interface CodeDao {
    /**
     * Get all the PubType codes from the PUB_TYPE_CODES table
     * @return a list of PubType objects
     */
    List<PubTypeCode> getAllPubTypeCodes();

    /**
     * Get a PubType Code from the PUB_TYPE_CODES table that match PUB_TYPE_CODES_ID
     * @param pubTypeCodeId
     * @return
     */
    PubTypeCode getPubTypeCodeById(Long pubTypeCodeId);

    /**
     * Get a PubType Code from the PUB_TYPE_CODES table that match PUB_TYPE_CODES_NAME
     * @param pubTypeCodeName
     * @return
     */
    PubTypeCode getPubTypeCodeByName(String pubTypeCodeName);

    /**
     * Create or Update a PubType Code to the PUB_TYPE_CODES table
     * @param pubTypeCode
     * @return
     */
    void savePubTypeCode(PubTypeCode pubTypeCode);

    /**
     * Delete a PubType Code in the PUB_TYPE_CODES table
     * @param pubTypeCode
     * @return
     */
    void deletePubTypeCode(PubTypeCode pubTypeCode);

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
     * @param keywordTypeCode
     * @return
     */
    void deleteKeywordTypeCode(KeywordTypeCode keywordTypeCode);

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
    void deleteKeywordTypeValue(KeywordTypeValue keywordTypeValue);
}
