package com.thomsonreuters.uscl.ereader.gather.image.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * The TraceInformation JSON object that comes back from a Image Vertical HTTP REST web service request.
 * This is a JSON object embedded in the HTTP response body.
 */
public class TraceInformation
{
    private String executionType;
    private String parentGuid;
    private String product;
    private String rootGuid;
    private String serverInformation;
    private String sessionGuid;
    private String transactionGuid;
    private String userGuid;

    @JsonProperty("ExecutionType")
    public String getExecutionType()
    {
        return executionType;
    }

    @JsonProperty("ParentGuid")
    public String getParentGuid()
    {
        return parentGuid;
    }

    @JsonProperty("Product")
    public String getProduct()
    {
        return product;
    }

    @JsonProperty("RootGuid")
    public String getRootGuid()
    {
        return rootGuid;
    }

    @JsonProperty("ServerInformation")
    public String getServerInformation()
    {
        return serverInformation;
    }

    @JsonProperty("SessionGuid")
    public String getSessionGuid()
    {
        return sessionGuid;
    }

    @JsonProperty("TransactionGuid")
    public String getTransactionGuid()
    {
        return transactionGuid;
    }

    @JsonProperty("UserGuid")
    public String getUserGuid()
    {
        return userGuid;
    }

    public void setExecutionType(final String execType)
    {
        executionType = execType;
    }

    public void setParentGuid(final String parentGuid)
    {
        this.parentGuid = parentGuid;
    }

    public void setProduct(final String product)
    {
        this.product = product;
    }

    public void setRootGuid(final String rootGuid)
    {
        this.rootGuid = rootGuid;
    }

    public void setServerInformation(final String serverInformation)
    {
        this.serverInformation = serverInformation;
    }

    public void setSessionGuid(final String sessionGuid)
    {
        this.sessionGuid = sessionGuid;
    }

    public void setTransactionGuid(final String transactionGuid)
    {
        this.transactionGuid = transactionGuid;
    }

    public void setUserGuid(final String userGuid)
    {
        this.userGuid = userGuid;
    }

    @Override
    public String toString()
    {
        return ReflectionToStringBuilder.toString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }
}
