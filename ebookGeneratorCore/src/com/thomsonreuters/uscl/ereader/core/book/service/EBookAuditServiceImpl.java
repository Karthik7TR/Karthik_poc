/*
 * Copyright 2015: Thomson Reuters Global Resources. All Rights Reserved.
 * Proprietary and Confidential information of TRGR. Disclosure, Use or
 * Reproduction without the written authorization of TRGR is prohibited
 */
package com.thomsonreuters.uscl.ereader.core.book.service;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Required;
import org.springframework.transaction.annotation.Transactional;

import com.thomsonreuters.uscl.ereader.core.book.dao.EbookAuditDao;
import com.thomsonreuters.uscl.ereader.core.book.domain.EbookAudit;
import com.thomsonreuters.uscl.ereader.core.book.domain.EbookAuditFilter;
import com.thomsonreuters.uscl.ereader.core.book.domain.EbookAuditSort;

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
	@Transactional
	public void saveEBookAudit(EbookAudit eBookAudit) {
		// Update the date time
		eBookAudit.setLastUpdated(new Date());
		
		eBookAuditDAO.saveAudit(eBookAudit);
	}

	/**
	 * Delete an existing Author entity
	 * 
	 */
	@Transactional
	public void deleteEBookAudit(EbookAudit eBookAudit) {
		eBookAuditDAO.remove(eBookAudit);
	}

	@Override
	public EbookAudit findEBookAuditByPrimaryKey(Long auditId) {
		return eBookAuditDAO.findEbookAuditByPrimaryKey(auditId);
	}
	
	public Long findEbookAuditByEbookDefId(Long ebookDefId) {
		return eBookAuditDAO.findEbookAuditIdByEbookDefId(ebookDefId);
	}

	/**
	 * Return all EbookAudits
	 * @return
	 */
	public List<EbookAudit> findEbookAudits(EbookAuditFilter filter, EbookAuditSort sort) {
		return eBookAuditDAO.findEbookAudits(filter, sort);
	}
	
	public int numberEbookAudits(EbookAuditFilter filter) {
		return eBookAuditDAO.numberEbookAudits(filter);
	}
	
	@Override
	@Transactional(readOnly = true)
	public Long findMaxAuditId(){
		return eBookAuditDAO.findMaxAuditId();
	}
	
	@Override
	@Transactional(readOnly = true)
	public EbookAudit findEbookAuditIdByTtileId(String titleId){
		return eBookAuditDAO.findEbookAuditIdByTtileId(titleId);
	}
	
	@Transactional
	public void updateSplitDocumentsAudit(EbookAudit audit, String splitDocumentsConcat, int parts){
		eBookAuditDAO.updateSpliDocumentsAudit(audit, splitDocumentsConcat,parts);
	}
	
	@Transactional
	public EbookAudit editIsbn(String titleId, String isbn) {
		List<EbookAudit> audits = eBookAuditDAO.findEbookAuditByTitleIdAndIsbn(titleId, isbn);
		EbookAudit latestAudit = null;
		
		for(EbookAudit audit: audits) {
			StringBuilder buffer = new StringBuilder();
			buffer.append(EbookAuditDao.MOD_TEXT);
			buffer.append(audit.getIsbn());
			audit.setIsbn(buffer.toString());
			eBookAuditDAO.saveAudit(audit);
			
			if(latestAudit == null || latestAudit.getAuditId() < audit.getAuditId()) {
				latestAudit = audit;
			}
		}
		
		return latestAudit;
	}

	@Required
	/**
	 * @param eBookAuditDAO the eBookAuditDAO to set
	 */
	public void seteBookAuditDAO(EbookAuditDao eBookAuditDAO) {
		this.eBookAuditDAO = eBookAuditDAO;
	}

	/**
	 * @return the eBookAuditDAO
	 */
	public EbookAuditDao geteBookAuditDAO() {
		return eBookAuditDAO;
	}

}
