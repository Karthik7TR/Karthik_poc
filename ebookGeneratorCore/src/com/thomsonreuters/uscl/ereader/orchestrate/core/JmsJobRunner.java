/*
 * Copyright 2011: Thomson Reuters Global Resources. All Rights Reserved.
 * Proprietary and Confidential information of TRGR. Disclosure, Use or
 * Reproduction without the written authorization of TRGR is prohibited
 */
package com.thomsonreuters.uscl.ereader.orchestrate.core;
 
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Queue;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.springframework.beans.factory.annotation.Required;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;

public class JmsJobRunner implements JobRunner {
	
	private JmsTemplate jmsTemplate;
	private Queue highPriorityQueue;
	private Queue normalPriorityQueue;
	
	@Override
	public void enqueueNormalPriorityJobRunRequest(JobRunRequest jobRunRequest) throws Exception {
		enqueueJobRunRequest(normalPriorityQueue, jobRunRequest);
	}
	
	@Override
	public void enqueueHighPriorityJobRunRequest(JobRunRequest jobRunRequest) throws Exception {
		enqueueJobRunRequest(highPriorityQueue, jobRunRequest);
	}

	private void enqueueJobRunRequest(Queue queue, JobRunRequest jobRunRequest) throws Exception {
		String xmlRequest = jobRunRequest.marshal();
		MyStringMessageCreator stringMessageCreator = new MyStringMessageCreator(xmlRequest);
		jmsTemplate.send(queue, stringMessageCreator);
	}
	
	@Required
	public void setJmsTemplate(JmsTemplate jmsTemplate) {
		this.jmsTemplate = jmsTemplate;
	}
	@Required
	public void setHighPriorityQueue(Queue highPriorityQueue) {
		this.highPriorityQueue = highPriorityQueue;
	}
	@Required
	public void setNormalPriorityQueue(Queue normalPriorityQueue) {
		this.normalPriorityQueue = normalPriorityQueue;
	}
	
	final class MyStringMessageCreator implements MessageCreator {
		private String payload;
		
		public MyStringMessageCreator(String payload) {
			this.payload = payload;
		}
		public Message createMessage(Session jmsSession) throws JMSException {
			TextMessage message = jmsSession.createTextMessage(payload);
			return message;
		}
	}
}
