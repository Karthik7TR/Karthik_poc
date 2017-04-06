package com.thomsonreuters.uscl.ereader.request;

import java.io.File;
import java.util.Date;

import com.thomsonreuters.uscl.ereader.request.dao.XppBundleArchiveDaoImpl;
import com.thomsonreuters.uscl.ereader.request.domain.XppBundleArchive;
import org.easymock.EasyMock;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public final class BundleArchiveDaoImplTest
{
    private XppBundleArchiveDaoImpl archiveDao;

    private SessionFactory mockSessionFactory;
    private Session mockSession;
    private Criteria mockCriteria;

    @Before
    public void setUp()
    {
        mockSessionFactory = EasyMock.createMock(SessionFactory.class);
        mockSession = EasyMock.createMock(Session.class);
        mockCriteria = EasyMock.createMock(Criteria.class);

        archiveDao = new XppBundleArchiveDaoImpl(mockSessionFactory);
    }

    @Test
    public void happyPath()
    {
        final long pkey = 1L;
        final XppBundleArchive expected = createEBookArchive();
        EasyMock.expect(mockSessionFactory.getCurrentSession()).andReturn(mockSession);
        EasyMock.expect(mockSession.get(XppBundleArchive.class, pkey)).andReturn(expected);
        replayAll();

        final XppBundleArchive actual = archiveDao.findByPrimaryKey(pkey);
        Assert.assertEquals(expected, actual);
    }

    private XppBundleArchive createEBookArchive()
    {
        final XppBundleArchive request = new XppBundleArchive();
        request.setBundleHash("asdfasda");
        request.setDateTime(new Date());
        request.setXppBundleArchiveId(127L);
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
