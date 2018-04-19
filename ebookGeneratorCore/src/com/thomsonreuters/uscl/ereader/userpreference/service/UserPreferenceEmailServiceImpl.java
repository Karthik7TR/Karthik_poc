package com.thomsonreuters.uscl.ereader.userpreference.service;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;

import com.thomsonreuters.uscl.ereader.userpreference.domain.UserPreference;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.stereotype.Service;

@Slf4j
@Service("userPreferenceEmailService")
public class UserPreferenceEmailServiceImpl implements UserPreferenceEmailService {
    @NotNull
    @Override
    public Set<InternetAddress> getEmails(@NotNull final UserPreference userPreference) {
        return getEmailsString(userPreference).stream()
            .map(this::createInternetAddress)
            .filter(Objects::nonNull)
            .collect(Collectors.toSet());
    }

    @NotNull
    @Override
    public List<String> getEmailsString(@NotNull final UserPreference userPreference) {
        return Optional.ofNullable(userPreference.getEmails())
            .map(emails -> Arrays.asList(emails.split(",")))
            .orElse(Collections.emptyList());
    }

    @Nullable
    private InternetAddress createInternetAddress(final String email) {
        try {
            return new InternetAddress(email);
        } catch (final AddressException e) {
            log.error("Invalid user preference email address - ignored: " + email, e);
            return null;
        }
    }
}
