package com.thomsonreuters.uscl.ereader.core.outage.domain;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.thomsonreuters.uscl.ereader.core.CoreConstants;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

@Entity
@XmlRootElement(name = "outage")
@Table(name = "PLANNED_OUTAGE")
public class PlannedOutage implements Serializable {
    private static final long serialVersionUID = 1L;

    public static final int NUMBER_DAYS_DISPLAY = 7;

    public enum Operation {
        SAVE,
        REMOVE
    };

    @Column(name = "OUTAGE_ID", nullable = false)
    @Id
    @SequenceGenerator(name = "outageIdSequence", sequenceName = "OUTAGE_ID_SEQ")
    @GeneratedValue(generator = "outageIdSequence")
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumns({@JoinColumn(name = "OUTAGE_TYPE_ID", referencedColumnName = "OUTAGE_TYPE_ID")})
    private OutageType outageType;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "START_TIME", nullable = false)
    private Date startTime;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "END_TIME", nullable = false)
    private Date endTime;

    @Column(name = "REASON", nullable = false, length = 2048)
    private String reason;

    @Column(name = "SYSTEM_IMPACT_DESCRIPTION", nullable = true, length = 2048)
    private String systemImpactDescription;

    @Column(name = "SERVERS_IMPACTED", nullable = true, length = 2048)
    private String serversImpacted;

    @Column(name = "NOTIFICATION_EMAIL_SENT", nullable = true)
    private String notificationEmailSent;

    @Column(name = "ALL_CLEAR_EMAIL_SENT", nullable = true)
    private String allClearEmailSent;

    @Column(name = "UPDATED_BY", nullable = false, length = 128)
    private String updatedBy;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "LAST_UPDATED", nullable = false)
    private Date lastUpdated;

    @Transient
    private Operation operation;

    /*
     * a more extensive equals method exists in JAXBMarshallingTest, but this should be sufficient
     */
    @Override
    public boolean equals(final Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        final PlannedOutage other = (PlannedOutage) obj;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        return true;
    }

    public boolean isAllClearEmailSent() {
        return ("Y".equalsIgnoreCase(allClearEmailSent));
    }

    public Date getEndTime() {
        return endTime;
    }

    public Long getId() {
        return id;
    }

    public Date getLastUpdated() {
        return lastUpdated;
    }

    public boolean isNotificationEmailSent() {
        return ("Y".equalsIgnoreCase(notificationEmailSent));
    }

    @Transient
    public Operation getOperation() {
        return operation;
    }

    public OutageType getOutageType() {
        return outageType;
    }

    public String getReason() {
        return reason;
    }

    public String getServersImpacted() {
        return serversImpacted;
    }

    public Date getStartTime() {
        return startTime;
    }

    public String getSystemImpactDescription() {
        return systemImpactDescription;
    }

    public String getUpdatedBy() {
        return updatedBy;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        return result;
    }

    /**
     * Returns true if we are in the middle of the outage with respect to a specific time.
     * @param timeInstant point in time to compare against the outage interval.
     * @return true if within the interval.
     */
    public boolean isActive(final Date timeInstant) {
        return (((timeInstant.equals(startTime) || timeInstant.after(startTime)) && timeInstant.before(endTime)));
    }

    public void setAllClearEmailSent(final boolean allClearEmailSent) {
        this.allClearEmailSent = ((allClearEmailSent) ? "Y" : "N");
    }

    @XmlElement(name = "endTime", required = false)
    public void setEndTime(final Date endTime) {
        this.endTime = endTime;
    }

    @XmlElement(name = "id", required = true)
    public void setId(final Long id) {
        this.id = id;
    }

    public void setLastUpdated(final Date lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    public void setNotificationEmailSent(final boolean notificationEmailSent) {
        this.notificationEmailSent = ((notificationEmailSent) ? "Y" : "N");
    }

    @Transient
    public void setOperation(final Operation operation) {
        this.operation = operation;
    }

    public void setOutageType(final OutageType outageType) {
        this.outageType = outageType;
    }

    @XmlElement(name = "reason", required = false)
    public void setReason(final String reason) {
        this.reason = reason;
    }

    public void setServersImpacted(final String serversImpacted) {
        this.serversImpacted = serversImpacted;
    }

    @XmlElement(name = "startTime", required = false)
    public void setStartTime(final Date startTime) {
        this.startTime = startTime;
    }

    public void setSystemImpactDescription(final String systemImpactDescription) {
        this.systemImpactDescription = systemImpactDescription;
    }

    public void setUpdatedBy(final String updatedBy) {
        this.updatedBy = updatedBy;
    }

    @Override
    public String toString() {
        return ReflectionToStringBuilder.toString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }

    /**
     * Serves as the email notification body.
     */
    public String toEmailBody() {
        final SimpleDateFormat sdf = new SimpleDateFormat(CoreConstants.DATE_TIME_FORMAT_PATTERN);
        final StringBuffer body = new StringBuffer();
        body.append(String.format("ID:    %d\n", id));
        body.append(String.format("Start: %s\n", (startTime != null) ? sdf.format(startTime) : ""));
        body.append(String.format("End:   %s\n", (endTime != null) ? sdf.format(endTime) : ""));
        body.append(
            String.format(
                "Type:  %s\n\n",
                (outageType != null)
                    ? String.format("%s / %s", outageType.getSystem(), outageType.getSubSystem()) : "<none>"));
        body.append("Reason:\n");
        body.append(String.format("%s\n\n", (reason != null) ? reason : ""));
        body.append("System Impact Description:\n");
        body.append(String.format("%s\n\n", (systemImpactDescription != null) ? systemImpactDescription : ""));
        body.append(String.format("Servers Impacted: %s\n", (serversImpacted != null) ? serversImpacted : ""));
        return body.toString();
    }
}
