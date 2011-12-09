/*
 * Copyright 2011: Thomson Reuters Global Resources. All Rights Reserved.
 * Proprietary and Confidential information of TRGR. Disclosure, Use or
 * Reproduction without the written authorization of TRGR is prohibited
 */
package com.thomsonreuters.uscl.ereader.orchestrate.core;

import javax.jms.Queue;

import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.jms.core.JmsTemplate;

public class JobRunnerTest  {
	private static final String BOOK_CODE = "junitBook";
	private JmsJobRunner jobRunner;
	
	@Before
	public void setUp() {
		Queue highPriorityQueue = EasyMock.createMock(Queue.class);
		Queue normalPriorityQueue = EasyMock.createMock(Queue.class);
		JmsTemplate jmsTemplate = EasyMock.createMock(JmsTemplate.class);
		
		this.jobRunner = new JmsJobRunner();
		jobRunner.setHighPriorityQueue(highPriorityQueue);
		jobRunner.setNormalPriorityQueue(normalPriorityQueue);
		jobRunner.setJmsTemplate(jmsTemplate);
	}
	
	@Test
	public void testHighPriorityJobRunRequest() {
		try {
			JobRunRequest jobRunRequest = JobRunRequest.create(BOOK_CODE, "joeh", "joeh@bogus.com");
			jobRunner.enqueueHighPriorityJobRunRequest(jobRunRequest);
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail(e.getMessage());
		}
	}
	@Test
	public void testNormalPriorityJobRunRequest() {
		try {
			JobRunRequest jobRunRequest = JobRunRequest.create(BOOK_CODE, "joen", "joen@bogus.com");
			jobRunner.enqueueNormalPriorityJobRunRequest(jobRunRequest);
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail(e.getMessage());
		}
	}
}
