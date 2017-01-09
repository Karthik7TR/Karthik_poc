/*
 * Copyright 2012: Thomson Reuters Global Resources. All Rights Reserved.
 * Proprietary and Confidential information of TRGR. Disclosure, Use or
 * Reproduction without the written authorization of TRGR is prohibited.
 */

package com.thomsonreuters.uscl.ereader.jms.client;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.jms.JMSException;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;

import com.thomsonreuters.uscl.ereader.jms.client.impl.JmsClientImpl;

public final class JMSClientTest
{
	@Mock
	private JmsTemplate jmsTemplate;

	@Mock
	private Session session;

	@Mock
	private TextMessage message;

	private JmsClientImpl client;

	@Before
	public void setup() throws JMSException
	{
		initMocks(this);

		client = new JmsClientImpl();

		when(session.createTextMessage()).thenReturn(message);
	}

	@Test
	public void createMessage() throws JMSException
	{
		final Map<String, String> properties = new HashMap<String, String>();
		properties.put("prop key", "prop val");

		doAnswer(new Answer<Void>()
		{
			@Override
			public Void answer(final InvocationOnMock invocation) throws JMSException
			{
				final MessageCreator param = (MessageCreator) invocation.getArguments()[0];

				param.createMessage(session);

				return null;
			}
		}).when(jmsTemplate).send(any(MessageCreator.class));

		client.sendMessageToQueue(jmsTemplate, "message text", properties);

		verify(jmsTemplate).send(any(MessageCreator.class));
		verify(session).createTextMessage();
		verify(message).setText("message text");
		verify(message).setStringProperty("prop key", "prop val");
		verifyNotAnymoreInteractions();
	}

	@Test(expected = JMSException.class)
	public void createMessageProblem() throws JMSException
	{
		when(session.createTextMessage()).thenThrow(new JMSException("problem"));

		doAnswer(new Answer<Void>()
		{
			@Override
			public Void answer(final InvocationOnMock invocation) throws JMSException
			{
				final MessageCreator param = (MessageCreator) invocation.getArguments()[0];

				param.createMessage(session);

				return null;
			}
		}).when(jmsTemplate).send(any(MessageCreator.class));

		client.sendMessageToQueue(jmsTemplate, "message text", Collections.<String, String> emptyMap());
	}

	@Test
	public void createMessageEmptyProperties() throws JMSException
	{
		doAnswer(new Answer<Void>()
		{
			@Override
			public Void answer(final InvocationOnMock invocation) throws JMSException
			{
				final MessageCreator param = (MessageCreator) invocation.getArguments()[0];

				param.createMessage(session);

				return null;
			}
		}).when(jmsTemplate).send(any(MessageCreator.class));

		client.sendMessageToQueue(jmsTemplate, "message text", Collections.<String, String> emptyMap());

		verify(jmsTemplate).send(any(MessageCreator.class));
		verify(session).createTextMessage();
		verify(message).setText("message text");
		verifyNotAnymoreInteractions();
	}

	@Test
	public void createMessageNoProperties() throws JMSException
	{
		doAnswer(new Answer<Void>()
		{
			@Override
			public Void answer(final InvocationOnMock invocation) throws JMSException
			{
				final MessageCreator param = (MessageCreator) invocation.getArguments()[0];

				param.createMessage(session);

				return null;
			}
		}).when(jmsTemplate).send(any(MessageCreator.class));

		client.sendMessageToQueue(jmsTemplate, "message text", null);

		verify(jmsTemplate).send(any(MessageCreator.class));
		verify(session).createTextMessage();
		verify(message).setText("message text");
		verifyNotAnymoreInteractions();
	}

	@Test
	public void sendMessageToQueue()
	{
		client.sendMessageToQueue(jmsTemplate, "message text", Collections.<String, String> emptyMap());

		verify(jmsTemplate).send(any(MessageCreator.class));
		verifyNotAnymoreInteractions();
	}

	@Test
	public void sendMessageToQueueNoTemplate()
	{
		client.sendMessageToQueue(null, "message text", Collections.<String, String> emptyMap());

		verifyNotAnymoreInteractions();
	}

	private void verifyNotAnymoreInteractions()
	{
		verifyNoMoreInteractions(jmsTemplate);
		verifyNoMoreInteractions(session);
		verifyNoMoreInteractions(message);
	}
}