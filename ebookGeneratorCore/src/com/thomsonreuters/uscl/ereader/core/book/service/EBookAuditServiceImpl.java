package com.thomsonreuters.uscl.ereader.core.book.service;

import java.util.Date;
import java.util.List;

import com.thomsonreuters.uscl.ereader.core.book.dao.EbookAuditDao;
import com.thomsonreuters.uscl.ereader.core.book.domain.EbookAudit;
import com.thomsonreuters.uscl.ereader.core.book.domain.EbookAuditFilter;
import com.thomsonreuters.uscl.ereader.core.book.domain.EbookAuditSort;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.transaction.annotation.Transactional;

/**
 * Spring service that handles CRUD requests for EBookAudit entities
 *
 */

@Transactional
public class EBookAuditServiceImpl implements EBookAuditService {
    /**
     * DAO injected by Spring that manages EBookAudit entities
     *
     */

    private EbookAuditDao eBookAuditDAO;

    /**
     * Save an existing eBookAudit entity
     *
     */
    @Override
    @Transactional
    public void saveEBookAudit(final EbookAudit eBookAudit) {
        // Update the date time
        eBookAudit.setLastUpdated(new Date());

        eBookAuditDAO.saveAudit(eBookAudit);
    }

    /**
     * Delete an existing Author entity
     *
     */
    @Override
    @Transactional
    public void deleteEBookAudit(final EbookAudit eBookAudit) {
        eBookAuditDAO.remove(eBookAudit);
    }

    @Override
    public EbookAudit findEBookAuditByPrimaryKey(final Long auditId) {
        return eBookAuditDAO.findEbookAuditByPrimaryKey(auditId);
    }

    @Override
    public Long findEbookAuditByEbookDefId(final Long ebookDefId) {
        return eBookAuditDAO.findEbookAuditIdByEbookDefId(ebookDefId);
    }

    /**
     * Return all EbookAudits
     * @return
     */
    @Override
    public List<EbookAudit> findEbookAudits(final EbookAuditFilter filter, final EbookAuditSort sort) {
        return eBookAuditDAO.findEbookAudits(filter, sort);
    }

    @Override
    public int numberEbookAudits(final EbookAuditFilter filter) {
        return eBookAuditDAO.numberEbookAudits(filter);
    }

    @Override
    @Transactional(readOnly = true)
    public Long findMaxAuditId() {
        return eBookAuditDAO.findMaxAuditId();
    }

    @Override
    @Transactional(readOnly = true)
    public EbookAudit findEbookAuditIdByTtileId(final String titleId) {
        return eBookAuditDAO.findEbookAuditIdByTtileId(titleId);
    }

    @Override
    @Transactional
    public void updateSplitDocumentsAudit(final EbookAudit audit, final String splitDocumentsConcat, final int parts) {
        eBookAuditDAO.updateSpliDocumentsAudit(audit, splitDocumentsConcat, parts);
    }

    @Override
    @Transactional
    public EbookAudit editIsbn(final String titleId, final String isbn) {
        final List<EbookAudit> audits = eBookAuditDAO.findEbookAuditByTitleIdAndIsbn(titleId, isbn);
        EbookAudit latestAudit = null;

        for (final EbookAudit audit : audits) {
            final StringBuilder buffer = new StringBuilder();
            buffer.append(EbookAuditDao.MOD_TEXT);
            buffer.append(audit.getIsbn());
            audit.setIsbn(buffer.toString());
            eBookAuditDAO.saveAudit(audit);

            if (latestAudit == null || latestAudit.getAuditId() < audit.getAuditId()) {
                latestAudit = audit;
            }
        }

        return latestAudit;
    }

    @Required
    /**
     * @param eBookAuditDAO the eBookAuditDAO to set
     */
    public void seteBookAuditDAO(final EbookAuditDao eBookAuditDAO) {
        this.eBookAuditDAO = eBookAuditDAO;
    }

    /**
     * @return the eBookAuditDAO
     */
    public EbookAuditDao geteBookAuditDAO() {
        return eBookAuditDAO;
    }
}
