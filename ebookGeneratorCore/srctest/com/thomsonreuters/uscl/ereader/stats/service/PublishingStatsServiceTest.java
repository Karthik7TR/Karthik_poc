package com.thomsonreuters.uscl.ereader.stats.service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.thomsonreuters.uscl.ereader.core.book.domain.EbookAudit;
import com.thomsonreuters.uscl.ereader.core.book.model.Version;
import com.thomsonreuters.uscl.ereader.stats.dao.PublishingStatsDao;
import com.thomsonreuters.uscl.ereader.stats.domain.PublishingStats;
import com.thomsonreuters.uscl.ereader.stats.domain.PublishingStatsFilter;
import com.thomsonreuters.uscl.ereader.stats.util.PublishingStatsUtil;
import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import static org.mockito.Matchers.any;

public final class PublishingStatsServiceTest {
    private static final Long BOOK_DEFINITION_ID = 1L;
    private static final String TITLE_ID = "uscl/an/book";
    private static final String BOOK_NAME = "demoBook";
    private static final String ISBN = "978-054-7-34124-8";
    private static final int MAJOR_VERSION = 1;
    private static final String VERSION = "v" + MAJOR_VERSION + ".0";
    private List<PublishingStats> STATS = new ArrayList<>();
    private Set<String> isbns;

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

        isbns = new HashSet<>();
        isbns.add("1-2-3");
        isbns.add("1-1");
        isbns.add("1-2");
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
    public void testFindPublishingStats() {
        PublishingStatsFilter filterForm = new PublishingStatsFilter(TITLE_ID,BOOK_NAME,ISBN);
        EasyMock.expect(mockDao.findPublishingStats(filterForm)).andReturn(STATS);
        EasyMock.replay(mockDao);

        final List<PublishingStats> lstSelectedStats = service.findPublishingStats(filterForm);
        final int actualCountSelectedStats = lstSelectedStats.size();
        Assert.assertEquals(STATS.size(), actualCountSelectedStats);
        EasyMock.verify(mockDao);
    }

    @Test
    public void testGetIsbnByTitleAndVersion() {
        final String expectedIsbn = "44-22-6";
        EasyMock.expect(mockDao.findSuccessfullyPublishedIsbnByTitleIdAndVersion(TITLE_ID, VERSION.replace(Version.VERSION_PREFIX, "")))
            .andReturn(expectedIsbn);
        EasyMock.replay(mockDao);

        final String actualIsbn = service.getIsbnByTitleAndVersion(TITLE_ID, VERSION);

        Assert.assertEquals(expectedIsbn, actualIsbn);
        EasyMock.verify(mockDao);
    }
}
