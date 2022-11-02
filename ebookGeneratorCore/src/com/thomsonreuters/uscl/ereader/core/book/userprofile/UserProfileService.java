package com.thomsonreuters.uscl.ereader.core.book.userprofile;
import org.jetbrains.annotations.NotNull;
import org.springframework.transaction.annotation.Transactional;



import java.util.List;

public interface UserProfileService {

    List<UserProfiles> getAllUserProfiles();

    /**
     * Get a State Code by Id
     * @param userProfileId
     */
    UserProfiles getUserProfileById(@NotNull String userProfileId);

    /**
     * Get a State Code by name
     * @param userProfileFirstName
     */
    List<UserProfiles> getUserProfileByFirstName(@NotNull String userProfileFirstName);

    @Transactional
    void saveUserProfile(@NotNull UserProfiles userProfile);

    @Transactional
    void deleteUserProfile(@NotNull UserProfiles userProfile);
}
