/*
 * Copyright 2012: Thomson Reuters Global Resources. All Rights Reserved.
 * Proprietary and Confidential information of TRGR. Disclosure, Use or
 * Reproduction without the written authorization of TRGR is prohibited
 */

package com.thomsonreuters.uscl.ereader.core.book.service;

import com.thomsonreuters.uscl.ereader.core.book.domain.EbookAudit;

/**
 * Spring service that handles CRUD requests for EbookAudit entities
 * 
 */
public interface EBookAuditService {

	/**
	 * Save an existing EBookAudit entity
	 * 
	 */
	public void saveEBookAudit(EbookAudit eBookAudit);

	/**
	 * Delete an existing EBookAudit entity
	 * 
	 */
	public void deleteEBookAudit(EbookAudit eBookAudit);

	/**
	 */
	public EbookAudit findEBookAuditByPrimaryKey(Long auditId);
}