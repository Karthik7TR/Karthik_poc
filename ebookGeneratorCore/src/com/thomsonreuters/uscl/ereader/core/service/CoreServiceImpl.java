package com.thomsonreuters.uscl.ereader.core.service;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.mail.internet.InternetAddress;

import com.thomsonreuters.uscl.ereader.userpreference.domain.UserPreference;
import com.thomsonreuters.uscl.ereader.userpreference.service.UserPreferenceService;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Required;

public class CoreServiceImpl implements CoreService {
    private UserPreferenceService userPreferenceService;
    private InternetAddress groupEmailAddress;

    @NotNull
    @Override
    public Collection<InternetAddress> getEmailRecipientsByUsername(final String username) {
        List<InternetAddress> userRecipientInternetAddressList = Collections.EMPTY_LIST;
        final UserPreference userPreference = userPreferenceService.findByUsername(username);
        if (userPreference != null) {
            final String userRecipientCsv = userPreference.getEmails();
            final List<String> userRecipientStringList = UserPreference.toStringEmailAddressList(userRecipientCsv);
            userRecipientInternetAddressList = UserPreference.toInternetAddressList(userRecipientStringList);
        }
        return createEmailRecipients(userRecipientInternetAddressList);
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

    @Required
    public void setGroupEmailAddress(final InternetAddress addr) {
        groupEmailAddress = addr;
    }

    @Required
    public void setUserPreferenceService(final UserPreferenceService userPreferenceService) {
        this.userPreferenceService = userPreferenceService;
    }
}
