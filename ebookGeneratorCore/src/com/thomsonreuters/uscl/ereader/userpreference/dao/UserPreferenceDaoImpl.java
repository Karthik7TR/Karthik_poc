package com.thomsonreuters.uscl.ereader.userpreference.dao;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.mail.internet.InternetAddress;

import com.thomsonreuters.uscl.ereader.userpreference.domain.UserPreference;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

/**
 * DAO to manage UserPreference entities.
 *
 */

public class UserPreferenceDaoImpl implements UserPreferenceDao
{
    //private static Logger log = LogManager.getLogger(UserPreferenceDaoImpl.class);
    private SessionFactory sessionFactory;

    public UserPreferenceDaoImpl(final SessionFactory hibernateSessionFactory)
    {
        sessionFactory = hibernateSessionFactory;
    }

    @Override
    public void save(final UserPreference preference)
    {
        final Session session = sessionFactory.getCurrentSession();

        preference.setLastUpdated(new Date());
        session.saveOrUpdate(preference);
        session.flush();
    }

    @Override
    public UserPreference findByUsername(final String username)
    {
        final Session session = sessionFactory.getCurrentSession();
        return (UserPreference) session.get(UserPreference.class, username);
    }

    @Override
    public Set<InternetAddress> findAllUniqueEmailAddresses()
    {
        final Set<InternetAddress> uniqueAddresses = new HashSet<>();
        final Session session = sessionFactory.getCurrentSession();
        final String hql = "select emails from UserPreference";
        final Query query = session.createQuery(hql);
        // get the list of csv email addrs { "a@foo.com,b@foo.com,c@foo.com", "d@foo.com,e@foo.com,f@foo.com" }
        final List<String> csvEmailAddrs = query.list(); // a list of CSV email addresses

        // Parse all the csv strings into a unique list of address objects
        for (final String csvAddr : csvEmailAddrs)
        {
            final List<String> addrStrings = UserPreference.toStringEmailAddressList(csvAddr);
            final List<InternetAddress> addrs = UserPreference.toInternetAddressList(addrStrings);
            uniqueAddresses.addAll(addrs);
        }
        return uniqueAddresses;
    }
}
