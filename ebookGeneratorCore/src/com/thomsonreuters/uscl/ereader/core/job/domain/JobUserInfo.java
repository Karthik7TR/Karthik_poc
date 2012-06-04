package com.thomsonreuters.uscl.ereader.core.job.domain;

public class JobUserInfo {
	private String username;
	private String titleId;
	private String proviewDisplayName;
	
	public JobUserInfo() {
		super();
	}
	public JobUserInfo(String username, String titleId, String proviewDisplayName) {
		super();
		this.username = username;
		this.titleId = titleId;
		this.proviewDisplayName = proviewDisplayName;
	}
	
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getTitleId() {
		return titleId;
	}
	public void setTitleId(String titleId) {
		this.titleId = titleId;
	}
	public String getProviewDisplayName() {
		return proviewDisplayName;
	}
	public void setProviewDisplayName(String proviewDisplayName) {
		this.proviewDisplayName = proviewDisplayName;
	}
	public String getInfoAsCsv() {
		StringBuilder buffer = new StringBuilder();
		buffer.append(username).append(",");
		buffer.append(titleId).append(",");
		buffer.append(proviewDisplayName);
		return buffer.toString();
	}
	@Override
	public String toString() {
		return "JobUserInfo [username=" + username + ", titleId=" + titleId
				+ ", proviewDisplayName=" + proviewDisplayName + "]";
	}
}
