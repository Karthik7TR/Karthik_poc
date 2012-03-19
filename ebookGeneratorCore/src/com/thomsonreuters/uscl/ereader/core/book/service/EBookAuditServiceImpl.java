package com.thomsonreuters.uscl.ereader.core.book.service;

import org.springframework.beans.factory.annotation.Required;
import org.springframework.transaction.annotation.Transactional;

import com.thomsonreuters.uscl.ereader.core.book.dao.EbookAuditDao;
import com.thomsonreuters.uscl.ereader.core.book.domain.EbookAudit;

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
