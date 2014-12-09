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
	
	@Transactional
	public void editIsbn(String titleId, String isbn) {
		List<EbookAudit> audits = eBookAuditDAO.findEbookAuditByTitleIdAndIsbn(titleId, isbn);
		for(EbookAudit audit: audits) {
			StringBuilder buffer = new StringBuilder();
			buffer.append(EbookAuditDao.MOD_TEXT);
			buffer.append(audit.getIsbn());
			audit.setIsbn(buffer.toString());
			eBookAuditDAO.saveAudit(audit);
		}
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
