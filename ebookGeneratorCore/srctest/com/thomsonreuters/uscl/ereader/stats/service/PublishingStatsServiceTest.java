package com.thomsonreuters.uscl.ereader.stats.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import com.thomsonreuters.uscl.ereader.core.book.dao.EbookAuditDao;
import com.thomsonreuters.uscl.ereader.core.book.domain.BookDefinition;
import com.thomsonreuters.uscl.ereader.core.book.domain.EbookAudit;
import com.thomsonreuters.uscl.ereader.core.book.model.Version;
import com.thomsonreuters.uscl.ereader.core.book.service.BookDefinitionService;
import com.thomsonreuters.uscl.ereader.core.book.service.EBookAuditService;
import com.thomsonreuters.uscl.ereader.deliver.service.ProviewHandler;
import com.thomsonreuters.uscl.ereader.deliver.service.ProviewTitleContainer;
import com.thomsonreuters.uscl.ereader.deliver.service.ProviewTitleInfo;
import com.thomsonreuters.uscl.ereader.stats.dao.PublishingStatsDao;
import com.thomsonreuters.uscl.ereader.stats.domain.PublishingStats;
import com.thomsonreuters.uscl.ereader.stats.util.PublishingStatsUtil;
import lombok.SneakyThrows;
import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public final class PublishingStatsServiceTest {
    private static final Long BOOK_DEFINITION_ID = 1L;
    private static final String TITLE_ID = "uscl/an/book";
    private static final int MAJOR_VERSION = 1;
    private static final String VERSION = "v" + MAJOR_VERSION + ".0";
    private static final String ISBN = "22-00-77";
    private List<PublishingStats> STATS = new ArrayList<>();
    private Set<String> isbns;
    private String versionWithoutPrefix;

    private PublishingStatsServiceImpl service;

    private PublishingStatsDao mockDao;
    private PublishingStatsUtil mockUtil;
    private EBookAuditService mockAuditService;
    private BookDefinitionService mockBookDefinitionService;
    private ProviewHandler mockProviewHandler;
    private ProviewTitleContainer titleContainer;
    private ProviewTitleInfo titleInfo;

    @Before
    public void setUp() {
        mockDao = EasyMock.createMock(PublishingStatsDao.class);
        mockUtil = EasyMock.createMock(PublishingStatsUtil.class);
        mockAuditService = EasyMock.createMock(EBookAuditService.class);
        mockBookDefinitionService = EasyMock.createMock(BookDefinitionService.class);
        mockProviewHandler = EasyMock.createMock(ProviewHandler.class);

        service = new PublishingStatsServiceImpl(mockDao, mockUtil, mockAuditService,
            mockBookDefinitionService,mockProviewHandler);

        titleContainer = EasyMock.createMock(ProviewTitleContainer.class);
        titleInfo = EasyMock.createMock(ProviewTitleInfo.class);

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

        versionWithoutPrefix = new Version(VERSION).getVersionWithoutPrefix();
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
    public void testHasIsbnBeenPublishedWhenWasPublished() {
        EasyMock.expect(mockDao.findSuccessfullyPublishedIsbns()).andReturn(isbns);
        EasyMock.replay(mockDao);

        final String isbn = "123";
        final Boolean hasBeenPublished = service.hasIsbnBeenPublished(isbn);
        Assert.assertEquals(true, hasBeenPublished);
        EasyMock.verify(mockDao);
    }

    @Test
    public void testHasIsbnBeenPublishedWhenWasNotPublished() {
        EasyMock.expect(mockDao.findSuccessfullyPublishedIsbns()).andReturn(isbns);
        EasyMock.replay(mockDao);

        final String isbn = "1";
        final Boolean hasBeenPublished = service.hasIsbnBeenPublished(isbn);
        Assert.assertEquals(false, hasBeenPublished);
        EasyMock.verify(mockDao);
    }

    @Test
    public void testDeleteIsbnLastVersion() {
        EbookAudit audit = EasyMock.createMock(EbookAudit.class);
        BookDefinition book = EasyMock.createMock(BookDefinition.class);

        EasyMock.expect(mockDao.findSuccessfullyPublishedIsbnByTitleIdAndVersion(TITLE_ID, versionWithoutPrefix))
            .andReturn(ISBN);
        recordIsLastVersion();
        EasyMock.expect(mockAuditService.modifyIsbn(TITLE_ID, ISBN, EbookAuditDao.DELETE_ISBN_TEXT))
            .andReturn(Optional.of(audit));
        recordRestoreIsbn(audit, book);

        EasyMock.replay(mockDao, mockProviewHandler, titleContainer, titleInfo, mockAuditService,
            mockBookDefinitionService, audit, book);

        service.deleteIsbn(TITLE_ID, VERSION);

        EasyMock.verify(mockDao, mockProviewHandler, titleContainer, titleInfo, mockAuditService,
            mockBookDefinitionService, audit, book);
    }

    @SneakyThrows
    @Test
    public void testDeleteIsbnNotLastVersion() {
        EasyMock.expect(mockDao.findSuccessfullyPublishedIsbnByTitleIdAndVersion(TITLE_ID, versionWithoutPrefix))
            .andReturn(ISBN);
        EasyMock.expect(mockProviewHandler.getProviewTitleContainer(TITLE_ID)).andReturn(titleContainer);
        EasyMock.expect(titleContainer.getAllMajorVersions()).andReturn(Collections.singletonList(titleInfo));
        EasyMock.expect(titleInfo.getMajorVersion()).andReturn(MAJOR_VERSION);
        EasyMock.replay(mockDao,mockProviewHandler, titleContainer, titleInfo, mockAuditService);

        service.deleteIsbn(TITLE_ID, VERSION);

        EasyMock.verify(mockDao, mockProviewHandler, titleContainer, titleInfo, mockAuditService);
    }

    @Test
    public void testDeleteIsbnModified() {
        String isbn = EbookAuditDao.MODIFY_ISBN_TEXT + ISBN;
        String version = new Version(VERSION).getVersionWithoutPrefix();
        EasyMock.expect(mockDao.findSuccessfullyPublishedIsbnByTitleIdAndVersion(TITLE_ID, version)).andReturn(isbn);
        EasyMock.replay(mockDao, mockProviewHandler, mockAuditService);

        service.deleteIsbn(TITLE_ID, VERSION);

        EasyMock.verify(mockDao, mockProviewHandler, mockAuditService);
    }

    @Test
    public void testDeleteIsbnDeleted() {
        String isbn = EbookAuditDao.DELETE_ISBN_TEXT+ ISBN;
        String version = new Version(VERSION).getVersionWithoutPrefix();
        EasyMock.expect(mockDao.findSuccessfullyPublishedIsbnByTitleIdAndVersion(TITLE_ID, version)).andReturn(isbn);
        EasyMock.replay(mockDao, mockProviewHandler, mockAuditService);

        service.deleteIsbn(TITLE_ID, VERSION);

        EasyMock.verify(mockDao, mockProviewHandler, mockAuditService);
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

    @SneakyThrows
    private void recordIsLastVersion() {
        EasyMock.expect(mockProviewHandler.getProviewTitleContainer(TITLE_ID)).andReturn(titleContainer);
        EasyMock.expect(titleContainer.getAllMajorVersions()).andReturn(Collections.singletonList(titleInfo));
        EasyMock.expect(titleInfo.getMajorVersion()).andReturn(MAJOR_VERSION + 3);
    }

    private void recordRestoreIsbn(EbookAudit audit, BookDefinition book) {
        EasyMock.expect(audit.getEbookDefinitionId()).andReturn(1L);
        EasyMock.expect(mockBookDefinitionService.findBookDefinitionByEbookDefId(EasyMock.anyLong())).andReturn(book);
        mockAuditService.restoreIsbn(book, PublishingStatsServiceImpl.AUTOMATIC_UPDATE);
    }
}
