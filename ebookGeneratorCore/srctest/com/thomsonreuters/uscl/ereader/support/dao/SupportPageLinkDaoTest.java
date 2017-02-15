package com.thomsonreuters.uscl.ereader.support.dao;

import java.util.ArrayList;
import java.util.List;

import com.thomsonreuters.uscl.ereader.support.domain.SupportPageLink;
import org.easymock.EasyMock;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public final class SupportPageLinkDaoTest
{
    private final SupportPageLink SUPPORT_PAGE_LINK = new SupportPageLink();
    private final Long SUPPORT_PAGE_LINK_ID = Long.valueOf(1);
    private final List<SupportPageLink> ALL_SUPPORT_PAGE_LINK = new ArrayList<>();

    private SessionFactory mockSessionFactory;
    private Session mockSession;
    private Criteria mockCriteria;
    private SupportPageLinkDaoImpl dao;

    @Before
    public void setUp()
    {
        mockSessionFactory = EasyMock.createMock(SessionFactory.class);
        mockSession = EasyMock.createMock(Session.class);
        mockCriteria = EasyMock.createMock(Criteria.class);
        dao = new SupportPageLinkDaoImpl(mockSessionFactory);

        SUPPORT_PAGE_LINK.setId(SUPPORT_PAGE_LINK_ID);
    }

    @Test
    public void testFindByPrimaryKey()
    {
        EasyMock.expect(mockSessionFactory.getCurrentSession()).andReturn(mockSession);
        EasyMock.expect(mockSession.get(SupportPageLink.class, SUPPORT_PAGE_LINK_ID)).andReturn(SUPPORT_PAGE_LINK);
        EasyMock.replay(mockSessionFactory);
        EasyMock.replay(mockSession);

        final SupportPageLink actualSupportPageLink = dao.findByPrimaryKey(SUPPORT_PAGE_LINK_ID);
        final SupportPageLink expected = new SupportPageLink();
        expected.setId(SUPPORT_PAGE_LINK_ID);

        Assert.assertEquals(expected, actualSupportPageLink);

        EasyMock.verify(mockSessionFactory);
        EasyMock.verify(mockSession);
    }

    @Test
    public void testGetAllSupportPageLinks()
    {
        ALL_SUPPORT_PAGE_LINK.add(SUPPORT_PAGE_LINK);
        EasyMock.expect(mockSessionFactory.getCurrentSession()).andReturn(mockSession);
        EasyMock.expect(mockSession.createCriteria(SupportPageLink.class)).andReturn(mockCriteria);
        EasyMock.expect(mockCriteria.addOrder(EasyMock.anyObject(Order.class))).andReturn(mockCriteria);
        EasyMock.expect(mockCriteria.list()).andReturn(ALL_SUPPORT_PAGE_LINK);
        EasyMock.replay(mockSessionFactory);
        EasyMock.replay(mockSession);
        EasyMock.replay(mockCriteria);

        final List<SupportPageLink> actualSupportPageLink = dao.findAllSupportPageLink();
        final List<SupportPageLink> expectedSupportPageLinks = new ArrayList<>();
        expectedSupportPageLinks.add(SUPPORT_PAGE_LINK);
        Assert.assertEquals(expectedSupportPageLinks, actualSupportPageLink);

        EasyMock.verify(mockSessionFactory);
        EasyMock.verify(mockSession);
        EasyMock.verify(mockCriteria);
    }
}
