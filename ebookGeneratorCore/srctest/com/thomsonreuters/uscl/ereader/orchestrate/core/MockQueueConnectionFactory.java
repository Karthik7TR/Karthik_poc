package com.thomsonreuters.uscl.ereader.orchestrate.core;

import javax.jms.Connection;
import javax.jms.JMSException;
import javax.jms.QueueConnection;
import javax.jms.QueueConnectionFactory;

public class MockQueueConnectionFactory implements QueueConnectionFactory {

	@Override
	public Connection createConnection() throws JMSException {
		return null;
	}

	@Override
	public Connection createConnection(String paramString1, String paramString2)
			throws JMSException {
		return null;
	}

	@Override
	public QueueConnection createQueueConnection() throws JMSException {
		return null;
	}

	@Override
	public QueueConnection createQueueConnection(String paramString1,
			String paramString2) throws JMSException {
		return null;
	}
}
