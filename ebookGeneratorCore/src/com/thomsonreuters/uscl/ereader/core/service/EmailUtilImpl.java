package com.thomsonreuters.uscl.ereader.core.service;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import javax.mail.internet.InternetAddress;

import com.thomsonreuters.uscl.ereader.userpreference.domain.UserPreference;
import com.thomsonreuters.uscl.ereader.userpreference.service.UserPreferenceEmailService;
import com.thomsonreuters.uscl.ereader.userpreference.service.UserPreferenceService;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service("emailUtil")
public class EmailUtilImpl implements EmailUtil {
    @Autowired
    private UserPreferenceService userPreferenceService;
    @Autowired
    private UserPreferenceEmailService userPreferenceEmailService;
    @Value("${job.owner.email.group}")
    private InternetAddress groupEmailAddress;

    @NotNull
    @Override
    public Collection<InternetAddress> getEmailRecipientsByUsername(final String username) {
        final UserPreference userPreference = userPreferenceService.findByUsername(username);
        final Set<InternetAddress> emails = Optional.ofNullable(userPreference)
            .map(userPreferenceEmailService::getEmails).orElse(Collections.emptySet());
        return createEmailRecipients(emails);
    }

    @NotNull
    @Override
    public Collection<InternetAddress> createEmailRecipients(
        final Collection<InternetAddress> userRecipientInternetAddressList) {
        final Set<InternetAddress> uniqueRecipients = new HashSet<>();
        uniqueRecipients.addAll(userRecipientInternetAddressList);
        uniqueRecipients.add(groupEmailAddress);
        return uniqueRecipients;
    }
}
