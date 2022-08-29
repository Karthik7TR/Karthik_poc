package com.thomsonreuters.uscl.ereader.proviewaudit.dao;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.thomsonreuters.uscl.ereader.proviewaudit.domain.ProviewAudit;
import com.thomsonreuters.uscl.ereader.proviewaudit.domain.ProviewAuditFilter;
import com.thomsonreuters.uscl.ereader.proviewaudit.domain.ProviewAuditSort;
import com.thomsonreuters.uscl.ereader.proviewaudit.domain.ProviewAuditSort.SortProperty;
import org.easymock.EasyMock;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projection;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public final class ProviewAuditDaoTest {
    private static final List<ProviewAudit> PROVIEW_AUDIT_LIST = new ArrayList<>();

    private SessionFactory mockSessionFactory;
    private org.hibernate.Session mockSession;
    private Criteria mockCriteria;
    private SQLQuery mockQuery;
    private ProviewAuditDaoImpl dao;

    @Before
    public void setUp() {
        mockSessionFactory = EasyMock.createMock(SessionFactory.class);
        mockSession = EasyMock.createMock(org.hibernate.Session.class);
        mockCriteria = EasyMock.createMock(Criteria.class);
        mockQuery = EasyMock.createMock(SQLQuery.class);
        dao = new ProviewAuditDaoImpl(mockSessionFactory);
    }

    @Test
    public void testFindProviewAudits() {
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
    public void testFindBooksInReviewStageForMoreThan24Hrs() {
        final StringBuffer hql =
                new StringBuffer("select a.title_id, a.book_version, a.proview_request, a.request_date, a.user_name ");
        hql.append(" from proview_audit a ");
        hql.append(" where a.proview_request = 'REVIEW' ");
        hql.append(" and sysdate - to_date(to_char(a.request_date,'DD/MM/YYYY HH24:MI:SS'), 'DD/MM/YYYY HH24:MI:SS') > 1 ");

        List<ProviewAudit> PROVIEW_AUDIT_LIST_REVIEW = new ArrayList<>();
        List<Object[]> PROVIEW_REVIEW_LIST = new ArrayList<Object[]>();

        EasyMock.expect(mockSessionFactory.getCurrentSession()).andReturn(mockSession);
        EasyMock.expect(mockSession.createSQLQuery(hql.toString())).andReturn(mockQuery);
        EasyMock.expect(mockQuery.list()).andReturn(PROVIEW_REVIEW_LIST);
        EasyMock.replay(mockSessionFactory);
        EasyMock.replay(mockSession);
        EasyMock.replay(mockQuery);

        final List<ProviewAudit> actualReviewAudits = dao.findBooksInReviewStageForMoreThan24Hrs();
        Assert.assertEquals(PROVIEW_AUDIT_LIST_REVIEW, actualReviewAudits);
        EasyMock.verify(mockSessionFactory);
        EasyMock.verify(mockSession);
        EasyMock.verify(mockQuery);

    }
    @Test
    public void testFindBooksInReviewStageForMoreThan24HrsWithData() throws ParseException {
        final StringBuffer hql =
                new StringBuffer("select a.title_id, a.book_version, a.proview_request, a.request_date, a.user_name ");
        hql.append(" from proview_audit a ");
        hql.append(" where a.proview_request = 'REVIEW' ");
        hql.append(" and sysdate - to_date(to_char(a.request_date,'DD/MM/YYYY HH24:MI:SS'), 'DD/MM/YYYY HH24:MI:SS') > 1 ");

        String requestDateStr="08/29/2022";
        Date requestDate=new SimpleDateFormat("MM/dd/yyyy").parse(requestDateStr);

        List<ProviewAudit> PROVIEW_AUDIT_LIST_REVIEW = new ArrayList<>();
        PROVIEW_AUDIT_LIST_REVIEW.add(new ProviewAudit("uscl/an/ebooktest_en", "v1.0", "REVIEW", requestDate, "c286076"));

        List<Object[]> PROVIEW_REVIEW_LIST = new ArrayList<Object[]>();
        PROVIEW_REVIEW_LIST.add(new Object[]{"uscl/an/ebooktest_en", "v1.0", "REVIEW", requestDate, "c286076"});


        EasyMock.expect(mockSessionFactory.getCurrentSession()).andReturn(mockSession);
        EasyMock.expect(mockSession.createSQLQuery(hql.toString())).andReturn(mockQuery);
        EasyMock.expect(mockQuery.list()).andReturn(PROVIEW_REVIEW_LIST);
        EasyMock.replay(mockSessionFactory);
        EasyMock.replay(mockSession);
        EasyMock.replay(mockQuery);

        final List<ProviewAudit> actualReviewAudits = dao.findBooksInReviewStageForMoreThan24Hrs();
        Assert.assertEquals(PROVIEW_AUDIT_LIST_REVIEW.size(), actualReviewAudits.size());
        Assert.assertEquals(PROVIEW_AUDIT_LIST_REVIEW.get(0).getTitleId(), actualReviewAudits.get(0).getTitleId());
        EasyMock.verify(mockSessionFactory);
        EasyMock.verify(mockSession);
        EasyMock.verify(mockQuery);

    }

    @Test
    public void testFindJobSubmitterNameForAllTitlesLatestVersionWithData() throws ParseException {
        final StringBuffer hql =
                new StringBuffer("select x.title_id, x.book_version, x.proview_request, x.request_date, x.user_name from ( ");
        hql.append(" select a.title_id, a.book_version, ");
        hql.append(" case a.proview_request WHEN 'REMOVE' THEN 'Removed' WHEN 'PROMOTE' THEN 'Final' ELSE 'Review' end as proview_request, ");
        hql.append(" a.request_date, a.user_name, ");
        hql.append(" row_number() over(partition by a.title_id ");
        hql.append(" order by a.book_version desc, ");
        hql.append(" case a.proview_request WHEN 'REMOVE' THEN 1 WHEN 'PROMOTE' THEN 2 ELSE 3 end, a.proview_audit_id desc) proview_rank ");
        hql.append(" from proview_audit a ");
        hql.append(" where a.proview_request in ('REVIEW','PROMOTE','REMOVE') ) x ");
        hql.append(" where x.proview_rank = 1 ");

        String requestDateStr="08/29/2022";
        Date requestDate=new SimpleDateFormat("MM/dd/yyyy").parse(requestDateStr);

        List<Object[]> PROVIEW_REVIEW_LIST = new ArrayList<Object[]>();
        PROVIEW_REVIEW_LIST.add(new Object[]{"uscl/an/ebooktest_en", "v1.0", "REVIEW", requestDate, "c286076"});
        PROVIEW_REVIEW_LIST.add(new Object[]{"uscl/an/ebooktest2_en", "v2.0", "PROMOTE", requestDate, "c670682"});

        List<ProviewAudit> PROVIEW_AUDIT_LIST_REVIEW = new ArrayList<>();
        PROVIEW_AUDIT_LIST_REVIEW.add(new ProviewAudit("uscl/an/ebooktest_en", "v1.0", "REVIEW", requestDate, "c286076"));
        PROVIEW_AUDIT_LIST_REVIEW.add(new ProviewAudit("uscl/an/ebooktest2_en", "v2.0", "PROMOTE", requestDate, "c670682"));

        EasyMock.expect(mockSessionFactory.getCurrentSession()).andReturn(mockSession);
        EasyMock.expect(mockSession.createSQLQuery(hql.toString())).andReturn(mockQuery);
        EasyMock.expect(mockQuery.list()).andReturn(PROVIEW_REVIEW_LIST);
        EasyMock.replay(mockSessionFactory);
        EasyMock.replay(mockSession);
        EasyMock.replay(mockQuery);

        final List<ProviewAudit> actualReviewAudits = dao.findJobSubmitterNameForAllTitlesLatestVersion();
        Assert.assertEquals(PROVIEW_AUDIT_LIST_REVIEW.size(), actualReviewAudits.size());
        Assert.assertEquals(PROVIEW_AUDIT_LIST_REVIEW.get(0).getTitleId(), actualReviewAudits.get(0).getTitleId());
        Assert.assertEquals(PROVIEW_AUDIT_LIST_REVIEW.get(0).getUsername(), actualReviewAudits.get(0).getUsername());
        Assert.assertEquals(PROVIEW_AUDIT_LIST_REVIEW.get(1).getUsername(), actualReviewAudits.get(1).getUsername());

        EasyMock.verify(mockSessionFactory);
        EasyMock.verify(mockSession);
        EasyMock.verify(mockQuery);

    }

    @Test
    public void testNumberProviewAudits() {
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
