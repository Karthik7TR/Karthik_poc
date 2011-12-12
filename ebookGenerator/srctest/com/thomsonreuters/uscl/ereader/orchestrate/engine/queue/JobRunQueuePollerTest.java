/*
 * Copyright 2011: Thomson Reuters Global Resources. All Rights Reserved.
 * Proprietary and Confidential information of TRGR. Disclosure, Use or
 * Reproduction without the written authorization of TRGR is prohibited
 */
package com.thomsonreuters.uscl.ereader.orchestrate.engine.queue;

import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;
import org.springframework.batch.core.JobExecution;

import com.thomsonreuters.uscl.ereader.orchestrate.core.JobRunRequest;
import com.thomsonreuters.uscl.ereader.orchestrate.engine.service.EngineService;
import com.thomsonreuters.uscl.ereader.orchestrate.engine.throttle.Throttle;

public class JobRunQueuePollerTest  {
	private static JobRunRequest RUN_REQ = JobRunRequest.create("theCode", "theUserName", "theUserEmail");
	private Throttle mockThrottle;
	private EngineService mockEngineService;
	private JobQueueManager mockJobQueueManager;
	private JobExecution mockJobExecution;
	private JobRunQueuePoller poller;  // Class being tested
	
	@Before
	public void setUp() throws Exception {
		// Create mock collaborators
		this.mockThrottle = EasyMock.createMock(Throttle.class);
		this.mockEngineService = EasyMock.createMock(EngineService.class);
		this.mockJobQueueManager = EasyMock.createMock(JobQueueManager.class);
		this.mockJobExecution = EasyMock.createMock(JobExecution.class);
		
		// Create the class to be tested and inject mock collaborators
		this.poller = new JobRunQueuePoller();
		poller.setEngineService(mockEngineService);
		poller.setThrottle(mockThrottle);
		poller.setJobQueueManager(mockJobQueueManager);
	}
	
	/**
	 * Test that nothing happens when we are throttled and no job request are being consumed from 
	 * the queues.
	 */
	@Test
	public void testJobRunQueueThrottled() {
		EasyMock.expect(mockThrottle.isAtMaximum()).andReturn(true);
		verifyPoller();
	}

	/**
	 * Test that the job queue poller returns the proper high priority queue when not throttled.
	 */
	@Test
	public void testJobRunQueuePollerHighPriority() throws Exception {
		// Record expected behavior
		EasyMock.expect(mockThrottle.isAtMaximum()).andReturn(false);	// we are not currently restricted from consuming messages 
		EasyMock.expect(mockJobQueueManager.getHighPriorityJobRunRequest()).andReturn(RUN_REQ);  // a message is sitting on high priority queue
		EasyMock.expect(mockEngineService.runJob(RUN_REQ)).andReturn(mockJobExecution);
		verifyPoller();
	}

	/**
	 * Test that the job queue poller returns the proper normal priority queue when not throttled.
	 */
	@Test
	public void testJobRunQueuePollerNormalPriority() throws Exception {
		// Record expected behavior
		EasyMock.expect(mockThrottle.isAtMaximum()).andReturn(false);	// we are not currently restricted from consuming messages
		EasyMock.expect(mockJobQueueManager.getHighPriorityJobRunRequest()).andReturn(null);  // Pretend nothing on high queue
		EasyMock.expect(mockJobQueueManager.getNormalPriorityJobRunRequest()).andReturn(RUN_REQ);  // but something is sitting on the normal queue
		EasyMock.expect(mockEngineService.runJob(RUN_REQ)).andReturn(mockJobExecution);
		verifyPoller();
	}
	
	/**
	 * Test exception handling
	 */
	@Test
	public void testJobRunQueuePollerException() throws Exception {
		EasyMock.expect(mockThrottle.isAtMaximum()).andReturn(false);
		EasyMock.expect(mockJobQueueManager.getHighPriorityJobRunRequest()).andThrow(new Exception("get message exception")); // Pretend exception getting message from high queue
		verifyPoller();
	}

	
	/**
	 * Ensure that the expected methods in the mock object collaborators have been called (or not) as expected. 
	 */
	private void verifyPoller() {
		EasyMock.replay(mockThrottle);
		EasyMock.replay(mockJobQueueManager);
		EasyMock.replay(mockEngineService);
		
		// Invoke the poller
		poller.run();
		
		// Verify that the proper methods were called
		EasyMock.verify(mockThrottle);
		EasyMock.verify(mockJobQueueManager);
		EasyMock.verify(mockEngineService);
	}
}
