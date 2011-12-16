/*
 * Copyright 2011: Thomson Reuters Global Resources. All Rights Reserved.
 * Proprietary and Confidential information of TRGR. Disclosure, Use or
 * Reproduction without the written authorization of TRGR is prohibited
 */
package com.thomsonreuters.uscl.ereader.orchestrate.engine.service;

import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.configuration.JobRegistry;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.JobOperator;

import com.thomsonreuters.uscl.ereader.JobParameterKey;
import com.thomsonreuters.uscl.ereader.orchestrate.core.BookDefinition;
import com.thomsonreuters.uscl.ereader.orchestrate.core.BookDefinitionKey;
import com.thomsonreuters.uscl.ereader.orchestrate.core.JobRunRequest;
import com.thomsonreuters.uscl.ereader.orchestrate.engine.dao.EngineDao;

public class EngineServiceTest  {
	private static final BookDefinitionKey BOOK_KEY = new BookDefinitionKey("titleId", 1234l);
	private static String BOOK_NAME = "Junit book name";
	private static String USER_NAME = "theUserName";
	private static String USER_EMAIL = "theUserEmail";
	private static JobRunRequest JOB_RUN_REQUEST = JobRunRequest.create(BOOK_KEY, USER_NAME, USER_EMAIL);

	private EngineServiceImpl service;
	
	private EngineDao mockEngineDao;
	private JobLauncher mockJobLauncher;
	private JobOperator mockJobOperator;
	private JobRegistry mockJobRegistry;
	private JobExecution mockJobExecution;
	private Job mockJob;
	private BookDefinition expectedBookDefinition;
	
	@Before
	public void setUp() throws Exception {
		
		this.mockEngineDao = EasyMock.createMock(EngineDao.class);
		this.mockJobLauncher = EasyMock.createMock(JobLauncher.class);
		this.mockJobOperator = EasyMock.createMock(JobOperator.class);
		this.mockJobRegistry = EasyMock.createMock(JobRegistry.class);
		this.mockJobExecution = EasyMock.createMock(JobExecution.class);
		this.mockJob = EasyMock.createMock(Job.class);
		
		// Set up an expected book definition entity
		this.expectedBookDefinition = new BookDefinition();
		expectedBookDefinition.setBookDefinitionKey(BOOK_KEY);
		expectedBookDefinition.setName(BOOK_NAME);
		
		this.service = new EngineServiceImpl();
		service.setJobLauncher(mockJobLauncher);
		service.setJobOperator(mockJobOperator);
		service.setJobRegistry(mockJobRegistry);
	}
	
	@Test
	public void testCreateBookDefinitionJobParameters() {
		JobParameters jobParams = service.createBookDefinitionJobParameters(expectedBookDefinition);
		Assert.assertEquals(BOOK_NAME, jobParams.getString(JobParameterKey.BOOK_NAME));
		Assert.assertEquals(BOOK_KEY.getBookTitleId(), jobParams.getString(JobParameterKey.BOOK_TITLE_ID));
	}

	@Test
	public void testCreateDynamicJobParameters() {
		JobParameters dynamicJobParams = service.createDynamicJobParameters(JOB_RUN_REQUEST);
		Assert.assertEquals(USER_NAME, dynamicJobParams.getString(JobParameterKey.USER_NAME));
		Assert.assertEquals(USER_EMAIL, dynamicJobParams.getString(JobParameterKey.USER_EMAIL));
		Assert.assertNotNull(dynamicJobParams.getLong(JobParameterKey.JOB_TIMESTAMP));
	}

	@Test
	public void testRunJob() throws Exception {
		JobParameters allJobParams = service.createDynamicJobParameters(JOB_RUN_REQUEST);

		EasyMock.expect(mockJobLauncher.run(mockJob, allJobParams)).andReturn(mockJobExecution);
		EasyMock.expect(mockJobRegistry.getJob(JobRunRequest.JOB_NAME_CREATE_EBOOK)).andReturn(mockJob);
		
		EasyMock.replay(mockEngineDao);
		EasyMock.replay(mockJobLauncher);
		EasyMock.replay(mockJob);
		EasyMock.replay(mockJobRegistry);
		
		try {
			JobExecution jobExecution = service.runJob(JobRunRequest.JOB_NAME_CREATE_EBOOK, allJobParams);
			Assert.assertNotNull(jobExecution);
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail("Running job should not have thrown an exception");
		}
		EasyMock.verify(mockEngineDao);
		EasyMock.verify(mockJobLauncher);
		EasyMock.verify(mockJob);
		EasyMock.verify(mockJobRegistry);
	}

	@Test
	public void testRestartJob() {

		try {
			Long jobExecutionId = new Long(1234);
			EasyMock.expect(mockJobOperator.restart(jobExecutionId)).andReturn(jobExecutionId);
			EasyMock.replay(mockJobOperator);
			
			Long restartedId = service.restartJob(jobExecutionId);
			Assert.assertEquals(jobExecutionId, restartedId);
			Assert.assertTrue(true); // expected
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail("Restarting job should not have thrown an exception");
		}
		EasyMock.verify(mockJobOperator);
	}

	@Test
	public void testStopJob() {
		try {
			Long jobExecutionId = new Long(1234);
			EasyMock.expect(mockJobOperator.stop(jobExecutionId)).andReturn(true);
			EasyMock.replay(mockJobOperator);
			
			service.stopJob(jobExecutionId);
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail("Stopping job should not have thrown an exception");
		}
		EasyMock.verify(mockJobOperator);
	}
}
