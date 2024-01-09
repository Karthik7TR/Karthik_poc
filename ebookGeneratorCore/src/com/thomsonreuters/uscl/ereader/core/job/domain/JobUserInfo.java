package com.thomsonreuters.uscl.ereader.core.job.domain;

public class JobUserInfo {
    private String username;
    private String titleId;
    private String proviewDisplayName;

    public JobUserInfo() {
        super();
    }

    public JobUserInfo(final String username, final String titleId, final String proviewDisplayName) {
        super();
        this.username = username;
        this.titleId = titleId;
        this.proviewDisplayName = proviewDisplayName;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(final String username) {
        this.username = username;
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

    public void setProviewDisplayName(final String proviewDisplayName) {
        this.proviewDisplayName = proviewDisplayName;
    }

    public String getInfoAsCsv() {
        final StringBuilder buffer = new StringBuilder();
        buffer.append(username).append(",");
        buffer.append(titleId).append(",");
        buffer.append(proviewDisplayName);
        return buffer.toString();
    }

    @Override
    public String toString() {
        return "JobUserInfo [username="
            + username
            + ", titleId="
            + titleId
            + ", proviewDisplayName="
            + proviewDisplayName
            + "]";
    }
}
