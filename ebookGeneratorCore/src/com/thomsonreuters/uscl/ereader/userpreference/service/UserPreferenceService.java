/*
 * Copyright 2012: Thomson Reuters Global Resources. All Rights Reserved.
 * Proprietary and Confidential information of TRGR. Disclosure, Use or
 * Reproduction without the written authorization of TRGR is prohibited
 */

package com.thomsonreuters.uscl.ereader.userpreference.service;

import com.thomsonreuters.uscl.ereader.userpreference.domain.UserPreference;

/**
 * Service to manage UserPreference entities.
 * 
 */
public interface UserPreferenceService {

	public void save(UserPreference preference);

	public UserPreference findByUsername(String username);

}