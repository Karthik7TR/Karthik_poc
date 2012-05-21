/*
 * Copyright 2011: Thomson Reuters Global Resources. All Rights Reserved.
 * Proprietary and Confidential information of TRGR. Disclosure, Use or
 * Reproduction without the written authorization of TRGR is prohibited
 */

package com.thomsonreuters.uscl.ereader.userpreference.service;

import org.springframework.beans.factory.annotation.Required;
import org.springframework.transaction.annotation.Transactional;

import com.thomsonreuters.uscl.ereader.userpreference.dao.UserPreferenceDao;
import com.thomsonreuters.uscl.ereader.userpreference.domain.UserPreference;

/**
 * Service to manage UserPreference entities.
 * 
 */

public class UserPreferenceServiceImpl implements UserPreferenceService {

	private UserPreferenceDao dao;
	
	@Override
	@Transactional
	public void save(UserPreference preference) {
		dao.save(preference);
	}

	@Override
	@Transactional(readOnly=true)
	public UserPreference findByUsername(String username) {
		return dao.findByUsername(username);
	}
	
	@Required
	public void setUserPreferenceDao(UserPreferenceDao dao){
		this.dao = dao;
	}
}
