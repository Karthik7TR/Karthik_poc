package com.thomsonreuters.uscl.ereader.proviewaudit.dao;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.thomsonreuters.uscl.ereader.proviewaudit.domain.ProviewAudit;
import com.thomsonreuters.uscl.ereader.proviewaudit.domain.ProviewAuditFilter;
import com.thomsonreuters.uscl.ereader.proviewaudit.domain.ProviewAuditSort;

/**
 * Spring service that handles CRUD requests for ProviewAudit entities
 *
 */
public interface ProviewAuditDao {
    /**
     * Save an existing ProviewAudit entity
     */
    void save(ProviewAudit audit);

    /**
     * Return all ProviewAudit that are filtered
     *
     * @return
     */
    List<ProviewAudit> findProviewAudits(ProviewAuditFilter filter, ProviewAuditSort sort);

    int numberProviewAudits(ProviewAuditFilter filter);

    String getBookStatus(String titleId, String version);

    List<ProviewAudit> findRemovedAndDeletedVersions(String titleId);

    Map<String, Date> findMaxRequestDateByTitleIds(Collection<String> titleIds);

    List<ProviewAudit> findJobSubmitterNameForAllTitlesLatestVersion();

    List<ProviewAudit> findBooksInReviewStageForMoreThan24Hrs();
}
