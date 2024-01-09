package com.thomsonreuters.uscl.ereader.core.library.service.support;

import java.util.ArrayList;
import java.util.List;

import com.thomsonreuters.uscl.ereader.mgr.web.service.support.SupportPageLinkServiceImpl;
import com.thomsonreuters.uscl.ereader.support.dao.SupportPageLinkDao;
import com.thomsonreuters.uscl.ereader.support.domain.SupportPageLink;
import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public final class SupportPageLinkServiceTest {
    private final SupportPageLink SUPPORT_PAGE_LINK = new SupportPageLink();
    private final List<SupportPageLink> ALL_SUPPORT_PAGE_LINK = new ArrayList<>();
    private final Long SUPPORT_PAGE_LINK_ID = Long.valueOf(1);

    private SupportPageLinkServiceImpl service;
    private SupportPageLinkDao mockDao;

    @Before
    public void setUp() {
        mockDao = EasyMock.createMock(SupportPageLinkDao.class);

        service = new SupportPageLinkServiceImpl(mockDao);

        SUPPORT_PAGE_LINK.setId(SUPPORT_PAGE_LINK_ID);
    }

    @Test
    public void testFindByPrimaryKey() {
        EasyMock.expect(mockDao.findOne(SUPPORT_PAGE_LINK_ID)).andReturn(SUPPORT_PAGE_LINK);
        EasyMock.replay(mockDao);
        final SupportPageLink actual = service.findByPrimaryKey(SUPPORT_PAGE_LINK_ID);
        Assert.assertEquals(SUPPORT_PAGE_LINK, actual);
        EasyMock.verify(mockDao);
    }

    @Test
    public void testFindAllSupportPageLinks() {
        ALL_SUPPORT_PAGE_LINK.add(SUPPORT_PAGE_LINK);
        EasyMock.expect(mockDao.findAllByOrderByLinkDescriptionDesc()).andReturn(ALL_SUPPORT_PAGE_LINK);
        EasyMock.replay(mockDao);
        final List<SupportPageLink> actual = service.findAllSupportPageLink();
        final List<SupportPageLink> expected = new ArrayList<>();
        expected.add(SUPPORT_PAGE_LINK);

        Assert.assertEquals(expected, actual);
        EasyMock.verify(mockDao);
    }
}
