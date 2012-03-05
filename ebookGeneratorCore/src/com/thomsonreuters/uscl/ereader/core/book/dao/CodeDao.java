/*
 * Copyright 2011: Thomson Reuters Global Resources. All Rights Reserved.
 * Proprietary and Confidential information of TRGR. Disclosure, Use or
 * Reproduction without the written authorization of TRGR is prohibited
 */
package com.thomsonreuters.uscl.ereader.core.book.dao;

import java.util.List;

import com.thomsonreuters.uscl.ereader.core.book.domain.DocumentTypeCode;
import com.thomsonreuters.uscl.ereader.core.book.domain.JurisTypeCode;
import com.thomsonreuters.uscl.ereader.core.book.domain.PubTypeCode;
import com.thomsonreuters.uscl.ereader.core.book.domain.PublisherCode;
import com.thomsonreuters.uscl.ereader.core.book.domain.StateCode;


public interface CodeDao {
	
	/**
	 * Get all the State codes from the STATE_CODES table
	 * @return a list of StateCode objects
	 */
	public List<StateCode> getAllStateCodes();
	
	/**
	 * Get a State Code from the STATE_CODES table that match STATE_CODES_ID
	 * @param stateCode
	 * @return
	 */
	public StateCode getStateCodeById(Long stateCodeId);
	
	/**
	 * Create or Update a State Code to the STATE_CODES table
	 * @param stateCode
	 * @return
	 */
	public void saveStateCode(StateCode stateCode);
	
	/**
	 * Delete a State Code in the STATE_CODES table
	 * @param stateCode
	 * @return
	 */
	public void deleteStateCode(StateCode stateCode);
	
	/**
	 * Get all the PubType codes from the PUB_TYPE_CODES table
	 * @return a list of PubType objects
	 */
	public List<PubTypeCode> getAllPubTypeCodes();
	
	/**
	 * Get a PubType Code from the PUB_TYPE_CODES table that match PUB_TYPE_CODES_ID
	 * @param pubTypeCodeId
	 * @return
	 */
	public PubTypeCode getPubTypeCodeById(Long pubTypeCodeId);
	
	/**
	 * Create or Update a PubType Code to the PUB_TYPE_CODES table
	 * @param pubTypeCode
	 * @return
	 */
	public void savePubTypeCode(PubTypeCode pubTypeCode);
	
	/**
	 * Delete a PubType Code in the PUB_TYPE_CODES table
	 * @param pubTypeCode
	 * @return
	 */
	public void deletePubTypeCode(PubTypeCode pubTypeCode);
	
	/**
	 * Get all the JurisType codes from the JURIS_TYPE_CODES table
	 * @return a list of JurisType objects
	 */
	public List<JurisTypeCode> getAllJurisTypeCodes();
	
	/**
	 * Get a JurisType Code from the JURIS_TYPE_CODES table that match JURIS_TYPE_CODES_ID
	 * @param jurisTypeCodeId
	 * @return
	 */
	public JurisTypeCode getJurisTypeCodeById(Long jurisTypeCodeId);
	
	/**
	 * Create or Update a JurisType Code to the JURIS_TYPE_CODES table
	 * @param jurisTypeCode
	 * @return
	 */
	public void saveJurisTypeCode(JurisTypeCode jurisTypeCode);
	
	/**
	 * Delete a JurisType Code in the JURIS_TYPE_CODES table
	 * @param jurisTypeCode
	 * @return
	 */
	public void deleteJurisTypeCode(JurisTypeCode jurisTypeCode);
	
	
	/**
	 * Get all the DocumentType codes from the DOCUMENT_TYPE_CODES table
	 * @return a list of DocumentType objects
	 */
	public List<DocumentTypeCode> getAllDocumentTypeCodes();
	
	/**
	 * Get a DocumentType Code from the DOCUMENT_TYPE_CODES table that match DOCUMENT_TYPE_CODES_ID
	 * @param documentTypeCodeId
	 * @return
	 */
	public DocumentTypeCode getDocumentTypeCodeById(Long documentTypeCodeId);
	
	/**
	 * Create or Update a DocumentType Code to the DOCUMENT_TYPE_CODES table
	 * @param documentTypeCode
	 * @return
	 */
	public void saveDocumentTypeCode(DocumentTypeCode documentTypeCode);
	
	/**
	 * Delete a DocumentType Code in the DOCUMENT_TYPE_CODES table
	 * @param documentTypeCode
	 * @return
	 */
	public void deleteDocumentTypeCode(DocumentTypeCode documentTypeCode);
	
	
	/**
	 * Get all the Publisher codes from the PUBLISHER_CODES table
	 * @return a list of Publisher objects
	 */
	public List<PublisherCode> getAllPublisherCodes();
	
	/**
	 * Get a Publisher Code from the PUBLISHER_CODES table that match PUBLISHER_CODES_ID
	 * @param publisherCodeId
	 * @return
	 */
	public PublisherCode getPublisherCodeById(Long publisherCodeId);
	
	/**
	 * Create or Update a Publisher Code to the PUBLISHER_CODES table
	 * @param publisherCode
	 * @return
	 */
	public void savePublisherCode(PublisherCode publisherCode);
	
	/**
	 * Delete a Publisher Code in the PUBLISHER_CODES table
	 * @param publisherCode
	 * @return
	 */
	public void deletePublisherCode(PublisherCode publisherCode);

}
