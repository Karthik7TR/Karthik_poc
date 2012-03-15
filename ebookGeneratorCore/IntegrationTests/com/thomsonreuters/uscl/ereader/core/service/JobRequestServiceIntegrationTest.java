/*
 * Copyright 2011: Thomson Reuters Global Resources. All Rights Reserved.
 * Proprietary and Confidential information of TRGR. Disclosure, Use or
 * Reproduction without the written authorization of TRGR is prohibited
 */
package com.thomsonreuters.uscl.ereader.core.service;


import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import junit.framework.Assert;

import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import com.thomsonreuters.uscl.ereader.core.job.domain.JobRequest;
import com.thomsonreuters.uscl.ereader.core.job.service.JobRequestService;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration 
@TransactionConfiguration(transactionManager = "transactionManager", defaultRollback = false)
@Transactional
public class JobRequestServiceIntegrationTest  {
	private static final Logger log = Logger.getLogger(JobRequestServiceIntegrationTest.class);

	public Timestamp UPDATE_DATE = getCurrentTimeStamp();

	@Autowired
	private JobRequestService  jobRequestService;
	
	public void setJobRequestservice(JobRequestService jobRequestservice) {
		this.jobRequestService = jobRequestservice;
	}
	
	@Before
	public void setUp() throws Exception {
		
		
	}
	
	private Timestamp getCurrentTimeStamp() {
		// create a java calendar instance
		Calendar calendar = Calendar.getInstance();
		return new java.sql.Timestamp(calendar.getTime().getTime());

	}
	
	
	//@Test
	@Rollback
	public void testGetAllJobsByCriteria(){
		JobRequest jobRequestTest = null;
		
		long testEbookDefinitionId = 1014L;
		String testEbookVersionSubmitted = "next";
		int testJobPriority = 1;
		String testJobSubmittersName = "Isaac_Newton";
		
		jobRequestService.saveQueuedJobRequest(testEbookDefinitionId, testEbookVersionSubmitted, testJobPriority, testJobSubmittersName);

		testEbookDefinitionId = 1015L;
		 testEbookVersionSubmitted = "next";
		testJobPriority = 2;
		testJobSubmittersName = "Archimedes";
		jobRequestService.saveQueuedJobRequest(testEbookDefinitionId, testEbookVersionSubmitted, testJobPriority, testJobSubmittersName);


		testEbookDefinitionId = 1016L;
		 testEbookVersionSubmitted = "next";
		testJobPriority = 3;
		testJobSubmittersName = "Albert_Einstein";

		jobRequestService.saveQueuedJobRequest(testEbookDefinitionId, testEbookVersionSubmitted, testJobPriority, testJobSubmittersName);
		
		testEbookDefinitionId = 1017L;
		 testEbookVersionSubmitted = "next";
		testJobPriority = 4;
		testJobSubmittersName = "Galileo_Galilei";
		
		
		jobRequestService.saveQueuedJobRequest(testEbookDefinitionId, testEbookVersionSubmitted, testJobPriority, testJobSubmittersName);
		
	}
	

	@Test
	@Rollback
	public void testGetNextJobToExecute(){

		
		long testEbookDefinitionId = 1010L;
		String testEbookVersionSubmitted = "next";
		int testJobPriority = 1;
		Date testScheduledTime = getCurrentTimeStamp();
		String testJobSubmittersName = "nextJobToRun_1";
		
		jobRequestService.saveQueuedJobRequest(testEbookDefinitionId, testEbookVersionSubmitted, testJobPriority, testJobSubmittersName);


		testEbookDefinitionId = 1011L;
		testEbookVersionSubmitted = "next";
		testJobPriority = 2;
		testScheduledTime = getCurrentTimeStamp();
		testJobSubmittersName = "nextJobToRun_2";
		jobRequestService.saveQueuedJobRequest(testEbookDefinitionId, testEbookVersionSubmitted, testJobPriority, testJobSubmittersName);


		testEbookDefinitionId = 1012L;
		 testEbookVersionSubmitted = "next";
		testJobPriority = 3;
		testScheduledTime = getCurrentTimeStamp();
		testJobSubmittersName = "nextJobToRun_3";

		jobRequestService.saveQueuedJobRequest(testEbookDefinitionId, testEbookVersionSubmitted, testJobPriority, testJobSubmittersName);

		testEbookDefinitionId = 1013L;
		 testEbookVersionSubmitted = "next";
		testJobPriority = 4;
		testScheduledTime = getCurrentTimeStamp();
		testJobSubmittersName = "nextJobToRun_4";
		
		Long pk = jobRequestService.saveQueuedJobRequest(testEbookDefinitionId, testEbookVersionSubmitted, testJobPriority, testJobSubmittersName);

		JobRequest jobRequestToExecute = jobRequestService.findByPrimaryKey(pk);
		Assert.assertNotNull(jobRequestToExecute);
		Assert.assertEquals(pk, jobRequestToExecute.getPrimaryKey());
		Assert.assertEquals(testJobPriority, jobRequestToExecute.getPriority());
	}
	
	@Test
	@Rollback
	public void testGetAllJobRequest(){
		
		long testEbookDefinitionId_1 = 1006L;
		String testEbookVersionSubmitted = "update1";
		int testJobPriority = 100;
		String testJobSubmittersName = "testSubmitter";
		
		jobRequestService.saveQueuedJobRequest(testEbookDefinitionId_1, testEbookVersionSubmitted, testJobPriority, testJobSubmittersName);


		long testEbookDefinitionId_2 = 1007L;
		 testEbookVersionSubmitted = "update1";
		testJobPriority = 101;
		testJobSubmittersName = "testSubmitter";
		
		jobRequestService.saveQueuedJobRequest(testEbookDefinitionId_2, testEbookVersionSubmitted, testJobPriority, testJobSubmittersName);


		long testEbookDefinitionId_3 = 1008L;
		 testEbookVersionSubmitted = "update1";
		testJobPriority = 102;
		testJobSubmittersName = "testSubmitter";
		
		jobRequestService.saveQueuedJobRequest(testEbookDefinitionId_3, testEbookVersionSubmitted, testJobPriority, testJobSubmittersName);

		List<JobRequest> jobRequestList = jobRequestService.findAllJobRequests();
		Assert.assertTrue(jobRequestList.size()> 2);
		boolean contiansFlag= false;
		for (JobRequest jobRequest : jobRequestList) {
			if(jobRequest.getEbookDefinitionId() == 1006L || 
					jobRequest.getEbookDefinitionId() == 1007L || 
							jobRequest.getEbookDefinitionId() == 1008L ){
					
				contiansFlag = true;
				break;
			}
		}
		
		Assert.assertTrue(contiansFlag);
		
		JobRequest jobRequest_1 = jobRequestService.getJobRequestByBookDefinationId(testEbookDefinitionId_1);
		JobRequest jobRequest_2 = jobRequestService.getJobRequestByBookDefinationId(testEbookDefinitionId_2);
		JobRequest jobRequest_3 = jobRequestService.getJobRequestByBookDefinationId(testEbookDefinitionId_3);

		Assert.assertEquals(new Long(1006L), jobRequest_1.getEbookDefinitionId());
		Assert.assertEquals(new Long(1007L),jobRequest_2.getEbookDefinitionId());
		Assert.assertEquals(new Long(1008L),jobRequest_3.getEbookDefinitionId());
		
		
	}
	
	@Test
	@Rollback
	public void testDeleteJobByJobId(){
		
		long testEbookDefinitionId = 1009L;
		String testEbookVersionSubmitted = "update1";
		int testJobPriority = 15;
		String testJobSubmittersName = "deleteByJobId";
		
		jobRequestService.saveQueuedJobRequest(testEbookDefinitionId, testEbookVersionSubmitted, testJobPriority, testJobSubmittersName);

		JobRequest jobRequestRerieved = jobRequestService.getJobRequestByBookDefinationId(testEbookDefinitionId);
		Assert.assertNotNull(jobRequestRerieved);
		
		
		jobRequestService.deleteJobByJobId(jobRequestRerieved.getPrimaryKey());
		
		JobRequest jobRequestRerieved_2 = jobRequestService.getJobRequestByBookDefinationId(testEbookDefinitionId);
		Assert.assertNull(jobRequestRerieved_2);
		
		
	}
	
	@Test
	@Rollback
	public void testUpdateJobPriority(){

		long testEbookDefinitionId = 1003L;
		String testEbookVersionSubmitted = "update1";
		int testJobPriority = 34;
		String testJobSubmittersName = "testSubmitter";
		jobRequestService.saveQueuedJobRequest(testEbookDefinitionId, testEbookVersionSubmitted, testJobPriority, testJobSubmittersName);

		JobRequest jobRequestObj = jobRequestService.getJobRequestByBookDefinationId(testEbookDefinitionId);
		
		long jobRequestId = jobRequestObj.getPrimaryKey();
		
		int jobPriority = 95;	
		jobRequestService.updateJobPriority(jobRequestId, jobPriority);
		JobRequest jobRequestUpdatedObj = jobRequestService.getJobRequestByBookDefinationId(testEbookDefinitionId);
		Assert.assertEquals(jobPriority, jobRequestUpdatedObj.getPriority());
	}
	
	@Test
	@Rollback
	public void testSaveJobRequest(){

		long testEbookDefinitionId = 101L;
		String testEbookVersionSubmitted = "EBVersion1";
		int testJobPriority = 945;
		Timestamp testScheduledTime = getCurrentTimeStamp();
		String testJobSubmittersName = "testSubmitter";
		jobRequestService.saveQueuedJobRequest(testEbookDefinitionId, testEbookVersionSubmitted, testJobPriority, testJobSubmittersName);

		JobRequest jobRequestUpdatedObj = jobRequestService.getJobRequestByBookDefinationId(testEbookDefinitionId);
		Assert.assertEquals(testJobPriority, jobRequestUpdatedObj.getPriority());

		
	}
	/**
	 * provide scheduled time and verify if job is marked for scheduled job in job status.
	 */
	@Test
	@Rollback
	public void testIfjobMarkedforScheduled(){
		
		//test data
		long testEbookDefinitionId = 1004L;
		String testEbookVersionSubmitted = "EBVersion1";
		int testJobPriority = 1024;
		String testJobSubmittersName = "Scheduled Job";
		//Save test data
		jobRequestService.saveQueuedJobRequest(testEbookDefinitionId, testEbookVersionSubmitted, testJobPriority, testJobSubmittersName);

		
		JobRequest jobRequestRetrieved = jobRequestService.getJobRequestByBookDefinationId(testEbookDefinitionId);
		Assert.assertTrue(jobRequestRetrieved.isQueuedRequest());
		Assert.assertFalse(jobRequestRetrieved.isScheduledRequest());
	}

	
	@Test
	public void tesIsBookInJobRequestNegative(){
		int ebookDefinationId = 134343401; // some fake book id
		boolean isBookFlag = true;
		isBookFlag = jobRequestService.isBookInJobRequest(ebookDefinationId);
		Assert.assertFalse(isBookFlag);
	}


	@Test
	@Rollback
	public void tesIsBookInJobRequestPositive(){
		long testEbookDefinitionId = 1002L;  // some fake book id
		String testEbookVersionSubmitted = "isBook";
		int testJobPriority = 938;
		String testJobSubmittersName = "isEbookInRequestPositiveTest";
		jobRequestService.saveQueuedJobRequest(testEbookDefinitionId, testEbookVersionSubmitted, testJobPriority, testJobSubmittersName);

		boolean isBookFlag = false; 
		isBookFlag = jobRequestService.isBookInJobRequest(testEbookDefinitionId);
		Assert.assertTrue(isBookFlag);
	}

	
}
