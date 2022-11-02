package com.thomsonreuters.uscl.ereader.core.book.userprofile;

import org.jetbrains.annotations.NotNull;

public class UserProfileTestUtil {

    private UserProfileTestUtil() {
    }

    @NotNull
    public static UserProfiles userprofile(final String id) {
        final UserProfiles userProfiles = new UserProfiles();
        userProfiles.setId(id);
        return userProfiles;
    }
}
