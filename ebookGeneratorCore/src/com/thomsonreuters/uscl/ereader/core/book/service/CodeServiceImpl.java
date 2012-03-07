/*
 * Copyright 2011: Thomson Reuters Global Resources. All Rights Reserved.
 * Proprietary and Confidential information of TRGR. Disclosure, Use or
 * Reproduction without the written authorization of TRGR is prohibited
 */
package com.thomsonreuters.uscl.ereader.core.book.service;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Required;
//import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.thomsonreuters.uscl.ereader.core.book.dao.CodeDao;
import com.thomsonreuters.uscl.ereader.core.book.domain.DocumentTypeCode;
import com.thomsonreuters.uscl.ereader.core.book.domain.JurisTypeCode;
import com.thomsonreuters.uscl.ereader.core.book.domain.KeywordTypeCode;
import com.thomsonreuters.uscl.ereader.core.book.domain.KeywordTypeValue;
import com.thomsonreuters.uscl.ereader.core.book.domain.PubTypeCode;
import com.thomsonreuters.uscl.ereader.core.book.domain.PublisherCode;
import com.thomsonreuters.uscl.ereader.core.book.domain.StateCode;

//@Service
public class CodeServiceImpl implements CodeService {
	private CodeDao dao;
	
	/**
	 * Get all the State codes from the STATE_CODES table
	 * @return a list of StateCode objects
	 */
	@Transactional(readOnly = true)
	public List<StateCode> getAllStateCodes() {
		return dao.getAllStateCodes();
	}
	
	/**
	 * Get a State Code from the STATE_CODES table that match STATE_CODES_ID
	 * @param stateCode
	 * @return
	 */
	@Transactional(readOnly = true)
	public StateCode getStateCodeById(Long stateCodeId) {
		return dao.getStateCodeById(stateCodeId);
	}
	
	/**
	 * Create or Update a State Code to the STATE_CODES table
	 * @param stateCode
	 * @return
	 */
	@Transactional
	public void saveStateCode(StateCode stateCode) {
			stateCode.setLastUpdated(new Date());
			dao.saveStateCode(stateCode);
	}
	
	/**
	 * Delete a State Code in the STATE_CODES table
	 * @param stateCode
	 * @return
	 */
	@Transactional
	public void deleteStateCode(StateCode stateCode) {
		 dao.deleteStateCode(stateCode);
	}
	
	/**
	 * Get all the PubType codes from the PUB_TYPE_CODES table
	 * @return a list of pubTypeCode objects
	 */
	@Transactional(readOnly = true)
	public List<PubTypeCode> getAllPubTypeCodes() {
		return dao.getAllPubTypeCodes();
	}
	
	/**
	 * Get a PubType Code from the PUB_TYPE_CODES table that match PUB_TYPE_CODES_ID
	 * @param pubTypeCodeId
	 * @return
	 */
	@Transactional(readOnly = true)
	public PubTypeCode getPubTypeCodeById(Long pubTypeCodeId){
		return dao.getPubTypeCodeById(pubTypeCodeId);
	}
	
	/**
	 * Create or Update a PubType Code to the PUB_TYPE_CODES table
	 * @param pubTypeCode
	 * @return
	 */
	@Transactional
	public void savePubTypeCode(PubTypeCode pubTypeCode){
		pubTypeCode.setLastUpdated(new Date());
		dao.savePubTypeCode(pubTypeCode);
	}
	
	/**
	 * Delete a PubType Code in the PUB_TYPE_CODES table
	 * @param pubTypeCode
	 * @return
	 */
	@Transactional
	public void deletePubTypeCode(PubTypeCode pubTypeCode) {
		dao.deletePubTypeCode(pubTypeCode);
	}
	
	
	/**
	 * Get all the JurisType codes from the Juris_TYPE_CODES table
	 * @return a list of JurisTypeCode objects
	 */
	@Transactional(readOnly = true)
	public List<JurisTypeCode> getAllJurisTypeCodes() {
		return dao.getAllJurisTypeCodes();
	}
	
	/**
	 * Get a JurisType Code from the Juris_TYPE_CODES table that match Juris_TYPE_CODES_ID
	 * @param JurisTypeCodeId
	 * @return
	 */
	@Transactional(readOnly = true)
	public JurisTypeCode getJurisTypeCodeById(Long jurisTypeCodeId){
		return dao.getJurisTypeCodeById(jurisTypeCodeId);
	}
	
	/**
	 * Create or Update a JurisType Code to the Juris_TYPE_CODES table
	 * @param JurisTypeCode
	 * @return
	 */
	@Transactional
	public void saveJurisTypeCode(JurisTypeCode jurisTypeCode){
		jurisTypeCode.setLastUpdated(new Date());
		dao.saveJurisTypeCode(jurisTypeCode);
	}
	
	/**
	 * Delete a JurisType Code in the Juris_TYPE_CODES table
	 * @param JurisTypeCode
	 * @return
	 */
	@Transactional
	public void deleteJurisTypeCode(JurisTypeCode jurisTypeCode) {
		dao.deleteJurisTypeCode(jurisTypeCode);
	}
	
	
	/**
	 * Get all the DocumentType codes from the DOCUMENT_TYPE_CODES table
	 * @return a list of DocumentTypeCode objects
	 */
	@Transactional(readOnly = true)
	public List<DocumentTypeCode> getAllDocumentTypeCodes() {
		return dao.getAllDocumentTypeCodes();
	}
	
	/**
	 * Get a DocumentType Code from the DOCUMENT_TYPE_CODES table that match DOCUMENT_TYPE_CODES_ID
	 * @param DocumentTypeCodeId
	 * @return
	 */
	@Transactional(readOnly = true)
	public DocumentTypeCode getDocumentTypeCodeById(Long documentTypeCodeId){
		return dao.getDocumentTypeCodeById(documentTypeCodeId);
	}
	
	/**
	 * Create or Update a DocumentType Code to the DOCUMENT_TYPE_CODES table
	 * @param DocumentTypeCode
	 * @return
	 */
	@Transactional
	public void saveDocumentTypeCode(DocumentTypeCode documentTypeCode){
		documentTypeCode.setLastUpdated(new Date());
		dao.saveDocumentTypeCode(documentTypeCode);
	}
	
	/**
	 * Delete a DocumentType Code in the DOCUMENT_TYPE_CODES table
	 * @param DocumentTypeCode
	 * @return
	 */
	@Transactional
	public void deleteDocumentTypeCode(DocumentTypeCode documentTypeCode) {
		dao.deleteDocumentTypeCode(documentTypeCode);
	}
	
	
	/**
	 * Get all the Publisher codes from the PUBLISHER_TYPE_CODES table
	 * @return a list of PublisherCode objects
	 */
	@Transactional(readOnly = true)
	public List<PublisherCode> getAllPublisherCodes() {
		return dao.getAllPublisherCodes();
	}
	
	/**
	 * Get a Publisher Code from the PUBLISHER_TYPE_CODES table that match PUBLISHER_TYPE_CODES_ID
	 * @param PublisherCodeId
	 * @return
	 */
	@Transactional(readOnly = true)
	public PublisherCode getPublisherCodeById(Long publisherCodeId){
		return dao.getPublisherCodeById(publisherCodeId);
	}
	
	/**
	 * Create or Update a Publisher Code to the PUBLISHER_TYPE_CODES table
	 * @param PublisherCode
	 * @return
	 */
	@Transactional
	public void savePublisherCode(PublisherCode publisherCode){
		publisherCode.setLastUpdated(new Date());
		dao.savePublisherCode(publisherCode);
	}
	
	/**
	 * Delete a Publisher Code in the PUBLISHER_TYPE_CODES table
	 * @param PublisherCode
	 * @return
	 */
	@Transactional
	public void deletePublisherCode(PublisherCode publisherCode) {
		dao.deletePublisherCode(publisherCode);
	}
	
	
	/**
	 * Get all the KeywordType codes from the KEYWORD_TYPE_CODES table
	 * @return a list of KeywordTypeCode objects
	 */
	@Transactional(readOnly = true)
	public List<KeywordTypeCode> getAllKeywordTypeCodes() {
		return dao.getAllKeywordTypeCodes();
	}
	
	/**
	 * Get a KeywordType Code from the KEYWORD_TYPE_CODES table that match KEYWORD_TYPE_CODES_ID
	 * @param KeywordTypeCodeId
	 * @return
	 */
	@Transactional(readOnly = true)
	public KeywordTypeCode getKeywordTypeCodeById(Long keywordTypeCodeId){
		return dao.getKeywordTypeCodeById(keywordTypeCodeId);
	}
	
	/**
	 * Create or Update a KeywordType Code to the KEYWORD_TYPE_CODES table
	 * @param KeywordTypeCode
	 * @return
	 */
	@Transactional
	public void saveKeywordTypeCode(KeywordTypeCode keywordTypeCode){
		keywordTypeCode.setLastUpdated(new Date());
		dao.saveKeywordTypeCode(keywordTypeCode);
	}
	
	/**
	 * Delete a KeywordType Code in the KEYWORD_TYPE_CODES table
	 * @param KeywordTypeCode
	 * @return
	 */
	@Transactional
	public void deleteKeywordTypeCode(KeywordTypeCode keywordTypeCode) {
		dao.deleteKeywordTypeCode(keywordTypeCode);
	}
	
	
	/**
	 * Get all the KeywordType codes from the KEYWORD_TYPE_VALUES table
	 * @return a list of KeywordTypeValue objects
	 */
	@Transactional(readOnly = true)
	public List<KeywordTypeValue> getAllKeywordTypeValues() {
		return dao.getAllKeywordTypeValues();
	}
	
	/**
	 * Get all the KeywordTypeValue codes from the KEYWORD_TYPE_VALUES table
	 * that has keywordTypeCodeId
	 * @return a list of KeywordTypeValue objects
	 */
	@Transactional(readOnly = true)
	public List<KeywordTypeValue> getAllKeywordTypeValues(Long keywordTypeCodeId) {
		return dao.getAllKeywordTypeValues(keywordTypeCodeId);
	}
	
	/**
	 * Get a KeywordType Value from the KEYWORD_TYPE_VALUES table that match KEYWORD_TYPE_VALUES_ID
	 * @param KeywordTypeValueId
	 * @return
	 */
	@Transactional(readOnly = true)
	public KeywordTypeValue getKeywordTypeValueById(Long keywordTypeValueId){
		return dao.getKeywordTypeValueById(keywordTypeValueId);
	}
	
	/**
	 * Create or Update a KeywordType Value to the KEYWORD_TYPE_VALUES table
	 * @param KeywordTypeValue
	 * @return
	 */
	@Transactional
	public void saveKeywordTypeValue(KeywordTypeValue keywordTypeValue){
		keywordTypeValue.setLastUpdated(new Date());
		dao.saveKeywordTypeValue(keywordTypeValue);
	}
	
	/**
	 * Delete a KeywordType Value in the KEYWORD_TYPE_VALUES table
	 * @param KeywordTypeValue
	 * @return
	 */
	@Transactional
	public void deleteKeywordTypeValue(KeywordTypeValue keywordTypeValue) {
		dao.deleteKeywordTypeValue(keywordTypeValue);
	}
	
	@Required
	public void setCodeDao(CodeDao dao) {
		this.dao = dao;
	}

}
