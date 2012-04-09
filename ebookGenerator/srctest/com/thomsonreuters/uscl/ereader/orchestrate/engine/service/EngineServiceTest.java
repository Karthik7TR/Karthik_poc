/*
 * Copyright 2011: Thomson Reuters Global Resources. All Rights Reserved.
 * Proprietary and Confidential information of TRGR. Disclosure, Use or
 * Reproduction without the written authorization of TRGR is prohibited
 */
package com.thomsonreuters.uscl.ereader.orchestrate.engine.service;

import static org.junit.Assert.assertEquals;

import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.configuration.JobRegistry;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.JobOperator;

import com.thomsonreuters.uscl.ereader.JobParameterKey;
import com.thomsonreuters.uscl.ereader.core.book.domain.BookDefinition;
import com.thomsonreuters.uscl.ereader.core.job.domain.JobRequest;
import com.thomsonreuters.uscl.ereader.orchestrate.core.JobRunRequest;
import com.thomsonreuters.uscl.ereader.orchestrate.engine.dao.EngineDao;

public class EngineServiceTest  {
	private static final String TITLE_ID = "titleId";
	private static final String FULLY_QUALIFIED_TITLE_ID = "a/b/c/d/e/f/"+TITLE_ID;
	private static Long BOOK_DEFINITION_ID = 101L;
	private static BookDefinition BOOK_DEFINITION;
	static {
		BOOK_DEFINITION = new BookDefinition();
		BOOK_DEFINITION.setEbookDefinitionId(BOOK_DEFINITION_ID);
	}
	private static String VERSION ="testVersion";
	private static int PRIORITY = 1;
	private static String SUBMITTED_BY ="testSubmittedBy";
	private static JobRequest JOB_REQUEST =	JobRequest.createQueuedJobRequest(BOOK_DEFINITION, VERSION, PRIORITY, SUBMITTED_BY);

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
		this.expectedBookDefinition = EasyMock.createMock(BookDefinition.class);
		this.expectedBookDefinition.setFullyQualifiedTitleId(FULLY_QUALIFIED_TITLE_ID);
		this.service = new EngineServiceImpl();
		service.setJobLauncher(mockJobLauncher);
		service.setJobOperator(mockJobOperator);
		service.setJobRegistry(mockJobRegistry);
	}
	
	@Test
	public void testCreateDynamicJobParameters() {
		JobParameters dynamicJobParams = service.createDynamicJobParameters(JOB_REQUEST);
		assertEquals(SUBMITTED_BY, dynamicJobParams.getString(JobParameterKey.USER_NAME));
		//assertEquals(USER_EMAIL, dynamicJobParams.getString(JobParameterKey.USER_EMAIL));
		Assert.assertNotNull(dynamicJobParams.getDate(JobParameterKey.JOB_TIMESTAMP));
	}

	@Test
	public void testRunJob() throws Exception {
		JobParameters allJobParams = service.createDynamicJobParameters(JOB_REQUEST);

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
