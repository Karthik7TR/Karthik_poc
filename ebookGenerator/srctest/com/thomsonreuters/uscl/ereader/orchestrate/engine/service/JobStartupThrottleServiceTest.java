/*
 * Copyright 2011: Thomson Reuters Global Resources. All Rights Reserved.
 * Proprietary and Confidential information of TRGR. Disclosure, Use or
 * Reproduction without the written authorization of TRGR is prohibited
 */
package com.thomsonreuters.uscl.ereader.orchestrate.engine.service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobInstance;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.repository.JobRepository;

import com.thomsonreuters.uscl.ereader.core.job.domain.AppConfig;


/**
 * This test class tests JobStartupThrottleService where before starting new job jobRepository is queried to find 
 * how many jobs are currently running and what is current throttle limit. if number of jobs running are more or 
 * equal to throttle limit each running job is verified if they have crossed throttle step then total number of jobs which 
 * have already crossed throttle limit are considered do decide if new job can be launched.
 *   
 *  @author Mahendra Survase (u0105927)
 */
public class JobStartupThrottleServiceTest  {
	
	
	private static final String JOB_NAME ="TestJob";
	private static final String THROTTLE_STEP = "formatAddHTMLWrapper";
	private JobRepository mockJobRepository;
	private JobExplorer mockJobExplorer;

	List<String> jobNames = new ArrayList<String>();
	
	/**
	 * Generic setup for all the tests.
	 * @throws Exception
	 */
	@Before
	public void setUp() throws Exception {
		this.mockJobRepository = EasyMock.createMock(JobRepository.class);
		this.mockJobExplorer = EasyMock.createMock(JobExplorer.class);
		jobNames.add(JOB_NAME);
	}
	
	/**
	 * Tests positive scenario where throttle limit is 4 and current running jobs are 3 
	 * spring batch should be able to launch next job. 
	 * 
	 */
	@Test
	public void checkIfnewJobCanbeLaunched_positive_1(){
		JobStartupThrottleServiceImpl service = new JobStartupThrottleServiceImpl(mockJobExplorer, mockJobRepository,
									AppConfig.createJobThrottleConfig(8, true, THROTTLE_STEP, 6));

		// test specific setup.
		Set<JobExecution> runningJobExecutions = new HashSet<JobExecution>(3); 
		JobExecution jobExecutionTest = new JobExecution(new JobInstance(234l,new JobParameters(),"ThrottleTestJob"));
		runningJobExecutions.add(jobExecutionTest);
		
		EasyMock.expect(mockJobExplorer.getJobNames()).andReturn(jobNames).anyTimes();  
		EasyMock.expect(mockJobExplorer.findRunningJobExecutions(jobNames.get(0))).andReturn(runningJobExecutions); // Already running job # 3 so that there will be capacity to run one more job.

		EasyMock.replay(mockJobExplorer);
		EasyMock.replay(mockJobRepository);

		boolean startUpFlag = service.checkIfnewJobCanbeLaunched();
		EasyMock.verify(mockJobExplorer);
		EasyMock.verify(mockJobRepository);
		Assert.assertTrue(startUpFlag);
		
		
	}
	
	/**
	 * Positive scenario number of current jobs running is 2 and throttle limit is 2 but all the running jobs have crossed 
	 * throttle step. Spring batch should be able to launch next job.  
	 * 
	 */
	@Test
	public void checkIfnewJobCanbeLaunched_positive_2(){
		JobStartupThrottleServiceImpl service = new JobStartupThrottleServiceImpl(mockJobExplorer, mockJobRepository,
									AppConfig.createJobThrottleConfig(8, true, THROTTLE_STEP, 6));
		// test specific setup.
		Set<JobExecution> runningJobExecutions = new HashSet<JobExecution>(2);
		JobExecution jobExecutionTest_1 = new JobExecution(new JobInstance(234l,new JobParameters(),"ThrottleTestJob"));
		JobExecution jobExecutionTest_2 = new JobExecution(new JobInstance(234l,new JobParameters(),"ThrottleTestJob"));
		JobExecution jobExecutionTest_3 = new JobExecution(new JobInstance(234l,new JobParameters(),"ThrottleTestJob"));
		
		runningJobExecutions.add(jobExecutionTest_1);
		runningJobExecutions.add(jobExecutionTest_2);
		runningJobExecutions.add(jobExecutionTest_3);
		JobExecution jobExecutionOuter = null;
		long longJobId = 3343l;
		for (JobExecution jobExecution : runningJobExecutions) {
			jobExecutionOuter = jobExecution ;
		}
		JobInstance jobInstance = jobExecutionOuter.getJobInstance();
		StepExecution stepExecution  = new StepExecution(THROTTLE_STEP, new JobExecution(longJobId));
		
		
		EasyMock.expect(mockJobExplorer.getJobNames()).andReturn(jobNames).anyTimes();  
		EasyMock.expect(mockJobExplorer.findRunningJobExecutions(jobNames.get(0))).andReturn(runningJobExecutions).times(3);
		EasyMock.expect(mockJobRepository.getLastStepExecution(jobInstance, THROTTLE_STEP)).andReturn(stepExecution).anyTimes();

		EasyMock.replay(mockJobExplorer);
		EasyMock.replay(mockJobRepository);

		boolean startUpFlag = service.checkIfnewJobCanbeLaunched();

		EasyMock.verify(mockJobRepository);

		Assert.assertTrue(startUpFlag);
		
	}

	/**
	 * Negative scenario number of current jobs running is 3 and throttle limit is 3 and nun of running jobs have crossed throttle step. 
	 * Spring batch will not be allowed to start next job.  
	 * 
	 */
	@Test
	public void checkIfnewJobCanbeLaunched_negative_1(){
		JobStartupThrottleServiceImpl service = new JobStartupThrottleServiceImpl(mockJobExplorer, mockJobRepository,
										AppConfig.createJobThrottleConfig(8, true, THROTTLE_STEP, 3));
		// test specific setup.
		Set<JobExecution> runningJobExecutions = new HashSet<JobExecution>(3);
		JobExecution jobExecutionTest_1 = new JobExecution(new JobInstance(234l,new JobParameters(),"ThrottleTestJob"));
		JobExecution jobExecutionTest_2 = new JobExecution(new JobInstance(234l,new JobParameters(),"ThrottleTestJob"));
		JobExecution jobExecutionTest_3 = new JobExecution(new JobInstance(234l,new JobParameters(),"ThrottleTestJob"));
		
		runningJobExecutions.add(jobExecutionTest_1);
		runningJobExecutions.add(jobExecutionTest_2);
		runningJobExecutions.add(jobExecutionTest_3);
		
		JobExecution jobExecutionOuter = null;
		
		for (JobExecution jobExecution : runningJobExecutions) {
			jobExecutionOuter = jobExecution ;
		}
		JobInstance jobInstance = jobExecutionOuter.getJobInstance();
		
		EasyMock.expect(mockJobExplorer.getJobNames()).andReturn(jobNames).anyTimes();  
		EasyMock.expect(mockJobExplorer.findRunningJobExecutions(jobNames.get(0))).andReturn(runningJobExecutions).times(3);
		EasyMock.expect(mockJobRepository.getLastStepExecution(jobInstance, THROTTLE_STEP)).andReturn(null).anyTimes();

		EasyMock.replay(mockJobExplorer);
		EasyMock.replay(mockJobRepository);

		boolean startUpFlag = service.checkIfnewJobCanbeLaunched();

		EasyMock.verify(mockJobRepository);

		Assert.assertFalse(startUpFlag);
		
	}

}

