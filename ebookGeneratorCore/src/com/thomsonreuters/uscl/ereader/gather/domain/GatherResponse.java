package com.thomsonreuters.uscl.ereader.gather.domain;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.thomsonreuters.uscl.ereader.gather.util.ImgMetadataInfo;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

@XmlRootElement(name = "gatherResponse")
@XmlAccessorType(XmlAccessType.FIELD)
public class GatherResponse
{
    public static final int CODE_SUCCESS = 0;
    public static final int CODE_NOVUS_ERROR = 1;
    public static final int CODE_FILE_ERROR = 2;
    public static final int CODE_UNHANDLED_ERROR = 3;
    public static final int CODE_DATA_ERROR = 4;

    @XmlElement(name = "errorMessage")
    private String errorMessage;
    @XmlElement(name = "errorCode")
    private int errorCode;
    @XmlElement(name = "docCount")
    private int docCount;
    @XmlElement(name = "docCount2")
    private int docCount2;
    @XmlElement(name = "nodeCount")
    private int nodeCount;
    @XmlElement(name = "skipCount")
    private int skipCount;
    @XmlElement(name = "retryCount")
    private int retryCount;
    @XmlElement(name = "retryCount2")
    private int retryCount2;
    @XmlElement(name = "expectedCount")
    private int expectedCount;
    @XmlElement(name = "publishStatus")
    private String publishStatus;
    @XmlElement(name = "missingImgCount")
    private int missingImgCount;

    @XmlElement(name = "splitGuids")
    private List<String> splitTocGuidList;
    @XmlElement(name = "findSplitsAgain")
    private boolean findSplitsAgain;
    @XmlElement(name = "dupTocGuids")
    private List<String> duplicateTocGuids;
    @XmlElement(name = "imgMetadataInfo")
    private List<ImgMetadataInfo> imageMetadataList;

    public GatherResponse()
    {
        this(0, null, 0, 0, 0, 0, 0, 0, null);
    }

    public GatherResponse(
        final int errorCode,
        final String errorMessage,
        final int docCount,
        final int docCount2,
        final int nodeCount,
        final int skipCount,
        final int retryCount,
        final int retryCount2,
        final String publishStatus)
    {
        setErrorCode(errorCode);
        setErrorMessage(errorMessage);
        setDocCount(docCount);
        setDocCount2(docCount2);
        setNodeCount(nodeCount);
        setRetryCount(retryCount);
        setRetryCount2(retryCount2);
        setSkipCount(skipCount);
        setPublishStatus(publishStatus);
    }

    public GatherResponse(
        final int errorCode,
        final String errorMessage,
        final int docCount,
        final int retryCount,
        final int expectedCount,
        final String publishStatus)
    {
        setErrorCode(errorCode);
        setErrorMessage(errorMessage);
        setDocCount(docCount);
        setRetryCount(retryCount);
        setExpectedCount(expectedCount);
        setPublishStatus(publishStatus);
    }

    public GatherResponse(final int errorCode, final String errorMessage)
    {
        setErrorCode(errorCode);
        setErrorMessage(errorMessage);
    }

    public void setErrorMessage(final String errorMessage)
    {
        this.errorMessage = errorMessage;
    }

    public String getErrorMessage()
    {
        return errorMessage;
    }

    public void setErrorCode(final int errorCode)
    {
        this.errorCode = errorCode;
    }

    public int getErrorCode()
    {
        return errorCode;
    }

    public void setDocCount(final int docCount)
    {
        this.docCount = docCount;
    }

    public int getDocCount()
    {
        return docCount;
    }

    public void setDocCount2(final int docCount2)
    {
        this.docCount2 = docCount2;
    }

    public int getDocCount2()
    {
        return docCount2;
    }

    public void setNodeCount(final int nodeCount)
    {
        this.nodeCount = nodeCount;
    }

    public int getNodeCount()
    {
        return nodeCount;
    }

    public void setSkipCount(final int skipCount)
    {
        this.skipCount = skipCount;
    }

    public int getSkipCount()
    {
        return skipCount;
    }

    public void setRetryCount(final int retryCount)
    {
        this.retryCount = retryCount;
    }

    public int getRetryCount()
    {
        return retryCount;
    }

    public void setRetryCount2(final int retryCount2)
    {
        this.retryCount2 = retryCount2;
    }

    public int getRetryCount2()
    {
        return retryCount2;
    }

    public void setExpectedCount(final int expectedCount)
    {
        this.expectedCount = expectedCount;
    }

    public int getExpectedCount()
    {
        return expectedCount;
    }

    public void setPublishStatus(final String publishStatus)
    {
        this.publishStatus = publishStatus;
    }

    public String getPublishStatus()
    {
        return publishStatus;
    }

    public void setMissingImgCount(final int missingImgCount)
    {
        this.missingImgCount = missingImgCount;
    }

    public int getMissingImgCount()
    {
        return missingImgCount;
    }

    public void setSplitTocGuidList(final Collection<String> splitTocGuidList)
    {
        if (splitTocGuidList != null)
        {
            this.splitTocGuidList = new ArrayList<>(splitTocGuidList);
        }
    }

    public List<String> getSplitTocGuidList()
    {
        return splitTocGuidList;
    }

    public void setFindSplitsAgain(final boolean findSplitsAgain)
    {
        this.findSplitsAgain = findSplitsAgain;
    }

    public boolean isFindSplitsAgain()
    {
        return findSplitsAgain;
    }

    public void setDuplicateTocGuids(final Collection<String> duplicateTocGuids)
    {
        if (duplicateTocGuids != null)
        {
            this.duplicateTocGuids = new ArrayList<>(duplicateTocGuids);
        }
    }

    public List<String> getDuplicateTocGuids()
    {
        return duplicateTocGuids;
    }

    public List<ImgMetadataInfo> getImageMetadataList()
    {
        return imageMetadataList;
    }

    public void setImageMetadataList(final List<ImgMetadataInfo> imageMetadataList)
    {
        this.imageMetadataList = imageMetadataList;
    }

    @Override
    public String toString()
    {
        return ReflectionToStringBuilder.toString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + errorCode;
        result = prime * result + ((errorMessage == null) ? 0 : errorMessage.hashCode());
        return result;
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
        final GatherResponse other = (GatherResponse) obj;
        if (errorCode != other.errorCode)
            return false;
        if (errorMessage == null)
        {
            if (other.errorMessage != null)
                return false;
        }
        else if (!errorMessage.equals(other.errorMessage))
            return false;
        if (docCount != other.docCount)
            return false;
        if (docCount2 != other.docCount2)
            return false;
        if (skipCount != other.skipCount)
            return false;
        if (nodeCount != other.nodeCount)
            return false;
        if (retryCount != other.retryCount)
            return false;
        if (retryCount2 != other.retryCount2)
            return false;
        if (expectedCount != other.expectedCount)
            return false;
        if (publishStatus == null)
        {
            if (other.publishStatus != null)
                return false;
        }
        else if (!publishStatus.equals(other.publishStatus))
            return false;
        return true;
    }
}
