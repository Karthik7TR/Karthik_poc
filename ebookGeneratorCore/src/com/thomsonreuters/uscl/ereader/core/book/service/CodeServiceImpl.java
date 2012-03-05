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
	public List<PubTypeCode> getAllPubTypeCodes() {
		return dao.getAllPubTypeCodes();
	}
	
	/**
	 * Get a PubType Code from the PUB_TYPE_CODES table that match PUB_TYPE_CODES_ID
	 * @param pubTypeCodeId
	 * @return
	 */
	public PubTypeCode getPubTypeCodeById(Long pubTypeCodeId){
		return dao.getPubTypeCodeById(pubTypeCodeId);
	}
	
	/**
	 * Create or Update a PubType Code to the PUB_TYPE_CODES table
	 * @param pubTypeCode
	 * @return
	 */
	public void savePubTypeCode(PubTypeCode pubTypeCode){
		pubTypeCode.setLastUpdated(new Date());
		dao.savePubTypeCode(pubTypeCode);
	}
	
	/**
	 * Delete a PubType Code in the PUB_TYPE_CODES table
	 * @param pubTypeCode
	 * @return
	 */
	public void deletePubTypeCode(PubTypeCode pubTypeCode) {
		dao.deletePubTypeCode(pubTypeCode);
	}
	
	
	/**
	 * Get all the JurisType codes from the Juris_TYPE_CODES table
	 * @return a list of JurisTypeCode objects
	 */
	public List<JurisTypeCode> getAllJurisTypeCodes() {
		return dao.getAllJurisTypeCodes();
	}
	
	/**
	 * Get a JurisType Code from the Juris_TYPE_CODES table that match Juris_TYPE_CODES_ID
	 * @param JurisTypeCodeId
	 * @return
	 */
	public JurisTypeCode getJurisTypeCodeById(Long jurisTypeCodeId){
		return dao.getJurisTypeCodeById(jurisTypeCodeId);
	}
	
	/**
	 * Create or Update a JurisType Code to the Juris_TYPE_CODES table
	 * @param JurisTypeCode
	 * @return
	 */
	public void saveJurisTypeCode(JurisTypeCode jurisTypeCode){
		jurisTypeCode.setLastUpdated(new Date());
		dao.saveJurisTypeCode(jurisTypeCode);
	}
	
	/**
	 * Delete a JurisType Code in the Juris_TYPE_CODES table
	 * @param JurisTypeCode
	 * @return
	 */
	public void deleteJurisTypeCode(JurisTypeCode jurisTypeCode) {
		dao.deleteJurisTypeCode(jurisTypeCode);
	}
	
	
	/**
	 * Get all the DocumentType codes from the DOCUMENT_TYPE_CODES table
	 * @return a list of DocumentTypeCode objects
	 */
	public List<DocumentTypeCode> getAllDocumentTypeCodes() {
		return dao.getAllDocumentTypeCodes();
	}
	
	/**
	 * Get a DocumentType Code from the DOCUMENT_TYPE_CODES table that match DOCUMENT_TYPE_CODES_ID
	 * @param DocumentTypeCodeId
	 * @return
	 */
	public DocumentTypeCode getDocumentTypeCodeById(Long documentTypeCodeId){
		return dao.getDocumentTypeCodeById(documentTypeCodeId);
	}
	
	/**
	 * Create or Update a DocumentType Code to the DOCUMENT_TYPE_CODES table
	 * @param DocumentTypeCode
	 * @return
	 */
	public void saveDocumentTypeCode(DocumentTypeCode documentTypeCode){
		documentTypeCode.setLastUpdated(new Date());
		dao.saveDocumentTypeCode(documentTypeCode);
	}
	
	/**
	 * Delete a DocumentType Code in the DOCUMENT_TYPE_CODES table
	 * @param DocumentTypeCode
	 * @return
	 */
	public void deleteDocumentTypeCode(DocumentTypeCode documentTypeCode) {
		dao.deleteDocumentTypeCode(documentTypeCode);
	}
	
	
	/**
	 * Get all the Publisher codes from the PUBLISHER_TYPE_CODES table
	 * @return a list of PublisherCode objects
	 */
	public List<PublisherCode> getAllPublisherCodes() {
		return dao.getAllPublisherCodes();
	}
	
	/**
	 * Get a Publisher Code from the PUBLISHER_TYPE_CODES table that match PUBLISHER_TYPE_CODES_ID
	 * @param PublisherCodeId
	 * @return
	 */
	public PublisherCode getPublisherCodeById(Long publisherCodeId){
		return dao.getPublisherCodeById(publisherCodeId);
	}
	
	/**
	 * Create or Update a Publisher Code to the PUBLISHER_TYPE_CODES table
	 * @param PublisherCode
	 * @return
	 */
	public void savePublisherCode(PublisherCode publisherCode){
		publisherCode.setLastUpdated(new Date());
		dao.savePublisherCode(publisherCode);
	}
	
	/**
	 * Delete a Publisher Code in the PUBLISHER_TYPE_CODES table
	 * @param PublisherCode
	 * @return
	 */
	public void deletePublisherCode(PublisherCode publisherCode) {
		dao.deletePublisherCode(publisherCode);
	}
	
	@Required
	public void setCodeDao(CodeDao dao) {
		this.dao = dao;
	}

}
