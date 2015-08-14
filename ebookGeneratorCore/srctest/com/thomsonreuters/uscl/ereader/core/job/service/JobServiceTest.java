package com.thomsonreuters.uscl.ereader.core.job.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Assert;

import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.explore.JobExplorer;

import com.thomsonreuters.uscl.ereader.core.job.dao.JobDao;
import com.thomsonreuters.uscl.ereader.core.job.domain.JobFilter;
import com.thomsonreuters.uscl.ereader.core.job.domain.JobSort;

public class JobServiceTest {
	private static final Long JOB_EXECUTION_ID = 100l;
	private static final JobExecution EXPECTED_JOB_EXECUTION = new JobExecution(JOB_EXECUTION_ID);
	private JobExplorer mockJobExplorer;
	private JobDao mockJobDao;
	private JobServiceImpl jobService;
	
	
	@Before
	public void setUp() {
		this.mockJobExplorer = EasyMock.createMock(JobExplorer.class);
		this.mockJobDao = EasyMock.createMock(JobDao.class);
		this.jobService = new JobServiceImpl();
		jobService.setJobExplorer(mockJobExplorer);
		jobService.setJobDao(mockJobDao);
	}

	@Test
	public void testFindJobExecutionByPrimaryKey() {
		EasyMock.expect(mockJobExplorer.getJobExecution(JOB_EXECUTION_ID)).andReturn(EXPECTED_JOB_EXECUTION);
		EasyMock.replay(mockJobExplorer);
		
		JobExecution actualJobExecution = jobService.findJobExecution(JOB_EXECUTION_ID);
		Assert.assertNotNull(actualJobExecution);
		Assert.assertEquals(EXPECTED_JOB_EXECUTION.getId(), actualJobExecution.getId());
		
		EasyMock.verify(mockJobExplorer);
	}
	
	@Test
	public void testFindJobExecutionsFiltered() {
		int SIZE = 5;
		JobFilter filter = new JobFilter();
		JobSort sort = new JobSort();
		List<Long> listOfLong = new ArrayList<Long>();
		for (int i = 0; i < SIZE; i++) {
			listOfLong.add(new Long(i));
		}
		EasyMock.expect(mockJobDao.findJobExecutions(filter,sort)).andReturn(listOfLong);
		EasyMock.replay(mockJobDao);
		
		List<Long> ids = jobService.findJobExecutions(filter, sort);
		Assert.assertNotNull(ids);
		Assert.assertEquals(SIZE, ids.size());
		
		EasyMock.verify(mockJobDao);
	}
	
	@Test
	public void testFindJobExecutionByPrimaryKeys() {
		int SIZE = 3;
		Long[] ids = new Long[SIZE];
		List<JobExecution> expectedJobExecutions = new ArrayList<JobExecution>();
		for (int i = 0; i < SIZE; i++) {
			ids[i] = new Long(i + 101l);
			expectedJobExecutions.add(new JobExecution(ids[i]));
			EasyMock.expect(mockJobExplorer.getJobExecution(ids[i])).andReturn(expectedJobExecutions.get(i));
		}
		EasyMock.replay(mockJobExplorer);
		
		List<JobExecution> actualJobExecutions = jobService.findJobExecutions(Arrays.asList(ids));
		Assert.assertNotNull(actualJobExecutions);
		Assert.assertEquals(SIZE, actualJobExecutions.size());
		for (int i = 0; i < SIZE; i++) {
			Assert.assertEquals(expectedJobExecutions.get(i).getId(), actualJobExecutions.get(i).getId());
		}
		EasyMock.verify(mockJobExplorer);
	}
}
