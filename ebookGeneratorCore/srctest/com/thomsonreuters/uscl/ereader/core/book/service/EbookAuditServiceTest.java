package com.thomsonreuters.uscl.ereader.core.book.service;

import static com.thomsonreuters.uscl.ereader.core.book.dao.EbookAuditDao.MODIFY_ISBN_TEXT;
import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import com.thomsonreuters.uscl.ereader.core.book.dao.EbookAuditDao;
import com.thomsonreuters.uscl.ereader.core.book.domain.EbookAudit;
import com.thomsonreuters.uscl.ereader.core.book.domain.EbookAuditFilter;
import com.thomsonreuters.uscl.ereader.core.book.domain.EbookAuditSort;
import com.thomsonreuters.uscl.ereader.core.book.domain.EbookAuditSort.SortProperty;
import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;

public final class EbookAuditServiceTest {
    private static final long BOOK_KEY = 1L;
    private static final String TITLE_ID = "book/title";
    private static final String ISBN = "123456789";
    private List<EbookAudit> auditList;
    private EbookAudit expectedAudit;

    private EBookAuditServiceImpl service;

    private EbookAuditDao mockDao;
    private VersionIsbnService mockVersionIsbnService;

    @Before
    public void setUp() {
        mockDao = EasyMock.createMock(EbookAuditDao.class);
        mockVersionIsbnService = EasyMock.createMock(VersionIsbnService.class);

        service = new EBookAuditServiceImpl(mockDao, mockVersionIsbnService);

        auditList = new ArrayList<>();
        expectedAudit = new EbookAudit();
        expectedAudit.setAuditId(BOOK_KEY);
        expectedAudit.setTitleId(TITLE_ID);
        expectedAudit.setIsbn(ISBN);
    }

    @Test
    public void testFindBookDefinition() {
        EasyMock.expect(mockDao.findEbookAuditByPrimaryKey(BOOK_KEY)).andReturn(expectedAudit);
        EasyMock.replay(mockDao);
        final EbookAudit actualAudit = service.findEBookAuditByPrimaryKey(BOOK_KEY);
        assertEquals(expectedAudit, actualAudit);
        EasyMock.verify(mockDao);
    }

    @Test
    public void testSaveBookDefinition() {
        mockDao.saveAudit(expectedAudit);
        EasyMock.replay(mockDao);

        service.saveEBookAudit(expectedAudit);
        EasyMock.verify(mockDao);
    }

    @Test
    public void testFindEbookAudits() {
        final EbookAuditSort sort = new EbookAuditSort(SortProperty.SUBMITTED_DATE, false, 1, 20);
        final EbookAuditFilter filter = new EbookAuditFilter();

        EasyMock.expect(mockDao.findEbookAudits(filter, sort)).andReturn(auditList);
        EasyMock.replay(mockDao);

        final List<EbookAudit> actual = service.findEbookAudits(filter, sort);
        assertEquals(auditList, actual);
        EasyMock.verify(mockDao);
    }

    @Test
    public void findNumberEbookAudits() {
        final int number = 0;
        final EbookAuditFilter filter = new EbookAuditFilter();

        EasyMock.expect(mockDao.numberEbookAudits(filter)).andReturn(number);
        EasyMock.replay(mockDao);

        final int actual = service.numberEbookAudits(filter);
        assertEquals(number, actual);
        EasyMock.verify(mockDao);
    }

    @Test
    public void shouldModifyIsbn() {
        auditList.add(expectedAudit);
        EasyMock.expect(mockDao.findEbookAuditByTitleIdAndIsbn(TITLE_ID, ISBN)).andReturn(auditList);
        mockDao.saveAudit(expectedAudit);
        EasyMock.replay(mockDao);

        final EbookAudit actualAudit = service.modifyIsbn(TITLE_ID, ISBN).get();

        assertEquals(MODIFY_ISBN_TEXT + ISBN, actualAudit.getIsbn());
        EasyMock.verify(mockDao);
    }

    @Test
    public void shouldCheckIsbnModification() {
        //given
        final boolean expectedResult = true;
        EasyMock.expect(mockDao.isIsbnModified(TITLE_ID, ISBN)).andReturn(expectedResult);
        EasyMock.replay(mockDao);
        //when
        final boolean actualResult = service.isIsbnModified(TITLE_ID, ISBN);
        //then
        assertEquals(expectedResult, actualResult);
    }

    @Test
    public void shouldResetIsbn() {
        //given
        expectedAudit.setIsbn(MODIFY_ISBN_TEXT + expectedAudit.getIsbn());
        auditList.add(expectedAudit);
        EasyMock.expect(mockDao.findEbookAuditByTitleIdAndModifiedIsbn(TITLE_ID, ISBN)).andReturn(auditList);
        mockDao.saveAudit(expectedAudit);
        EasyMock.expectLastCall().once();
        EasyMock.replay(mockDao);
        //when
        service.resetIsbn(TITLE_ID, ISBN);
        //then
        EasyMock.verify(mockDao);
        assertEquals(expectedAudit.getIsbn(), ISBN);
    }
}
