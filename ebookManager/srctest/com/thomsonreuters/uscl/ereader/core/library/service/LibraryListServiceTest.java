package com.thomsonreuters.uscl.ereader.core.library.service;

import java.util.ArrayList;
import java.util.List;

import com.thomsonreuters.uscl.ereader.mgr.library.dao.LibraryListDao;
import com.thomsonreuters.uscl.ereader.mgr.library.service.LibraryListServiceImpl;
import com.thomsonreuters.uscl.ereader.mgr.library.vdo.LibraryList;
import com.thomsonreuters.uscl.ereader.mgr.library.vdo.LibraryListFilter;
import com.thomsonreuters.uscl.ereader.mgr.library.vdo.LibraryListSort;
import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public final class LibraryListServiceTest {
    private static final List<LibraryList> EXPECTED_LIBRARY_LIST = new ArrayList<LibraryList>();
    private static final Integer EXPECTED_NUMBER_BOOKS = 1;
    private LibraryListDao mockDao;
    private LibraryListServiceImpl LibraryListService;

    @Before
    public void setUp() {
        mockDao = EasyMock.createMock(LibraryListDao.class);
        LibraryListService = new LibraryListServiceImpl();
        LibraryListService.setLibraryListDao(mockDao);
    }

    @Test
    public void testFindBookDefinitions() {
        final LibraryListFilter filter = new LibraryListFilter();
        final LibraryListSort sort = new LibraryListSort();

        EasyMock.expect(mockDao.findBookDefinitions(filter, sort)).andReturn(new ArrayList<LibraryList>());
        EasyMock.replay(mockDao);

        final List<LibraryList> actualList = LibraryListService.findBookDefinitions(filter, sort);
        Assert.assertNotNull(actualList);
        Assert.assertEquals(EXPECTED_LIBRARY_LIST, actualList);

        EasyMock.verify(mockDao);
    }

    @Test
    public void testCountNumberOfBookDefinitions() {
        final LibraryListFilter filter = new LibraryListFilter();
        EasyMock.expect(mockDao.numberOfBookDefinitions(filter)).andReturn(EXPECTED_NUMBER_BOOKS);
        EasyMock.replay(mockDao);

        final Integer actual = LibraryListService.numberOfBookDefinitions(filter);
        Assert.assertEquals(EXPECTED_NUMBER_BOOKS, actual);

        EasyMock.verify(mockDao);
    }
}
