package com.thomsonreuters.uscl.ereader.jms.service.impl;

public enum QueueType
{
	UserExperience("EventUserExperienceConnectionPool"),
	LTCReply("LTCReplyConnectionPool");

	private String propertyHeader;

	private QueueType(final String propertyHeader)
	{
		this.propertyHeader = propertyHeader;
	}

	public String host(final String queueManager)
	{
		return queueManager + ".queueConnection.java.properties.property.HOST";
	}

	public String port(final String queueManager)
	{
		return queueManager + ".queueConnection.java.properties.property.PORT";
	}

	public String queueName()
	{
		return propertyHeader + ".queue.name";
	}

	public String connections()
	{
		return propertyHeader + ".queueConnections";
	}

	public String channel(final String queueManager)
	{
		return queueManager + ".queueConnection.java.properties.property.CHAN";
	}

	public String transportType(final String queueManager)
	{
		return queueManager + ".queueConnection.java.properties.property.TRAN";
	}

	@Override
	public String toString()
	{
		return propertyHeader;
	}

	public String toString(final int queueNumber)
	{
		return propertyHeader + queueNumber;
	}
}
