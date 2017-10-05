package com.thomsonreuters.uscl.ereader.userpreference.service;

import java.util.Set;

import javax.mail.internet.InternetAddress;

import com.thomsonreuters.uscl.ereader.userpreference.domain.UserPreference;

/**
 * Service to manage UserPreference entities.
 *
 */
public interface UserPreferenceService {
    void save(UserPreference preference);

    UserPreference findByUsername(String username);

    /**
     * Return all the unique email addresses listed under the USER_PREFERENCE.EMAIL_LIST column.
     * These are used to create the dynamic list of planned outage notification email recipients.
     * @return a set of email addresses
     */
    Set<InternetAddress> findAllUniqueEmailAddresses();
}
