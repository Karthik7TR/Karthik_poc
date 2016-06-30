/*
 * Copyright 2016: Thomson Reuters Global Resources. All Rights Reserved.
 * Proprietary and Confidential information of TRGR. Disclosure, Use or
 * Reproduction without the written authorization of TRGR is prohibited
 */
package com.thomsonreuters.uscl.ereader.group.service;

import java.util.List;
import java.util.Map;

import com.thomsonreuters.uscl.ereader.core.book.domain.BookDefinition;
import com.thomsonreuters.uscl.ereader.deliver.exception.ProviewException;
import com.thomsonreuters.uscl.ereader.deliver.service.GroupDefinition;
import com.thomsonreuters.uscl.ereader.deliver.service.ProviewTitleInfo;

public interface GroupService {

	public String getGroupId(BookDefinition bookDefinition);
	
	public List<GroupDefinition> getGroups(String groupId) throws Exception;
	
	public List<GroupDefinition> getGroups(BookDefinition book) throws Exception;
	
	public GroupDefinition getGroupInfoByVersion(String groupId, Long groupVersion) throws ProviewException;
	
	public GroupDefinition getGroupInfoByVersionAutoDecrement(String groupId, Long groupVersion) throws ProviewException;
	
	public GroupDefinition getLastGroup(BookDefinition book) throws Exception;
	
	public GroupDefinition getLastGroup(String groupId) throws Exception;

	public void createGroup(GroupDefinition groupDefinition) throws ProviewException;
	
	public boolean isTitleWithVersion(String fullyQualifiedTitle);
	
	public GroupDefinition createGroupDefinition(BookDefinition bookDefinition, String bookVersion, List<String> splitTitles) throws Exception;
	
	public void removeAllPreviousGroups(BookDefinition bookDefinition) throws Exception;
	
	public Map<String, ProviewTitleInfo> getProViewTitlesForGroup(BookDefinition bookDef) throws Exception;
	
	public Map<String, ProviewTitleInfo> getPilotBooksForGroup(BookDefinition book) throws Exception;
	
	public List<ProviewTitleInfo> getMajorVersionProviewTitles(String titleId) throws ProviewException;
	
	public List<String> getPilotBooksNotFound();
	
}
