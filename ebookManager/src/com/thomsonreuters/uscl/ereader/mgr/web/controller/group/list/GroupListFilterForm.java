package com.thomsonreuters.uscl.ereader.mgr.web.controller.group.list;

import java.util.Date;
import java.util.List;

import com.thomsonreuters.uscl.ereader.mgr.web.UserUtils;
import com.thomsonreuters.uscl.ereader.proviewaudit.domain.ProviewAudit;






public class GroupListFilterForm  {
	
	public static final String FORM_NAME = "proviewGroupForm";
	public enum FilterCommand { SEARCH, RESET };
	public enum GroupCmd {REMOVE, DELETE, PROMOTE};
	
	
	private String titleId;
	private String proviewDisplayName;
	private String groupName;
	private String proviewGroupID;
	private String groupVersion;
	private String version;
	private Long bookDefinitionId;
	private FilterCommand command;
	private GroupCmd groupCmd;
	private String comments;
	private List<String> groupIds;
	private String groupStatus;
	

	public String getProviewGroupID() {
		return proviewGroupID;
	}

	public void setProviewGroupID(String groupID) {
		this.proviewGroupID = groupID;
	}

	public String getGroupVersion() {
		return groupVersion;
	}

	public void setGroupVersion(String groupVersion) {
		this.groupVersion = groupVersion;
	}
	
	public String getGroupStatus() {
		return groupStatus;
	}

	public void setGroupStatus(String groupStatus) {
		this.groupStatus = groupStatus;
	}

	public List<String> getGroupIds() {
		return groupIds;
	}

	public void setGroupIds(List<String> groupIds) {
		this.groupIds = groupIds;
	}

	public ProviewAudit createAudit(String splitTitleId, String bookVersion, Date lastUpdate, String command) {
		ProviewAudit audit = new ProviewAudit();
		audit.setAuditNote(comments);
		audit.setBookLastUpdated(lastUpdate);
		audit.setBookVersion(bookVersion);
		audit.setProviewRequest(command);
		audit.setRequestDate(new Date());
		audit.setTitleId(splitTitleId);
		audit.setUsername(UserUtils.getAuthenticatedUserName());
		return audit;
	}
	
	
	public String getComments() {
		return comments;
	}
	public void setComments(String comments) {
		this.comments = comments;
	}
	public GroupCmd getGroupCmd() {
		return groupCmd;
	}
	public void setGroupCmd(GroupCmd groupCmd) {
		this.groupCmd = groupCmd;
	}
	public String getVersion() {
		return version;
	}
	public void setVersion(String version) {
		this.version = version;
	}
	public String getGroupName() {
		return groupName;
	}
	public void setGroupName(String groupName) {
		this.groupName = groupName;
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
	public Long getBookDefinitionId() {
		return bookDefinitionId;
	}
	public void setBookDefinitionId(Long bookDefinitionId) {
		this.bookDefinitionId = bookDefinitionId;
	}
	public FilterCommand getFilterCommand() {
		return command;
	}
	public void setFilterCommand(FilterCommand command) {
		this.command = command;
	}
	
	public void initialize() {
		populate(null, null, null, null, null);
	}
	
	public GroupListFilterForm() {
		initialize();
	}
		
	public GroupListFilterForm(String groupName, Long bookId, List<String> groupIds,String groupID, String groupVersion) {
		this.groupName = groupName;
		this.bookDefinitionId = bookId;
		this.groupIds = groupIds;
		this.proviewGroupID = groupID;
		this.groupVersion = groupVersion;
	}
	
	public void populate(String titleId, String proviewDisplayName, String groupName, String version, Long bookId) {
		this.titleId = titleId;
		this.proviewDisplayName = proviewDisplayName;
		this.groupName = groupName;
		this.version = version;
		this.bookDefinitionId = bookId;
	}
	

}
