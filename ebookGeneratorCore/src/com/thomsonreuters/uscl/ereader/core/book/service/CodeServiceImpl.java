package com.thomsonreuters.uscl.ereader.core.book.service;

import java.util.Date;
import java.util.List;

import com.thomsonreuters.uscl.ereader.core.book.dao.CodeDao;
import com.thomsonreuters.uscl.ereader.core.book.domain.KeywordTypeCode;
import com.thomsonreuters.uscl.ereader.core.book.domain.KeywordTypeValue;
import com.thomsonreuters.uscl.ereader.core.book.domain.PubTypeCode;
import com.thomsonreuters.uscl.ereader.core.book.domain.PublisherCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service("codeService")
public class CodeServiceImpl implements CodeService {
    private final CodeDao dao;

    @Autowired
    public CodeServiceImpl(final CodeDao dao) {
        this.dao = dao;
    }

    /**
     * Get all the PubType codes from the PUB_TYPE_CODES table
     * @return a list of pubTypeCode objects
     */
    @Override
    @Transactional(readOnly = true)
    public List<PubTypeCode> getAllPubTypeCodes() {
        return dao.getAllPubTypeCodes();
    }

    /**
     * Get a PubType Code from the PUB_TYPE_CODES table that match PUB_TYPE_CODES_ID
     * @param pubTypeCodeId
     * @return
     */
    @Override
    @Transactional(readOnly = true)
    public PubTypeCode getPubTypeCodeById(final Long pubTypeCodeId) {
        return dao.getPubTypeCodeById(pubTypeCodeId);
    }

    /**
     * Get a PubType Code from the PUB_TYPE_CODES table that match PUB_TYPE_CODES_NAME
     * @param pubTypeCodeName
     * @return
     */
    @Override
    @Transactional(readOnly = true)
    public PubTypeCode getPubTypeCodeByName(final String pubTypeCodeName) {
        return dao.getPubTypeCodeByName(pubTypeCodeName);
    }

    /**
     * Create or Update a PubType Code to the PUB_TYPE_CODES table
     * @param pubTypeCode
     * @return
     */
    @Override
    @Transactional
    public void savePubTypeCode(final PubTypeCode pubTypeCode) {
        pubTypeCode.setLastUpdated(new Date());
        dao.savePubTypeCode(pubTypeCode);
    }

    /**
     * Delete a PubType Code in the PUB_TYPE_CODES table
     * @param pubTypeCode
     * @return
     */
    @Override
    @Transactional
    public void deletePubTypeCode(final PubTypeCode pubTypeCode) {
        dao.deletePubTypeCode(pubTypeCode);
    }

    /**
     * Get all the Publisher codes from the PUBLISHER_TYPE_CODES table
     * @return a list of PublisherCode objects
     */
    @Override
    @Transactional(readOnly = true)
    public List<PublisherCode> getAllPublisherCodes() {
        return dao.getAllPublisherCodes();
    }

    /**
     * Get a Publisher Code from the PUBLISHER_TYPE_CODES table that match PUBLISHER_TYPE_CODES_ID
     * @param PublisherCodeId
     * @return
     */
    @Override
    @Transactional(readOnly = true)
    public PublisherCode getPublisherCodeById(final Long publisherCodeId) {
        return dao.getPublisherCodeById(publisherCodeId);
    }

    /**
     * Create or Update a Publisher Code to the PUBLISHER_TYPE_CODES table
     * @param PublisherCode
     * @return
     */
    @Override
    @Transactional
    public void savePublisherCode(final PublisherCode publisherCode) {
        publisherCode.setLastUpdated(new Date());
        dao.savePublisherCode(publisherCode);
    }

    /**
     * Delete a Publisher Code in the PUBLISHER_TYPE_CODES table
     * @param PublisherCode
     * @return
     */
    @Override
    @Transactional
    public void deletePublisherCode(final PublisherCode publisherCode) {
        dao.deletePublisherCode(publisherCode);
    }

    /**
     * Get all the KeywordType codes from the KEYWORD_TYPE_CODES table
     * @return a list of KeywordTypeCode objects
     */
    @Override
    @Transactional(readOnly = true)
    public List<KeywordTypeCode> getAllKeywordTypeCodes() {
        return dao.getAllKeywordTypeCodes();
    }

    /**
     * Get a KeywordType Code from the KEYWORD_TYPE_CODES table that match KEYWORD_TYPE_CODES_ID
     * @param KeywordTypeCodeId
     * @return
     */
    @Override
    @Transactional(readOnly = true)
    public KeywordTypeCode getKeywordTypeCodeById(final Long keywordTypeCodeId) {
        return dao.getKeywordTypeCodeById(keywordTypeCodeId);
    }

    /**
     * Get a KeywordTypeCode Code from the KEYWORD_TYPE_CODES table that match KEYWORD_TYPE_CODES_NAME
     * @param keywordTypeCodeName
     * @return
     */
    @Override
    @Transactional(readOnly = true)
    public KeywordTypeCode getKeywordTypeCodeByName(final String keywordTypeCodeName) {
        return dao.getKeywordTypeCodeByName(keywordTypeCodeName);
    }

    /**
     * Create or Update a KeywordType Code to the KEYWORD_TYPE_CODES table
     * @param KeywordTypeCode
     * @return
     */
    @Override
    @Transactional
    public void saveKeywordTypeCode(final KeywordTypeCode keywordTypeCode) {
        keywordTypeCode.setLastUpdated(new Date());
        dao.saveKeywordTypeCode(keywordTypeCode);
    }

    /**
     * Delete a KeywordType Code in the KEYWORD_TYPE_CODES table
     * @param KeywordTypeCode
     * @return
     */
    @Override
    @Transactional
    public void deleteKeywordTypeCode(final KeywordTypeCode keywordTypeCode) {
        dao.deleteKeywordTypeCode(keywordTypeCode);
    }

    /**
     * Get all the KeywordType codes from the KEYWORD_TYPE_VALUES table
     * @return a list of KeywordTypeValue objects
     */
    @Override
    @Transactional(readOnly = true)
    public List<KeywordTypeValue> getAllKeywordTypeValues() {
        return dao.getAllKeywordTypeValues();
    }

    /**
     * Get all the KeywordTypeValue codes from the KEYWORD_TYPE_VALUES table
     * that has keywordTypeCodeId
     * @return a list of KeywordTypeValue objects
     */
    @Override
    @Transactional(readOnly = true)
    public List<KeywordTypeValue> getAllKeywordTypeValues(final Long keywordTypeCodeId) {
        return dao.getAllKeywordTypeValues(keywordTypeCodeId);
    }

    /**
     * Get a KeywordType Value from the KEYWORD_TYPE_VALUES table that match KEYWORD_TYPE_VALUES_ID
     * @param KeywordTypeValueId
     * @return
     */
    @Override
    @Transactional(readOnly = true)
    public KeywordTypeValue getKeywordTypeValueById(final Long keywordTypeValueId) {
        return dao.getKeywordTypeValueById(keywordTypeValueId);
    }

    /**
     * Create or Update a KeywordType Value to the KEYWORD_TYPE_VALUES table
     * @param KeywordTypeValue
     * @return
     */
    @Override
    @Transactional
    public void saveKeywordTypeValue(final KeywordTypeValue keywordTypeValue) {
        keywordTypeValue.setLastUpdated(new Date());
        dao.saveKeywordTypeValue(keywordTypeValue);
    }

    /**
     * Delete a KeywordType Value in the KEYWORD_TYPE_VALUES table
     * @param KeywordTypeValue
     * @return
     */
    @Override
    @Transactional
    public void deleteKeywordTypeValue(final KeywordTypeValue keywordTypeValue) {
        dao.deleteKeywordTypeValue(keywordTypeValue);
    }
}
