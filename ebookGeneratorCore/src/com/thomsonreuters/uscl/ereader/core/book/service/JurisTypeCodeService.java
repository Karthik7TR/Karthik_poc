package com.thomsonreuters.uscl.ereader.core.book.service;

import java.util.List;

import com.thomsonreuters.uscl.ereader.core.book.domain.JurisTypeCode;

/**
 * Service for JurisTypeCode
 */
public interface JurisTypeCodeService {
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
}
