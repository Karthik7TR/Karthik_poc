package com.thomsonreuters.uscl.ereader.jms.client;

import java.util.List;
import javax.jms.JMSException;
import javax.jms.QueueConnectionFactory;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.jms.core.JmsTemplate;

import com.thomsonreuters.uscl.ereader.jms.client.impl.JmsClientImpl;
import com.thomsonreuters.uscl.ereader.jms.service.impl.MQConnectionFactory;

@Ignore
public class JMSClientIntegrationTest {
	/* configuration for test queue in CI Queue Manager */
	private static final String HOST = "CTCO002-04";
	private static final int PORT = 1414;
	private static final String QUEUE_MANAGER = "CTCO00204";
	private static final String QUEUE = "TEST.QUEUE";
	private static final String CHANNEL = "CLIENTCONNECTION";
	private static final int TRANSPORT_TYPE = 1;

	private JmsTemplate jmsTemplate;
	private JMSClient client;

	@Before
	public void init() {
		client = new JmsClientImpl();

		MQConnectionFactory factory = new MQConnectionFactory();
		try {
			QueueConnectionFactory connectionFactory = factory.getNewQueueConnectionFactory(HOST, PORT, QUEUE_MANAGER, QUEUE, CHANNEL,
					TRANSPORT_TYPE);
			jmsTemplate = factory.getNewJmsTemplate(connectionFactory, HOST, PORT, QUEUE_MANAGER, QUEUE, CHANNEL, TRANSPORT_TYPE);
			
			// clear queue
			client.receiveMessages(jmsTemplate, "");
		} catch (JMSException e) {
			e.printStackTrace();
		}
	}

	@After
	public void cleanUp() {
		// clear queue
		client.receiveMessages(jmsTemplate, "");
	}

	@Test
	public void testSendReceive() {
		initQueue();
		List<String> contents = client.receiveMessages(jmsTemplate, "");

		Assert.assertEquals(5, contents.size());
		Assert.assertTrue(contents.contains("aaa"));
		Assert.assertTrue(contents.contains("bbb"));
		Assert.assertTrue(contents.contains("ccc"));
		Assert.assertTrue(contents.contains("ddd"));
		Assert.assertTrue(contents.contains("eee"));
	}

	@Test
	public void testReceiveNext() {
		initQueue();

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
		initQueue();
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
		initQueue();
		client.sendMessageToQueue(jmsTemplate, "abc", null);

		String content = client.receiveSingleMessage(jmsTemplate, "b");
		Assert.assertEquals("bbb", content);
		content = client.receiveSingleMessage(jmsTemplate, "b");
		Assert.assertEquals("abc", content);

		List<String> contents = client.receiveMessages(jmsTemplate, "");

		Assert.assertEquals(4, contents.size());
		Assert.assertTrue(contents.contains("aaa"));
		Assert.assertTrue(contents.contains("ccc"));
		Assert.assertTrue(contents.contains("ddd"));
		Assert.assertTrue(contents.contains("eee"));
	}

	@Test
	public void testClearQueue() {
		initQueue();

		client.receiveMessages(jmsTemplate, "");

		String content = client.receiveSingleMessage(jmsTemplate, "");
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