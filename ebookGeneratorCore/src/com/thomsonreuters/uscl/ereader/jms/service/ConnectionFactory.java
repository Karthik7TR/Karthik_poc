package com.thomsonreuters.uscl.ereader.jms.service;

import javax.jms.JMSException;
import javax.jms.QueueConnectionFactory;

import org.springframework.jms.core.JmsTemplate;

/**
 * @author C089278
 *
 */
public interface ConnectionFactory {
    /**
     * Return a queue connection factory instance
     *
     * @param host
     * @param port
     * @param queueManager
     * @param queue
     * @param channel
     * @param transportType
     * @return
     * @throws JMSException
     */
    QueueConnectionFactory getNewQueueConnectionFactory(
        String host,
        int port,
        String queueManager,
        String queue,
        String channel,
        int transportType) throws JMSException;

    /**
     * Return a template instance used to send messages
     *
     * @param connectionFactory
     * @param host
     * @param port
     * @param queueManager
     * @param queue
     * @param channel
     * @param transportType
     * @return
     */
    JmsTemplate getNewJmsTemplate(
        QueueConnectionFactory connectionFactory,
        String host,
        int port,
        String queueManager,
        String queue,
        String channel,
        int transportType);
}
