package com.thomsonreuters.uscl.ereader.stats.domain;

import java.io.Serializable;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.Id;

public class PublishingStatsPK implements Serializable {
    private static final long serialVersionUID = 1L;

    public PublishingStatsPK() {
    }

    public PublishingStatsPK(final long jobInstanceId) {
        this.jobInstanceId = jobInstanceId;
    }

    @Column(name = "JOB_INSTANCE_ID", nullable = false)
    @Basic(fetch = FetchType.EAGER)
    @Id
    private Long jobInstanceId;

    public void setJobInstanceId(final Long jobInstanceId) {
        this.jobInstanceId = jobInstanceId;
    }

    public Long getJobInstanceId() {
        return jobInstanceId;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((jobInstanceId == null) ? 0 : jobInstanceId.hashCode());
        return result;
    }

    /**
     */
    @Override
    public boolean equals(final Object obj) {
        if (obj == this)
            return true;
        if (!(obj instanceof PublishingStatsPK))
            return false;
        final PublishingStatsPK equalCheck = (PublishingStatsPK) obj;
        if ((jobInstanceId == null && equalCheck.jobInstanceId != null)
            || (jobInstanceId != null && equalCheck.jobInstanceId == null))
            return false;
        if (jobInstanceId != null && !jobInstanceId.equals(equalCheck.jobInstanceId))
            return false;
        return true;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("PublishingStatsPK");
        sb.append(" jobInstanceId: ").append(getJobInstanceId());
        return sb.toString();
    }
}
