package com.thomsonreuters.uscl.ereader.gather.domain;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import com.thomsonreuters.uscl.ereader.core.book.domain.ExcludeDocument;
import com.thomsonreuters.uscl.ereader.core.book.domain.RenameTocEntry;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

@XmlRootElement(name = "gatherTocRequest")
@XmlAccessorType(XmlAccessType.FIELD)
public class GatherTocRequest
{
    @XmlElement(name = "guid")
    private String guid;
    @XmlElement(name = "collectionName")
    private String collectionName;
    @XmlElement(name = "tocFile")
    private File tocFile;
    @XmlElementWrapper(name = "excludeDocument")
    private List<ExcludeDocument> excludeDocuments;
    @XmlElementWrapper(name = "renameTocEntry")
    private List<RenameTocEntry> renameTocEntries;
    @XmlElement(name = "isFinalStage")
    private boolean isFinalStage;
    private List<String> splitTocGuidList;
    private int thresholdValue;

    public GatherTocRequest()
    {
        super();
    }

    public GatherTocRequest(
        final String guid,
        final String collectionName,
        final File tocFile,
        final List<ExcludeDocument> excludeDocuments,
        final List<RenameTocEntry> renameTocEntries,
        final boolean isFinalStage,
        final Collection<String> splitTocGuidList,
        final int thresholdValue)
    {
        super();
        this.guid = guid;
        this.collectionName = collectionName;
        this.tocFile = tocFile;
        this.excludeDocuments = excludeDocuments;
        this.renameTocEntries = renameTocEntries;
        this.isFinalStage = isFinalStage;
        if (splitTocGuidList != null)
        {
            setSplitTocGuidList(splitTocGuidList);
        }
        this.thresholdValue = thresholdValue;
    }

    public int getThresholdValue()
    {
        return thresholdValue;
    }

    public void setThresholdValue(final int thresholdValue)
    {
        this.thresholdValue = thresholdValue;
    }

    public List<String> getSplitTocGuidList()
    {
        return splitTocGuidList;
    }

    public void setSplitTocGuidList(final Collection<String> splitTocGuidList)
    {
        this.splitTocGuidList = new ArrayList<>(splitTocGuidList);
    }

    public String getCollectionName()
    {
        return collectionName;
    }

    public String getGuid()
    {
        return guid;
    }

    public File getTocFile()
    {
        return tocFile;
    }

    public void setCollectionName(final String collectionName)
    {
        this.collectionName = collectionName;
    }

    public void setGuid(final String guid)
    {
        this.guid = guid;
    }

    public void setTocFile(final File tocFile)
    {
        this.tocFile = tocFile;
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
        result = prime * result + ((collectionName == null) ? 0 : collectionName.hashCode());
        result = prime * result + ((guid == null) ? 0 : guid.hashCode());
        result = prime * result + (isFinalStage ? 1231 : 1237);
        result = prime * result + ((tocFile == null) ? 0 : tocFile.hashCode());
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
        final GatherTocRequest other = (GatherTocRequest) obj;
        if (collectionName == null)
        {
            if (other.collectionName != null)
                return false;
        }
        else if (!collectionName.equals(other.collectionName))
            return false;
        if (guid == null)
        {
            if (other.guid != null)
                return false;
        }
        else if (!guid.equals(other.guid))
            return false;
        if (isFinalStage != other.isFinalStage)
            return false;
        if (tocFile == null)
        {
            if (other.tocFile != null)
                return false;
        }
        else if (!tocFile.equals(other.tocFile))
            return false;
        return true;
    }

    public List<ExcludeDocument> getExcludeDocuments()
    {
        return excludeDocuments;
    }

    public void setExcludeDocuments(final List<ExcludeDocument> excludeDocuments)
    {
        this.excludeDocuments = excludeDocuments;
    }

    public List<RenameTocEntry> getRenameTocEntries()
    {
        return renameTocEntries;
    }

    public void setRenameTocEntries(final List<RenameTocEntry> renameTocEntries)
    {
        this.renameTocEntries = renameTocEntries;
    }

    public boolean isFinalStage()
    {
        return isFinalStage;
    }

    public void setFinalStage(final boolean isFinalStage)
    {
        this.isFinalStage = isFinalStage;
    }
}
