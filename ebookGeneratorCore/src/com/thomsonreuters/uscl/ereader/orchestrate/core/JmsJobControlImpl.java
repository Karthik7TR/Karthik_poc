package com.thomsonreuters.uscl.ereader.orchestrate.core;

import javax.jms.JMSException;
import javax.jms.Queue;
import javax.jms.QueueConnection;
import javax.jms.QueueConnectionFactory;
import javax.jms.QueueSender;
import javax.jms.QueueSession;
import javax.jms.Session;
import javax.jms.TextMessage;

public class JmsJobControlImpl implements JobControl {
	
	private QueueConnectionFactory qcf;
	private Queue normalQueue;
	private Queue immediateQueue;


	@Override
	public void startJob(String jobName) throws Exception {
		startJob(jobName, Thread.NORM_PRIORITY);
	}
	
	@Override
	public void startJob(String jobName, Integer threadPriority) throws Exception {
		startJob(normalQueue, jobName, threadPriority);
	}
	
	@Override
	public void startJobImmediately(String jobName) throws Exception {
		startJob(immediateQueue, jobName, Thread.MAX_PRIORITY);
	}
	

	private void startJob(Queue queue, String jobName, Integer priority) throws Exception {
		JobRunRequest requestObj = JobRunRequest.createStartRequest(jobName, priority);
		String xmlRequest = requestObj.marshal();
		sendJmsMessage(queue, xmlRequest);
	}

	/**
	 * Send a message to the queue configured to accept job operation related messages.
	 * @param queue the JMS message queue resource
	 * @param payload the XML job operation request, a marshalled JobControlRequest object
	 * @throws JMSException on problems using messaging system
	 */
	private void sendJmsMessage(Queue queue, String payload) throws JMSException {
		QueueSession session = null;
		try {
			QueueConnection connection = qcf.createQueueConnection();
			session = connection.createQueueSession(false, Session.AUTO_ACKNOWLEDGE);
			TextMessage message = session.createTextMessage(payload);
			QueueSender sender = session.createSender(queue);
			sender.send(message);
		} finally {
			if (session != null) {
				session.close();
			}
		}
	}
	
	public void setQueueConnectionFacory(QueueConnectionFactory qcf) {
		this.qcf = qcf;
	}
}
