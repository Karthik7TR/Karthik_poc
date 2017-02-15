package com.thomsonreuters.uscl.ereader.jms.service.impl;

import javax.jms.JMSException;
import javax.jms.QueueConnectionFactory;

import com.ibm.mq.jms.MQQueueConnectionFactory;
import com.thomsonreuters.uscl.ereader.jms.client.impl.JmsClientImpl;
import com.thomsonreuters.uscl.ereader.jms.service.ConnectionFactory;
import org.apache.log4j.Logger;
import org.springframework.context.annotation.Lazy;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;

@Component("mqConnectionFactory")
@Lazy
public class MQConnectionFactory implements ConnectionFactory
{
    private static final Logger log = Logger.getLogger(JmsClientImpl.class);

    @Override
    public QueueConnectionFactory getNewQueueConnectionFactory(
        final String host,
        final int port,
        final String queueManager,
        final String queue,
        final String channel,
        final int transportType) throws JMSException
    {
        final MQQueueConnectionFactory factory = new MQQueueConnectionFactory();
        factory.setHostName(host);
        factory.setPort(port);
        factory.setQueueManager(queueManager);
        factory.setChannel(channel);
        factory.setTransportType(transportType);

        log.info("factory returned");

        return factory;
    }

    @Override
    public JmsTemplate getNewJmsTemplate(
        final QueueConnectionFactory connectionFactory,
        final String host,
        final int port,
        final String queueManager,
        final String queue,
        final String channel,
        final int transportType)
    {
        final JmsTemplate jmsTemplate = new JmsTemplate(connectionFactory);
        jmsTemplate.setDefaultDestinationName(queue);

        log.info("template returned");

        return jmsTemplate;
    }
}
