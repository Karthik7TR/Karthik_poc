/*
 * Copyright 2011: Thomson Reuters Global Resources. All Rights Reserved.
 * Proprietary and Confidential information of TRGR. Disclosure, Use or
 * Reproduction without the written authorization of TRGR is prohibited
 */
package com.thomsonreuters.uscl.ereader.orchestrate.engine.queue;

import javax.jms.Message;
import javax.jms.Queue;
import javax.jms.TextMessage;

import org.springframework.beans.factory.annotation.Required;
import org.springframework.jms.core.JmsTemplate;

import com.thomsonreuters.uscl.ereader.orchestrate.core.JobRunRequest;

/**
 * Handle receiving messages from the high and normal priority job request queues.
 */
public class JobQueueManagerImpl implements JobQueueManager {
	// private static final Logger log = Logger.getLogger(JobQueueManagerImpl.class);
	
	private JmsTemplate jmsTemplate;
	private Queue highPriorityJobRunRequestQueue;
	private Queue normalPriorityJobRunRequestQueue;
	
	@Override
	public JobRunRequest getHighPriorityJobRunRequest() throws Exception {
		return getJobRunRequest(highPriorityJobRunRequestQueue);
	}
	
	@Override
	public JobRunRequest getNormalPriorityJobRunRequest() throws Exception {
		return getJobRunRequest(normalPriorityJobRunRequestQueue);		
	}
	
	/**
	 * Return the message at the front of the JMS queue.
	 * @param queue the queue destination to check.
	 * @return the message at the front of the queue, or null if the queue is emtpy.
	 * @throws Exception if there is a JMS fetch error, or a parse error with the received message.
	 */
	private JobRunRequest getJobRunRequest(Queue queue) throws Exception {
		JobRunRequest jobRunRequest = null;
		// Synchronous receive, but the timeout value is set so it will timeout and return quickly if there is nothing on the JMS queue.
		// See jmsTemplate definition in Spring bean xml file.
		Message message = jmsTemplate.receive(queue);
		if (message != null) {
			TextMessage textMessage = (TextMessage) message;
			String xmlRequest = textMessage.getText();
			jobRunRequest = JobRunRequest.unmarshal(xmlRequest);
		}
		return jobRunRequest;
	}
	@Required
	public void setJmsTemplate(JmsTemplate template) {
		this.jmsTemplate = template;
	}
	@Required
	public void setHighPriorityQueue(Queue high) {
		this.highPriorityJobRunRequestQueue = high;
	}
	@Required
	public void setNormalPriorityQueue(Queue normal) {
		this.normalPriorityJobRunRequestQueue = normal;
	}
}
