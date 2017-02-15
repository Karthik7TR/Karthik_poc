package com.thomsonreuters.uscl.ereader.jms.service.impl;

import java.util.Properties;

// formerly org.apache.commons.lang3. . .
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.math.NumberUtils;

/**
 * Hold onto information describing a queue
 */
public class QueueDescriptor
{
    private QueueType queueType;
    private String host;
    private Integer port;
    private String manager;
    private String name;
    private String channel;
    private Integer transportType;
    private boolean enabled = true;
    private boolean valid;

    public QueueDescriptor()
    {
    }

    public QueueDescriptor(final QueueType queueType, final String queueManager)
    {
        this.queueType = queueType;
        manager = queueManager;
        initialize();
    }

    private void initialize()
    {
        try
        {
            final Properties props = System.getProperties();
            host = StringUtils.trimToNull(props.getProperty(queueType.host(manager)));
            name = StringUtils.trimToNull(props.getProperty(queueType.queueName()));
            channel = StringUtils.trimToNull(props.getProperty(queueType.channel(manager)));
            transportType =
                NumberUtils.createInteger(StringUtils.trimToNull(props.getProperty(queueType.transportType(manager))));

            port = NumberUtils.createInteger(props.getProperty(queueType.port(manager)));

            valid = host != null
                && manager != null
                && name != null
                && channel != null
                && transportType != null
                && port != null
                && port > 0;

            // Override for manual disabling of the queue at startup
            final String disableProperty = StringUtils.trimToNull(props.getProperty(queueType.toString() + ".disable"));
            if (disableProperty != null)
            {
                enabled = !BooleanUtils.toBoolean(disableProperty);
                valid = valid && enabled;
            }
        }
        catch (final Exception e)
        {
            // IndexOutOfBoundsException, NullPointerException,
            // NumberFormatException
            valid = false;
        }
    }

    public QueueType getQueueType()
    {
        return queueType;
    }

    public void setQueueType(final QueueType queueType)
    {
        if (this.queueType != queueType)
        {
            this.queueType = queueType;
            initialize();
        }
    }

    public boolean isValid()
    {
        return valid;
    }

    public String getHost()
    {
        return host;
    }

    public Integer getPort()
    {
        return port;
    }

    public String getManager()
    {
        return manager;
    }

    public String getName()
    {
        return name;
    }

    public String getChannel()
    {
        return channel;
    }

    public Integer getTransportType()
    {
        return transportType;
    }

    @Override
    public boolean equals(final Object obj)
    {
        return EqualsBuilder.reflectionEquals(this, obj);
    }

    @Override
    public int hashCode()
    {
        return HashCodeBuilder.reflectionHashCode(this);
    }

    @Override
    public String toString()
    {
        return ToStringBuilder.reflectionToString(this);
    }
}
