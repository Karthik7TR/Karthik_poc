package com.thomsonreuters.uscl.ereader.mgr.web.controller.admin.userprofile;
import com.thomsonreuters.uscl.ereader.core.book.userprofile.UserProfileService;
import com.thomsonreuters.uscl.ereader.core.book.userprofile.UserProfiles;
import com.thomsonreuters.uscl.ereader.mgr.web.controller.BaseFormValidator;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;
import java.util.List;


@Component("userProfileFormValidator")
public class UserProfileFormValidator extends BaseFormValidator implements Validator {

    private static final int MAXIMUM_CHARACTER_1024 = 1024;
    private static final int MAXIMUM_CHARACTER_7 = 7;
    private final UserProfileService userProfileService;


    @Autowired
    public UserProfileFormValidator(final UserProfileService userProfileService) {
        this.userProfileService = userProfileService;
    }

    @Override
    public boolean supports(final Class clazz) {
        return UserProfileForm.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(final Object obj, final Errors errors) {
        final UserProfileForm form = (UserProfileForm) obj;

        final String userId = form.getUserId();
        final String firstName= form.getFirstName();
        final String lastName= form.getLastName();


        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "userId", "error.required");

        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "firstName", "error.required");

        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "lastName", "error.required");


        checkMaxLength(errors, MAXIMUM_CHARACTER_7, userId, "userId", new Object[] {"User Id", MAXIMUM_CHARACTER_7});
        checkForSpaces(errors, userId, "userId", "User Id");
        checkSpecialCharacters(errors, userId, "userId", true);

        checkMaxLength(errors, MAXIMUM_CHARACTER_1024, firstName, "firstName", new Object[] {"First Name", MAXIMUM_CHARACTER_1024});
        checkForSpaces(errors, firstName, "firstName", "First Name");
        checkSpecialCharacters(errors, firstName, "firstName", true);

        checkMaxLength(errors, MAXIMUM_CHARACTER_1024, lastName, "lastName", new Object[] {"Last Name", MAXIMUM_CHARACTER_1024});
        checkForSpaces(errors, lastName, "lastName", "Last Name");
        checkSpecialCharacters(errors, lastName, "lastName", true);

        final int isEdit =form.getIsEdit();


        int flag=0;



        if (!StringUtils.isBlank(firstName) && flag!=1 ) {
            final String name_form =firstName.concat(lastName);
            List<UserProfiles> names = userProfileService.getUserProfileByFirstName(firstName);

            String names_exist;
            for (UserProfiles i : names) {
                if (i.getFirstName() !=null) {
                    names_exist = i.getFirstName().concat(i.getLastName());
                    if (names_exist.equalsIgnoreCase(name_form) && !i.getId().equalsIgnoreCase(form.getUserId()) ) {
                        errors.rejectValue("firstName", "error.exist", new Object[]{"First Name"}, "Already exists");
                        errors.rejectValue("lastName", "error.exist", new Object[]{"Last Name"}, "Already exists");
                        flag = 1;
                    }
                }
            }
        }

        if (!StringUtils.isBlank(userId)  && isEdit == 0  && flag ==0)  {
            final UserProfiles code = userProfileService.getUserProfileById(userId);
            if (code != null && code.getId().equalsIgnoreCase(form.getUserId())) {
                errors.rejectValue("userId", "error.exist", new Object[]{"User ID"}, "Already exists");
                flag=1;
            }
        }






    }
}
