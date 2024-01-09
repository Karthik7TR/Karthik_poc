package com.thomsonreuters.uscl.ereader.gather.metadata.domain;

import java.io.Serializable;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.Id;

public class DocMetadataPK implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     */

    @Column(name = "TITLE_ID", length = 64, nullable = false)
    @Basic(fetch = FetchType.EAGER)
    @Id
    private String titleId;
    /**
     */

    @Column(name = "JOB_INSTANCE_ID", nullable = false)
    @Basic(fetch = FetchType.EAGER)
    @Id
    private Long jobInstanceId;
    /**
     */

    @Column(name = "DOC_UUID", length = 36, nullable = false)
    @Basic(fetch = FetchType.EAGER)
    @Id
    private String docUuid;

    /**
     */
    public DocMetadataPK() {
    }

    /**
     */
    public void setTitleId(final String titleId) {
        this.titleId = titleId;
    }

    /**
     */
    public String getTitleId() {
        return titleId;
    }

    /**
     */
    public void setJobInstanceId(final Long jobInstanceId) {
        this.jobInstanceId = jobInstanceId;
    }

    /**
     */
    public Long getJobInstanceId() {
        return jobInstanceId;
    }

    /**
     */
    public void setDocUuid(final String docUuid) {
        this.docUuid = docUuid;
    }

    /**
     */
    public String getDocUuid() {
        return docUuid;
    }

    /**
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((titleId == null) ? 0 : titleId.hashCode());
        result = prime * result + ((jobInstanceId == null) ? 0 : jobInstanceId.hashCode());
        result = prime * result + ((docUuid == null) ? 0 : docUuid.hashCode());
        return result;
    }

    /**
     */
    @Override
    public boolean equals(final Object obj) {
        if (obj == this)
            return true;
        if (!(obj instanceof DocMetadataPK))
            return false;
        final DocMetadataPK equalCheck = (DocMetadataPK) obj;
        if ((titleId == null && equalCheck.titleId != null) || (titleId != null && equalCheck.titleId == null))
            return false;
        if (titleId != null && !titleId.equals(equalCheck.titleId))
            return false;
        if ((jobInstanceId == null && equalCheck.jobInstanceId != null)
            || (jobInstanceId != null && equalCheck.jobInstanceId == null))
            return false;
        if (jobInstanceId != null && !jobInstanceId.equals(equalCheck.jobInstanceId))
            return false;
        if ((docUuid == null && equalCheck.docUuid != null) || (docUuid != null && equalCheck.docUuid == null))
            return false;
        if (docUuid != null && !docUuid.equals(equalCheck.docUuid))
            return false;
        return true;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("DocMetadataPK");
        sb.append(" titleId: ").append(getTitleId());
        sb.append(" jobInstanceId: ").append(getJobInstanceId());
        sb.append(" docUuid: ").append(getDocUuid());
        return sb.toString();
    }
}
