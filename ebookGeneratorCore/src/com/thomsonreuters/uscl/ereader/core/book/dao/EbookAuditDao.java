package com.thomsonreuters.uscl.ereader.core.book.dao;

import java.util.List;

import com.thomsonreuters.uscl.ereader.core.book.domain.EbookAudit;
import com.thomsonreuters.uscl.ereader.core.book.domain.EbookAuditFilter;
import com.thomsonreuters.uscl.ereader.core.book.domain.EbookAuditSort;
import org.springframework.dao.DataAccessException;

/**
 * DAO to manage EbookAudit entities.
 *
 */
public interface EbookAuditDao
{
    String MOD_TEXT = "modified-";

    /**
     * Query - findEbookAuditByPrimaryKey
     *
     */
    EbookAudit findEbookAuditByPrimaryKey(Long auditId) throws DataAccessException;

    void remove(EbookAudit toRemove) throws DataAccessException;

    List<EbookAudit> findEbookAuditByTitleIdAndIsbn(String titleId, String isbn);

    void saveAudit(EbookAudit eBookAuditRecord);

    Long findEbookAuditIdByEbookDefId(Long ebookDefId) throws DataAccessException;

    List<EbookAudit> findEbookAudits(EbookAuditFilter filter, EbookAuditSort sort);

    int numberEbookAudits(EbookAuditFilter filter);

    Long findMaxAuditId();

    EbookAudit findEbookAuditIdByTtileId(String titleId);

    void updateSpliDocumentsAudit(EbookAudit audit, String splitDocumentsConcat, int parts);
}
