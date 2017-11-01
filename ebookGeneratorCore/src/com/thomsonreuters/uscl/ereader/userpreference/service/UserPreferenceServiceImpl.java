package com.thomsonreuters.uscl.ereader.userpreference.service;

import java.util.Set;

import javax.mail.internet.InternetAddress;

import com.thomsonreuters.uscl.ereader.userpreference.dao.UserPreferenceDao;
import com.thomsonreuters.uscl.ereader.userpreference.domain.UserPreference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service to manage UserPreference entities.
 *
 */
@Service("userPreferenceService")
public class UserPreferenceServiceImpl implements UserPreferenceService {
    private final UserPreferenceDao userPreferenceDao;

    @Autowired
    public UserPreferenceServiceImpl(final UserPreferenceDao userPreferenceDao) {
        this.userPreferenceDao = userPreferenceDao;
    }

    @Override
    @Transactional
    public void save(final UserPreference preference) {
        userPreferenceDao.save(preference);
    }

    @Override
    @Transactional(readOnly = true)
    public UserPreference findByUsername(final String username) {
        return userPreferenceDao.findByUsername(username);
    }

    @Override
    @Transactional(readOnly = true)
    public Set<InternetAddress> findAllUniqueEmailAddresses() {
        return userPreferenceDao.findAllUniqueEmailAddresses();
    }
}
