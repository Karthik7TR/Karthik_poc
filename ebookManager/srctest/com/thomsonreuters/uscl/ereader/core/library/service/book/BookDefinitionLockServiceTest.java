package com.thomsonreuters.uscl.ereader.core.library.service.book;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.thomsonreuters.uscl.ereader.core.book.dao.BookDefinitionLockDao;
import com.thomsonreuters.uscl.ereader.core.book.domain.BookDefinition;
import com.thomsonreuters.uscl.ereader.core.book.domain.BookDefinitionLock;
import com.thomsonreuters.uscl.ereader.mgr.web.service.book.BookDefinitionLockServiceImpl;
import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public final class BookDefinitionLockServiceTest {
    private static final BookDefinitionLock BOOK_LOCK = new BookDefinitionLock();
    private List<BookDefinitionLock> BOOK_LOCK_LIST;

    private BookDefinitionLockDao mockDao;
    private BookDefinitionLockServiceImpl service;

    @Before
    public void setUp() {
        mockDao = EasyMock.createMock(BookDefinitionLockDao.class);

        service = new BookDefinitionLockServiceImpl(mockDao);

        BOOK_LOCK_LIST = new ArrayList<>();
    }

    @Test
    public void testFindBookLockByBookDefinitionNoLocksFound() {
        final BookDefinition book = new BookDefinition();

        EasyMock.expect(mockDao.findLocksByBookDefinition(book)).andReturn(BOOK_LOCK_LIST);
        EasyMock.replay(mockDao);

        final BookDefinitionLock lock = service.findActiveBookLock(book);
        Assert.assertEquals(null, lock);

        EasyMock.verify(mockDao);
    }

    @Test
    public void testFindBookLockByBookDefinitionLocksFound() {
        final BookDefinition book = new BookDefinition();
        final BookDefinitionLock expectedLock = new BookDefinitionLock();
        prepareLock(book, expectedLock);
        EasyMock.replay(mockDao);

        final BookDefinitionLock actualLock = service.findActiveBookLock(book);
        Assert.assertEquals(expectedLock, actualLock);

        EasyMock.verify(mockDao);
    }

    @Test
    public void testFindAllActiveLocks() {
        EasyMock.expect(mockDao.findAllActiveLocks()).andReturn(BOOK_LOCK_LIST);
        EasyMock.replay(mockDao);

        final List<BookDefinitionLock> locks = service.findAllActiveLocks();
        Assert.assertEquals(BOOK_LOCK_LIST, locks);

        EasyMock.verify(mockDao);
    }

    @Test
    public void testFindBookDefinitionLockByPrimaryKey() {
        EasyMock.expect(mockDao.findBookDefinitionLockByPrimaryKey(EasyMock.anyLong())).andReturn(BOOK_LOCK);
        EasyMock.replay(mockDao);

        final BookDefinitionLock lock = service.findBookDefinitionLockByPrimaryKey(1L);
        Assert.assertEquals(BOOK_LOCK, lock);

        EasyMock.verify(mockDao);
    }

    @Test
    public void testExtendLock() {
        //given
        final BookDefinition book = new BookDefinition();
        final BookDefinitionLock expectedLock = new BookDefinitionLock();
        prepareLock(book, expectedLock);
        mockDao.extendLock(expectedLock);
        EasyMock.expectLastCall();
        EasyMock.replay(mockDao);
        //when
        service.extendLock(book);
        //then
        EasyMock.verify(mockDao);
    }

    private void prepareLock(final BookDefinition book, final BookDefinitionLock expectedLock) {
        expectedLock.setCheckoutTimestamp(new Date());
        BOOK_LOCK_LIST.add(expectedLock);
        EasyMock.expect(mockDao.findLocksByBookDefinition(book)).andReturn(BOOK_LOCK_LIST);
    }
}
