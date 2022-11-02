package com.thomsonreuters.uscl.ereader.core.book.userprofile;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import static com.thomsonreuters.uscl.ereader.core.book.userprofile.UserProfileTestUtil.userprofile;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.BDDMockito.then;

@RunWith(MockitoJUnitRunner.class)
public class UserProfileServiceImplTest {

    @InjectMocks
    private UserProfileServiceImpl service;
    @Mock
    private UserProfileDao dao;
    @Captor
    private ArgumentCaptor<UserProfiles> captor;

    @Test
    public void shouldCreateUserProfileIfIdIsNull() {
        // given
        final UserProfiles userprofile = userprofile(null);

        // when

        service.saveUserProfile(userprofile);
        // then
        then(dao).should().createUserProfile(captor.capture());

        assertThat(captor.getValue(), notNullValue());
    }

    @Test
    public void shouldUpdateUserProfileIfIdIsNotNull() {
        // given
        final UserProfiles userprofile = userprofile("1234");
        // when
        service.saveUserProfile(userprofile);
        // then
        then(dao).should().updateUserProfile(captor.capture());
        assertThat(captor.getValue(), notNullValue());
    }
}
