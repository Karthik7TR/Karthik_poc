package com.thomsonreuters.uscl.ereader.core.book.dao;

import com.thomsonreuters.uscl.ereader.core.book.domain.DocumentTypeCode;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * DAO for DocumentTypeCode
 */
public interface DocumentTypeCodeDao extends JpaRepository<DocumentTypeCode, Long> {
    // Intentionally left blank
}
