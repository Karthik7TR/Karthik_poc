package com.thomsonreuters.uscl.ereader.userpreference.dao;

import com.thomsonreuters.uscl.ereader.userpreference.domain.UserPreference;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * DAO to manage UserPreference entities.
 */
public interface UserPreferenceDao extends JpaRepository<UserPreference, String> {
    //Intentionally left blank
}
