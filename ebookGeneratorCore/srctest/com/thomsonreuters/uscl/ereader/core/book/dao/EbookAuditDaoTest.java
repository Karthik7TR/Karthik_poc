package com.thomsonreuters.uscl.ereader.core.book.dao;

import static com.thomsonreuters.uscl.ereader.core.book.dao.EbookAuditDao.MOD_TEXT;
import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import com.thomsonreuters.uscl.ereader.core.book.domain.EbookAudit;
import com.thomsonreuters.uscl.ereader.core.book.domain.EbookAuditFilter;
import com.thomsonreuters.uscl.ereader.core.book.domain.EbookAuditSort;
import com.thomsonreuters.uscl.ereader.core.book.domain.EbookAuditSort.SortProperty;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projection;
import org.hibernate.criterion.Restrictions;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public final class EbookAuditDaoTest {
    //mocking doesn't want to work for eq() for some reason, so '%' was
    //added to TITLE_ID to correspond to how it would look like inside of a method
    private static final String TITLE_ID = "%title/id";
    private static final String ISBN = "123-456";
    private List<EbookAudit> bookAuditList;
    private final EbookAudit audit = new EbookAudit();

    @InjectMocks
    private EBookAuditDaoImpl dao;
    @Mock
    private SessionFactory mockSessionFactory;
    @Mock
    private org.hibernate.Session mockSession;
    @Mock
    private Query mockQuery;
    @Mock
    private Criteria mockCriteria;

    @Before
    public void setup() {
        audit.setAuditId(1L);
        bookAuditList = new ArrayList<>();
        bookAuditList.add(audit);
    }

    @Test
    public void testFindAuditById() {
        final long auditKey = 1L;
        when(mockSessionFactory.getCurrentSession()).thenReturn(mockSession);
        when(mockSession.getNamedQuery("findEbookAuditByPrimaryKey")).thenReturn(mockQuery);
        when(mockQuery.setLong("auditId", auditKey)).thenReturn(mockQuery);
        when(mockQuery.uniqueResult()).thenReturn(audit);
        final EbookAudit actualBookDefinition = dao.findEbookAuditByPrimaryKey(auditKey);
        assertEquals(audit, actualBookDefinition);
    }

    @Test
    public void testFindEbookAudits() {
        final EbookAuditSort sort = new EbookAuditSort(SortProperty.SUBMITTED_DATE, false, 1, 20);
        final EbookAuditFilter filter = new EbookAuditFilter();

        when(mockSessionFactory.getCurrentSession()).thenReturn(mockSession);
        when(mockSession.createCriteria(EbookAudit.class)).thenReturn(mockCriteria);
        when(mockCriteria.addOrder(Order.asc(Matchers.anyString()))).thenReturn(mockCriteria);

        final int itemsPerPage = sort.getItemsPerPage();
        when(mockCriteria.setFirstResult((sort.getPageNumber() - 1) * (itemsPerPage))).thenReturn(mockCriteria);
        when(mockCriteria.setMaxResults(itemsPerPage)).thenReturn(mockCriteria);
        when(mockCriteria.list()).thenReturn(bookAuditList);

        final List<EbookAudit> actualAudits = dao.findEbookAudits(filter, sort);
        assertEquals(bookAuditList, actualAudits);
    }

    @Test
    public void testNumberEbookAudits() {
        final EbookAuditFilter filter = new EbookAuditFilter();

        when(mockSessionFactory.getCurrentSession()).thenReturn(mockSession);
        when(mockSession.createCriteria(EbookAudit.class)).thenReturn(mockCriteria);
        when(mockCriteria.setProjection(Matchers.any(Projection.class))).thenReturn(mockCriteria);
        when(mockCriteria.list()).thenReturn(bookAuditList);

        final int actual = dao.numberEbookAudits(filter);
        assertEquals(bookAuditList.size(), actual);
    }

    @Test
    public void shouldCheckIsbnModification() {
        //given
        final boolean expectedResult = true;

        when(mockSessionFactory.getCurrentSession()).thenReturn(mockSession);
        when(mockSession.createCriteria(EbookAudit.class)).thenReturn(mockCriteria);
        when(mockCriteria.add(any())).thenReturn(mockCriteria);
        when(mockCriteria.add(eq(Restrictions.eq("ISBN", MOD_TEXT + ISBN)))).thenReturn(mockCriteria);
        when(mockCriteria.setMaxResults(1)).thenReturn(mockCriteria);
        when(mockCriteria.uniqueResult()).thenReturn(expectedResult);
        //when
        final boolean actualResult = dao.isIsbnModified(TITLE_ID, ISBN);
        //then
        assertEquals(expectedResult, actualResult);
    }

    @Test
    public void shouldFindModifiedAudits() {
        //given
        final List<EbookAudit> expectedList = bookAuditList;

        when(mockSessionFactory.getCurrentSession()).thenReturn(mockSession);
        when(mockSession.createCriteria(EbookAudit.class)).thenReturn(mockCriteria);
        when(mockCriteria.add(any())).thenReturn(mockCriteria);
        when(mockCriteria.add(eq(Restrictions.eq("ISBN", MOD_TEXT + ISBN)))).thenReturn(mockCriteria);
        when(mockCriteria.list()).thenReturn(expectedList);
        //when
        final List<EbookAudit> actualList = dao.findEbookAuditByTitleIdAndModifiedIsbn(TITLE_ID, ISBN);
        //then
        assertEquals(expectedList, actualList);
    }
}
