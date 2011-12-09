/*
 * Copyright 2011: Thomson Reuters Global Resources. All Rights Reserved.
 * Proprietary and Confidential information of TRGR. Disclosure, Use or
 * Reproduction without the written authorization of TRGR is prohibited
 */
package com.thomsonreuters.uscl.ereader.orchestrate.engine.service;

import java.util.HashMap;
import java.util.Map;

import org.easymock.EasyMock;
import org.junit.Assert;
import static org.junit.Assert.assertEquals;
import org.junit.Before;
import org.junit.Test;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameter;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.configuration.JobRegistry;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.JobOperator;
import org.springframework.beans.factory.annotation.Autowired;

import com.thomsonreuters.uscl.ereader.orchestrate.core.JobRunRequest;
import com.thomsonreuters.uscl.ereader.orchestrate.core.engine.EngineConstants;
import com.thomsonreuters.uscl.ereader.orchestrate.engine.dao.EngineDao;

public class EngineServiceTest  {
	private static String myIntKey = "myIntKey";
	private static Long myIntValue = Long.MAX_VALUE;
	private static String myStrKey = "myStrKey";
	private static String myStrValue = "myStrValue";

	@Autowired
	private EngineServiceImpl service;
	private JobRunRequest jobRunRequest;
	private JobParameters databaseParams;
	
	@Before
	public void setUp() throws Exception {
		EngineDao dao = EasyMock.createMock(EngineDao.class);
		JobLauncher jobLauncher = EasyMock.createMock(JobLauncher.class);
		JobOperator jobOperator = EasyMock.createMock(JobOperator.class);
		JobRegistry jobRegistry = EasyMock.createMock(JobRegistry.class);
		JobExecution jobExecution = EasyMock.createMock(JobExecution.class);
		Job job = EasyMock.createMock(Job.class);
		
		this.jobRunRequest = JobRunRequest.create("bookCode", "bookTitle", "userName", "userEmail");
		Map<String,JobParameter> paramMap = new HashMap<String,JobParameter>();
		paramMap.put(myIntKey, new JobParameter(myIntValue));
		paramMap.put(myStrKey, new JobParameter(myStrValue));
		this.databaseParams = new JobParameters(paramMap);
		JobParameters jobParams = EngineServiceImpl.createCombinedJobParameters(jobRunRequest, databaseParams);
				
		EasyMock.expect(dao.loadJobParameters(EngineConstants.JOB_DEFINITION_EBOOK)).andReturn(jobParams);
		EasyMock.replay(dao);
		EasyMock.expect(job.getName()).andReturn(EngineConstants.JOB_DEFINITION_EBOOK);
		EasyMock.replay(job);
		EasyMock.expect(jobRegistry.getJob(EngineConstants.JOB_DEFINITION_EBOOK)).andReturn(job);
		EasyMock.replay(jobRegistry);
		EasyMock.expect(jobLauncher.run(job, jobParams)).andReturn(jobExecution);
		EasyMock.replay(jobLauncher);
		
		this.service = new EngineServiceImpl();
		service.setDao(dao);
		service.setJobLauncher(jobLauncher);
		service.setJobOperator(jobOperator);
		service.setJobRegistry(jobRegistry);
	}
	@Test
	public void testCreateCombinedJobParameters() {
//		JobRunRequest jobRunRequest = JobRunRequest.create("bookCode", "bookTitle", "userName", "userEmail");
//		Map<String,JobParameter> paramMap = new HashMap<String,JobParameter>();
//		String myIntKey = "myIntKey";
//		Long myIntValue = Long.MAX_VALUE;
//		String myStrKey = "myStrKey";
//		String myStrValue = "myStrValue";
//		paramMap.put(myIntKey, new JobParameter(myIntValue));
//		paramMap.put(myStrKey, new JobParameter(myStrValue));
//		JobParameters databaseParams = new JobParameters(paramMap);
//		
		JobParameters combinedJobParams = EngineServiceImpl.createCombinedJobParameters(jobRunRequest, databaseParams);
		assertEquals(myIntValue, (Long) combinedJobParams.getLong(myIntKey));
		assertEquals(myStrValue, combinedJobParams.getString(myStrKey));
		assertEquals("bookCode", combinedJobParams.getString(EngineConstants.JOB_PARAM_BOOK_CODE));
		assertEquals("bookTitle", combinedJobParams.getString(EngineConstants.JOB_PARAM_BOOK_TITLE));
		assertEquals("userName", combinedJobParams.getString(EngineConstants.JOB_PARAM_USER_NAME));
		assertEquals("userEmail", combinedJobParams.getString(EngineConstants.JOB_PARAM_USER_EMAIL));
	}

	@Test
	public void testRunJob() {
		try {
			JobExecution jobExecution = service.runJob(jobRunRequest);
			Assert.assertNotNull(jobExecution);
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail("Running job should not have thrown an exception");
		}
	}

	@Test
	public void testRestartJob() {
		try {
			long jobExecutionId = 1234;
			service.restartJob(jobExecutionId);
			Assert.assertTrue(true); // expected
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail("Restarting job should not have thrown an exception");
		}
	}

	@Test
	public void testStopJob() {
		try {
			long jobExecutionId = 1234;
			service.stopJob(jobExecutionId);
			Assert.assertTrue(true); // expected
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail("Stopping job should not have thrown an exception");
		}
	}
}
