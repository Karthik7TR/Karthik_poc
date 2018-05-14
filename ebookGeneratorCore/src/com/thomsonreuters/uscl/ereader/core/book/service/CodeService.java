package com.thomsonreuters.uscl.ereader.core.book.service;

import java.util.List;

import com.thomsonreuters.uscl.ereader.core.book.domain.PubTypeCode;

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
}
