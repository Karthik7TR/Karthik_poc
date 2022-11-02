package com.thomsonreuters.uscl.ereader.mgr.web.controller.admin.userprofile;
import com.thomsonreuters.uscl.ereader.core.book.userprofile.UserProfileService;
import com.thomsonreuters.uscl.ereader.core.book.userprofile.UserProfiles;

import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.validation.BindException;
import org.springframework.validation.Errors;

import java.util.ArrayList;
import java.util.List;

@RunWith(MockitoJUnitRunner.class)
public class UserProfileFormValidatorTest {
    private static final UserProfiles USER_PROFILE = new UserProfiles();
    private static final String USER_ID = "C286035";
    private static final String FIRST_NAME = "test";
    private static final String LAST_NAME = "Arshad";
    private final List<UserProfiles> userProfiles = new ArrayList<>();

    private UserProfileService mockUserProfileService;
    private UserProfileFormValidator validator;
    private UserProfileForm form;
    private Errors errors;

    @Before
    public void setUp() {
        // Mock up the service
        mockUserProfileService = EasyMock.createMock(UserProfileService.class);

        // Setup Validator
        validator = new UserProfileFormValidator(mockUserProfileService);

        form = new UserProfileForm();
        form.setUserId(USER_ID);
        form.setFirstName(FIRST_NAME);
        form.setLastName(LAST_NAME);

        USER_PROFILE.setId(USER_ID);
        USER_PROFILE.setFirstName(FIRST_NAME);
        USER_PROFILE.setLastName(LAST_NAME);

        userProfiles.add(USER_PROFILE);


        errors = new BindException(form, "form");
    }

    @Test
    public void testNoName() {
        // Check Valid name entry
        // Check Valid name entry
        EasyMock.expect(mockUserProfileService.getUserProfileByFirstName(FIRST_NAME)).andReturn(userProfiles);
        EasyMock.expect(mockUserProfileService.getUserProfileById(USER_ID)).andReturn(USER_PROFILE);

        EasyMock.replay(mockUserProfileService);
        validator.validate(form, errors);
        Assert.assertTrue(errors.hasErrors());


     }

    @Test
    public void testNameExists() {
        final UserProfiles userProfile = new UserProfiles();
      userProfile.setFirstName("test");

        EasyMock.expect(mockUserProfileService.getUserProfileByFirstName("test")).andReturn(userProfiles);
        EasyMock.replay(mockUserProfileService);

        // Verify name requirement
        validator.validate(form, errors);
        Assert.assertEquals("error.exist", errors.getFieldError("firstName").getCode());

        EasyMock.verify(mockUserProfileService);
    }
}


