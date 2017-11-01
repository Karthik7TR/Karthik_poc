package com.thomsonreuters.uscl.ereader.proviewaudit.service;

import java.util.List;

import com.thomsonreuters.uscl.ereader.proviewaudit.dao.ProviewAuditDao;
import com.thomsonreuters.uscl.ereader.proviewaudit.domain.ProviewAudit;
import com.thomsonreuters.uscl.ereader.proviewaudit.domain.ProviewAuditFilter;
import com.thomsonreuters.uscl.ereader.proviewaudit.domain.ProviewAuditSort;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Spring service that handles CRUD requests for ProviewAudit entities
 *
 */
@Service("proviewAuditService")
@Transactional
public class ProviewAuditServiceImpl implements ProviewAuditService {
    private final ProviewAuditDao dao;

    @Autowired
    public ProviewAuditServiceImpl(final ProviewAuditDao dao) {
        this.dao = dao;
    }

    /**
     * Save an existing ProviewAudit entity
     *
     */
    @Override
    @Transactional
    public void save(final ProviewAudit audit) {
        dao.save(audit);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProviewAudit> getRemovedAndDeletedVersions(final String fullyQualifiedTitleId) {
        return dao.findRemovedAndDeletedVersions(fullyQualifiedTitleId);
    }

    /**
     * Return all ProviewAudit that are filtered
     * @return
     */
    @Override
    @Transactional(readOnly = true)
    public List<ProviewAudit> findProviewAudits(final ProviewAuditFilter filter, final ProviewAuditSort sort) {
        return dao.findProviewAudits(filter, sort);
    }

    @Override
    @Transactional(readOnly = true)
    public int numberProviewAudits(final ProviewAuditFilter filter) {
        return dao.numberProviewAudits(filter);
    }

    @Override
    @Transactional(readOnly = true)
    public String getBookStatus(final String titleId, final String version) {
        return dao.getBookStatus(titleId, version);
    }
}
