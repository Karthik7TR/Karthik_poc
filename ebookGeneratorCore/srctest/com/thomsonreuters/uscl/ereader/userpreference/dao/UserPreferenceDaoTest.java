package com.thomsonreuters.uscl.ereader.userpreference.dao;

import com.thomsonreuters.uscl.ereader.userpreference.domain.UserPreference;
import org.easymock.EasyMock;
import org.hibernate.SessionFactory;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public final class UserPreferenceDaoTest
{
    private SessionFactory mockSessionFactory;
    private org.hibernate.Session mockSession;
    private UserPreferenceDaoImpl dao;

    @Before
    public void setUp()
    {
        mockSessionFactory = EasyMock.createMock(SessionFactory.class);
        mockSession = EasyMock.createMock(org.hibernate.Session.class);
        dao = new UserPreferenceDaoImpl(mockSessionFactory);
    }

    @Test
    public void testFindByUsername()
    {
        final String username = "name";

        final UserPreference expected = new UserPreference();
        expected.setUserName(username);

        EasyMock.expect(mockSessionFactory.getCurrentSession()).andReturn(mockSession);
        EasyMock.expect(mockSession.get(UserPreference.class, username)).andReturn(expected);
        EasyMock.replay(mockSessionFactory);
        EasyMock.replay(mockSession);
        final UserPreference actual = dao.findByUsername(username);
        Assert.assertEquals(actual, expected);
        EasyMock.verify(mockSessionFactory);
        EasyMock.verify(mockSession);
    }
}
