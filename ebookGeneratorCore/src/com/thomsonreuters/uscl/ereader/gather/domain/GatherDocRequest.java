package com.thomsonreuters.uscl.ereader.gather.domain;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * A HTTP request body to the Gather document REST service. Eventually serialized into XML for transmission over the wire.
 */
@XmlRootElement(name = "gatherDocRequest", namespace = "com.thomsonreuters.uscl.ereader.gather.domain")
@XmlAccessorType(XmlAccessType.FIELD)
public class GatherDocRequest implements Serializable {
    private static final long serialVersionUID = -3445724162855653304L;

    /**
     * Document GUID's, the document key.
     */
    @XmlElementWrapper(name = "docGuids")
    private List<String> guids;

    /** Document collection name */
    @XmlElement(name = "collectionName")
    private String collectionName;

    /**
     * Filesystem directory where document content will be placed as guid.xml
     */
    @XmlElement(name = "contentDestinationDirectory")
    private File contentDestinationDirectory;

    /**
     * Filesystem directory where document metadata will be placed as guid.xml
     */
    @XmlElement(name = "metadataDestinationDirectory")
    private File metadataDestinationDirectory;
    @XmlElement(name = "isFinalStage")
    private boolean isFinalStage;
    @XmlElement(name = "useReloadContent")
    private boolean useReloadContent;

    public GatherDocRequest() {
        super();
    }

    /**
     * Full constructor for document requests to Gather REST service.
     *
     * @param guid the document key
     * @param collectionName
     * @param destinationDirectory filesystem directory where created the XML document files are to be placed
     */
    public GatherDocRequest(
        final Collection<String> guids,
        final String collectionName,
        final File contentDestinationDirectory,
        final File metadataDestinationDirectory,
        final boolean isFinalStage,
        final boolean useReloadContent) {
        setGuids(guids);
        setCollectionName(collectionName);
        setContentDestinationDirectory(contentDestinationDirectory);
        setMetadataDestinationDirectory(metadataDestinationDirectory);
        setIsFinalStage(isFinalStage);
        setUseReloadContent(useReloadContent);
    }

    public boolean getUseReloadContent() {
        return useReloadContent;
    }

    public void setUseReloadContent(final boolean useReloadContent) {
        this.useReloadContent = useReloadContent;
    }

    public Collection<String> getGuids() {
        return guids;
    }

    public void setGuids(final Collection<String> guidCollection) {
        guids = new ArrayList<>(guidCollection);
    }

    public String getCollectionName() {
        return collectionName;
    }

    public void setCollectionName(final String collectionName) {
        this.collectionName = collectionName;
    }

    public File getContentDestinationDirectory() {
        return contentDestinationDirectory;
    }

    public void setContentDestinationDirectory(final File destinationDirectory) {
        contentDestinationDirectory = destinationDirectory;
    }

    public File getMetadataDestinationDirectory() {
        return metadataDestinationDirectory;
    }

    public void setMetadataDestinationDirectory(final File destinationDirectory) {
        metadataDestinationDirectory = destinationDirectory;
    }

    public boolean getIsFinalStage() {
        return isFinalStage;
    }

    public void setIsFinalStage(final boolean isFinalStage) {
        this.isFinalStage = isFinalStage;
    }

    @Override
    public String toString() {
        return ReflectionToStringBuilder.toString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((collectionName == null) ? 0 : collectionName.hashCode());
        result = prime * result + ((contentDestinationDirectory == null) ? 0 : contentDestinationDirectory.hashCode());
        result = prime * result + ((guids == null) ? 0 : guids.hashCode());
        result = prime * result + (isFinalStage ? 1231 : 1237);
        result =
            prime * result + ((metadataDestinationDirectory == null) ? 0 : metadataDestinationDirectory.hashCode());
        result = prime * result + (useReloadContent ? 1231 : 1237);
        return result;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        final GatherDocRequest other = (GatherDocRequest) obj;
        if (collectionName == null) {
            if (other.collectionName != null)
                return false;
        } else if (!collectionName.equals(other.collectionName))
            return false;
        if (contentDestinationDirectory == null) {
            if (other.contentDestinationDirectory != null)
                return false;
        } else if (!contentDestinationDirectory.equals(other.contentDestinationDirectory))
            return false;
        if (guids == null) {
            if (other.guids != null)
                return false;
        } else if (!guids.equals(other.guids))
            return false;
        if (isFinalStage != other.isFinalStage)
            return false;
        if (metadataDestinationDirectory == null) {
            if (other.metadataDestinationDirectory != null)
                return false;
        } else if (!metadataDestinationDirectory.equals(other.metadataDestinationDirectory))
            return false;
        if (useReloadContent != other.useReloadContent)
            return false;
        return true;
    }
}
