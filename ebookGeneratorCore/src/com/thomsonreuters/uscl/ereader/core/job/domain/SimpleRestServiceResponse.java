package com.thomsonreuters.uscl.ereader.core.job.domain;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * Encapsulates a generic response from a REST service provider.
 * This object is marshalled and becomes the body of the REST response back to the REST client.
 */
@XmlRootElement(name = "simpleRestServiceResponse")
public class SimpleRestServiceResponse
{
    private Long id;
    private boolean success;
    private String message;

    public SimpleRestServiceResponse()
    {
        super();
    }

    /**
     * The nominal success response object.
     * @param jobExecutionId job execution id of job stopped or restarted.
     */
    public SimpleRestServiceResponse(final Long id)
    {
        this(id, true, null);
    }

    /**
     * The full constructor for a response.
     * @param id
     * @param success true if the operation was successful.
     * @param message informational message as to what went wrong.
     */
    public SimpleRestServiceResponse(final Long id, final boolean success, final String message)
    {
        this.id = id;
        this.success = success;
        this.message = message;
    }

    public Long getId()
    {
        return id;
    }

    public boolean isSuccess()
    {
        return success;
    }

    public String getMessage()
    {
        return message;
    }

    @XmlElement(name = "id", required = false)
    public void setId(final Long id)
    {
        this.id = id;
    }

    @XmlElement(name = "success", required = true)
    public void setSuccess(final boolean success)
    {
        this.success = success;
    }

    @XmlElement(name = "message", required = false)
    public void setMessage(final String message)
    {
        this.message = message;
    }

    @Override
    public String toString()
    {
        return ReflectionToStringBuilder.toString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }

    @Override
    public boolean equals(final Object obj)
    {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        final SimpleRestServiceResponse other = (SimpleRestServiceResponse) obj;
        if (id == null)
        {
            if (other.id != null)
                return false;
        }
        else if (!id.equals(other.id))
            return false;
        if (success != other.success)
            return false;
        if (message == null)
        {
            if (other.message != null)
                return false;
        }
        else if (!message.equals(other.message))
            return false;
        return true;
    }
}
