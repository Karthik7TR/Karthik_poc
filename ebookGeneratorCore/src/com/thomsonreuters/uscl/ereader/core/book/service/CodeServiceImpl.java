package com.thomsonreuters.uscl.ereader.core.book.service;

import java.util.Date;
import java.util.List;

import com.thomsonreuters.uscl.ereader.core.book.dao.CodeDao;
import com.thomsonreuters.uscl.ereader.core.book.domain.DocumentTypeCode;
import com.thomsonreuters.uscl.ereader.core.book.domain.JurisTypeCode;
import com.thomsonreuters.uscl.ereader.core.book.domain.KeywordTypeCode;
import com.thomsonreuters.uscl.ereader.core.book.domain.KeywordTypeValue;
import com.thomsonreuters.uscl.ereader.core.book.domain.PubTypeCode;
import com.thomsonreuters.uscl.ereader.core.book.domain.PublisherCode;
import com.thomsonreuters.uscl.ereader.core.book.domain.StateCode;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.transaction.annotation.Transactional;

//@Service
public class CodeServiceImpl implements CodeService
{
    private CodeDao dao;

    /**
     * Get all the State codes from the STATE_CODES table
     * @return a list of StateCode objects
     */
    @Override
    @Transactional(readOnly = true)
    public List<StateCode> getAllStateCodes()
    {
        return dao.getAllStateCodes();
    }

    /**
     * Get a State Code from the STATE_CODES table that match STATE_CODES_ID
     * @param stateCode
     * @return
     */
    @Override
    @Transactional(readOnly = true)
    public StateCode getStateCodeById(final Long stateCodeId)
    {
        return dao.getStateCodeById(stateCodeId);
    }

    /**
     * Get a State Code from the STATE_CODES table that match STATE_CODES_NAME
     * @param stateCodeName
     * @return
     */
    @Override
    @Transactional(readOnly = true)
    public StateCode getStateCodeByName(final String stateCodeName)
    {
        return dao.getStateCodeByName(stateCodeName);
    }

    /**
     * Create or Update a State Code to the STATE_CODES table
     * @param stateCode
     * @return
     */
    @Override
    @Transactional
    public void saveStateCode(final StateCode stateCode)
    {
        stateCode.setLastUpdated(new Date());
        dao.saveStateCode(stateCode);
    }

    /**
     * Delete a State Code in the STATE_CODES table
     * @param stateCode
     * @return
     */
    @Override
    @Transactional
    public void deleteStateCode(final StateCode stateCode)
    {
        dao.deleteStateCode(stateCode);
    }

    /**
     * Get all the PubType codes from the PUB_TYPE_CODES table
     * @return a list of pubTypeCode objects
     */
    @Override
    @Transactional(readOnly = true)
    public List<PubTypeCode> getAllPubTypeCodes()
    {
        return dao.getAllPubTypeCodes();
    }

    /**
     * Get a PubType Code from the PUB_TYPE_CODES table that match PUB_TYPE_CODES_ID
     * @param pubTypeCodeId
     * @return
     */
    @Override
    @Transactional(readOnly = true)
    public PubTypeCode getPubTypeCodeById(final Long pubTypeCodeId)
    {
        return dao.getPubTypeCodeById(pubTypeCodeId);
    }

    /**
     * Get a PubType Code from the PUB_TYPE_CODES table that match PUB_TYPE_CODES_NAME
     * @param pubTypeCodeName
     * @return
     */
    @Override
    @Transactional(readOnly = true)
    public PubTypeCode getPubTypeCodeByName(final String pubTypeCodeName)
    {
        return dao.getPubTypeCodeByName(pubTypeCodeName);
    }

    /**
     * Create or Update a PubType Code to the PUB_TYPE_CODES table
     * @param pubTypeCode
     * @return
     */
    @Override
    @Transactional
    public void savePubTypeCode(final PubTypeCode pubTypeCode)
    {
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
    public void deletePubTypeCode(final PubTypeCode pubTypeCode)
    {
        dao.deletePubTypeCode(pubTypeCode);
    }

    /**
     * Get all the JurisType codes from the Juris_TYPE_CODES table
     * @return a list of JurisTypeCode objects
     */
    @Override
    @Transactional(readOnly = true)
    public List<JurisTypeCode> getAllJurisTypeCodes()
    {
        return dao.getAllJurisTypeCodes();
    }

    /**
     * Get a JurisType Code from the Juris_TYPE_CODES table that match Juris_TYPE_CODES_ID
     * @param JurisTypeCodeId
     * @return
     */
    @Override
    @Transactional(readOnly = true)
    public JurisTypeCode getJurisTypeCodeById(final Long jurisTypeCodeId)
    {
        return dao.getJurisTypeCodeById(jurisTypeCodeId);
    }

    /**
     * Get a JurisType Code from the Juris_TYPE_CODES table that match Juris_TYPE_CODES_NAME
     * @param JurisTypeCodeName
     * @return
     */
    @Override
    @Transactional(readOnly = true)
    public JurisTypeCode getJurisTypeCodeByName(final String JurisTypeCodeName)
    {
        return dao.getJurisTypeCodeByName(JurisTypeCodeName);
    }

    /**
     * Create or Update a JurisType Code to the Juris_TYPE_CODES table
     * @param JurisTypeCode
     * @return
     */
    @Override
    @Transactional
    public void saveJurisTypeCode(final JurisTypeCode jurisTypeCode)
    {
        jurisTypeCode.setLastUpdated(new Date());
        dao.saveJurisTypeCode(jurisTypeCode);
    }

    /**
     * Delete a JurisType Code in the Juris_TYPE_CODES table
     * @param JurisTypeCode
     * @return
     */
    @Override
    @Transactional
    public void deleteJurisTypeCode(final JurisTypeCode jurisTypeCode)
    {
        dao.deleteJurisTypeCode(jurisTypeCode);
    }

    /**
     * Get all the DocumentType codes from the DOCUMENT_TYPE_CODES table
     * @return a list of DocumentTypeCode objects
     */
    @Override
    @Transactional(readOnly = true)
    public List<DocumentTypeCode> getAllDocumentTypeCodes()
    {
        return dao.getAllDocumentTypeCodes();
    }

    /**
     * Get a DocumentType Code from the DOCUMENT_TYPE_CODES table that match DOCUMENT_TYPE_CODES_ID
     * @param DocumentTypeCodeId
     * @return
     */
    @Override
    @Transactional(readOnly = true)
    public DocumentTypeCode getDocumentTypeCodeById(final Long documentTypeCodeId)
    {
        return dao.getDocumentTypeCodeById(documentTypeCodeId);
    }

    /**
     * Create or Update a DocumentType Code to the DOCUMENT_TYPE_CODES table
     * @param DocumentTypeCode
     * @return
     */
    @Override
    @Transactional
    public void saveDocumentTypeCode(final DocumentTypeCode documentTypeCode)
    {
        documentTypeCode.setLastUpdated(new Date());
        dao.saveDocumentTypeCode(documentTypeCode);
    }

    /**
     * Delete a DocumentType Code in the DOCUMENT_TYPE_CODES table
     * @param DocumentTypeCode
     * @return
     */
    @Override
    @Transactional
    public void deleteDocumentTypeCode(final DocumentTypeCode documentTypeCode)
    {
        dao.deleteDocumentTypeCode(documentTypeCode);
    }

    @Override
    @Transactional
    public void saveDocumentTypeMetric(final DocumentTypeCode documentTypeCode)
    {
        dao.saveDocumentTypeCode(documentTypeCode);
    }

    /**
     * Get all the Publisher codes from the PUBLISHER_TYPE_CODES table
     * @return a list of PublisherCode objects
     */
    @Override
    @Transactional(readOnly = true)
    public List<PublisherCode> getAllPublisherCodes()
    {
        return dao.getAllPublisherCodes();
    }

    /**
     * Get a Publisher Code from the PUBLISHER_TYPE_CODES table that match PUBLISHER_TYPE_CODES_ID
     * @param PublisherCodeId
     * @return
     */
    @Override
    @Transactional(readOnly = true)
    public PublisherCode getPublisherCodeById(final Long publisherCodeId)
    {
        return dao.getPublisherCodeById(publisherCodeId);
    }

    /**
     * Create or Update a Publisher Code to the PUBLISHER_TYPE_CODES table
     * @param PublisherCode
     * @return
     */
    @Override
    @Transactional
    public void savePublisherCode(final PublisherCode publisherCode)
    {
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
    public void deletePublisherCode(final PublisherCode publisherCode)
    {
        dao.deletePublisherCode(publisherCode);
    }

    /**
     * Get all the KeywordType codes from the KEYWORD_TYPE_CODES table
     * @return a list of KeywordTypeCode objects
     */
    @Override
    @Transactional(readOnly = true)
    public List<KeywordTypeCode> getAllKeywordTypeCodes()
    {
        return dao.getAllKeywordTypeCodes();
    }

    /**
     * Get a KeywordType Code from the KEYWORD_TYPE_CODES table that match KEYWORD_TYPE_CODES_ID
     * @param KeywordTypeCodeId
     * @return
     */
    @Override
    @Transactional(readOnly = true)
    public KeywordTypeCode getKeywordTypeCodeById(final Long keywordTypeCodeId)
    {
        return dao.getKeywordTypeCodeById(keywordTypeCodeId);
    }

    /**
     * Get a KeywordTypeCode Code from the KEYWORD_TYPE_CODES table that match KEYWORD_TYPE_CODES_NAME
     * @param keywordTypeCodeName
     * @return
     */
    @Override
    @Transactional(readOnly = true)
    public KeywordTypeCode getKeywordTypeCodeByName(final String keywordTypeCodeName)
    {
        return dao.getKeywordTypeCodeByName(keywordTypeCodeName);
    }

    /**
     * Create or Update a KeywordType Code to the KEYWORD_TYPE_CODES table
     * @param KeywordTypeCode
     * @return
     */
    @Override
    @Transactional
    public void saveKeywordTypeCode(final KeywordTypeCode keywordTypeCode)
    {
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
    public void deleteKeywordTypeCode(final KeywordTypeCode keywordTypeCode)
    {
        dao.deleteKeywordTypeCode(keywordTypeCode);
    }

    /**
     * Get all the KeywordType codes from the KEYWORD_TYPE_VALUES table
     * @return a list of KeywordTypeValue objects
     */
    @Override
    @Transactional(readOnly = true)
    public List<KeywordTypeValue> getAllKeywordTypeValues()
    {
        return dao.getAllKeywordTypeValues();
    }

    /**
     * Get all the KeywordTypeValue codes from the KEYWORD_TYPE_VALUES table
     * that has keywordTypeCodeId
     * @return a list of KeywordTypeValue objects
     */
    @Override
    @Transactional(readOnly = true)
    public List<KeywordTypeValue> getAllKeywordTypeValues(final Long keywordTypeCodeId)
    {
        return dao.getAllKeywordTypeValues(keywordTypeCodeId);
    }

    /**
     * Get a KeywordType Value from the KEYWORD_TYPE_VALUES table that match KEYWORD_TYPE_VALUES_ID
     * @param KeywordTypeValueId
     * @return
     */
    @Override
    @Transactional(readOnly = true)
    public KeywordTypeValue getKeywordTypeValueById(final Long keywordTypeValueId)
    {
        return dao.getKeywordTypeValueById(keywordTypeValueId);
    }

    /**
     * Create or Update a KeywordType Value to the KEYWORD_TYPE_VALUES table
     * @param KeywordTypeValue
     * @return
     */
    @Override
    @Transactional
    public void saveKeywordTypeValue(final KeywordTypeValue keywordTypeValue)
    {
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
    public void deleteKeywordTypeValue(final KeywordTypeValue keywordTypeValue)
    {
        dao.deleteKeywordTypeValue(keywordTypeValue);
    }

    @Required
    public void setCodeDao(final CodeDao dao)
    {
        this.dao = dao;
    }
}
