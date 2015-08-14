package com.thomsonreuters.uscl.ereader.core.library.dao;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;

import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;
import org.springframework.jdbc.core.JdbcTemplate;

import com.thomsonreuters.uscl.ereader.mgr.library.dao.LibraryListDaoImpl;
import com.thomsonreuters.uscl.ereader.mgr.library.dao.LibraryListRowMapper;
import com.thomsonreuters.uscl.ereader.mgr.library.vdo.LibraryList;
import com.thomsonreuters.uscl.ereader.mgr.library.vdo.LibraryListFilter;
import com.thomsonreuters.uscl.ereader.mgr.library.vdo.LibraryListSort;

public class LibraryListDaoTest {
	private static final List<LibraryList> EXPECTED_LIBRARY_LIST = new ArrayList<LibraryList>();
	private static final Integer EXPECTED_NUMBER_BOOKS = 1;
	private LibraryListDaoImpl dao;
	private JdbcTemplate mockJdbcTemplate;
	
	
	@Before
	public void setUp() {
		this.mockJdbcTemplate = EasyMock.createMock(JdbcTemplate.class);
		this.dao = new LibraryListDaoImpl();
		this.dao.setJdbcTemplate(mockJdbcTemplate);

	}

	@Test
	public void testFindBookDefinitions() {
		LibraryListFilter filter = new LibraryListFilter();
		LibraryListSort sort = new LibraryListSort();
		
		EasyMock.expect(mockJdbcTemplate.query(EasyMock.anyObject(String.class), EasyMock.anyObject(LibraryListRowMapper.class), new Object[]{})).andReturn(EXPECTED_LIBRARY_LIST);
		EasyMock.replay(mockJdbcTemplate);
		
		List<LibraryList> actualList = dao.findBookDefinitions(filter, sort);
		Assert.assertNotNull(actualList);
		Assert.assertEquals(EXPECTED_LIBRARY_LIST, actualList);
		
		EasyMock.verify(mockJdbcTemplate);
	}
	
	@Test
	public void testCountNumberOfBookDefinitions() {
		LibraryListFilter filter = new LibraryListFilter();
		
		EasyMock.expect(mockJdbcTemplate.queryForInt(EasyMock.anyObject(String.class), new Object[]{})).andReturn(EXPECTED_NUMBER_BOOKS);
		EasyMock.replay(mockJdbcTemplate);
		
		Integer actual = dao.numberOfBookDefinitions(filter);
		
		Assert.assertEquals(EXPECTED_NUMBER_BOOKS, actual);
		
		EasyMock.verify(mockJdbcTemplate);
	}
	
}
