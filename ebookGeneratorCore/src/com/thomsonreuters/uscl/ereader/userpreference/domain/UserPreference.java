package com.thomsonreuters.uscl.ereader.userpreference.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

@Entity
@Table(name = "USER_PREFERENCE")
public class UserPreference implements Serializable {
    private static Logger log = LogManager.getLogger(UserPreference.class);
    private static final long serialVersionUID = 1L;

    @Column(name = "USER_NAME", length = 1024, nullable = false)
    @Id
    private String userName;

    @Column(name = "EMAIL_LIST", length = 2048)
    private String emails;

    @Column(name = "LIBRARY_PROVIEW_NAME_FILTER", length = 1024)
    private String libraryProviewName;

    @Column(name = "LIBRARY_TITLE_ID_FILTER", length = 1024)
    private String libraryTitleId;

    @Column(name = "AUDIT_PROVIEW_NAME_FILTER", length = 1024)
    private String auditProviewName;

    @Column(name = "AUDIT_TITLE_ID_FILTER", length = 1024)
    private String auditTitleId;

    @Column(name = "JOB_SUM_PROVIEW_NAME_FILTER", length = 1024)
    private String jobSummaryProviewName;

    @Column(name = "JOB_SUM_TITLE_ID_FILTER", length = 1024)
    private String jobSummaryTitleId;

    @Column(name = "GROUP_NAME_FILTER", length = 1024)
    private String groupListGroupName;

    @Column(name = "GROUP_ID_FILTER", length = 1024)
    private String groupListGroupId;

    @Column(name = "START_PAGE", length = 64)
    private String startPage;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "LAST_UPDATED", nullable = false)
    private Date lastUpdated;

    public String getUserName() {
        return userName;
    }

    public void setUserName(final String userName) {
        this.userName = userName;
    }

    public String getEmails() {
        return emails;
    }

    public void setEmails(final String emails) {
        this.emails = emails;
    }

    public String getLibraryProviewName() {
        return libraryProviewName;
    }

    public void setLibraryProviewName(final String libraryProviewName) {
        this.libraryProviewName = libraryProviewName;
    }

    public String getLibraryTitleId() {
        return libraryTitleId;
    }

    public void setLibraryTitleId(final String libraryTitleId) {
        this.libraryTitleId = libraryTitleId;
    }

    public String getAuditProviewName() {
        return auditProviewName;
    }

    public void setAuditProviewName(final String auditProviewName) {
        this.auditProviewName = auditProviewName;
    }

    public String getAuditTitleId() {
        return auditTitleId;
    }

    public void setAuditTitleId(final String auditTitleId) {
        this.auditTitleId = auditTitleId;
    }

    public String getJobSummaryProviewName() {
        return jobSummaryProviewName;
    }

    public void setJobSummaryProviewName(final String jobSummaryProviewName) {
        this.jobSummaryProviewName = jobSummaryProviewName;
    }

    public String getJobSummaryTitleId() {
        return jobSummaryTitleId;
    }

    public void setJobSummaryTitleId(final String jobSummaryTitleId) {
        this.jobSummaryTitleId = jobSummaryTitleId;
    }

    public String getGroupListGroupName() {
        return groupListGroupName;
    }

    public void setGroupListGroupName(final String groupListGroupName) {
        this.groupListGroupName = groupListGroupName;
    }

    public String getGroupListGroupId() {
        return groupListGroupId;
    }

    public void setGroupListGroupId(final String groupListGroupId) {
        this.groupListGroupId = groupListGroupId;
    }

    public String getStartPage() {
        return startPage;
    }

    public void setStartPage(final String startPage) {
        this.startPage = startPage;
    }

    public Date getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(final Date lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    @Transient
    public List<String> getEmailAddressList() {
        return toStringEmailAddressList(emails);
    }

    @Transient
    public List<InternetAddress> getInternetEmailAddressList() {
        return toInternetAddressList(toStringEmailAddressList(emails));
    }

    public static List<String> toStringEmailAddressList(final String addressCsv) {
        final String[] recipientArray = StringUtils.split(addressCsv, ",");
        if (recipientArray != null) {
            return Arrays.asList(recipientArray);
        } else {
            return new ArrayList<>();
        }
    }

    public static List<InternetAddress> toInternetAddressList(final List<String> addrStrings) {
        final List<InternetAddress> uniqueAddresses = new ArrayList<>();
        for (final String addrString : addrStrings) {
            try {
                final InternetAddress inetAddr = new InternetAddress(addrString);
                uniqueAddresses.add(inetAddr);
            } catch (final AddressException e) {
                log.error("Invalid user preference email address - ignored: " + addrString, e);
            }
        }
        return uniqueAddresses;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((userName == null) ? 0 : userName.hashCode());
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
        final UserPreference other = (UserPreference) obj;
        if (userName == null) {
            if (other.userName != null)
                return false;
        } else if (!userName.equals(other.userName))
            return false;
        return true;
    }
}
