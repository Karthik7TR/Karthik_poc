package com.thomsonreuters.uscl.ereader.jms.service;

import java.util.List;
import java.util.Map;

import com.thomsonreuters.uscl.ereader.jms.service.impl.QueueType;

public interface JavaMessageService {
    boolean enableQueue(QueueType queueType);

    boolean disableQueue(QueueType queueType);

    /**
     * Send a text message to a JMS queue
     *
     * @param queueType
     *
     * @param messageText
     *            The text to send
     * @param properties
     *            Properties to be added to the <code>Message</code>
     */
    void sendMessageToQueue(QueueType queueType, String messageText, Map<String, String> properties);

    /**
     * Consumes messages via the passed <code>JmsTemplate</code> if the message
     * body contains the search text
     *
     * @param queueType
     *
     * @param searchText
     *            The text to search for in the body of the message
     * @throws javax.jms.JMSException
     *
     */
    List<String> receiveMessages(QueueType queueType, String collaborationKey);
}
