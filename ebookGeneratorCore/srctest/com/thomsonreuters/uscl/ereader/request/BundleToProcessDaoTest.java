package com.thomsonreuters.uscl.ereader.request;

import java.util.Date;

import com.thomsonreuters.uscl.ereader.request.dao.BundleToProcessDao;
import org.easymock.EasyMock;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public final class BundleToProcessDaoTest
{
    private BundleToProcessDao bundleDao;

    private SessionFactory mockSessionFactory;
    private Session mockSession;
    private Criteria mockCriteria;

    @Before
    public void setUp()
    {
        mockSessionFactory = EasyMock.createMock(SessionFactory.class);
        mockSession = EasyMock.createMock(Session.class);
        mockCriteria = EasyMock.createMock(Criteria.class);

        bundleDao = new BundleToProcessDao(mockSessionFactory);
    }

    @Test
    public void happyPath()
    {
        final long pkey = 1L;
        final BundleToProcess expected = createBundleToProcess();
        EasyMock.expect(mockSessionFactory.getCurrentSession()).andReturn(mockSession);
        EasyMock.expect(mockSession.get(BundleToProcess.class, pkey)).andReturn(expected);
        replayAll();

        final BundleToProcess actual = bundleDao.findByPrimaryKey(pkey);
        Assert.assertEquals(expected, actual);
    }

    private BundleToProcess createBundleToProcess()
    {
        final BundleToProcess request = new BundleToProcess(new EBookRequest());
        request.setDateTime(new Date());
        request.setBundleToProcessId(127L);
        request.setSourceLocation("asdfasdfa");
        request.setProductName("productname");
        request.setProductType("producttype");
        return request;
    }

    private void replayAll()
    {
        EasyMock.replay(mockSessionFactory);
        EasyMock.replay(mockSession);
        EasyMock.replay(mockCriteria);
    }
}
