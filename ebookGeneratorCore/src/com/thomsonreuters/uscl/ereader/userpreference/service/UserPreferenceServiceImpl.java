package com.thomsonreuters.uscl.ereader.userpreference.service;

import java.util.Date;
import java.util.Set;
import java.util.stream.Collectors;

import javax.mail.internet.InternetAddress;

import com.thomsonreuters.uscl.ereader.userpreference.dao.UserPreferenceDao;
import com.thomsonreuters.uscl.ereader.userpreference.domain.UserPreference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Service to manage UserPreference entities.
 */
@Service("userPreferenceService")
public class UserPreferenceServiceImpl implements UserPreferenceService {
    private final UserPreferenceDao userPreferenceDao;
    private final UserPreferenceEmailService userPreferenceEmailService;

    @Autowired
    public UserPreferenceServiceImpl(
        final UserPreferenceDao userPreferenceDao,
        final UserPreferenceEmailService userPreferenceEmailService) {
        this.userPreferenceDao = userPreferenceDao;
        this.userPreferenceEmailService = userPreferenceEmailService;
    }

    @Override
    public void save(final UserPreference preference) {
        preference.setLastUpdated(new Date());
        userPreferenceDao.save(preference);
    }

    @Override
    public UserPreference findByUsername(final String username) {
        return userPreferenceDao.findOne(username);
    }

    @Override
    public Set<InternetAddress> findAllUniqueEmailAddresses() {
        return userPreferenceDao.findAll()
            .stream()
            .flatMap(userPreference -> userPreferenceEmailService.getEmails(userPreference).stream())
            .collect(Collectors.toSet());
    }
}
