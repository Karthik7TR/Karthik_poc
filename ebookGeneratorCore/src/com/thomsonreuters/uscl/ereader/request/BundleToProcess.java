package com.thomsonreuters.uscl.ereader.request;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.springframework.batch.core.BatchStatus;

@Entity
@Table(name = "BUNDLE_TO_PROCESS")
public class BundleToProcess
{
    // TODO identify enum fields
    /*public enum ProcessState
    {
        NEW,
        IN_PROGRESS,
        COMPLETE
    };*/

    @Id
    @Column(name = "BUNDLE_TO_PROCESS_ID")
    private Long bundleToProcessId;
    @Column(name = "DATE_TIME")
    private Date dateTime;
    @Column(name = "MESSAGE_REQUEST")
    private String messageRequest;
    @Column(name = "PRODUCT_TYPE")
    private String productType;
    @Column(name = "PRODUCT_NAME")
    private String productName;
    @Column(name = "SOURCE_LOCATION")
    private String sourceLocation;
    @Column(name = "PROCESS_STATE")
    private String processState;
    @Column(name = "PROCESS_USER")
    private String processUser;

    public BundleToProcess(final EBookRequest request)
    {
        //TODO verify all necessary fields are set.
        bundleToProcessId = request.getEBookArchiveId();
        dateTime = request.getDateTime(); // TODO Verify datetime should be the request send time.
        messageRequest = request.getMessageRequest();
        productType = request.getProductType();
        productName = request.getProductName();
        sourceLocation = request.getEBookSrcPath();
        processState = "new";
        processUser = "unset"; //TODO identify if it is possible to attach a user at this point, and what value this field has
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

    public Long getBundleToProcessId()
    {
        return bundleToProcessId;
    }

    public void setBundleToProcessId(final Long bundleToProcessId)
    {
        this.bundleToProcessId = bundleToProcessId;
    }

    public Date getDateTime()
    {
        return dateTime;
    }

    public void setDateTime(final Date dateTime)
    {
        this.dateTime = dateTime;
    }

    public String getMessageRequest()
    {
        return messageRequest;
    }

    public void setMessageRequest(final String messageRequest)
    {
        this.messageRequest = messageRequest;
    }

    public String getProductType()
    {
        return productType;
    }

    public void setProductType(final String productType)
    {
        this.productType = productType;
    }

    public String getProductName()
    {
        return productName;
    }

    public void setProductName(final String productName)
    {
        this.productName = productName;
    }

    public String getSourceLocation()
    {
        return sourceLocation;
    }

    public void setSourceLocation(final String sourceLocation)
    {
        this.sourceLocation = sourceLocation;
    }

    public BatchStatus getProcessState()
    {
        return BatchStatus.valueOf(processState);
    }

    public void setProcessState(final BatchStatus processState)
    {
        this.processState = processState.toString();
    }

    public String getProcessUser()
    {
        return processUser;
    }

    public void setProcessUser(final String processUser)
    {
        this.processUser = processUser;
    }
}
