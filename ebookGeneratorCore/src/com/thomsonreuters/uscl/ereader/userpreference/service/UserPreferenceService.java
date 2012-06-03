/*
 * Copyright 2012: Thomson Reuters Global Resources. All Rights Reserved.
 * Proprietary and Confidential information of TRGR. Disclosure, Use or
 * Reproduction without the written authorization of TRGR is prohibited
 */

package com.thomsonreuters.uscl.ereader.userpreference.service;

import java.util.Set;

import javax.mail.internet.InternetAddress;

import com.thomsonreuters.uscl.ereader.userpreference.domain.UserPreference;

/**
 * Service to manage UserPreference entities.
 * 
 */
public interface UserPreferenceService {

	public void save(UserPreference preference);

	public UserPreference findByUsername(String username);
	
	/**
	 * Return all the unique email addresses listed under the USER_PREFERENCE.EMAIL_LIST column.
	 * These are used to create the dynamic list of planned outage notification email recipients.
	 * @return a set of email addresses
	 */
	public Set<InternetAddress> findAllUniqueEmailAddresses();

}