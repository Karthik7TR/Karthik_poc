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
     * Receives all messages via the passed <code>JmsTemplate</code> where the message body contains the search text
     *
     * @param jmsTemplate The queue template used to browse and consume messages
     * @param searchText The text to search for in the body of the message
     * @return The message bodies of matching messages. Null if no such message is found
     * @throws javax.jms.JMSException TODO
     */
    List<String> receiveMessages(JmsTemplate jmsTemplate, String searchText);
}
