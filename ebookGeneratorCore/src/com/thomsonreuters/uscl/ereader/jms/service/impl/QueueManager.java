package com.thomsonreuters.uscl.ereader.jms.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.jms.JMSException;
import javax.jms.QueueConnectionFactory;

import com.thomsonreuters.uscl.ereader.jms.client.JMSClient;
import com.thomsonreuters.uscl.ereader.jms.service.ConnectionFactory;
import org.apache.log4j.Logger;
import org.springframework.jms.JmsException;
import org.springframework.jms.core.JmsTemplate;

/**
 * @author C089278 - Robert Hannah
 *
 */
public class QueueManager
{
    private static final Logger log = Logger.getLogger(JavaMessageServiceImpl.class);

    private QueueType queueType;
    private ConnectionFactory factory;
    private JMSClient jmsClient;
    private JmsTemplate jmsTemplate;
    private boolean enabled;
    private List<QueueDescriptor> descriptors = new ArrayList<>();

    public QueueManager(final QueueType queueType, final ConnectionFactory factory, final JMSClient jmsClient)
    {
        this.queueType = queueType;
        this.factory = factory;
        this.jmsClient = jmsClient;
    }

    public void add(final QueueDescriptor descriptor)
    {
        descriptors.add(descriptor);
    }

    public int size()
    {
        return descriptors.size();
    }

    public boolean isQueueEnabled()
    {
        return enabled;
    }

    public boolean enableQueue()
    {
        if (enabled)
        {
            return true;
        }

        failover(null, null);
        if (jmsTemplate != null)
        {
            enabled = true;
            log.info(queueMessage("Event queue has been enabled"));
            return true;
        }

        log.warn(queueMessage("Unable to enable event queue - queue failed initialization"));
        return false;
    }

    public boolean disableQueue()
    {
        enabled = false;
        jmsTemplate = null;
        log.info(queueMessage("Event queue has been disabled"));
        return true;
    }

    public boolean sendMessageToQueue(final String messageText, final Map<String, String> properties)
    {
        if (enabled)
        {
            try
            {
                jmsClient.sendMessageToQueue(jmsTemplate, messageText, properties);
                return true;
            }
            catch (final JmsException e)
            {
                log.error(queueMessage("Error sending message - changing destination and retrying"), e);
                return failover(messageText, properties);
            }
        }

        return false;
    }

    public List<String> receiveMessages(final String searchText)
    {
        List<String> messages = null;

        if (enabled)
        {
            messages = jmsClient.receiveMessages(jmsTemplate, searchText);
        }

        return messages;
    }

    private synchronized void initialize(final QueueDescriptor descriptor) throws JMSException
    {
        final QueueConnectionFactory connectionFactory = factory.getNewQueueConnectionFactory(
            descriptor.getHost(),
            descriptor.getPort(),
            descriptor.getManager(),
            descriptor.getName(),
            descriptor.getChannel(),
            descriptor.getTransportType());
        jmsTemplate = factory.getNewJmsTemplate(
            connectionFactory,
            descriptor.getHost(),
            descriptor.getPort(),
            descriptor.getManager(),
            descriptor.getName(),
            descriptor.getChannel(),
            descriptor.getTransportType());
    }

    private synchronized boolean failover(final String messageText, final Map<String, String> properties)
    {
        QueueDescriptor activeDescriptor = null;
        for (final QueueDescriptor descriptor : descriptors)
        {
            try
            {
                initialize(descriptor);
                if (messageText != null)
                {
                    jmsClient.sendMessageToQueue(jmsTemplate, messageText, properties);
                }
                activeDescriptor = descriptor;
                break;
            }
            catch (final JMSException e)
            {
                jmsTemplate = null;
                log.error(queueMessage("Error initializing queue connection factory for descriptor: " + descriptor), e);
            }
            catch (final JmsException e)
            {
                jmsTemplate = null;
                log.error(queueMessage("Error initializing queue connection factory for descriptor: " + descriptor), e);
            }
        }

        // Push it to the top so that on a future failure, it is first retried before moving on.
        if (activeDescriptor != null)
        {
            descriptors.remove(activeDescriptor);
            descriptors.add(0, activeDescriptor);
            return true;
        }

        log.error(queueMessage("Could not send message - no queues succeeded"));
        return false;
    }

    private String queueMessage(final String message)
    {
        return queueType.toString() + ": " + message;
    }
}
