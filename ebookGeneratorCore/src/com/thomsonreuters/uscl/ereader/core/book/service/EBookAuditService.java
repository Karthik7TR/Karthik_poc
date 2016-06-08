/*
 * Copyright 2015: Thomson Reuters Global Resources. All Rights Reserved.
 * Proprietary and Confidential information of TRGR. Disclosure, Use or
 * Reproduction without the written authorization of TRGR is prohibited
 */

package com.thomsonreuters.uscl.ereader.core.book.service;

import java.util.List;

import org.springframework.transaction.annotation.Transactional;

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
	public void saveEBookAudit(EbookAudit eBookAudit);

	/**
	 * Delete an existing EBookAudit entity
	 * 
	 */
	public void deleteEBookAudit(EbookAudit eBookAudit);

	/**
	 */
	public EbookAudit findEBookAuditByPrimaryKey(Long auditId);
	
	public Long findMaxAuditId();

	public EbookAudit findEbookAuditIdByTtileId(String titleId);

	/**
	 * Find max audit id for ebook Definition Id
	 * 
	 */
	public Long findEbookAuditByEbookDefId(Long ebookDefId);
	
	/**
	 * Return all EbookAudits
	 * @return
	 */
	public List<EbookAudit> findEbookAudits(EbookAuditFilter filter, EbookAuditSort sort);
	
	public int numberEbookAudits(EbookAuditFilter filter);
	
	public EbookAudit editIsbn(String titleId, String isbn);
	
	public void updateSplitDocumentsAudit(EbookAudit audit, String splitDocumentsConcat, int parts);

}