/*
 * Copyright 2012: Thomson Reuters Global Resources. All Rights Reserved.
 * Proprietary and Confidential information of TRGR. Disclosure, Use or
 * Reproduction without the written authorization of TRGR is prohibited
 */

package com.thomsonreuters.uscl.ereader.proviewaudit.dao;

import java.util.List;

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
	 * 
	 */
	public void save(ProviewAudit audit);
	
	/**
	 * Return all ProviewAudit that are filtered
	 * @return
	 */
	public List<ProviewAudit> findProviewAudits(ProviewAuditFilter filter, ProviewAuditSort sort);
	
	public int numberProviewAudits(ProviewAuditFilter filter);

}