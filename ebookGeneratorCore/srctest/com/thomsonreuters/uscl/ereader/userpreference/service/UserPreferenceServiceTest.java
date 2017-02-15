package com.thomsonreuters.uscl.ereader.userpreference.service;

import com.thomsonreuters.uscl.ereader.userpreference.dao.UserPreferenceDao;
import com.thomsonreuters.uscl.ereader.userpreference.domain.UserPreference;
import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public final class UserPreferenceServiceTest
{
    private UserPreferenceServiceImpl service;

    private UserPreferenceDao mockDao;

    @Before
    public void setUp()
    {
        mockDao = EasyMock.createMock(UserPreferenceDao.class);

        service = new UserPreferenceServiceImpl();
        service.setUserPreferenceDao(mockDao);
    }

    @Test
    public void testFindBookDefinition()
    {
        final String username = "name";

        final UserPreference expected = new UserPreference();
        expected.setUserName(username);

        EasyMock.expect(mockDao.findByUsername(username)).andReturn(expected);
        EasyMock.replay(mockDao);
        final UserPreference actual = service.findByUsername(username);
        Assert.assertEquals(expected, actual);
        EasyMock.verify(mockDao);
    }

    @Test
    public void testSaveBookDefinition()
    {
        final UserPreference expected = new UserPreference();

        mockDao.save(expected);
        EasyMock.replay(mockDao);

        service.save(expected);
        EasyMock.verify(mockDao);
    }
}
