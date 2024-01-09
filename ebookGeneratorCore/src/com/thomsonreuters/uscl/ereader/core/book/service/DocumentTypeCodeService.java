package com.thomsonreuters.uscl.ereader.core.book.service;

import java.util.List;

import com.thomsonreuters.uscl.ereader.core.book.domain.DocumentTypeCode;

/**
 * Service for DocumentTypeCode
 */
public interface DocumentTypeCodeService {
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
}
