package com.thomsonreuters.uscl.ereader.core.book.userprofile;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.jetbrains.annotations.NotNull;
import java.util.Collections;
import java.util.List;



public class UserProfileDaoImpl implements  UserProfileDao {

    private SessionFactory sessionFactory;

    public UserProfileDaoImpl(final SessionFactory sessFactory) {
        sessionFactory = sessFactory;
    }

    @Override
    public void createUserProfile(@NotNull final UserProfiles userProfiles) {
        final Session session = sessionFactory.getCurrentSession();
        session.save(userProfiles);
        session.flush();
    }



    @Override
    public void updateUserProfile(@NotNull final UserProfiles userProfiles) {
        final Session session = sessionFactory.getCurrentSession();
        session.merge(userProfiles);
        session.flush();
    }

    @Override
    public List<UserProfiles> getAllUserProfiles() {
        final Session session = sessionFactory.getCurrentSession();
        final Criteria criteria = session.createCriteria(UserProfiles.class);
        return Collections.checkedList(criteria.list(), UserProfiles.class);
    }

    @Override
    public UserProfiles getUserProfileById( final @NotNull String userProfileId) {
        return (UserProfiles) sessionFactory.getCurrentSession().get(UserProfiles.class, userProfileId);
    }

    @Override
    public List<UserProfiles> getUserProfileByFirstName(final @NotNull String userProfileFirstName) {
        final Session session = sessionFactory.getCurrentSession();
        final Criteria criteria = session.createCriteria(UserProfiles.class);
        return Collections.checkedList(criteria.list(), UserProfiles.class);
    }


    @Override
    public void deleteUserProfile(@NotNull final UserProfiles userProfiles) {
        final UserProfiles merged = (UserProfiles) sessionFactory.getCurrentSession().merge(userProfiles);
        final Session session = sessionFactory.getCurrentSession();
        session.delete(merged);
        session.flush();
    }
}
