package com.thomsonreuters.uscl.ereader.mgr.web.controller.admin.userprofile;

import com.thomsonreuters.uscl.ereader.core.book.userprofile.UserProfiles;

public class UserProfileForm {

    public static final String FORM_NAME = "userProfileForm";

    private String id;
    private String firstName;
    private String lastName;
    private int isEdit;

    public UserProfileForm() {
        super();
    }

    public void initialize(final UserProfiles user) {
        id = user.getId();
        lastName = user.getLastName();
        firstName=user.getFirstName();
        isEdit=0;
    }

    public UserProfiles makeUser() {
        final UserProfiles user = new UserProfiles();
        user.setId(id);
        user.setLastName(lastName);
        user.setFirstName(firstName);


        return user;
    }

    public String getUserId() {
        return id;
    }

    public void setUserId(final String id) {
        this.id = id;
    }

    public int getIsEdit() {
        return isEdit;
    }

    public void setIsEdit(final int isEdit) {
        this.isEdit = isEdit;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(final String lastName) {   this.lastName = lastName; }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(final String firstName) {
        this.firstName = firstName;
    }
}

