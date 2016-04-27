package com.thomsonreuters.uscl.ereader.group.service;

import java.util.List;
import java.util.Map;

import com.thomsonreuters.uscl.ereader.GroupDefinition;
import com.thomsonreuters.uscl.ereader.GroupDefinition.SubGroupInfo;
import com.thomsonreuters.uscl.ereader.core.book.domain.BookDefinition;
import com.thomsonreuters.uscl.ereader.deliver.exception.ProviewException;
import com.thomsonreuters.uscl.ereader.deliver.service.ProviewTitleInfo;

public interface GroupService {

	public String getGroupId(BookDefinition bookDefinition);

	public SubGroupInfo getSubGroupInfo(String majorVersion, String subGroupHeading,
			List<String> splitTitles)  throws Exception;

	public Long getLastGroupVerionFromProviewResponse(String response, List<String> groupVersions) throws Exception;

	public GroupDefinition buildGroupDefinition(String groupName, String subGroupHeading, String fullyQualifiedTitleId,
			String majorVersion, List<String> splitTitles) throws Exception;
	
	public GroupDefinition getGroupDefinitionforAllBooks(String proviewResponse, String majorVersion, String newGroupName,
			String newSubGroupHeading, String fullyQualifiedTitleId, List<String> splitTitles, boolean versionChange, boolean isSplitBook) throws Exception;
	
	public String getGroupInfoByVersion(String groupId, Long groupVersion) throws ProviewException;
	 
	public void createGroup(GroupDefinition groupDefinition) throws ProviewException;
	 
	public Long getLastGroupVersionById(String groupId) throws Exception;
	 
	public GroupDefinition createGroupDefinition(BookDefinition bookDefinition, String bookVersion, List<String> splitTitles) throws Exception;
	 
	public GroupDefinition getLastGroupDefinition(BookDefinition bookDefinition) throws Exception;

	public void removeAllPreviousGroups(BookDefinition bookDefinition) throws Exception;
	
	public boolean isTitleWithVersion(String fullyQualifiedTitle);
	
	public Map<String, ProviewTitleInfo> getProViewTitlesForGroup(BookDefinition bookDef) throws Exception;
}
