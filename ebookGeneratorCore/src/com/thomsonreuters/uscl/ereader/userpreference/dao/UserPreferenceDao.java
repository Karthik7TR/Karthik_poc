package com.thomsonreuters.uscl.ereader.userpreference.dao;

import java.util.Set;

import javax.mail.internet.InternetAddress;

import com.thomsonreuters.uscl.ereader.userpreference.domain.UserPreference;

/**
 * DAO to manage UserPreference entities.
 *
 */
public interface UserPreferenceDao {
    void save(UserPreference preference);

    UserPreference findByUsername(String username);

    Set<InternetAddress> findAllUniqueEmailAddresses();
}
