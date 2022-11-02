package com.thomsonreuters.uscl.ereader.core.book.userprofile;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface UserProfileDao {

    /**
     * Create State Code
     */
    void createUserProfile(@NotNull UserProfiles userProfiles);

    /**
     * Update State Code
     */
    void updateUserProfile(@NotNull UserProfiles userProfiles);

    /**
     * Get all the State codes from the STATE_CODES table
     *
     * @return a list of UserProfile objects
     */
    List<UserProfiles> getAllUserProfiles();

    /**
     * Get a State Code with given ID
     * @param userProfileId
     */
    UserProfiles getUserProfileById(@NotNull String userProfileId);

    /**
     * Get a State Code with given name
     */
    List<UserProfiles> getUserProfileByFirstName(@NotNull String userProfileFirstName);
    /**UserProfiles getUserProfileByLastName(@NotNull String userProfileLastName);

     * Delete a State Code
     */
    void deleteUserProfile(@NotNull UserProfiles userProfiles);
}
