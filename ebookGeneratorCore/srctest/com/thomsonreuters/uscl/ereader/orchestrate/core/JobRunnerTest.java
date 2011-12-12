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

import com.thomsonreuters.uscl.ereader.orchestrate.core.JmsJobRunner.MyStringMessageCreator;

public class JobRunnerTest  {
	private JmsJobRunner jobRunner;
	private JmsTemplate mockJmsTemplate;
	private JobRunRequest mockJobRunRequest;
	private Queue mockHighPriorityQueue;
	private Queue mockNormalPriorityQueue;
	
	@Before
	public void setUp() throws Exception {
		this.mockJobRunRequest = EasyMock.createMock(JobRunRequest.class);
		this.mockJmsTemplate = EasyMock.createMock(JmsTemplate.class);
		this.mockNormalPriorityQueue = EasyMock.createMock(Queue.class);
		this.mockHighPriorityQueue = EasyMock.createMock(Queue.class);
		
		// Set expectations
		EasyMock.expect(mockJobRunRequest.marshal()).andReturn("some xml");
		EasyMock.replay(mockJobRunRequest);
		
		this.jobRunner = new JmsJobRunner();
		jobRunner.setHighPriorityQueue(mockHighPriorityQueue);
		jobRunner.setNormalPriorityQueue(mockNormalPriorityQueue);
		jobRunner.setJmsTemplate(mockJmsTemplate);
	}
	
	@Test
	public void testHighPriorityJobRunRequest() {
		try {
			mockJmsTemplate.send(EasyMock.eq(mockHighPriorityQueue), EasyMock.anyObject(MyStringMessageCreator.class));
			EasyMock.replay(mockJmsTemplate);
			jobRunner.enqueueHighPriorityJobRunRequest(mockJobRunRequest);
			EasyMock.verify(mockJmsTemplate);
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail(e.getMessage());
		}
	}
	
	@Test
	public void testNormalPriorityJobRunRequest() {
		try {
			mockJmsTemplate.send(EasyMock.eq(mockNormalPriorityQueue), EasyMock.anyObject(MyStringMessageCreator.class));
			EasyMock.replay(mockJmsTemplate);
			jobRunner.enqueueNormalPriorityJobRunRequest(mockJobRunRequest);
			EasyMock.verify(mockJmsTemplate);
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail(e.getMessage());
		}
	}
}
