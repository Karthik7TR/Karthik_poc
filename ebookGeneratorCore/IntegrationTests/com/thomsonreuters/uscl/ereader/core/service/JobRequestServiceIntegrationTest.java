/*
 * Copyright 2011: Thomson Reuters Global Resources. All Rights Reserved.
 * Proprietary and Confidential information of TRGR. Disclosure, Use or
 * Reproduction without the written authorization of TRGR is prohibited
 */
package com.thomsonreuters.uscl.ereader.core.service;


import java.sql.Timestamp;
import java.util.Calendar;
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
		String testJobStatus = "Queued";
		String testJobPriority = "normal";
		Timestamp testScheduledTime = getCurrentTimeStamp();
		String testJobSubmittersName = "Isaac_Newton";
		
		jobRequestService.saveJobRequest(testEbookDefinitionId, testEbookVersionSubmitted, testJobStatus, testJobPriority, testScheduledTime, testJobSubmittersName);
		

		testEbookDefinitionId = 1015L;
		 testEbookVersionSubmitted = "next";
		testJobStatus = null;
		testJobPriority = "normal";
		testScheduledTime = getCurrentTimeStamp();
		testJobSubmittersName = "Archimedes";
		jobRequestService.saveJobRequest(testEbookDefinitionId, testEbookVersionSubmitted, testJobStatus, testJobPriority, testScheduledTime, testJobSubmittersName);


		testEbookDefinitionId = 1016L;
		 testEbookVersionSubmitted = "next";
		testJobStatus = "Queued";
		testJobPriority = "high";
		testScheduledTime = getCurrentTimeStamp();
		testJobSubmittersName = "Albert_Einstein";

		jobRequestService.saveJobRequest(testEbookDefinitionId, testEbookVersionSubmitted, testJobStatus, testJobPriority, testScheduledTime, testJobSubmittersName);
		
		testEbookDefinitionId = 1017L;
		 testEbookVersionSubmitted = "next";
		testJobStatus = null;
		testJobPriority = "high";
		testScheduledTime = getCurrentTimeStamp();
		testJobSubmittersName = "Galileo_Galilei";
		
		
		jobRequestService.saveJobRequest(testEbookDefinitionId, testEbookVersionSubmitted, testJobStatus, testJobPriority, testScheduledTime, testJobSubmittersName);

		List<JobRequest> jobRequestList_1 =jobRequestService.getAllJobRequestsBy(null, "high", null, "Isaac_Newton");
		if(jobRequestList_1 != null){
			jobRequestTest = jobRequestList_1.get(0);
		}
		
		Assert.assertEquals("Isaac_Newton", jobRequestTest.getJobSubmittersName());
		
		List<JobRequest> jobRequestList_2 =jobRequestService.getAllJobRequestsBy(null, "high", testScheduledTime, "Archimedes");
		if(jobRequestList_2 != null){
			jobRequestTest = jobRequestList_2.get(0);
		}

		Assert.assertEquals(testScheduledTime, jobRequestTest.getJobScheduleTimeStamp());
		
	}
	

	@Test
	@Rollback
	public void testGetNextJobToExecute(){

		
		long testEbookDefinitionId = 1010L;
		String testEbookVersionSubmitted = "next";
		String testJobStatus = "Queued";
		String testJobPriority = "normal";
		Timestamp testScheduledTime = getCurrentTimeStamp();
		String testJobSubmittersName = "nextJobToRun_1";
		
		jobRequestService.saveJobRequest(testEbookDefinitionId, testEbookVersionSubmitted, testJobStatus, testJobPriority, testScheduledTime, testJobSubmittersName);
		

		testEbookDefinitionId = 1011L;
		 testEbookVersionSubmitted = "next";
		testJobStatus = null;
		testJobPriority = "normal";
		testScheduledTime = getCurrentTimeStamp();
		testJobSubmittersName = "nextJobToRun_2";
		jobRequestService.saveJobRequest(testEbookDefinitionId, testEbookVersionSubmitted, testJobStatus, testJobPriority, testScheduledTime, testJobSubmittersName);


		testEbookDefinitionId = 1012L;
		 testEbookVersionSubmitted = "next";
		testJobStatus = "Queued";
		testJobPriority = "high";
		testScheduledTime = getCurrentTimeStamp();
		testJobSubmittersName = "nextJobToRun_3";

		jobRequestService.saveJobRequest(testEbookDefinitionId, testEbookVersionSubmitted, testJobStatus, testJobPriority, testScheduledTime, testJobSubmittersName);
		
		testEbookDefinitionId = 1013L;
		 testEbookVersionSubmitted = "next";
		testJobStatus = null;
		testJobPriority = "high";
		testScheduledTime = getCurrentTimeStamp();
		testJobSubmittersName = "nextJobToRun_4";
		
		

		
		jobRequestService.saveJobRequest(testEbookDefinitionId, testEbookVersionSubmitted, testJobStatus, testJobPriority, testScheduledTime, testJobSubmittersName);
		
		JobRequest jobRequestToExecute = jobRequestService.getNextJobToExecute();
		
		int priority = jobRequestToExecute.getJobPriority();
		Assert.assertNotNull(jobRequestToExecute);
		Assert.assertEquals(priority, 1);
		
		
		
	}
	
	@Test
	@Rollback
	public void testGetAllJobRequest(){
		
		long testEbookDefinitionId_1 = 1006L;
		String testEbookVersionSubmitted = "update1";
		String testJobStatus = "Queued";
		String testJobPriority = "high";
		Timestamp testScheduledTime = getCurrentTimeStamp();
		String testJobSubmittersName = "testSubmitter";
		
		jobRequestService.saveJobRequest(testEbookDefinitionId_1, testEbookVersionSubmitted, testJobStatus, testJobPriority, testScheduledTime, testJobSubmittersName);
		

		long testEbookDefinitionId_2 = 1007L;
		 testEbookVersionSubmitted = "update1";
		testJobStatus = "Queued";
		testJobPriority = "high";
		testScheduledTime = getCurrentTimeStamp();
		testJobSubmittersName = "testSubmitter";
		
		jobRequestService.saveJobRequest(testEbookDefinitionId_2, testEbookVersionSubmitted, testJobStatus, testJobPriority, testScheduledTime, testJobSubmittersName);


		long testEbookDefinitionId_3 = 1008L;
		 testEbookVersionSubmitted = "update1";
		testJobStatus = "Queued";
		testJobPriority = "high";
		testScheduledTime = getCurrentTimeStamp();
		testJobSubmittersName = "testSubmitter";
		
		jobRequestService.saveJobRequest(testEbookDefinitionId_3, testEbookVersionSubmitted, testJobStatus, testJobPriority, testScheduledTime, testJobSubmittersName);

		List<JobRequest> jobRequestList = jobRequestService.getAllJobRequests();
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

		Assert.assertEquals(jobRequest_1.getEbookDefinitionId(),1006L );
		Assert.assertEquals(jobRequest_2.getEbookDefinitionId(),1007L );
		Assert.assertEquals(jobRequest_3.getEbookDefinitionId(),1008L );
		
		
	}
	
	@Test
	@Rollback
	public void testDeleteJobByJobId(){
		
		long testEbookDefinitionId = 1009L;
		String testEbookVersionSubmitted = "update1";
		String testJobStatus = "Queued";
		String testJobPriority = "high";
		Timestamp testScheduledTime = getCurrentTimeStamp();
		String testJobSubmittersName = "deleteByJobId";
		
		jobRequestService.saveJobRequest(testEbookDefinitionId, testEbookVersionSubmitted, testJobStatus, testJobPriority, testScheduledTime, testJobSubmittersName);
		JobRequest jobRequestRerieved = jobRequestService.getJobRequestByBookDefinationId(testEbookDefinitionId);
		Assert.assertNotNull(jobRequestRerieved);
		
		
		jobRequestService.deleteJobByJobId(jobRequestRerieved.getJobRequestId());
		
		JobRequest jobRequestRerieved_2 = jobRequestService.getJobRequestByBookDefinationId(testEbookDefinitionId);
		Assert.assertNull(jobRequestRerieved_2);
		
		
	}
	
	@Test
	@Rollback
	public void testUpdateJobPriority(){

		long testEbookDefinitionId = 1003L;
		String testEbookVersionSubmitted = "update1";
		String testJobStatus = "Queued";
		String testJobPriority = "high";
		Timestamp testScheduledTime = getCurrentTimeStamp();
		String testJobSubmittersName = "testSubmitter";
		jobRequestService.saveJobRequest(testEbookDefinitionId, testEbookVersionSubmitted, testJobStatus, testJobPriority, testScheduledTime, testJobSubmittersName);
		
		JobRequest jobRequestObj = jobRequestService.getJobRequestByBookDefinationId(testEbookDefinitionId);
		
		long jobRequestId = jobRequestObj.getJobRequestId();
		
		String jobPriority = "normal";	
		jobRequestService.updateJobPriority(jobRequestId, jobPriority);
		JobRequest jobRequestUpdatedObj = jobRequestService.getJobRequestByBookDefinationId(testEbookDefinitionId);
		Assert.assertTrue(jobRequestUpdatedObj.getJobPriority() ==2);
	}
	
	@Test
	@Rollback
	public void testSaveJobRequest(){

		long testEbookDefinitionId = 101L;
		String testEbookVersionSubmitted = "EBVersion1";
		String testJobStatus = "Queued";
		String testJobPriority = "HIGH";
		Timestamp testScheduledTime = getCurrentTimeStamp();
		String testJobSubmittersName = "testSubmitter";
		jobRequestService.saveJobRequest(testEbookDefinitionId, testEbookVersionSubmitted, testJobStatus, testJobPriority, testScheduledTime, testJobSubmittersName);

		JobRequest jobRequestUpdatedObj = jobRequestService.getJobRequestByBookDefinationId(testEbookDefinitionId);
		Assert.assertTrue(jobRequestUpdatedObj.getJobPriority() ==1);

		
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
		String testJobStatus = null;//"Queued";
		String testJobPriority = "high";
		Timestamp testScheduledTime = getCurrentTimeStamp();
		String testJobSubmittersName = "Scheduled Job";
		//Save test data
		jobRequestService.saveJobRequest(testEbookDefinitionId, testEbookVersionSubmitted, testJobStatus, testJobPriority, testScheduledTime, testJobSubmittersName);
		
		
		JobRequest jobRequestRetrieved = jobRequestService.getJobRequestByBookDefinationId(testEbookDefinitionId);
		String jobStatusRetrieved = jobRequestRetrieved.getJobStatus();
		Assert.assertTrue(jobStatusRetrieved.equals("Scheduled"));
		
	}
	/**
	 * test if scheduled time is not provided job is maked for queue. 
	 */
	@Test
	@Rollback
	public void testIfjobMarkedforQueue(){
		//test data
		long testEbookDefinitionId = 1005L;
		String testEbookVersionSubmitted = "EBVersion1";
		String testJobStatus = null;//"Queued";
		String testJobPriority = "high";
		Timestamp testScheduledTime = null;
		String testJobSubmittersName = "Queued Job";
		//Save test data
		jobRequestService.saveJobRequest(testEbookDefinitionId, testEbookVersionSubmitted, testJobStatus, testJobPriority, testScheduledTime, testJobSubmittersName);
		
		
		JobRequest jobRequestRetrieved = jobRequestService.getJobRequestByBookDefinationId(testEbookDefinitionId);
		String jobStatusRetrieved = jobRequestRetrieved.getJobStatus();
		Assert.assertTrue(jobStatusRetrieved.equals("Queue"));
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
		String testJobStatus = "Queued";
		String testJobPriority = "high";
		Timestamp testScheduledTime = getCurrentTimeStamp();
		String testJobSubmittersName = "isEbookInRequestPositiveTest";
		jobRequestService.saveJobRequest(testEbookDefinitionId, testEbookVersionSubmitted, testJobStatus, testJobPriority, testScheduledTime, testJobSubmittersName);
		
		boolean isBookFlag = false; 
		isBookFlag = jobRequestService.isBookInJobRequest(testEbookDefinitionId);
		Assert.assertTrue(isBookFlag);
	}

	
}
