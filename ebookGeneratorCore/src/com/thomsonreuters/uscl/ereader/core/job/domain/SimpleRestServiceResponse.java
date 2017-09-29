package com.thomsonreuters.uscl.ereader.core.job.domain;

import java.util.Objects;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.apache.commons.lang3.builder.HashCodeBuilder;
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
        return Objects.equals(this, obj);
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(id)
                .append(success)
                .append(message)
                .toHashCode();
    }
}
