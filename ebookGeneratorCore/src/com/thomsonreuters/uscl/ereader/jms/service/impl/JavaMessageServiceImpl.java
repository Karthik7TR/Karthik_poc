/*
 * Copyright 2012: Thomson Reuters Global Resources. All Rights Reserved.
 * Proprietary and Confidential information of TRGR. Disclosure, Use or
 * Reproduction without the written authorization of TRGR is prohibited.
 */

package com.thomsonreuters.uscl.ereader.jms.service.impl;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.util.StringUtils;

import com.thomsonreuters.uscl.ereader.jms.client.JMSClient;
import com.thomsonreuters.uscl.ereader.jms.service.ConnectionFactory;
import com.thomsonreuters.uscl.ereader.jms.service.JavaMessageService;

public class JavaMessageServiceImpl implements JavaMessageService
{
	private static final Logger log = Logger.getLogger(JavaMessageServiceImpl.class);
	private static final int MAX_QUEUES_PER_TYPE = 5;

	private Map<QueueType, QueueManager> queueDescriptorMap = new EnumMap<QueueType, QueueManager>(QueueType.class);

	@Autowired
	@Qualifier("jmsClient")
	private JMSClient jmsClient;

	@Autowired
	@Qualifier("mqConnectionFactory")
	private ConnectionFactory factory;

	@PreDestroy
	public void destroy()
	{
		log.debug("@PreDestroy: cleaning up");
	}

	@PostConstruct
	public void init()
	{
		log.info("@PostConstruct: initializing");
		for (final QueueType queueType : QueueType.values())
		{
			final QueueManager manager = new QueueManager(queueType, factory, jmsClient);
			final String connections = System.getProperty(queueType.connections());
			if (StringUtils.hasLength(connections))
			{
				final String[] connectionsSplit = connections.split("\\|");
				for (int eventNumber = 0; eventNumber < MAX_QUEUES_PER_TYPE && eventNumber < connectionsSplit.length; eventNumber++)
				{
					initializeAndAddQueueDescriptor(queueType, connectionsSplit[eventNumber], manager);
				}
			}

			if (manager.size() > 0)
			{
				if (manager.enableQueue())
				{
					queueDescriptorMap.put(queueType, manager);
					log.info("@PostConstruct: " + queueMessage(queueType, "queue has been initialized"));
				}
				else
				{
					log.warn("@PostConstruct: " + queueMessage(queueType, "could not enable queue - skipping"));
				}
			}
			else
			{
				log.warn("@PostConstruct: " + queueMessage(queueType, "queue not initialized!"));
			}
		}

		log.info("@PostConstruct: initialization complete");
	}

	private void initializeAndAddQueueDescriptor(
		final QueueType queueType,
		final String queueName,
		final QueueManager manager)
	{
		final QueueDescriptor queueDescriptor = new QueueDescriptor(queueType, queueName);
		if (queueDescriptor.isValid())
		{
			manager.add(queueDescriptor);
		}
	}

	@Override
	public boolean enableQueue(final QueueType queueType)
	{
		final QueueManager manager = queueDescriptorMap.get(queueType);

		if (manager == null)
		{
			log.warn(queueMessage(queueType, "queue not found"));
			return false;
		}

		return manager.enableQueue();
	}

	@Override
	public boolean disableQueue(final QueueType queueType)
	{
		final QueueManager manager = queueDescriptorMap.get(queueType);

		if (manager == null)
		{
			log.warn(queueMessage(queueType, "queue not found"));
			return false;
		}

		return manager.disableQueue();
	}

	@Override
	public void sendMessageToQueue(
		final QueueType queueType,
		final String messageText,
		final Map<String, String> properties)
	{
		final QueueManager manager = queueDescriptorMap.get(queueType);

		if (manager == null)
		{
			log.warn(queueMessage(queueType, "queue not found"));
		}
		else
		{
			manager.sendMessageToQueue(messageText, properties);
		}
	}

	@Override
	public List<String> receiveMessages(final QueueType queueType, final String searchText)
	{
		List<String> messages = null;

		final QueueManager manager = queueDescriptorMap.get(queueType);

		if (manager == null)
		{
			log.warn(queueMessage(queueType, "queue not found"));
		}
		else
		{
			messages = manager.receiveMessages(searchText);
		}

		return messages;
	}

	private String queueMessage(final QueueType queueType, final String message)
	{
		return queueType.toString() + ": " + message;
	}
}
