/*
 * Copyright 2011: Thomson Reuters Global Resources. All Rights Reserved.
 * Proprietary and Confidential information of TRGR. Disclosure, Use or
 * Reproduction without the written authorization of TRGR is prohibited
 */

package com.thomsonreuters.uscl.ereader.userpreference.service;

import java.util.Set;

import javax.mail.internet.InternetAddress;

import org.springframework.beans.factory.annotation.Required;
import org.springframework.transaction.annotation.Transactional;

import com.thomsonreuters.uscl.ereader.userpreference.dao.UserPreferenceDao;
import com.thomsonreuters.uscl.ereader.userpreference.domain.UserPreference;

/**
 * Service to manage UserPreference entities.
 * 
 */

public class UserPreferenceServiceImpl implements UserPreferenceService {

	private UserPreferenceDao userPreferenceDao;
	
	@Override
	@Transactional
	public void save(UserPreference preference) {
		userPreferenceDao.save(preference);
	}

	@Override
	@Transactional(readOnly=true)
	public UserPreference findByUsername(String username) {
		return userPreferenceDao.findByUsername(username);
	}
	
	@Override
	@Transactional(readOnly=true)
	public Set<InternetAddress> findAllUniqueEmailAddresses() {
		return userPreferenceDao.findAllUniqueEmailAddresses();
	}
	
	@Required
	public void setUserPreferenceDao(UserPreferenceDao dao){
		this.userPreferenceDao = dao;
	}
}
