package com.thomsonreuters.uscl.ereader.proviewaudit.service;

import java.util.List;

import com.thomsonreuters.uscl.ereader.proviewaudit.dao.ProviewAuditDao;
import com.thomsonreuters.uscl.ereader.proviewaudit.domain.ProviewAudit;
import com.thomsonreuters.uscl.ereader.proviewaudit.domain.ProviewAuditFilter;
import com.thomsonreuters.uscl.ereader.proviewaudit.domain.ProviewAuditSort;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.transaction.annotation.Transactional;

/**
 * Spring service that handles CRUD requests for ProviewAudit entities
 *
 */

@Transactional
public class ProviewAuditServiceImpl implements ProviewAuditService
{
    /**
     * DAO injected by Spring that manages ProviewAudit entities
     *
     */
    private ProviewAuditDao dao;

    /**
     * Save an existing ProviewAudit entity
     *
     */
    @Override
    @Transactional
    public void save(final ProviewAudit audit)
    {
        dao.save(audit);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProviewAudit> getRemovedAndDeletedVersions(final String fullyQualifiedTitleId)
    {
        return dao.findRemovedAndDeletedVersions(fullyQualifiedTitleId);
    }

    /**
     * Return all ProviewAudit that are filtered
     * @return
     */
    @Override
    @Transactional(readOnly = true)
    public List<ProviewAudit> findProviewAudits(final ProviewAuditFilter filter, final ProviewAuditSort sort)
    {
        return dao.findProviewAudits(filter, sort);
    }

    @Override
    @Transactional(readOnly = true)
    public int numberProviewAudits(final ProviewAuditFilter filter)
    {
        return dao.numberProviewAudits(filter);
    }

    @Required
    public void setProviewAuditDao(final ProviewAuditDao dao)
    {
        this.dao = dao;
    }

    @Override
    @Transactional(readOnly = true)
    public String getBookStatus(final String titleId, final String version)
    {
        return dao.getBookStatus(titleId, version);
    }
}
