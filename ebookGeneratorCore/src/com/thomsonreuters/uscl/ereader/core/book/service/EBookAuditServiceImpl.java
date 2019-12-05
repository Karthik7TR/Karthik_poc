package com.thomsonreuters.uscl.ereader.core.book.service;

import static java.util.Comparator.comparingLong;

import static com.thomsonreuters.uscl.ereader.core.book.dao.EbookAuditDao.MODIFY_ISBN_TEXT;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import com.thomsonreuters.uscl.ereader.core.book.dao.EbookAuditDao;
import com.thomsonreuters.uscl.ereader.core.book.domain.BookDefinition;
import com.thomsonreuters.uscl.ereader.core.book.domain.EbookAudit;
import com.thomsonreuters.uscl.ereader.core.book.domain.EbookAuditFilter;
import com.thomsonreuters.uscl.ereader.core.book.domain.EbookAuditSort;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Spring service that handles CRUD requests for EBookAudit entities
 *
 */
@Service("eBookAuditService")
@Transactional
public class EBookAuditServiceImpl implements EBookAuditService {
    private final EbookAuditDao eBookAuditDAO;
    private final VersionIsbnService versionIsbnService;

    @Autowired
    public EBookAuditServiceImpl(final EbookAuditDao eBookAuditDAO, final VersionIsbnService versionIsbnService) {
        this.eBookAuditDAO = eBookAuditDAO;
        this.versionIsbnService = versionIsbnService;
    }

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
    @Transactional
    public void updateSplitDocumentsAudit(final EbookAudit audit, final String splitDocumentsConcat, final int parts) {
        eBookAuditDAO.updateSplitDocumentsAudit(audit, splitDocumentsConcat, parts);
    }

    @Override
    @Transactional
    public Optional<EbookAudit> modifyIsbn(final String titleId, final String isbn) {
        versionIsbnService.modifyIsbn(titleId, isbn);
        return eBookAuditDAO.findEbookAuditByTitleIdAndIsbn(titleId, isbn).stream()
                .peek(audit -> {
                    audit.setIsbn(MODIFY_ISBN_TEXT + audit.getIsbn());
                    eBookAuditDAO.saveAudit(audit);
                })
                .max(comparingLong(EbookAudit::getAuditId));
    }

    @Override
    @Transactional
    public void resetIsbn(final String titleId, final String isbn) {
        versionIsbnService.resetIsbn(titleId, isbn);
        eBookAuditDAO.findEbookAuditByTitleIdAndModifiedIsbn(titleId, isbn)
                .forEach(audit -> {
                    audit.setIsbn(StringUtils.substringAfter(audit.getIsbn(), MODIFY_ISBN_TEXT));
                    eBookAuditDAO.saveAudit(audit);
                });
    }

    @Override
    public boolean isIsbnModified(final String titleId, final String isbn) {
        return eBookAuditDAO.isIsbnModified(titleId, isbn);
    }
}
