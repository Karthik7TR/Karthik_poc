package com.thomsonreuters.uscl.ereader.proviewaudit.service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.thomsonreuters.uscl.ereader.proviewaudit.dao.ProviewAuditDao;
import com.thomsonreuters.uscl.ereader.proviewaudit.domain.ProviewAudit;
import com.thomsonreuters.uscl.ereader.proviewaudit.domain.ProviewAuditFilter;
import com.thomsonreuters.uscl.ereader.proviewaudit.domain.ProviewAuditSort;
import com.thomsonreuters.uscl.ereader.proviewaudit.domain.ProviewAuditSort.SortProperty;
import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public final class ProviewAuditServiceTest {
    private static final List<ProviewAudit> PROVIEW_AUDIT_LIST = new ArrayList<>();
    private static final List<ProviewAudit> PROVIEW_AUDIT_LIST_REVIEW = new ArrayList<>();

    private ProviewAuditServiceImpl service;

    private ProviewAuditDao mockDao;
    private ProviewAudit expectedAudit = new ProviewAudit();

    @Before
    public void setUp() throws ParseException {
        mockDao = EasyMock.createMock(ProviewAuditDao.class);

        service = new ProviewAuditServiceImpl(mockDao);

        String requestDateStr="08/29/2022";
        Date requestDate=new SimpleDateFormat("MM/dd/yyyy").parse(requestDateStr);
        PROVIEW_AUDIT_LIST_REVIEW.add(new ProviewAudit("uscl/an/ebooktest_en", "v1.0", "REVIEW", requestDate, "c286076"));
    }

    @Test
    public void testSave() {
        mockDao.save(expectedAudit);
        EasyMock.replay(mockDao);

        service.save(expectedAudit);
        EasyMock.verify(mockDao);
    }

    @Test
    public void testFindProviewAudits() {
        final ProviewAuditSort sort = new ProviewAuditSort(SortProperty.REQUEST_DATE, false, 1, 20);
        final ProviewAuditFilter filter = new ProviewAuditFilter();

        EasyMock.expect(mockDao.findProviewAudits(filter, sort)).andReturn(PROVIEW_AUDIT_LIST);
        EasyMock.replay(mockDao);

        final List<ProviewAudit> actual = service.findProviewAudits(filter, sort);
        Assert.assertEquals(PROVIEW_AUDIT_LIST, actual);
        EasyMock.verify(mockDao);
    }

    @Test
    public void findNumberProviewAudits() {
        final int number = 0;
        final ProviewAuditFilter filter = new ProviewAuditFilter();

        EasyMock.expect(mockDao.numberProviewAudits(filter)).andReturn(number);
        EasyMock.replay(mockDao);

        final int actual = service.numberProviewAudits(filter);
        Assert.assertEquals(number, actual);
        EasyMock.verify(mockDao);
    }

    @Test
    public void testFindJobSubmitterNameForAllTitlesLatestVersion() {

        EasyMock.expect(mockDao.findJobSubmitterNameForAllTitlesLatestVersion()).andReturn(PROVIEW_AUDIT_LIST_REVIEW);
        EasyMock.replay(mockDao);

        final List<ProviewAudit> actual = service.findJobSubmitterNameForAllTitlesLatestVersion();
        Assert.assertEquals(PROVIEW_AUDIT_LIST_REVIEW, actual);
        Assert.assertEquals(PROVIEW_AUDIT_LIST_REVIEW.get(0).getUsername(), actual.get(0).getUsername());
        EasyMock.verify(mockDao);
    }

    @Test
    public void testFindBooksInReviewStageForMoreThan24Hrs() {

        EasyMock.expect(mockDao.findBooksInReviewStageForMoreThan24Hrs()).andReturn(PROVIEW_AUDIT_LIST_REVIEW);
        EasyMock.replay(mockDao);

        final List<ProviewAudit> actual = service.findBooksInReviewStageForMoreThan24Hrs();
        Assert.assertEquals(PROVIEW_AUDIT_LIST_REVIEW, actual);
        Assert.assertEquals(PROVIEW_AUDIT_LIST_REVIEW.get(0).getProviewRequest(), actual.get(0).getProviewRequest());
        Assert.assertEquals("REVIEW", actual.get(0).getProviewRequest());
        EasyMock.verify(mockDao);
    }

}
