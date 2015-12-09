package com.thomsonreuters.uscl.ereader.proviewaudit.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Required;
import org.springframework.transaction.annotation.Transactional;

import com.thomsonreuters.uscl.ereader.proviewaudit.dao.ProviewAuditDao;
import com.thomsonreuters.uscl.ereader.proviewaudit.domain.ProviewAudit;
import com.thomsonreuters.uscl.ereader.proviewaudit.domain.ProviewAuditFilter;
import com.thomsonreuters.uscl.ereader.proviewaudit.domain.ProviewAuditSort;

/**
 * Spring service that handles CRUD requests for ProviewAudit entities
 * 
 */

@Transactional
public class ProviewAuditServiceImpl implements ProviewAuditService {

	/**
	 * DAO injected by Spring that manages ProviewAudit entities
	 * 
	 */
	private ProviewAuditDao dao;

	/**
	 * Save an existing ProviewAudit entity
	 * 
	 */
	@Transactional
	public void save(ProviewAudit audit) {
		dao.save(audit);
	}
	
	/**
	 * Return all ProviewAudit that are filtered
	 * @return
	 */
	@Transactional(readOnly=true)
	public List<ProviewAudit> findProviewAudits(ProviewAuditFilter filter, ProviewAuditSort sort) {
		return dao.findProviewAudits(filter, sort);
	}
	
	@Transactional(readOnly=true)
	public int numberProviewAudits(ProviewAuditFilter filter) {
		return dao.numberProviewAudits(filter);
	}

	@Required
	public void setProviewAuditDao(ProviewAuditDao dao) {
		this.dao = dao;
	}
	
	@Override
	@Transactional(readOnly=true)
	public List<String> getBookStatus(String titleId, String version){
		return dao.getBookStatus(titleId,version);
	}

}
