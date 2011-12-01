package com.thomsonreuters.uscl.ereader.orchestrate.core;

import javax.jms.JMSException;
import javax.jms.Queue;

public class MockQueue implements Queue {

	@Override
	public String getQueueName() throws JMSException {
		return "mockQueue_"+System.currentTimeMillis();
	}
}
