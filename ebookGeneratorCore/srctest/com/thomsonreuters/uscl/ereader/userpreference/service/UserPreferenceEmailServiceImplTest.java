package com.thomsonreuters.uscl.ereader.userpreference.service;

import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.junit.Assert.assertThat;

import java.util.List;
import java.util.Set;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;

import com.thomsonreuters.uscl.ereader.userpreference.domain.UserPreference;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public final class UserPreferenceEmailServiceImplTest {
    @InjectMocks
    private UserPreferenceEmailServiceImpl sut;

    @Test
    public void shouldReturnEmails() throws AddressException {
        // given
        final UserPreference userPreference = new UserPreference();
        userPreference.setEmails("a@mail.com,b@mail.com,@anothermail");
        // when
        final Set<InternetAddress> emails = sut.getEmails(userPreference);
        // then
        assertThat(emails, containsInAnyOrder(new InternetAddress("a@mail.com"), new InternetAddress("b@mail.com")));
    }

    @Test
    public void shouldReturnEmailsStrings() {
        // given
        final UserPreference userPreference = new UserPreference();
        userPreference.setEmails("a@mail.com,b@mail.com,@anothermail");
        // when
        final List<String> emails = sut.getEmailsString(userPreference);
        // then
        assertThat(emails, contains("a@mail.com", "b@mail.com", "@anothermail"));
    }
}
