package com.thomsonreuters.uscl.ereader.jms.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.jms.JMSException;
import javax.jms.QueueConnectionFactory;

import com.thomsonreuters.uscl.ereader.jms.client.JMSClient;
import com.thomsonreuters.uscl.ereader.jms.service.impl.QueueDescriptor;
import com.thomsonreuters.uscl.ereader.jms.service.impl.QueueManager;
import com.thomsonreuters.uscl.ereader.jms.service.impl.QueueType;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;
import org.powermock.reflect.Whitebox;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.support.converter.MessageConversionException;

@RunWith(MockitoJUnitRunner.class)
public final class QueueManagerImplTest {
    private QueueManager service;
    private List<QueueDescriptor> descriptors;
    private QueueDescriptor descriptor;
    private String host = "host";
    private String connection = "connection";
    private Integer port = 1414;
    private String queueManager = "queueManager";
    private String queue = connection;
    private String channel = "channel";
    private int transportType = 42;
    private String messageText = "messageText";
    private Map<String, String> properties = Collections.emptyMap();

    @Mock
    private ConnectionFactory mockConnectionFactoryHelper;

    @Mock
    private QueueConnectionFactory mockConnectionFactory;

    @Mock
    private JMSClient mockJmsClient;

    @Mock
    private JmsTemplate mockJmsTemplate;

    @Before
    public void letThereBeLight() {
        service = new QueueManager(QueueType.UserExperience, mockConnectionFactoryHelper, mockJmsClient);
        descriptors = Whitebox.getInternalState(service, "descriptors");
        setProperties(0);
        descriptor = new QueueDescriptor(QueueType.UserExperience, connection);
        descriptors.add(descriptor);
    }

    @After
    public void andAllWasDarkness() {
        clearProperties(0);
    }

    @Test
    public void add_and_size_and_enabled() {
        descriptors.clear();
        service.add(descriptor);
        assertEquals(1, descriptors.size());
        assertEquals(descriptor, descriptors.get(0));
        assertEquals(false, Whitebox.getInternalState(service, "enabled"));
    }

    @Test
    public void initialize() throws Exception {
        initializeAValidQueue();

        Whitebox.invokeMethod(service, "initialize", descriptor);

        verify(mockConnectionFactoryHelper)
            .getNewJmsTemplate(mockConnectionFactory, host, port, queue, queueManager, channel, transportType);
    }

    @Test
    public void failover() throws Exception {
        initializeAValidQueue();

        final boolean actual =
            Whitebox.<Boolean>invokeMethod(service, "failover", (String) null, (Map<String, String>) null);

        verify(mockConnectionFactoryHelper)
            .getNewJmsTemplate(mockConnectionFactory, host, port, queue, queueManager, channel, transportType);
        verify(mockJmsClient, never()).sendMessageToQueue(mockJmsTemplate, null, null);
        assertNotNull(Whitebox.getInternalState(service, "jmsTemplate"));
        assertEquals(true, actual);
    }

    @Test
    public void failover_withMessage() throws Exception {
        initializeAValidQueue();

        final boolean actual = Whitebox.<Boolean>invokeMethod(service, "failover", messageText, properties);

        verify(mockJmsClient).sendMessageToQueue(mockJmsTemplate, messageText, properties);
        assertEquals(true, actual);
        assertNotNull(Whitebox.getInternalState(service, "jmsTemplate"));
    }

    @Test
    public void failover_withMessage_failed() throws Exception {
        doThrow(new JMSException("throw me")).when(mockConnectionFactoryHelper)
            .getNewQueueConnectionFactory(host, port, queue, queueManager, channel, transportType);

        final boolean actual = Whitebox.<Boolean>invokeMethod(service, "failover", messageText, properties);

        verify(mockJmsClient, never()).sendMessageToQueue(mockJmsTemplate, messageText, properties);
        assertNull(Whitebox.getInternalState(service, "jmsTemplate"));
        assertEquals(false, actual);
    }

    @Test
    public void enableQueue() throws Exception {
        initializeAValidQueue();
        Whitebox.setInternalState(service, "enabled", false);

        final boolean actual = service.enableQueue();

        assertEquals(true, actual);
        assertEquals(true, Whitebox.getInternalState(service, "enabled"));
        assertEquals(mockJmsTemplate, Whitebox.getInternalState(service, "jmsTemplate"));
    }

    @Test
    public void enableQueue_alreadyEnabled() {
        Whitebox.setInternalState(service, "enabled", true);

        final boolean actual = service.enableQueue();

        assertEquals(true, actual);
        assertEquals(true, Whitebox.getInternalState(service, "enabled"));
        verify(mockConnectionFactoryHelper, never())
            .getNewJmsTemplate(mockConnectionFactory, host, port, queue, queueManager, channel, transportType);
    }

    @Test
    public void enableQueue_failed() throws Exception {
        Whitebox.setInternalState(service, "enabled", false);
        doThrow(new JMSException("throw me")).when(mockConnectionFactoryHelper)
            .getNewQueueConnectionFactory(host, port, queue, queueManager, channel, transportType);

        final boolean actual = service.enableQueue();
        assertEquals(false, actual);
        assertEquals(false, Whitebox.getInternalState(service, "enabled"));
    }

    @Test
    public void disableQueue() {
        final boolean actual = service.disableQueue();

        assertEquals(true, actual);
    }

    @Test
    public void sendMessageToQueue() {
        Whitebox.setInternalState(service, "enabled", true);
        Whitebox.setInternalState(service, "jmsTemplate", mockJmsTemplate);

        final boolean actual = service.sendMessageToQueue(messageText, properties);

        assertEquals(true, actual);
        verify(mockJmsClient).sendMessageToQueue(mockJmsTemplate, messageText, properties);
    }

    @Test
    public void sendMessageToQueue_withFailover() throws Exception {
        initializeAValidQueue();
        Whitebox.setInternalState(service, "jmsTemplate", (JmsTemplate) null);
        Whitebox.setInternalState(service, "enabled", true);
        doAnswer(new Answer<Boolean>() {
            @Override
            public Boolean answer(final InvocationOnMock invocation) throws Throwable {
                if (invocation.getArguments()[0] == null) {
                    throw new MessageConversionException("throw me");
                }
                return true;
            }
        }).when(mockJmsClient).sendMessageToQueue(any(JmsTemplate.class), eq(messageText), eq(properties));

        final boolean actual = service.sendMessageToQueue(messageText, properties);

        assertEquals(true, actual);
        verify(mockJmsClient, times(2)).sendMessageToQueue(any(JmsTemplate.class), eq(messageText), eq(properties));
    }

    @Test
    public void sendMessageToQueue_withFailover_fails() throws Exception {
        initializeAValidQueue();
        Whitebox.setInternalState(service, "jmsTemplate", mockJmsTemplate);
        Whitebox.setInternalState(service, "enabled", true);
        doThrow(new MessageConversionException("throw me")).when(mockJmsClient)
            .sendMessageToQueue(any(JmsTemplate.class), eq(messageText), eq(properties));

        final boolean actual = service.sendMessageToQueue(messageText, properties);

        assertEquals(false, actual);
        verify(mockJmsClient, times(2)).sendMessageToQueue(mockJmsTemplate, messageText, properties);
    }

    private void initializeAValidQueue() throws Exception {
        when(
            mockConnectionFactoryHelper
                .getNewQueueConnectionFactory(host, port, queue, queueManager, channel, transportType))
                    .thenReturn(mockConnectionFactory);
        when(
            mockConnectionFactoryHelper
                .getNewJmsTemplate(mockConnectionFactory, host, port, queue, queueManager, channel, transportType))
                    .thenReturn(mockJmsTemplate);
    }

    private void setProperties(final int queueNumber) {
        final Properties props = System.getProperties();
        props.put(QueueType.UserExperience.host(connection), host);
        props.put(QueueType.UserExperience.port(connection), port.toString());
        props.put(QueueType.UserExperience.queueName(), queueManager);
        props.put(QueueType.UserExperience.connections(), connection);
        props.put(QueueType.UserExperience.name(), queue);
        props.put(QueueType.UserExperience.channel(connection), channel);
        props.put(QueueType.UserExperience.transportType(connection), Integer.toString(transportType));
    }

    private void clearProperties(final int queueNumber) {
        final Properties props = System.getProperties();
        props.remove(QueueType.UserExperience.host(connection));
        props.remove(QueueType.UserExperience.port(connection));
        props.remove(QueueType.UserExperience.queueName());
        props.remove(QueueType.UserExperience.connections());
        props.remove(QueueType.UserExperience.name());
        props.remove(QueueType.UserExperience.channel(connection));
        props.remove(QueueType.UserExperience.transportType(connection));
    }
}
