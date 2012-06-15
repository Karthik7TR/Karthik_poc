/*
 * Copyright 2012: Thomson Reuters Global Resources. All Rights Reserved.
 * Proprietary and Confidential information of TRGR. Disclosure, Use or
 * Reproduction without the written authorization of TRGR is prohibited
 */

package com.thomsonreuters.uscl.ereader.support.dao;

import java.util.List;

import com.thomsonreuters.uscl.ereader.support.domain.SupportPageLink;


/**
 * DAO to manage SupportPageLink entities.
 * 
 */
public interface SupportPageLinkDao {

	public void save(SupportPageLink spl);
	
	public void delete(SupportPageLink spl);

	public SupportPageLink findByPrimaryKey(Long id);
	
	public List<SupportPageLink> findAllSupportPageLink();

}