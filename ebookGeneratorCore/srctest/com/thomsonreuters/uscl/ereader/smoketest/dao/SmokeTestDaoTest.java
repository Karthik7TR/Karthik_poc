package com.thomsonreuters.uscl.ereader.smoketest.dao;

import org.easymock.EasyMock;
import org.hibernate.SessionFactory;
import org.hibernate.jdbc.ReturningWork;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public final class SmokeTestDaoTest
{
    private SessionFactory mockSessionFactory;
    private org.hibernate.Session mockSession;
    private SmokeTestDaoImpl dao;

    @Before
    public void setUp()
    {
        mockSessionFactory = EasyMock.createMock(SessionFactory.class);
        mockSession = EasyMock.createMock(org.hibernate.Session.class);
        dao = new SmokeTestDaoImpl(mockSessionFactory);
    }

    @Test
    public void testConnectionStatus()
    {
        final boolean expectedStatus = true;

        EasyMock.expect(mockSessionFactory.getCurrentSession()).andReturn(mockSession);
        EasyMock.expect(mockSession.doReturningWork(EasyMock.anyObject(ReturningWork.class))).andReturn(expectedStatus);
        EasyMock.replay(mockSessionFactory);
        EasyMock.replay(mockSession);

        final boolean status = dao.testConnection();
        Assert.assertEquals(expectedStatus, status);

        EasyMock.verify(mockSessionFactory);
        EasyMock.verify(mockSession);
    }
}
