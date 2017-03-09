package com.thomsonreuters.uscl.ereader.request;

import java.io.File;
import java.util.Date;

import com.thomsonreuters.uscl.ereader.request.dao.EBookArchiveDaoImpl;
import org.easymock.EasyMock;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public final class BundleArchiveDaoImplTest
{
    private EBookArchiveDaoImpl archiveDao;

    private SessionFactory mockSessionFactory;
    private Session mockSession;
    private Criteria mockCriteria;

    @Before
    public void setUp()
    {
        mockSessionFactory = EasyMock.createMock(SessionFactory.class);
        mockSession = EasyMock.createMock(Session.class);
        mockCriteria = EasyMock.createMock(Criteria.class);

        archiveDao = new EBookArchiveDaoImpl(mockSessionFactory);
    }

    @Test
    public void happyPath()
    {
        final long pkey = 1L;
        final EBookRequest expected = createEbookRequest();
        EasyMock.expect(mockSessionFactory.getCurrentSession()).andReturn(mockSession);
        EasyMock.expect(mockSession.get(EBookRequest.class, pkey)).andReturn(expected);
        replayAll();

        final EBookRequest actual = archiveDao.findByPrimaryKey(pkey);
        Assert.assertEquals(expected, actual);
    }

    private EBookRequest createEbookRequest()
    {
        final EBookRequest request = new EBookRequest();
        request.setBundleHash("asdfasda");
        request.setDateTime(new Date());
        request.setEBookArchiveId(127L);
        request.setEBookSrcFile(new File("asdfasdfa"));
        return request;
    }

    private void replayAll()
    {
        EasyMock.replay(mockSessionFactory);
        EasyMock.replay(mockSession);
        EasyMock.replay(mockCriteria);
    }
}
