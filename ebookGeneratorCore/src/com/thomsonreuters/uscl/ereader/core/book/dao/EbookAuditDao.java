/*
 * Copyright 2012: Thomson Reuters Global Resources. All Rights Reserved.
 * Proprietary and Confidential information of TRGR. Disclosure, Use or
 * Reproduction without the written authorization of TRGR is prohibited
 */

package com.thomsonreuters.uscl.ereader.core.book.dao;

import java.util.List;

import org.springframework.dao.DataAccessException;

import com.thomsonreuters.uscl.ereader.core.book.domain.EbookAudit;
import com.thomsonreuters.uscl.ereader.core.book.domain.EbookAuditFilter;
import com.thomsonreuters.uscl.ereader.core.book.domain.EbookAuditSort;

/**
 * DAO to manage EbookAudit entities.
 * 
 */
public interface EbookAuditDao {

	/**
	 * Query - findEbookAuditByPrimaryKey
	 * 
	 */
	public EbookAudit findEbookAuditByPrimaryKey(Long auditId)
			throws DataAccessException;

	public void remove(EbookAudit toRemove) throws DataAccessException;

	public void saveAudit(EbookAudit eBookAuditRecord);
	public Long findEbookAuditIdByEbookDefId(Long ebookDefId)
			throws DataAccessException;

	public List<EbookAudit> findEbookAudits(EbookAuditFilter filter, EbookAuditSort sort);
	
	public int numberEbookAudits(EbookAuditFilter filter);

}