package com.thomsonreuters.uscl.ereader.core.service;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.junit.Assert.assertThat;
import static org.mockito.BDDMockito.given;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;

import com.thomsonreuters.uscl.ereader.userpreference.domain.UserPreference;
import com.thomsonreuters.uscl.ereader.userpreference.service.UserPreferenceEmailService;
import com.thomsonreuters.uscl.ereader.userpreference.service.UserPreferenceService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public final class EmailUtilTest {
    @InjectMocks
    private EmailUtilImpl service;
    @Mock
    private UserPreferenceService userPreferenceService;
    @Mock
    private UserPreferenceEmailService userPreferenceEmailService;
    @Mock
    private InternetAddress groupEmailAddress;

    @Test
    public void testGetEmailRecipientsByUsername() throws AddressException {
        //given
        final UserPreference userPreference = new UserPreference();
        given(userPreferenceService.findByUsername("C1234567")).willReturn(userPreference);
        final Set<InternetAddress> emails = new HashSet<>();
        emails.add(new InternetAddress("a@mail.com"));
        emails.add(new InternetAddress("b@mail.com"));
        given(userPreferenceEmailService.getEmails(userPreference)).willReturn(emails);
        // when
        final Collection<InternetAddress> actual = service.getEmailRecipientsByUsername("C1234567");
        //then
        assertThat(
            actual,
            containsInAnyOrder(
                new InternetAddress("a@mail.com"),
                new InternetAddress("b@mail.com"),
                groupEmailAddress));
    }
}
