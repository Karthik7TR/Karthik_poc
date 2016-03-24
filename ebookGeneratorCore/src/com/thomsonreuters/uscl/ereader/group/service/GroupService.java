package com.thomsonreuters.uscl.ereader.group.service;

import java.util.List;

import com.thomsonreuters.uscl.ereader.GroupDefinition;
import com.thomsonreuters.uscl.ereader.GroupDefinition.SubGroupInfo;
import com.thomsonreuters.uscl.ereader.core.book.domain.BookDefinition;
import com.thomsonreuters.uscl.ereader.deliver.exception.ProviewException;

public interface GroupService {

	public String getGroupId(BookDefinition bookDefinition);

	public SubGroupInfo getSubGroupInfo(String majorVersion, String subGroupHeading,
			List<String> splitTitles)  throws Exception;

	public GroupDefinition getGroupDefinitionForSingleBooks(String groupInfoXML, String majorVersion, String newGroupName,
			String subGroupHeading, String fullyQualifiedTitleId) throws Exception;

	public Long getLastGroupVerionFromProviewResponse(String response) throws Exception;

	public GroupDefinition buildGroupDefinition(String groupName, String subGroupHeading, String fullyQualifiedTitleId,
			String majorVersion, List<String> splitTitles) throws Exception;
	
	public GroupDefinition getGroupDefinitionForSplitBooks(String proviewResponse, String majorVersion,
			String newGroupName, String subGroupHeading, String fullyQualifiedTitleId, List<String> subGroupInfoList) throws Exception;
	
	public String getGroupInfoByVersion(String groupId, Long groupVersion) throws ProviewException;
	 
	public void createGroup(GroupDefinition groupDefinition) throws ProviewException;
	 
	public Long getLastGroupVersionById(String groupId) throws Exception;
	 
	public GroupDefinition createGroupDefinition(BookDefinition bookDefinition, String bookVersion, List<String> splitTitles) throws Exception;
	 
	public GroupDefinition getLastGroupDefinition(BookDefinition bookDefinition) throws Exception;

}
