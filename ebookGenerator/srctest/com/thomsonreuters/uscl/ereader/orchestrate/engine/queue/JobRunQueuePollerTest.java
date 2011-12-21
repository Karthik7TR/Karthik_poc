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
import org.springframework.batch.core.JobParameters;

import com.thomsonreuters.uscl.ereader.orchestrate.core.BookDefinition;
import com.thomsonreuters.uscl.ereader.orchestrate.core.BookDefinitionKey;
import com.thomsonreuters.uscl.ereader.orchestrate.core.JobRunRequest;
import com.thomsonreuters.uscl.ereader.orchestrate.core.service.CoreService;
import com.thomsonreuters.uscl.ereader.orchestrate.engine.service.EngineService;
import com.thomsonreuters.uscl.ereader.orchestrate.engine.throttle.Throttle;

public class JobRunQueuePollerTest  {
	private static final BookDefinitionKey BOOK_KEY = new BookDefinitionKey("bookTitleId");
	private static JobRunRequest RUN_REQ = JobRunRequest.create(BOOK_KEY, "theUserName", "theUserEmail");
	private Throttle mockThrottle;
	private CoreService mockCoreService;
	private EngineService mockEngineService;
	private JobQueueManager mockJobQueueManager;
	private JobExecution mockJobExecution;
	private JobRunQueuePoller poller;  // Class being tested
	private BookDefinition bookDefinition;
	private JobParameters bookDefinitionJobParameters;
	private JobParameters dynamicJobParameters;
	
	@Before
	public void setUp() throws Exception {
		// Create mock collaborators
		this.mockThrottle = EasyMock.createMock(Throttle.class);
		this.mockCoreService = EasyMock.createMock(CoreService.class);
		this.mockEngineService = EasyMock.createMock(EngineService.class);
		this.mockJobQueueManager = EasyMock.createMock(JobQueueManager.class);
		this.mockJobExecution = EasyMock.createMock(JobExecution.class);
		
		this.bookDefinition = new BookDefinition();
		this.bookDefinitionJobParameters = new JobParameters();
		this.dynamicJobParameters = new JobParameters();
		
		// Create the class to be tested and inject mock collaborators
		this.poller = new JobRunQueuePoller();
		poller.setCoreService(mockCoreService);
		poller.setEngineService(mockEngineService);
		poller.setThrottle(mockThrottle);
		poller.setJobQueueManager(mockJobQueueManager);
	}
	
	/**
	 * Test that nothing happens when we are throttled and no job request are being consumed from 
	 * the queues.
	 */
	@Test
	public void testJobRunQueueThrottled() throws Exception {
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
		recordRunExpectations();
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
		recordRunExpectations();
		verifyPoller();
	}
	
	/**
	 * Test exception handling
	 */
	@Test
	public void testJobRunQueuePollerException() throws Exception {
		EasyMock.expect(mockThrottle.isAtMaximum()).andReturn(false);
		EasyMock.expect(mockJobQueueManager.getHighPriorityJobRunRequest()).andThrow(new Exception("Bogus JUnit test exception getting " + JobRunRequest.class.getName() + " from high priority queue")); // Pretend exception getting message from high queue
		verifyPoller();
	}

	private void recordRunExpectations() throws Exception {
		EasyMock.expect(mockCoreService.findBookDefinition(RUN_REQ.getBookDefinitionKey())).andReturn(bookDefinition);
		EasyMock.expect(mockEngineService.createJobParametersFromBookDefinition(bookDefinition)).andReturn(bookDefinitionJobParameters);
		EasyMock.expect(mockEngineService.createDynamicJobParameters(RUN_REQ)).andReturn(dynamicJobParameters);
		EasyMock.expect(mockEngineService.runJob(JobRunRequest.JOB_NAME_CREATE_EBOOK, dynamicJobParameters)).andReturn(mockJobExecution);
	}

	/**
	 * Ensure that the expected methods in the mock object collaborators have been called (or not) as expected. 
	 */
	private void verifyPoller() {
		EasyMock.replay(mockThrottle);
		EasyMock.replay(mockJobQueueManager);
		EasyMock.replay(mockCoreService);
		EasyMock.replay(mockEngineService);
		
		// Invoke the poller
		poller.run();
		
		// Verify that the proper methods were called
		EasyMock.verify(mockThrottle);
		EasyMock.verify(mockJobQueueManager);
		EasyMock.verify(mockCoreService);
		EasyMock.verify(mockEngineService);
	}
}
