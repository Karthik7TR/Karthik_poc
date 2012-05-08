package com.thomsonreuters.uscl.ereader.core.library.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import junit.framework.Assert;

import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.explore.JobExplorer;

import com.thomsonreuters.uscl.ereader.core.job.dao.JobDao;
import com.thomsonreuters.uscl.ereader.core.job.domain.JobFilter;
import com.thomsonreuters.uscl.ereader.core.job.domain.JobSort;
import com.thomsonreuters.uscl.ereader.core.library.dao.LibraryListDao;
import com.thomsonreuters.uscl.ereader.core.library.domain.LibraryList;

public class LibraryListServiceTest {
	private static final List<LibraryList> EXPECTED_LIBRARY_LIST = new ArrayList<LibraryList>();
	private static final Long EXPECTED_NUMBER_BOOKS = 1L;
	private LibraryListDao mockDao;
	private LibraryListServiceImpl LibraryListService;
	
	
	@Before
	public void setUp() {
		this.mockDao = EasyMock.createMock(LibraryListDao.class);
		this.LibraryListService = new LibraryListServiceImpl();
		LibraryListService.setLibraryListDao(mockDao);
	}

	@Test
	public void testFindBookDefinitions() {
		EasyMock.expect(mockDao.findBookDefinitions("SORT", true, 1, 1)).andReturn(new ArrayList<LibraryList>());
		EasyMock.replay(mockDao);
		
		List<LibraryList> actualList = LibraryListService.findBookDefinitions("SORT", true, 1, 1);
		Assert.assertNotNull(actualList);
		Assert.assertEquals(EXPECTED_LIBRARY_LIST, actualList);
		
		EasyMock.verify(mockDao);
	}
	
	@Test
	public void testCountNumberOfBookDefinitions() {
		EasyMock.expect(mockDao.countNumberOfBookDefinitions()).andReturn(EXPECTED_NUMBER_BOOKS);
		EasyMock.replay(mockDao);
		
		Long actual = LibraryListService.countNumberOfBookDefinitions();
		Assert.assertEquals(EXPECTED_NUMBER_BOOKS, actual);
		
		EasyMock.verify(mockDao);
	}
	

}
