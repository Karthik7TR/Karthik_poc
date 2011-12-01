package com.thomsonreuters.uscl.ereader.orchestrate.core;
 
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Queue;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.springframework.beans.factory.annotation.Required;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;

import com.thomsonreuters.codes.security.authentication.LdapUserInfo;

public class JmsJobRunner implements JobRunner {
	
	private JmsTemplate jmsTemplate;
	private Queue highPriorityQueue;
	private Queue normalPriorityQueue;
	
	@Override
	public void enqueueNormalPriorityJobRunRequest(String jobName, int threadPriority) throws Exception {
		enqueueJobRunRequest(normalPriorityQueue, jobName, threadPriority);
	}
	
	@Override
	public void enqueueHighPriorityJobRunRequest(String jobName, int threadPriority) throws Exception {
		enqueueJobRunRequest(highPriorityQueue, jobName, threadPriority);
	}

	private void enqueueJobRunRequest(Queue queue, String jobName, int threadPriority) throws Exception {
		// Who is running the job?  Add the username of the currently authenticated user to the set of job launch parameters.
		LdapUserInfo authenticatedUser = LdapUserInfo.getAuthenticatedUser();
		String userName = (authenticatedUser != null) ? authenticatedUser.getUsername() : null;
		String userEmail = (authenticatedUser != null) ? authenticatedUser.getEmail() : null;

		JobRunRequest requestObj = JobRunRequest.createStartRequest(jobName, threadPriority, userName, userEmail);
		String xmlRequest = requestObj.marshal();
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
