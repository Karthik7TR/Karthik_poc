package com.thomsonreuters.uscl.ereader.userpreference.domain;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@EqualsAndHashCode(of = "userName")
@Entity
@Table(name = "USER_PREFERENCE")
public class UserPreference implements Serializable {
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

    @Column(name = "TITLES_PROVIEW_NAME_FILTER", length = 1024)
    private String proviewListProviewName;

    @Column(name = "TITLES_TITLE_ID_FILTER", length = 1024)
    private String proviewListTitleId;

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
}
