package com.thomsonreuters.uscl.ereader.core.book.service;

import java.util.List;

import com.thomsonreuters.uscl.ereader.core.book.domain.DocumentTypeCode;
import com.thomsonreuters.uscl.ereader.core.book.domain.JurisTypeCode;
import com.thomsonreuters.uscl.ereader.core.book.domain.KeywordTypeCode;
import com.thomsonreuters.uscl.ereader.core.book.domain.KeywordTypeValue;
import com.thomsonreuters.uscl.ereader.core.book.domain.PubTypeCode;
import com.thomsonreuters.uscl.ereader.core.book.domain.PublisherCode;

public interface CodeService {
    /**
     * Get all the PubType codes from the PUB_TYPE_CODES table
     * @return a list of pubTypeCode objects
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
     * Get all the JurisType codes from the Juris_TYPE_CODES table
     * @return a list of JurisTypeCode objects
     */
    List<JurisTypeCode> getAllJurisTypeCodes();

    /**
     * Get a JurisType Code from the Juris_TYPE_CODES table that match Juris_TYPE_CODES_ID
     * @param JurisTypeCodeId
     * @return
     */
    JurisTypeCode getJurisTypeCodeById(Long jurisTypeCodeId);

    /**
     * Get a JurisType Code from the Juris_TYPE_CODES table that match Juris_TYPE_CODES_NAME
     * @param jurisTypeCodeName
     * @return
     */
    JurisTypeCode getJurisTypeCodeByName(String jurisTypeCodeName);

    /**
     * Create or Update a JurisType Code to the Juris_TYPE_CODES table
     * @param JurisTypeCode
     * @return
     */
    void saveJurisTypeCode(JurisTypeCode jurisTypeCode);

    /**
     * Delete a JurisType Code in the Juris_TYPE_CODES table
     * @param JurisTypeCode
     * @return
     */
    void deleteJurisTypeCode(JurisTypeCode jurisTypeCode);

    /**
     * Get all the DocumentType codes from the DOCUMENT_TYPE_CODES table
     * @return a list of DocumentType objects
     */
    List<DocumentTypeCode> getAllDocumentTypeCodes();

    /**
     * Get a DocumentType Code from the DOCUMENT_TYPE_CODES table that match DOCUMENT_TYPE_CODES_ID
     * @param documentTypeCodeId
     * @return
     */
    DocumentTypeCode getDocumentTypeCodeById(Long documentTypeCodeId);

    /**
     * Create or Update a DocumentType Code to the DOCUMENT_TYPE_CODES table
     * @param documentTypeCode
     * @return
     */
    void saveDocumentTypeCode(DocumentTypeCode documentTypeCode);

    /**
     * Delete a DocumentType Code in the DOCUMENT_TYPE_CODES table
     * @param documentTypeCode
     * @return
     */
    void deleteDocumentTypeCode(DocumentTypeCode documentTypeCode);

    /**
     * Get all the Publisher codes from the PUBLISHER_CODES table
     * @return a list of Publisher objects
     */
    List<PublisherCode> getAllPublisherCodes();

    /**
     * Get a Publisher Code from the PUBLISHER_CODES table that match PUBLISHER_CODES_ID
     * @param publisherCodeId
     * @return
     */
    PublisherCode getPublisherCodeById(Long publisherCodeId);

    /**
     * Create or Update a Publisher Code to the PUBLISHER_CODES table
     * @param publisherCode
     * @return
     */
    void savePublisherCode(PublisherCode publisherCode);

    /**
     * Delete a Publisher Code in the PUBLISHER_CODES table
     * @param publisherCode
     * @return
     */
    void deletePublisherCode(PublisherCode publisherCode);

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

    void saveDocumentTypeMetric(DocumentTypeCode documentTypeCode);

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
