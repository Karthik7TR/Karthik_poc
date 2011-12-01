package com.thomsonreuters.uscl.ereader.orchestrate.core;

import javax.jms.Destination;

import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;

public class MockJmsTemplate extends JmsTemplate {
	
	@Override
	public void send(Destination dest, MessageCreator mc) {
		
	}
}
