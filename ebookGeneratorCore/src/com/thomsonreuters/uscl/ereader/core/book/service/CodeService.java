/*
 * Copyright 2011: Thomson Reuters Global Resources. All Rights Reserved.
 * Proprietary and Confidential information of TRGR. Disclosure, Use or
 * Reproduction without the written authorization of TRGR is prohibited
 */
package com.thomsonreuters.uscl.ereader.core.book.service;

import java.util.List;

import com.thomsonreuters.uscl.ereader.core.book.domain.JurisTypeCode;
import com.thomsonreuters.uscl.ereader.core.book.domain.PubTypeCode;
import com.thomsonreuters.uscl.ereader.core.book.domain.StateCode;


public interface CodeService {
	
	/**
	 * Get all the State codes from the STATE_CODES table
	 * @return a list of StateCode objects
	 */
	public List<StateCode> getAllStateCodes();
	
	/**
	 * Get a State Code from the STATE_CODES table that match STATE_CODES_ID
	 * @param stateCodeId
	 * @return
	 */
	public StateCode getStateCode(Long stateCodeId);
	
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
	 * @return a list of pubTypeCode objects
	 */
	public List<PubTypeCode> getAllPubTypeCodes();
	
	/**
	 * Get a PubType Code from the PUB_TYPE_CODES table that match PUB_TYPE_CODES_ID
	 * @param pubTypeCodeId
	 * @return
	 */
	public PubTypeCode getPubTypeCode(Long pubTypeCodeId);
	
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
	 * Get all the JurisType codes from the Juris_TYPE_CODES table
	 * @return a list of JurisTypeCode objects
	 */
	public List<JurisTypeCode> getAllJurisTypeCodes();
	
	/**
	 * Get a JurisType Code from the Juris_TYPE_CODES table that match Juris_TYPE_CODES_ID
	 * @param JurisTypeCodeId
	 * @return
	 */
	public JurisTypeCode getJurisTypeCode(Long jurisTypeCodeId);
	
	/**
	 * Create or Update a JurisType Code to the Juris_TYPE_CODES table
	 * @param JurisTypeCode
	 * @return
	 */
	public void saveJurisTypeCode(JurisTypeCode jurisTypeCode);
	
	/**
	 * Delete a JurisType Code in the Juris_TYPE_CODES table
	 * @param JurisTypeCode
	 * @return
	 */
	public void deleteJurisTypeCode(JurisTypeCode jurisTypeCode);
}
