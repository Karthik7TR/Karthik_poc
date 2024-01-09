package com.thomsonreuters.uscl.ereader.core.book.domain;

public class EbookGroup {
    private Long bookDefinitionId;
    private String titleId;
    private String proviewDisplayName;
    private String groupName;
    private String bookVersion;

    public EbookGroup() {
        super();
    }

    public EbookGroup(
        final String titleId,
        final String proviewDisplayName,
        final String groupName,
        final Long bookDefinitionId,
        final String bookVersion) {
        this.titleId = (titleId != null) ? titleId.trim() : null;
        this.proviewDisplayName = (proviewDisplayName != null) ? proviewDisplayName.trim() : null;
        this.groupName = (groupName != null) ? groupName.trim() : null;
        this.bookDefinitionId = bookDefinitionId;
        this.bookVersion = (bookVersion != null) ? bookVersion.trim() : null;
    }

    public Long getBookDefinitionId() {
        return bookDefinitionId;
    }

    public void setBookDefinitionId(final Long bookDefinitionId) {
        this.bookDefinitionId = bookDefinitionId;
    }

    public String getTitleId() {
        return titleId;
    }

    public void setTitleId(final String titleId) {
        this.titleId = titleId;
    }

    public String getProviewDisplayName() {
        return proviewDisplayName;
    }

    public void setProviewDisplayName(final String bookName) {
        proviewDisplayName = bookName;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(final String groupName) {
        this.groupName = groupName;
    }

    public String getBookVersion() {
        return bookVersion;
    }

    public void setBookVersion(final String bookVersionSubmitted) {
        bookVersion = bookVersionSubmitted;
    }
}
