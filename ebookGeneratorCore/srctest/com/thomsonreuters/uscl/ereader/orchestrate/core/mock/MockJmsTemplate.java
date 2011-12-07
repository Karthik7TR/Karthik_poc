package com.thomsonreuters.uscl.ereader.orchestrate.core.mock;

import javax.jms.Destination;
import javax.jms.Message;

import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;

public class MockJmsTemplate extends JmsTemplate {
	
	@Override
	public void send(Destination dest, MessageCreator mc) {
		
	}
	
//	@Override
//	public Message receive(Destination dest) {
//		
//	}
}
