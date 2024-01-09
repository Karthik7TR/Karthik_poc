package com.thomsonreuters.uscl.ereader.userpreference.service;

import java.util.List;
import java.util.Set;

import javax.mail.internet.InternetAddress;

import com.thomsonreuters.uscl.ereader.userpreference.domain.UserPreference;
import org.jetbrains.annotations.NotNull;

/**
 * Service to get emails from user preferences
 */
public interface UserPreferenceEmailService {
    @NotNull
    Set<InternetAddress> getEmails(@NotNull UserPreference userPreference);

    @NotNull
    List<String> getEmailsString(@NotNull UserPreference userPreference);
}
