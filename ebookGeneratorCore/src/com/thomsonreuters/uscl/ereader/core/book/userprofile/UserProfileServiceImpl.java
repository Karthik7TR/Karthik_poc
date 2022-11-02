package com.thomsonreuters.uscl.ereader.core.book.userprofile;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service("userProfileService")
public class UserProfileServiceImpl implements UserProfileService{

    private final UserProfileDao dao;

    @Autowired
    public UserProfileServiceImpl(final UserProfileDao dao) {
        this.dao = dao;
    }

    @Transactional(readOnly = true)
    @Override
    public List<UserProfiles> getAllUserProfiles() {
        return dao.getAllUserProfiles();
    }

    @Transactional(readOnly = true)
    @Override
    public UserProfiles getUserProfileById(@NotNull final String userProfileId) {
        return dao.getUserProfileById(userProfileId);
    }

    @Transactional(readOnly = true)
    @Override
    public List<UserProfiles> getUserProfileByFirstName(@NotNull final String userProfileFirstName) {
        return dao.getUserProfileByFirstName(userProfileFirstName);
    }

    @Transactional
    @Override
    public void saveUserProfile(@NotNull final UserProfiles userProfiles) {
        if (userProfiles.getId() == null) {
            dao.createUserProfile(userProfiles);
        } else {
            dao.updateUserProfile(userProfiles);
        }
    }


    @Transactional
    @Override
    public void deleteUserProfile(@NotNull final UserProfiles userProfiles) {

            dao.deleteUserProfile(userProfiles);
        }






}


