package com.thomsonreuters.uscl.ereader.core.library.dao;

import java.util.ArrayList;
import java.util.List;

import com.thomsonreuters.uscl.ereader.mgr.library.dao.LibraryListDaoImpl;
import com.thomsonreuters.uscl.ereader.mgr.library.dao.LibraryListRowMapper;
import com.thomsonreuters.uscl.ereader.mgr.library.vdo.LibraryList;
import com.thomsonreuters.uscl.ereader.mgr.library.vdo.LibraryListFilter;
import com.thomsonreuters.uscl.ereader.mgr.library.vdo.LibraryListSort;
import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

public final class LibraryListDaoTest {
    private static final List<LibraryList> EXPECTED_LIBRARY_LIST = new ArrayList<>();
    private static final Integer EXPECTED_NUMBER_BOOKS = 1;
    private LibraryListDaoImpl dao;
    private JdbcTemplate mockJdbcTemplate;
    private RowMapper<LibraryList> libraryListMapper;

    @Before
    public void setUp() {
        mockJdbcTemplate = EasyMock.createMock(JdbcTemplate.class);
        libraryListMapper = EasyMock.createMock(RowMapper.class);
        dao = new LibraryListDaoImpl(libraryListMapper, mockJdbcTemplate);
    }

    @Test
    public void testFindBookDefinitions() {
        final LibraryListFilter filter = new LibraryListFilter();
        final LibraryListSort sort = new LibraryListSort();

        EasyMock
            .expect(
                mockJdbcTemplate.query(
                    EasyMock.anyObject(String.class),
                    EasyMock.anyObject(LibraryListRowMapper.class),
                    new Object[] {}))
            .andReturn(EXPECTED_LIBRARY_LIST);
        EasyMock.replay(mockJdbcTemplate);

        final List<LibraryList> actualList = dao.findBookDefinitions(filter, sort);
        Assert.assertNotNull(actualList);
        Assert.assertEquals(EXPECTED_LIBRARY_LIST, actualList);

        EasyMock.verify(mockJdbcTemplate);
    }

    @Test
    public void testCountNumberOfBookDefinitions() {
        final LibraryListFilter filter = new LibraryListFilter();

        EasyMock
            .expect(
                mockJdbcTemplate.queryForObject(
                    EasyMock.anyObject(String.class),
                    EasyMock.anyObject(Object[].class),
                    EasyMock.anyObject(String.class.getClass())))
            .andReturn(EXPECTED_NUMBER_BOOKS);
        EasyMock.replay(mockJdbcTemplate);

        final Integer actual = dao.numberOfBookDefinitions(filter);

        Assert.assertEquals(EXPECTED_NUMBER_BOOKS, actual);

        EasyMock.verify(mockJdbcTemplate);
    }
}
