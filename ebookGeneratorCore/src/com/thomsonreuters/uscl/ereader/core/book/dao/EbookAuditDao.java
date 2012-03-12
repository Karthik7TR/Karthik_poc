/*
 * Copyright 2012: Thomson Reuters Global Resources. All Rights Reserved.
 * Proprietary and Confidential information of TRGR. Disclosure, Use or
 * Reproduction without the written authorization of TRGR is prohibited
 */

package com.thomsonreuters.uscl.ereader.core.book.dao;

import org.springframework.dao.DataAccessException;

import com.thomsonreuters.uscl.ereader.core.book.domain.EbookAudit;

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

}