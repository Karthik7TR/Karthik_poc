package com.thomsonreuters.uscl.ereader.core.book.service;

import java.util.Date;
import java.util.List;

import com.thomsonreuters.uscl.ereader.core.book.dao.DocumentTypeCodeDao;
import com.thomsonreuters.uscl.ereader.core.book.domain.DocumentTypeCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("documentTypeCodeService")
public class DocumentTypeCodeServiceImpl implements DocumentTypeCodeService {
    private final DocumentTypeCodeDao documentTypeCodeDao;

    @Autowired
    public DocumentTypeCodeServiceImpl(final DocumentTypeCodeDao documentTypeCodeDao) {
        this.documentTypeCodeDao = documentTypeCodeDao;
    }

    /**
     * Get all the DocumentType codes from the DOCUMENT_TYPE_CODES table
     * @return a list of DocumentTypeCode objects
     */
    @Override
    public List<DocumentTypeCode> getAllDocumentTypeCodes() {
        return documentTypeCodeDao.findAll();
    }

    /**
     * Get a DocumentType Code from the DOCUMENT_TYPE_CODES table that match DOCUMENT_TYPE_CODES_ID
     * @param DocumentTypeCodeId
     * @return
     */
    @Override
    public DocumentTypeCode getDocumentTypeCodeById(final Long documentTypeCodeId) {
        return documentTypeCodeDao.findOne(documentTypeCodeId);
    }

    /**
     * Create or Update a DocumentType Code to the DOCUMENT_TYPE_CODES table
     * @param DocumentTypeCode
     * @return
     */
    @Override
    public void saveDocumentTypeCode(final DocumentTypeCode documentTypeCode) {
        documentTypeCode.setLastUpdatedDocTypeCode(new Date());
        documentTypeCodeDao.save(documentTypeCode);
    }
}
