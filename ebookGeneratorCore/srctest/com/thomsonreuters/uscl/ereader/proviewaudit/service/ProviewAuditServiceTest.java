package com.thomsonreuters.uscl.ereader.proviewaudit.service;

import java.util.ArrayList;
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

public final class ProviewAuditServiceTest
{
    private static final List<ProviewAudit> PROVIEW_AUDIT_LIST = new ArrayList<>();

    private ProviewAuditServiceImpl service;

    private ProviewAuditDao mockDao;
    private ProviewAudit expectedAudit = new ProviewAudit();

    @Before
    public void setUp()
    {
        mockDao = EasyMock.createMock(ProviewAuditDao.class);

        service = new ProviewAuditServiceImpl();
        service.setProviewAuditDao(mockDao);
    }

    @Test
    public void testSave()
    {
        mockDao.save(expectedAudit);
        EasyMock.replay(mockDao);

        service.save(expectedAudit);
        EasyMock.verify(mockDao);
    }

    @Test
    public void testFindProviewAudits()
    {
        final ProviewAuditSort sort = new ProviewAuditSort(SortProperty.REQUEST_DATE, false, 1, 20);
        final ProviewAuditFilter filter = new ProviewAuditFilter();

        EasyMock.expect(mockDao.findProviewAudits(filter, sort)).andReturn(PROVIEW_AUDIT_LIST);
        EasyMock.replay(mockDao);

        final List<ProviewAudit> actual = service.findProviewAudits(filter, sort);
        Assert.assertEquals(PROVIEW_AUDIT_LIST, actual);
        EasyMock.verify(mockDao);
    }

    @Test
    public void findNumberProviewAudits()
    {
        final int number = 0;
        final ProviewAuditFilter filter = new ProviewAuditFilter();

        EasyMock.expect(mockDao.numberProviewAudits(filter)).andReturn(number);
        EasyMock.replay(mockDao);

        final int actual = service.numberProviewAudits(filter);
        Assert.assertEquals(number, actual);
        EasyMock.verify(mockDao);
    }
}
