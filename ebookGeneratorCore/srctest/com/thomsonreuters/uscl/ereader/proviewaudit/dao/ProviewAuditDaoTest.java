package com.thomsonreuters.uscl.ereader.proviewaudit.dao;

import java.util.ArrayList;
import java.util.List;

import com.thomsonreuters.uscl.ereader.proviewaudit.domain.ProviewAudit;
import com.thomsonreuters.uscl.ereader.proviewaudit.domain.ProviewAuditFilter;
import com.thomsonreuters.uscl.ereader.proviewaudit.domain.ProviewAuditSort;
import com.thomsonreuters.uscl.ereader.proviewaudit.domain.ProviewAuditSort.SortProperty;
import org.easymock.EasyMock;
import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projection;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public final class ProviewAuditDaoTest
{
    private static final List<ProviewAudit> PROVIEW_AUDIT_LIST = new ArrayList<>();

    private SessionFactory mockSessionFactory;
    private org.hibernate.Session mockSession;
    private Criteria mockCriteria;
    private ProviewAuditDaoImpl dao;

    @Before
    public void setUp()
    {
        mockSessionFactory = EasyMock.createMock(SessionFactory.class);
        mockSession = EasyMock.createMock(org.hibernate.Session.class);
        mockCriteria = EasyMock.createMock(Criteria.class);
        dao = new ProviewAuditDaoImpl(mockSessionFactory);
    }

    @Test
    public void testFindProviewAudits()
    {
        final ProviewAuditSort sort = new ProviewAuditSort(SortProperty.REQUEST_DATE, false, 1, 20);
        final ProviewAuditFilter filter = new ProviewAuditFilter();

        EasyMock.expect(mockSessionFactory.getCurrentSession()).andReturn(mockSession);
        EasyMock.expect(mockSession.createCriteria(ProviewAudit.class)).andReturn(mockCriteria);
        EasyMock.expect(mockCriteria.addOrder(Order.asc(EasyMock.anyObject(String.class)))).andReturn(mockCriteria);

        final int itemsPerPage = sort.getItemsPerPage();
        EasyMock.expect(mockCriteria.setFirstResult((sort.getPageNumber() - 1) * (itemsPerPage)))
            .andReturn(mockCriteria);
        EasyMock.expect(mockCriteria.setMaxResults(itemsPerPage)).andReturn(mockCriteria);
        EasyMock.expect(mockCriteria.list()).andReturn(PROVIEW_AUDIT_LIST);
        EasyMock.replay(mockSessionFactory);
        EasyMock.replay(mockSession);
        EasyMock.replay(mockCriteria);

        final List<ProviewAudit> actualAudits = dao.findProviewAudits(filter, sort);
        Assert.assertEquals(PROVIEW_AUDIT_LIST, actualAudits);
        EasyMock.verify(mockSessionFactory);
        EasyMock.verify(mockSession);
        EasyMock.verify(mockCriteria);
    }

    @Test
    public void testNumberProviewAudits()
    {
        final ProviewAuditFilter filter = new ProviewAuditFilter();

        EasyMock.expect(mockSessionFactory.getCurrentSession()).andReturn(mockSession);
        EasyMock.expect(mockSession.createCriteria(ProviewAudit.class)).andReturn(mockCriteria);
        EasyMock.expect(mockCriteria.setProjection(EasyMock.anyObject(Projection.class))).andReturn(mockCriteria);
        EasyMock.expect(mockCriteria.list()).andReturn(PROVIEW_AUDIT_LIST);
        EasyMock.replay(mockSessionFactory);
        EasyMock.replay(mockSession);
        EasyMock.replay(mockCriteria);

        final int actual = dao.numberProviewAudits(filter);
        Assert.assertEquals(PROVIEW_AUDIT_LIST.size(), actual);
        EasyMock.verify(mockSessionFactory);
        EasyMock.verify(mockSession);
        EasyMock.verify(mockCriteria);
    }
}
