package com.thomsonreuters.uscl.ereader.stats.service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.thomsonreuters.uscl.ereader.core.book.domain.EbookAudit;
import com.thomsonreuters.uscl.ereader.stats.dao.PublishingStatsDao;
import com.thomsonreuters.uscl.ereader.stats.domain.PublishingStats;
import com.thomsonreuters.uscl.ereader.stats.util.PublishingStatsUtil;
import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public final class PublishingStatsServiceTest {
    private static final Long BOOK_DEFINITION_ID = 1L;
    private static final String TITLE_ID = "uscl/an/book";
    private List<PublishingStats> STATS = new ArrayList<>();

    private PublishingStatsServiceImpl service;

    private PublishingStatsDao mockDao;
    private PublishingStatsUtil mockUtil;

    @Before
    public void setUp() {
        mockDao = EasyMock.createMock(PublishingStatsDao.class);
        mockUtil = EasyMock.createMock(PublishingStatsUtil.class);

        service = new PublishingStatsServiceImpl(mockDao, mockUtil);

        for (int i = 0; i < 10; i++) {
            final PublishingStats stat = new PublishingStats();
            stat.setJobInstanceId((long) i);
            stat.setPublishStatus(publishStatusMessage(i));
            final EbookAudit audit = new EbookAudit();
            audit.setAuditId((long) i);
            stat.setAudit(audit);
            STATS.add(stat);
        }
    }

    private String publishStatusMessage(final int i) {
        switch (i) {
        case 3:
            return PublishingStats.SEND_EMAIL_COMPLETE;
        case 5:
            return PublishingStats.SUCCESFULL_PUBLISH_STATUS;
        default:
            return "not this one";
        }
    }

    @Test
    public void testFindLastSuccessfulJobStatsAuditByEbookDef() {
        EasyMock.expect(mockDao.findPublishingStatsByEbookDef(BOOK_DEFINITION_ID)).andReturn(STATS);
        EasyMock.expect(mockUtil.isPublishedSuccessfully("not this one")).andReturn(false).times(8);
        EasyMock.expect(mockUtil.isPublishedSuccessfully(PublishingStats.SEND_EMAIL_COMPLETE)).andReturn(true);
        EasyMock.expect(mockUtil.isPublishedSuccessfully(PublishingStats.SUCCESFULL_PUBLISH_STATUS)).andReturn(true);
        EasyMock.replay(mockDao);
        EasyMock.replay(mockUtil);

        final EbookAudit audit = service.findLastSuccessfulJobStatsAuditByEbookDef(BOOK_DEFINITION_ID);

        final Long auditId = 5L;
        Assert.assertEquals(auditId, audit.getAuditId());
        EasyMock.verify(mockDao);
        EasyMock.verify(mockUtil);
    }

    @Test
    public void testHasIsbnBeenPublished() {
        final Set<String> isbns = new HashSet<>();
        isbns.add("1-2-3");
        isbns.add("1-1");
        isbns.add("1-2");

        EasyMock.expect(mockDao.findSuccessfullyPublishedIsbns()).andReturn(isbns);
        EasyMock.replay(mockDao);

        final String isbn = "123";
        final Boolean hasBeenPublished = service.hasIsbnBeenPublished(isbn);
        Assert.assertEquals(true, hasBeenPublished);
        EasyMock.verify(mockDao);
    }

    @Test
    public void testHasIsbnBeenPublished2() {
        final Set<String> isbns = new HashSet<>();
        isbns.add("1-2-3");
        isbns.add("1-1");
        isbns.add("1-2");

        EasyMock.expect(mockDao.findSuccessfullyPublishedIsbns()).andReturn(isbns);
        EasyMock.replay(mockDao);

        final String isbn = "1";
        final Boolean hasBeenPublished = service.hasIsbnBeenPublished(isbn);
        Assert.assertEquals(false, hasBeenPublished);
        EasyMock.verify(mockDao);
    }
}
