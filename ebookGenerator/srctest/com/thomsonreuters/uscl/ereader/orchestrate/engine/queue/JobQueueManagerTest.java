package com.thomsonreuters.uscl.ereader.orchestrate.engine.queue;

import javax.jms.Queue;
import javax.jms.TextMessage;

import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.jms.core.JmsTemplate;

import com.thomsonreuters.uscl.ereader.orchestrate.core.JobRunRequest;

public class JobQueueManagerTest  {
	private static JobRunRequest RUN_REQ = JobRunRequest.create("theCode", "theTitle", "theVersion", "theUserName", "theUserEmail");
	private JmsTemplate jmsTemplate;
	private TextMessage textMessage; 
	private JobQueueManagerImpl queueManager;
	private Queue highQueue, normalQueue;
	
	@Before
	public void setUp() throws Exception {
		jmsTemplate = EasyMock.createMock(JmsTemplate.class);
		textMessage = EasyMock.createMock(TextMessage.class);
		highQueue = EasyMock.createMock(Queue.class);
		normalQueue = EasyMock.createMock(Queue.class);
		
		// Set up the text in the Message object
		String xmlRequest = RUN_REQ.marshal();
		EasyMock.expect(textMessage.getText()).andReturn(xmlRequest);
		EasyMock.replay(textMessage);

		this.queueManager = new JobQueueManagerImpl();
		queueManager.setJmsTemplate(jmsTemplate);
		queueManager.setHighPriorityQueue(highQueue);
		queueManager.setNormalPriorityQueue(normalQueue);
	}
	
	/**
	 * Test fetching a job run request off the high priority job run queue.
	 */
	@Test
	public void testGetHighPriorityJobRunRequest() {
		try {
			EasyMock.expect(jmsTemplate.receive(highQueue)).andReturn(textMessage);
			EasyMock.replay(jmsTemplate);
			JobRunRequest req = queueManager.getHighPriorityJobRunRequest();
			Assert.assertNotNull(req);
			Assert.assertEquals(RUN_REQ.getBookCode(), req.getBookCode());
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail("Failed to get high priority run request: " + e.getMessage());
		}
	}

	/**
	 * Test fetching a job run request off the normal priority job run queue.
	 */
	@Test
	public void testGetNormalPriorityJobRunRequest() {
		try {
			EasyMock.expect(jmsTemplate.receive(normalQueue)).andReturn(textMessage);
			EasyMock.replay(jmsTemplate);
			JobRunRequest req = queueManager.getNormalPriorityJobRunRequest();
			Assert.assertNotNull(req);
			Assert.assertEquals(RUN_REQ.getBookCode(), req.getBookCode());
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail("Failed to get normal priority run request: " + e.getMessage());
		}
	}
}
