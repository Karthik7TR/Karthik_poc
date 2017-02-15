package com.thomsonreuters.uscl.ereader.core.book.dao;

import java.util.ArrayList;
import java.util.List;

import com.thomsonreuters.uscl.ereader.core.book.domain.EbookAudit;
import com.thomsonreuters.uscl.ereader.core.book.domain.EbookAuditFilter;
import com.thomsonreuters.uscl.ereader.core.book.domain.EbookAuditSort;
import com.thomsonreuters.uscl.ereader.core.book.domain.EbookAuditSort.SortProperty;
import org.easymock.EasyMock;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projection;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public final class EbookAuditDaoTest
{
    private static final long AUDIT_KEY = 1L;
    private static final EbookAudit BOOK_AUDIT = new EbookAudit();
    private static final List<EbookAudit> BOOK_AUDIT_LIST = new ArrayList<>();

    private SessionFactory mockSessionFactory;
    private org.hibernate.Session mockSession;
    private Query mockQuery;
    private Criteria mockCriteria;
    private EBookAuditDaoImpl dao;

    @Before
    public void setUp()
    {
        mockSessionFactory = EasyMock.createMock(SessionFactory.class);
        mockSession = EasyMock.createMock(org.hibernate.Session.class);
        mockQuery = EasyMock.createMock(Query.class);
        mockCriteria = EasyMock.createMock(Criteria.class);
        dao = new EBookAuditDaoImpl(mockSessionFactory);
    }

    @Test
    public void testFindAuditById()
    {
        EasyMock.expect(mockSessionFactory.getCurrentSession()).andReturn(mockSession);
        EasyMock.expect(mockSession.getNamedQuery("findEbookAuditByPrimaryKey")).andReturn(mockQuery);
        EasyMock.expect(mockQuery.setLong("auditId", AUDIT_KEY)).andReturn(mockQuery);
        EasyMock.expect(mockQuery.uniqueResult()).andReturn(BOOK_AUDIT);
        EasyMock.replay(mockSessionFactory);
        EasyMock.replay(mockSession);
        EasyMock.replay(mockQuery);
        final EbookAudit actualBookDefinition = dao.findEbookAuditByPrimaryKey(AUDIT_KEY);
        Assert.assertEquals(BOOK_AUDIT, actualBookDefinition);
        EasyMock.verify(mockSessionFactory);
        EasyMock.verify(mockSession);
    }

    @Test
    public void testFindEbookAudits()
    {
        final EbookAuditSort sort = new EbookAuditSort(SortProperty.SUBMITTED_DATE, false, 1, 20);
        final EbookAuditFilter filter = new EbookAuditFilter();

        EasyMock.expect(mockSessionFactory.getCurrentSession()).andReturn(mockSession);
        EasyMock.expect(mockSession.createCriteria(EbookAudit.class)).andReturn(mockCriteria);
        EasyMock.expect(mockCriteria.addOrder(Order.asc(EasyMock.anyObject(String.class)))).andReturn(mockCriteria);

        final int itemsPerPage = sort.getItemsPerPage();
        EasyMock.expect(mockCriteria.setFirstResult((sort.getPageNumber() - 1) * (itemsPerPage)))
            .andReturn(mockCriteria);
        EasyMock.expect(mockCriteria.setMaxResults(itemsPerPage)).andReturn(mockCriteria);
        EasyMock.expect(mockCriteria.list()).andReturn(BOOK_AUDIT_LIST);
        EasyMock.replay(mockSessionFactory);
        EasyMock.replay(mockSession);
        EasyMock.replay(mockCriteria);

        final List<EbookAudit> actualAudits = dao.findEbookAudits(filter, sort);
        Assert.assertEquals(BOOK_AUDIT_LIST, actualAudits);
        EasyMock.verify(mockSessionFactory);
        EasyMock.verify(mockSession);
        EasyMock.verify(mockCriteria);
    }

    @Test
    public void testNumberEbookAudits()
    {
        final EbookAuditFilter filter = new EbookAuditFilter();

        EasyMock.expect(mockSessionFactory.getCurrentSession()).andReturn(mockSession);
        EasyMock.expect(mockSession.createCriteria(EbookAudit.class)).andReturn(mockCriteria);
        EasyMock.expect(mockCriteria.setProjection(EasyMock.anyObject(Projection.class))).andReturn(mockCriteria);
        EasyMock.expect(mockCriteria.list()).andReturn(BOOK_AUDIT_LIST);
        EasyMock.replay(mockSessionFactory);
        EasyMock.replay(mockSession);
        EasyMock.replay(mockCriteria);

        final int actual = dao.numberEbookAudits(filter);
        Assert.assertEquals(BOOK_AUDIT_LIST.size(), actual);
        EasyMock.verify(mockSessionFactory);
        EasyMock.verify(mockSession);
        EasyMock.verify(mockCriteria);
    }
}
