package com.thomsonreuters.uscl.ereader.jms.client;

import java.util.List;

import com.thomsonreuters.uscl.ereader.jms.client.impl.JmsClientImpl;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.pool.PooledConnectionFactory;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.jms.core.JmsTemplate;

public final class JMSClientVMTransportTest {
    /* configuration for test queue */
    private static final String BROKER_URL = "vm://localhost?broker.persistent=false";
    private static final String QUEUE = "TEST.QUEUE";

    private JmsTemplate jmsTemplate;
    private JMSClient client;

    @Before
    public void init() {
        client = new JmsClientImpl();
        jmsTemplate = getJmsTemplateWithVMConnection();
        initQueue();
    }

    private static JmsTemplate getJmsTemplateWithVMConnection() {
        final ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory(BROKER_URL);
        final PooledConnectionFactory jmsFactory = new PooledConnectionFactory(connectionFactory);

        final JmsTemplate jmsTemplate = new JmsTemplate(jmsFactory);
        jmsTemplate.setDefaultDestinationName(QUEUE);
        return jmsTemplate;
    }

    @Test
    public void testSendReceive() {
        final List<String> contents = client.receiveMessages(jmsTemplate, "");

        Assert.assertEquals(5, contents.size());
        Assert.assertTrue(contents.contains("aaa"));
        Assert.assertTrue(contents.contains("bbb"));
        Assert.assertTrue(contents.contains("ccc"));
        Assert.assertTrue(contents.contains("ddd"));
        Assert.assertTrue(contents.contains("eee"));
    }

    @Test
    public void testReceiveNext() {
        String content = client.receiveSingleMessage(jmsTemplate, "");
        Assert.assertEquals("aaa", content);
        content = client.receiveSingleMessage(jmsTemplate, "");
        Assert.assertEquals("bbb", content);
        content = client.receiveSingleMessage(jmsTemplate, "");
        Assert.assertEquals("ccc", content);
        content = client.receiveSingleMessage(jmsTemplate, "");
        Assert.assertEquals("ddd", content);
        content = client.receiveSingleMessage(jmsTemplate, "");
        Assert.assertEquals("eee", content);
        content = client.receiveSingleMessage(jmsTemplate, "");
        Assert.assertEquals(null, content);
    }

    @Test
    public void testReceiveByKeyword() {
        client.sendMessageToQueue(jmsTemplate, "abc", null);

        List<String> contents = client.receiveMessages(jmsTemplate, "b");

        Assert.assertEquals(2, contents.size());
        Assert.assertEquals("bbb", contents.get(0));
        Assert.assertEquals("abc", contents.get(1));

        contents = client.receiveMessages(jmsTemplate, "");

        Assert.assertEquals(4, contents.size());
        Assert.assertTrue(contents.contains("aaa"));
        Assert.assertTrue(contents.contains("ccc"));
        Assert.assertTrue(contents.contains("ddd"));
        Assert.assertTrue(contents.contains("eee"));
    }

    @Test
    public void testReceiveNextByKeyword() {
        client.sendMessageToQueue(jmsTemplate, "abc", null);

        String content = client.receiveSingleMessage(jmsTemplate, "b");
        Assert.assertEquals("bbb", content);
        content = client.receiveSingleMessage(jmsTemplate, "b");
        Assert.assertEquals("abc", content);

        final List<String> contents = client.receiveMessages(jmsTemplate, "");

        Assert.assertEquals(4, contents.size());
        Assert.assertTrue(contents.contains("aaa"));
        Assert.assertTrue(contents.contains("ccc"));
        Assert.assertTrue(contents.contains("ddd"));
        Assert.assertTrue(contents.contains("eee"));
    }

    @Test
    public void testClearQueue() {
        client.receiveMessages(jmsTemplate, "");

        final String content = client.receiveSingleMessage(jmsTemplate, "");
        Assert.assertEquals(null, content);
    }

    private void initQueue() {
        client.sendMessageToQueue(jmsTemplate, "aaa", null);
        client.sendMessageToQueue(jmsTemplate, "bbb", null);
        client.sendMessageToQueue(jmsTemplate, "ccc", null);
        client.sendMessageToQueue(jmsTemplate, "ddd", null);
        client.sendMessageToQueue(jmsTemplate, "eee", null);
    }
}
