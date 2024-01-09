package com.thomsonreuters.uscl.ereader.jms.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.Map;
import java.util.Properties;

import javax.jms.QueueConnectionFactory;

import com.thomsonreuters.uscl.ereader.jms.client.JMSClient;
import com.thomsonreuters.uscl.ereader.jms.service.impl.JavaMessageServiceImpl;
import com.thomsonreuters.uscl.ereader.jms.service.impl.QueueManager;
import com.thomsonreuters.uscl.ereader.jms.service.impl.QueueType;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.powermock.reflect.Whitebox;
import org.springframework.jms.core.JmsTemplate;

@RunWith(MockitoJUnitRunner.class)
public final class JMSImplTest {
    @InjectMocks
    private JavaMessageServiceImpl service = new JavaMessageServiceImpl();

    @Mock
    private ConnectionFactory mockConnectionFactoryHelper;

    @Mock
    private QueueConnectionFactory mockConnectionFactory;

    @Mock
    private JMSClient mockClient;

    @Mock
    private JmsTemplate mockJmsTemplate;

    private String host = "host";
    private Integer port = 1414;
    private String manager = "queueManager";
    private String connection = "connection";
    private String name = connection;
    private String channel = "channel";
    private int transportType = 1;

    @Before
    public void inTheBeginning() {
        setProperties();
    }

    @After
    public void allWasDarkness() {
        clearProperties();
        System.getProperties().remove(QueueType.UserExperience.toString(0) + ".disable");
    }

    @Test
    public void initialize() throws Exception {
        when(
            mockConnectionFactoryHelper.getNewQueueConnectionFactory(host, port, name, manager, channel, transportType))
                .thenReturn(mockConnectionFactory);
        when(
            mockConnectionFactoryHelper
                .getNewJmsTemplate(mockConnectionFactory, host, port, name, manager, channel, transportType))
                    .thenReturn(mockJmsTemplate);

        service.init();

        verifyInitialization(true);
        final Map<QueueType, QueueManager> queueDescriptorMap =
            Whitebox.getInternalState(service, "queueDescriptorMap");
        assertEquals(1, queueDescriptorMap.size());
        final QueueManager queueManager = queueDescriptorMap.get(QueueType.UserExperience);
        assertNotNull(queueManager);
        assertEquals(true, queueManager.isQueueEnabled());
    }

    @Test
    public void initialize_not() throws Exception {
        port = 0;
        setProperties();

        service.init();

        verifyInitialization(false);
    }

    @Test
    public void initialize_explicitlyEnabled() throws Exception {
        when(
            mockConnectionFactoryHelper.getNewQueueConnectionFactory(host, port, name, manager, channel, transportType))
                .thenReturn(mockConnectionFactory);
        when(
            mockConnectionFactoryHelper
                .getNewJmsTemplate(mockConnectionFactory, host, port, name, manager, channel, transportType))
                    .thenReturn(mockJmsTemplate);

        System.setProperty(QueueType.UserExperience.toString() + ".disable", "false");
        service.init();

        verifyInitialization(true);
    }

    @Test
    public void initialize_explicitlyDisabled() throws Exception {
        System.setProperty(QueueType.UserExperience.toString() + ".disable", "true");
        service.init();

        verifyInitialization(false);
    }

    private void setProperties() {
        final Properties props = System.getProperties();
        props.put(QueueType.UserExperience.host(connection), host);
        props.put(QueueType.UserExperience.port(connection), port.toString());
        props.put(QueueType.UserExperience.queueName(), manager);
        props.put(QueueType.UserExperience.name(), name);
        props.put(QueueType.UserExperience.connections(), connection);
        props.put(QueueType.UserExperience.channel(connection), channel);
        props.put(QueueType.UserExperience.transportType(connection), Integer.toString(transportType));
    }

    private void clearProperties() {
        final Properties props = System.getProperties();
        props.remove(QueueType.UserExperience.host(connection));
        props.remove(QueueType.UserExperience.port(connection));
        props.remove(QueueType.UserExperience.queueName());
        props.remove(QueueType.UserExperience.name());
        props.remove(QueueType.UserExperience.connections());
        props.remove(QueueType.UserExperience.channel(connection));
        props.remove(QueueType.UserExperience.transportType(connection));
    }

    private void verifyInitialization(final boolean enabled) throws Exception {
        if (enabled) {
            verify(mockConnectionFactoryHelper)
                .getNewQueueConnectionFactory(host, port, name, manager, channel, transportType);
            verify(mockConnectionFactoryHelper)
                .getNewJmsTemplate(mockConnectionFactory, host, port, name, manager, channel, transportType);
        } else {
            verify(mockConnectionFactoryHelper, never())
                .getNewQueueConnectionFactory(host, port, name, manager, channel, transportType);
            verify(mockConnectionFactoryHelper, never())
                .getNewJmsTemplate(mockConnectionFactory, host, port, name, manager, channel, transportType);
        }
    }

    @Test
    public void enableQueue() {
        final QueueManager mockQueueManager = mock(QueueManager.class);
        final Map<QueueType, QueueManager> queueDescriptorMap =
            Whitebox.getInternalState(service, "queueDescriptorMap");
        queueDescriptorMap.put(QueueType.UserExperience, mockQueueManager);

        when(mockQueueManager.enableQueue()).thenReturn(true);

        final boolean actual = service.enableQueue(QueueType.UserExperience);

        assertEquals(true, actual);
        verify(mockQueueManager).enableQueue();
    }

    @Test
    public void enableQueue_fail() {
        final QueueManager mockQueueManager = mock(QueueManager.class);
        final Map<QueueType, QueueManager> queueDescriptorMap =
            Whitebox.getInternalState(service, "queueDescriptorMap");
        queueDescriptorMap.put(QueueType.UserExperience, mockQueueManager);

        when(mockQueueManager.enableQueue()).thenReturn(false);

        final boolean actual = service.enableQueue(QueueType.UserExperience);

        assertEquals(false, actual);
        verify(mockQueueManager).enableQueue();
    }

    @Test
    public void enableQueue_noQueues() {
        final boolean actual = service.enableQueue(QueueType.UserExperience);

        assertEquals(false, actual);
    }

    @Test
    public void disableQueue() {
        final QueueManager mockQueueManager = mock(QueueManager.class);
        final Map<QueueType, QueueManager> queueDescriptorMap =
            Whitebox.getInternalState(service, "queueDescriptorMap");
        queueDescriptorMap.put(QueueType.UserExperience, mockQueueManager);

        when(mockQueueManager.disableQueue()).thenReturn(true);

        final boolean actual = service.disableQueue(QueueType.UserExperience);

        assertEquals(true, actual);
        verify(mockQueueManager).disableQueue();
    }

    @Test
    public void sendMessageToQueue() {
        final String messageText = "messageText";
        final Map<String, String> properties = Collections.emptyMap();

        final QueueManager mockQueueManager = mock(QueueManager.class);
        final Map<QueueType, QueueManager> queueDescriptorMap =
            Whitebox.getInternalState(service, "queueDescriptorMap");
        queueDescriptorMap.put(QueueType.UserExperience, mockQueueManager);

        service.sendMessageToQueue(QueueType.UserExperience, messageText, properties);

        verify(mockQueueManager).sendMessageToQueue(messageText, properties);
    }

    @Test
    // matches enabled for now as test occurs a level deeper.
    public void sendMessageToQueue_disabled() {
        final String messageText = "messageText";
        final Map<String, String> properties = Collections.emptyMap();

        final QueueManager mockQueueManager = mock(QueueManager.class);
        final Map<QueueType, QueueManager> queueDescriptorMap =
            Whitebox.getInternalState(service, "queueDescriptorMap");
        queueDescriptorMap.put(QueueType.UserExperience, mockQueueManager);

        service.sendMessageToQueue(QueueType.UserExperience, messageText, properties);

        verify(mockQueueManager).sendMessageToQueue(messageText, properties);
    }
}
