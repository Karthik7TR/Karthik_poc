package com.thomsonreuters.uscl.ereader.core.book.service;

import com.thomsonreuters.uscl.ereader.core.book.dao.BookDefinitionDao;
import com.thomsonreuters.uscl.ereader.core.book.domain.BookDefinition;
import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public final class BookDefinitionServiceTest {
    private static final String BOOK_KEY = "titleId";

    private BookDefinitionServiceImpl service;

    private BookDefinitionDao bookDefinitionDao;
    private BookDefinition expectedBookDefinition = new BookDefinition();

    @Before
    public void setUp() {
        bookDefinitionDao = EasyMock.createMock(BookDefinitionDao.class);

        service = new BookDefinitionServiceImpl();
        service.setBookDefinitionDao(bookDefinitionDao);

        expectedBookDefinition.setFullyQualifiedTitleId(BOOK_KEY);
    }

    @Test
    public void testFindBookDefinition() {
        EasyMock.expect(bookDefinitionDao.findBookDefinitionByTitle(BOOK_KEY)).andReturn(expectedBookDefinition);
        EasyMock.replay(bookDefinitionDao);
        final BookDefinition actualBookDefinition = service.findBookDefinitionByTitle(BOOK_KEY);
        Assert.assertEquals(expectedBookDefinition, actualBookDefinition);
        EasyMock.verify(bookDefinitionDao);
    }

    @Test
    public void testSaveBookDefinition() {
        EasyMock.expect(bookDefinitionDao.saveBookDefinition(expectedBookDefinition)).andReturn(expectedBookDefinition);
        EasyMock.replay(bookDefinitionDao);

        final BookDefinition book = service.saveBookDefinition(expectedBookDefinition);
        Assert.assertEquals(expectedBookDefinition, book);
        EasyMock.verify(bookDefinitionDao);
    }
}
