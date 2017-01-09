/*
 * Copyright 2013: Thomson Reuters Global Resources. All Rights Reserved.
 * Proprietary and Confidential information of TRGR. Disclosure, Use or
 * Reproduction without the written authorization of TRGR is prohibited.
 */

package com.thomsonreuters.uscl.ereader.jms.client;

import java.util.List;
import java.util.Map;

import org.springframework.jms.core.JmsTemplate;

public interface JMSClient {
	/**
	 * Send a message via the passed <code>JmsTemplate</code>
	 *
	 * @param jmsTemplate The queue template used to send the message
	 * @param messageText The message to send
	 * @param properties Properties to be added to the <code>Message</code>
	 * @throws javax.jms.JMSException TODO
	 */
	void sendMessageToQueue(JmsTemplate jmsTemplate, String messageText, Map<String, String> properties);

	/**
	 * Receives a message via the passed <code>JmsTemplate</code> if the message body contains the search text
	 *
	 * @param jmsTemplate The queue template used to browse and consume messages
	 * @param searchText The text to search for in the body of the message
	 * @return The message bodies of matching messages
	 * @throws javax.jms.JMSException TODO
	 */
	List<String> receiveMessages(JmsTemplate jmsTemplate, String searchText);
	
	String receiveSingleMessageByKeyword(JmsTemplate jmsTemplate, String searchText);

	/**
	 * Receives a message via the passed <code>JmsTemplate</code> if there is a message in the queue
	 * 
	 * @param jmsTemplate The queue template used to read the next message.
	 * @return The message body of the next message in the queue. Null if the queue is empty.
	 */
	String receiveSingleMessage(final JmsTemplate jmsTemplate);


}
