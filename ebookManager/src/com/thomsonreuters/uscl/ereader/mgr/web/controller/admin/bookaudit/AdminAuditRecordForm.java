package com.thomsonreuters.uscl.ereader.mgr.web.controller.admin.bookaudit;

import java.util.Date;

public class AdminAuditRecordForm {
    public static final String FORM_NAME = "adminAuditRecordForm";

    private String titleId;
    private Long auditId;
    private Long bookDefinitionId;
    private String proviewDisplayName;
    private String isbn;
    private Date lastUpdated;

    public String getTitleId() {
        return titleId;
    }

    public void setTitleId(final String titleId) {
        this.titleId = titleId;
    }

    public Long getAuditId() {
        return auditId;
    }

    public void setAuditId(final Long auditId) {
        this.auditId = auditId;
    }

    public Long getBookDefinitionId() {
        return bookDefinitionId;
    }

    public void setBookDefinitionId(final Long bookDefinitionId) {
        this.bookDefinitionId = bookDefinitionId;
    }

    public String getProviewDisplayName() {
        return proviewDisplayName;
    }

    public void setProviewDisplayName(final String proviewDisplayName) {
        this.proviewDisplayName = proviewDisplayName;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(final String isbn) {
        this.isbn = isbn;
    }

    public Date getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(final Date lastUpdated) {
        this.lastUpdated = lastUpdated;
    }
}
