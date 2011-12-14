/*
 * Copyright 2011: Thomson Reuters Global Resources. All Rights Reserved.
 * Proprietary and Confidential information of TRGR. Disclosure, Use or
 * Reproduction without the written authorization of TRGR is prohibited
 */
package com.thomsonreuters.uscl.ereader.orchestrate.engine.service;

import static org.junit.Assert.assertEquals;

import java.util.HashMap;
import java.util.Map;

import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameter;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.configuration.JobRegistry;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.JobOperator;

import com.thomsonreuters.uscl.ereader.orchestrate.core.JobRunRequest;
import com.thomsonreuters.uscl.ereader.orchestrate.core.engine.EngineConstants;
import com.thomsonreuters.uscl.ereader.orchestrate.engine.dao.EngineDao;

public class EngineServiceTest  {
	private static String myIntKey = "myIntKey";
	private static Long myIntValue = Long.MAX_VALUE;
	private static String myStrKey = "myStrKey";
	private static String myStrValue = "myStrValue";
	private static String BOOK_CODE = "theBookCode";
	private static String BOOK_TITLE = "Junit book title";
	private static String USER_NAME = "theUserName";
	private static String USER_EMAIL = "theUserEmail";
	private static JobRunRequest JOB_RUN_REQUEST = JobRunRequest.create(BOOK_CODE, USER_NAME, USER_EMAIL);

	private EngineServiceImpl service;
	private JobParameters databaseParams;
	
	private EngineDao mockDao;
	private JobLauncher mockJobLauncher;
	private JobOperator mockJobOperator;
	private JobRegistry mockJobRegistry;
	private JobExecution mockJobExecution;
	private Job mockJob;
	
	@Before
	public void setUp() throws Exception {
		
		Map<String,JobParameter> paramMap = new HashMap<String,JobParameter>();
		paramMap.put(EngineConstants.JOB_PARAM_BOOK_TITLE, new JobParameter(BOOK_TITLE));
		paramMap.put(myIntKey, new JobParameter(myIntValue));
		paramMap.put(myStrKey, new JobParameter(myStrValue));
		this.databaseParams = new JobParameters(paramMap);

		this.mockDao = EasyMock.createMock(EngineDao.class);
		this.mockJobLauncher = EasyMock.createMock(JobLauncher.class);
		this.mockJobOperator = EasyMock.createMock(JobOperator.class);
		this.mockJobRegistry = EasyMock.createMock(JobRegistry.class);
		this.mockJobExecution = EasyMock.createMock(JobExecution.class);
		this.mockJob = EasyMock.createMock(Job.class);
		
		this.service = new EngineServiceImpl();
		service.setDao(mockDao);
		service.setJobLauncher(mockJobLauncher);
		service.setJobOperator(mockJobOperator);
		service.setJobRegistry(mockJobRegistry);
	}
	@Test
	public void testCreateCombinedJobParameters() {	
		JobParameters combinedJobParams = EngineServiceImpl.createCombinedJobParameters(JOB_RUN_REQUEST, databaseParams);
		assertEquals(myIntValue, (Long) combinedJobParams.getLong(myIntKey));
		assertEquals(myStrValue, combinedJobParams.getString(myStrKey));
		assertEquals(BOOK_CODE, combinedJobParams.getString(EngineConstants.JOB_PARAM_BOOK_CODE));
		assertEquals(BOOK_TITLE, combinedJobParams.getString(EngineConstants.JOB_PARAM_BOOK_TITLE));
		assertEquals(USER_NAME, combinedJobParams.getString(EngineConstants.JOB_PARAM_USER_NAME));
		assertEquals(USER_EMAIL, combinedJobParams.getString(EngineConstants.JOB_PARAM_USER_EMAIL));
	}

	//@Test
	public void testRunJob() throws Exception {
		JobParameters jobParams = EngineServiceImpl.createCombinedJobParameters(JOB_RUN_REQUEST, databaseParams);

		EasyMock.expect(mockDao.loadJobParameters(EngineConstants.JOB_DEFINITION_EBOOK)).andReturn(jobParams);
		EasyMock.expect(mockJobLauncher.run(mockJob, jobParams)).andReturn(mockJobExecution);
		EasyMock.expect(mockJobRegistry.getJob(EngineConstants.JOB_DEFINITION_EBOOK)).andReturn(mockJob);
		
		EasyMock.replay(mockDao);
		EasyMock.replay(mockJobLauncher);
		EasyMock.replay(mockJob);
		EasyMock.replay(mockJobRegistry);
		
		try {
			JobExecution jobExecution = service.runJob(JOB_RUN_REQUEST);
			Assert.assertNotNull(jobExecution);
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail("Running job should not have thrown an exception");
		}
		EasyMock.verify(mockDao);
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
