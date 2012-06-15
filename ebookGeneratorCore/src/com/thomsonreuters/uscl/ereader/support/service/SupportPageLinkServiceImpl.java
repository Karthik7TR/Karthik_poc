/*
 * Copyright 2011: Thomson Reuters Global Resources. All Rights Reserved.
 * Proprietary and Confidential information of TRGR. Disclosure, Use or
 * Reproduction without the written authorization of TRGR is prohibited
 */

package com.thomsonreuters.uscl.ereader.support.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Required;
import org.springframework.transaction.annotation.Transactional;

import com.thomsonreuters.uscl.ereader.support.dao.SupportPageLinkDao;
import com.thomsonreuters.uscl.ereader.support.domain.SupportPageLink;

/**
 * Service to manage SupportPageLink entities.
 * 
 */

public class SupportPageLinkServiceImpl implements SupportPageLinkService {

	private SupportPageLinkDao dao;
	
	@Transactional
	public void save(SupportPageLink spl) {
		dao.save(spl);
	}
	
	@Transactional
	public void delete(SupportPageLink spl) {
		dao.delete(spl);
	}

	@Transactional(readOnly=true)
	public SupportPageLink findByPrimaryKey(Long id) {
		return dao.findByPrimaryKey(id);
	}
	
	@Transactional(readOnly=true)
	public List<SupportPageLink> findAllSupportPageLink() {
		return dao.findAllSupportPageLink();
	}
	
	@Required
	public void setSupportPageLinkDao(SupportPageLinkDao dao){
		this.dao = dao;
	}
}
