package com.thomsonreuters.uscl.ereader.core.library.dao;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import junit.framework.Assert;

import org.easymock.EasyMock;
import org.hibernate.SessionFactory;
import org.junit.Before;
import org.junit.Test;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.jdbc.core.JdbcTemplate;

import com.thomsonreuters.uscl.ereader.core.book.dao.BookDefinitionDaoImpl;
import com.thomsonreuters.uscl.ereader.core.job.dao.JobDao;
import com.thomsonreuters.uscl.ereader.core.job.domain.JobFilter;
import com.thomsonreuters.uscl.ereader.core.job.domain.JobSort;
import com.thomsonreuters.uscl.ereader.core.library.domain.LibraryList;
import com.thomsonreuters.uscl.ereader.core.library.service.LibraryListServiceImpl;

public class LibraryListDaoTest {
	private static final List<LibraryList> EXPECTED_LIBRARY_LIST = new ArrayList<LibraryList>();
	private static final Long EXPECTED_NUMBER_BOOKS = 1L;
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
		EasyMock.expect(mockJdbcTemplate.query(EasyMock.anyObject(String.class), EasyMock.anyObject(LibraryListRowMapper.class))).andReturn(EXPECTED_LIBRARY_LIST);
		EasyMock.replay(mockJdbcTemplate);
		
		List<LibraryList> actualList = dao.findBookDefinitions("sort", true, 1, 1);
		Assert.assertNotNull(actualList);
		Assert.assertEquals(EXPECTED_LIBRARY_LIST, actualList);
		
		EasyMock.verify(mockJdbcTemplate);
	}
	
	@Test
	public void testCountNumberOfBookDefinitions() {
		EasyMock.expect(mockJdbcTemplate.queryForLong("SELECT COUNT (book.EBOOK_DEFINITION_ID) FROM EBOOK_DEFINITION book")).andReturn(EXPECTED_NUMBER_BOOKS);
		EasyMock.replay(mockJdbcTemplate);
		
		Long actual = dao.countNumberOfBookDefinitions();
		
		Assert.assertEquals(EXPECTED_NUMBER_BOOKS, actual);
		
		EasyMock.verify(mockJdbcTemplate);
	}
	
}
