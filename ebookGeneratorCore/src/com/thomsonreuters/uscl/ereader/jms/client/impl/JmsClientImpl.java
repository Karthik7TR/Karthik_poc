/*
 * Copyright 2012: Thomson Reuters Global Resources. All Rights Reserved.
 * Proprietary and Confidential information of TRGR. Disclosure, Use or
 * Reproduction without the written authorization of TRGR is prohibited.
 */

package com.thomsonreuters.uscl.ereader.jms.client.impl;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.QueueBrowser;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.apache.log4j.Logger;
import org.springframework.context.annotation.Lazy;
import org.springframework.jms.JmsException;
import org.springframework.jms.core.BrowserCallback;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.stereotype.Component;

import com.thomsonreuters.uscl.ereader.jms.client.JMSClient;


@Component("jmsClient")
@Lazy
public class JmsClientImpl implements JMSClient
{
	private static final Logger logger = Logger.getLogger(JmsClientImpl.class);

	@Override
	public void sendMessageToQueue(
		final JmsTemplate jmsTemplate,
		final String messageText,
		final Map<String, String> properties)
	{
		if (jmsTemplate == null)
		{
			logger.info("Message queue has not been initialized");
			return;
		}
		try
		{
			jmsTemplate.send(new MessageCreator()
			{
				@Override
				public Message createMessage(final Session session) throws JMSException
				{
					try
					{
						final TextMessage message = session.createTextMessage();
						message.setText(messageText);
						addPropertiesToMessage(properties, message);
						return message;
					}
					catch (final JMSException e)
					{
						logger.error("Error creating message", e);
						throw e;
					}
				}
			});
		}
		catch (final JmsException e)
		{
			logger.error("Issues sending the message", e);
			throw e;
		}

		logger.debug("Message has been sent");
	}

	private void addPropertiesToMessage(final Map<String, String> properties, final Message message)
		throws JMSException
	{
		if (properties != null && !properties.isEmpty())
		{
			for (final Map.Entry<String, String> entry : properties.entrySet())
			{
				message.setStringProperty(entry.getKey(), entry.getValue());
			}
		}
	}

	@Override
	@SuppressWarnings(
	{ "unchecked" })
	public List<String> receiveMessages(final JmsTemplate jmsTemplate, final String searchText)
	{
		final List<String> messages = jmsTemplate.browse(new BrowserCallback<List<String>>()
		{
			@Override
			public List<String> doInJms(final Session session, final QueueBrowser browser) throws JMSException
			{
				final List<String> messages = new ArrayList<String>();
				final Enumeration<Message> enumeration = browser.getEnumeration();
				while (enumeration.hasMoreElements())
				{
					final TextMessage msg = (TextMessage) enumeration.nextElement();
					final String msgBody = msg.getText();
					if (msgBody.contains(searchText))
					{
						messages.add(msgBody);
						final MessageConsumer consumer =
							session.createConsumer(browser.getQueue(), "JMSMessageID='" + msg.getJMSMessageID() + "'");
						consumer.receive(1000);
						consumer.close();
					}
				}
				return messages;
			}
		});

		return messages;
	}
	
	@Override
	@SuppressWarnings(
	{ "unchecked" })
	public String receiveSingleMessageByKeyword(final JmsTemplate jmsTemplate, final String searchText)
	{
		final String messages = jmsTemplate.browse(new BrowserCallback<String>()
		{
			@Override
			public String doInJms(final Session session, final QueueBrowser browser) throws JMSException
			{
				String message = null;
				final Enumeration<Message> enumeration = browser.getEnumeration();
				while (enumeration.hasMoreElements())
				{
					final TextMessage msg = (TextMessage) enumeration.nextElement();
					final String msgBody = msg.getText();
					if (msgBody.contains(searchText))
					{
						message = msgBody;
						final MessageConsumer consumer =
							session.createConsumer(browser.getQueue(), "JMSMessageID='" + msg.getJMSMessageID() + "'");
						consumer.receive(1000);
						consumer.close();
						break;
					}
				}
				return message;
			}
		});

		return messages;
	}
	
	@Override
	public String receiveSingleMessage(final JmsTemplate jmsTemplate)
	{
		return receiveSingleMessageByKeyword(jmsTemplate, "");
	}
}
