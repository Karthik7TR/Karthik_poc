package com.thomsonreuters.uscl.ereader.core.book.service;

import java.util.List;

import com.thomsonreuters.uscl.ereader.core.book.domain.EbookAudit;
import com.thomsonreuters.uscl.ereader.core.book.domain.EbookAuditFilter;
import com.thomsonreuters.uscl.ereader.core.book.domain.EbookAuditSort;

/**
 * Spring service that handles CRUD requests for EbookAudit entities
 *
 */
public interface EBookAuditService {
    /**
     * Save an existing EBookAudit entity
     *
     */
    void saveEBookAudit(EbookAudit eBookAudit);

    /**
     * Delete an existing EBookAudit entity
     *
     */
    void deleteEBookAudit(EbookAudit eBookAudit);

    /**
     */
    EbookAudit findEBookAuditByPrimaryKey(Long auditId);

    Long findMaxAuditId();

    EbookAudit findEbookAuditIdByTtileId(String titleId);

    /**
     * Find max audit id for ebook Definition Id
     *
     */
    Long findEbookAuditByEbookDefId(Long ebookDefId);

    /**
     * Return all EbookAudits
     * @return
     */
    List<EbookAudit> findEbookAudits(EbookAuditFilter filter, EbookAuditSort sort);

    int numberEbookAudits(EbookAuditFilter filter);

    EbookAudit editIsbn(String titleId, String isbn);

    void updateSplitDocumentsAudit(EbookAudit audit, String splitDocumentsConcat, int parts);
}
