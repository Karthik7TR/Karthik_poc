package com.thomsonreuters.uscl.ereader.gather.image.domain;

import java.io.Serializable;

import javax.persistence.Embeddable;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * The primary key for image meta-data entity read from database.
 */
@Embeddable
public class ImageMetadataEntityKey implements Serializable {
    private static final long serialVersionUID = 964134678526913067L;
    private Long jobInstanceId;
    private String imageGuid;
    private String docUuid;

    public ImageMetadataEntityKey() {
        super();
    }

    public ImageMetadataEntityKey(final Long jobInstanceId, final String guid, final String docUuid) {
        this.jobInstanceId = jobInstanceId;
        imageGuid = guid;
        this.docUuid = docUuid;
    }

    public Long getJobInstanceId() {
        return jobInstanceId;
    }

    public String getImageGuid() {
        return imageGuid;
    }

    public void setJobInstanceId(final Long jobInstanceId) {
        this.jobInstanceId = jobInstanceId;
    }

    public void setImageGuid(final String imageGuid) {
        this.imageGuid = imageGuid;
    }

    @Override
    public String toString() {
        return ReflectionToStringBuilder.toString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }

    public String getDocUuid() {
        return docUuid;
    }

    public void setDocUuid(final String docUuid) {
        this.docUuid = docUuid;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((jobInstanceId == null) ? 0 : jobInstanceId.hashCode());
        result = prime * result + ((imageGuid == null) ? 0 : imageGuid.hashCode());
        result = prime * result + ((docUuid == null) ? 0 : docUuid.hashCode());
        return result;
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj == this)
            return true;
        if (!(obj instanceof ImageMetadataEntityKey)) {
            return false;
        }

        final ImageMetadataEntityKey equalCheck = (ImageMetadataEntityKey) obj;

        if ((jobInstanceId == null && equalCheck.jobInstanceId != null)
            || (jobInstanceId != null && equalCheck.jobInstanceId == null))
            return false;
        if (jobInstanceId != null && !jobInstanceId.equals(equalCheck.jobInstanceId))
            return false;
        if ((docUuid == null && equalCheck.docUuid != null) || (docUuid != null && equalCheck.docUuid == null))
            return false;
        if (docUuid != null && !docUuid.equals(equalCheck.docUuid))
            return false;
        if ((imageGuid == null && equalCheck.imageGuid != null) || (imageGuid != null && equalCheck.imageGuid == null))
            return false;
        if (imageGuid != null && !imageGuid.equals(equalCheck.imageGuid))
            return false;
        return true;
    }
}
