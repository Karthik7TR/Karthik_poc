package com.thomsonreuters.uscl.ereader.mgr.web.controller.proviewgroup;

import java.util.Date;
import java.util.List;

import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import com.thomsonreuters.uscl.ereader.mgr.web.UserUtils;
import com.thomsonreuters.uscl.ereader.proviewaudit.domain.ProviewAudit;

public class ProviewGroupListFilterForm {

	public static final String FORM_NAME = "proviewGroupListFilterForm";

	public enum FilterCommand {
		SEARCH, RESET
	};

	public enum GroupCmd {
		REMOVE, DELETE, PROMOTE
	};

	private String groupName;
	private FilterCommand filterCommand;

	private GroupCmd groupCmd;
	private String proviewGroupID;
	private String groupVersion;
	private String groupStatus;
	private List<String> groupIds;
	private Long bookDefinitionId;
	private String comments;
	private boolean groupOperation;

	public boolean isGroupOperation() {
		return groupOperation;
	}

	public void setGroupOperation(boolean groupOperation) {
		this.groupOperation = groupOperation;
	}

	private String groupIdByVersion;

	public String getGroupIdByVersion() {
		return groupIdByVersion;
	}

	public void setGroupIdByVersion(String groupIdByVersion) {
		this.groupIdByVersion = groupIdByVersion;
	}

	public ProviewGroupListFilterForm() {

	}

	public List<String> getGroupIds() {
		return groupIds;
	}

	public void setGroupIds(List<String> groupIds) {
		this.groupIds = groupIds;
	}

	public String getProviewGroupID() {
		return proviewGroupID;
	}

	public Long getBookDefinitionId() {
		return bookDefinitionId;
	}

	public void setBookDefinitionId(Long bookDefinitionId) {
		this.bookDefinitionId = bookDefinitionId;
	}

	public void setProviewGroupID(String groupID) {
		this.proviewGroupID = groupID;
	}

	public String getGroupVersion() {
		return groupVersion;
	}

	public String getGroupStatus() {
		return groupStatus;
	}

	public void setGroupStatus(String groupStatus) {
		this.groupStatus = groupStatus;
	}

	public void setGroupVersion(String groupVersion) {
		this.groupVersion = groupVersion;
	}

	public GroupCmd getGroupCmd() {
		return groupCmd;
	}

	public void setGroupCmd(GroupCmd groupCmd) {
		this.groupCmd = groupCmd;
	}

	public void initNull() {
		init(null, null);
	}

	private void init(String proviewGroupID, String groupName) {
		this.proviewGroupID = proviewGroupID;
		this.groupName = groupName;
	}

	public String getGroupName() {
		return groupName;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

	public FilterCommand getFilterCommand() {
		return filterCommand;
	}

	public void setFilterCommand(FilterCommand filterCommand) {
		this.filterCommand = filterCommand;
	}

	public ProviewAudit createAudit(String titleId, String bookVersion, Date lastUpdate, String command, String comments) {
		ProviewAudit audit = new ProviewAudit();
		audit.setAuditNote(comments);
		audit.setBookLastUpdated(lastUpdate);
		audit.setBookVersion(bookVersion);
		audit.setProviewRequest(command);
		audit.setRequestDate(new Date());
		audit.setTitleId(titleId);
		audit.setUsername(UserUtils.getAuthenticatedUserName());
		return audit;
	}

	public String getComments() {
		return comments;
	}

	public void setComments(String comments) {
		this.comments = comments;
	}

	public ProviewGroupListFilterForm(String groupName, Long bookId, List<String> groupIds, String groupID,
			String groupVersion, String groupByVersion, boolean groupOperation) {
		this.groupName = groupName;
		this.bookDefinitionId = bookId;
		this.groupIds = groupIds;
		this.bookDefinitionId = bookId;
		this.proviewGroupID = groupID;
		this.groupVersion = groupVersion;
		this.groupOperation = groupOperation;
	}

	public String toString() {
		return ReflectionToStringBuilder.toString(this, ToStringStyle.SHORT_PREFIX_STYLE);
	}

}
