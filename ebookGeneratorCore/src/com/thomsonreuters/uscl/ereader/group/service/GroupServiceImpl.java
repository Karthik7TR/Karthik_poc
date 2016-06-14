/*
 * Copyright 2016: Thomson Reuters Global Resources. All Rights Reserved.
 * Proprietary and Confidential information of TRGR. Disclosure, Use or
 * Reproduction without the written authorization of TRGR is prohibited
 */
package com.thomsonreuters.uscl.ereader.group.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;

import com.thomsonreuters.uscl.ereader.core.CoreConstants;
import com.thomsonreuters.uscl.ereader.core.book.domain.BookDefinition;
import com.thomsonreuters.uscl.ereader.core.book.domain.SplitNodeInfo;
import com.thomsonreuters.uscl.ereader.deliver.exception.ProviewException;
import com.thomsonreuters.uscl.ereader.deliver.exception.ProviewRuntimeException;
import com.thomsonreuters.uscl.ereader.deliver.service.GroupDefinition;
import com.thomsonreuters.uscl.ereader.deliver.service.ProviewClient;
import com.thomsonreuters.uscl.ereader.deliver.service.ProviewTitleContainer;
import com.thomsonreuters.uscl.ereader.deliver.service.ProviewTitleInfo;
import com.thomsonreuters.uscl.ereader.deliver.service.GroupDefinition.SubGroupInfo;

public class GroupServiceImpl implements GroupService {
	
	private static final Logger LOG = Logger.getLogger(GroupServiceImpl.class);
	private GroupDefinitionParser proviewGroupParser = new GroupDefinitionParser();
	private ProviewClient proviewClient;  

	/**
	 * Group ID is unique to each major version
	 * @param bookDefinition
	 * @param versionNumber
	 * @return
	 */
	public String getGroupId(BookDefinition bookDefinition){
		StringBuffer buffer = new StringBuffer();
		buffer.append(bookDefinition.getPublisherCodes().getName());
		buffer.append("/");
		String contentType = null;
		if(bookDefinition.getDocumentTypeCodes() != null){
			contentType = bookDefinition.getDocumentTypeCodes().getAbbreviation();
		}
		if(!StringUtils.isBlank(contentType)){
			buffer.append(contentType+"_");
		}		
		buffer.append(StringUtils.substringAfterLast(bookDefinition.getFullyQualifiedTitleId(), "/"));		
		return buffer.toString();
	}
	
	public List<GroupDefinition> getGroups(BookDefinition bookDefinition) throws Exception {
		String groupId = getGroupId(bookDefinition);
		return getGroups(groupId);
	}
	
	public List<GroupDefinition> getGroups(String groupId) throws Exception {
		try{
			String response = proviewClient.getProviewGroupById(groupId);
			GroupDefinitionParser parser = new GroupDefinitionParser();
			List<GroupDefinition> groups = parser.parse(response);
			// sort by group versions
			Collections.sort(groups);
			return groups;
		} catch (ProviewRuntimeException ex) {
			String errorMsg = ex.getMessage();
			LOG.debug(errorMsg);
			if (ex.getStatusCode().equals("404") && errorMsg.contains("No such groups exist")) {
				LOG.debug("Group does not exist. Exception can be ignored");
			} else {
				throw new Exception(ex);
			}
		}
		return null;
	}
	
	public GroupDefinition getGroupInfoByVersion(String groupId, Long groupVersion) throws ProviewException {
			try {
				String response = proviewClient.getProviewGroupInfo(groupId, GroupDefinition.VERSION_NUMBER_PREFIX
						+ groupVersion.toString());
				List<GroupDefinition> groups = proviewGroupParser.parse(response);
				if(groups.size() == 1) {
					return groups.get(0);
				}
			} catch (ProviewRuntimeException ex) {
				if (ex.getStatusCode().equals("400") && ex.toString().contains("No such group id and version exist")) {
					// ignore and return null
				} else {
					throw new ProviewException(ex.getMessage());
				}
			} catch(Exception ex) {
				throw new ProviewException(ex.getMessage());
			}
		return null;
	}
	
	public GroupDefinition getGroupInfoByVersionAutoDecrement(String groupId, Long groupVersion) throws ProviewException {
		GroupDefinition group = null;
		do {
			group = getGroupInfoByVersion(groupId, groupVersion);
			if(group == null) {
				groupVersion = groupVersion - 1;
			} else {
				break;
			}
		} while (groupVersion > 0);
		return group;
	}
	
	public GroupDefinition getLastGroup(BookDefinition book) throws Exception {
		String groupId = getGroupId(book);
		return getLastGroup(groupId);
	}
    
    public GroupDefinition getLastGroup(String groupId) throws Exception{
    	List<GroupDefinition> groups = getGroups(groupId);
    	if(groups != null && groups.size() > 0) {
    		return groups.get(0);
    	}
		return null;
	}
	
	/**
	 * Send Group definition to Proview to create a group
	 */
	public void createGroup(GroupDefinition groupDefinition) throws ProviewException {
		try {
			proviewClient.createGroup(groupDefinition);
		} catch (ProviewRuntimeException ex) {
			String errorMsg = ex.getMessage();
			if (ex.getStatusCode().equalsIgnoreCase("400")) {
				if (errorMsg.contains("This Title does not exist")) {
					throw new ProviewException(CoreConstants.NO_TITLE_IN_PROVIEW);
				} else if (errorMsg.contains("GroupId already exists with same version")
						|| errorMsg.contains("Version Should be greater")) {
					throw new ProviewException(CoreConstants.GROUP_AND_VERSION_EXISTS);
				} else {
					throw new ProviewException(errorMsg);
				}
			}
			else{
				throw new ProviewException(errorMsg);
			}
		}
	}
	
    public boolean isTitleWithVersion(String fullyQualifiedTitle){
    	//Sammple title with version uscl/an/abcd/v1
    	if(StringUtils.isNotBlank(fullyQualifiedTitle)) {
	    	Pattern trimmer = Pattern.compile("/v\\d+(\\.\\d+)?$");
			Matcher m = trimmer.matcher(fullyQualifiedTitle);
			if (m.find()) {
				return true;
			}
    	}
		return false;
    }
     
    /**
     * Group will be created based on user input. splitTitles will be null if book is not a splitbook
     */
	public GroupDefinition createGroupDefinition(BookDefinition bookDefinition, String bookVersion, List<String> splitTitles)
			throws Exception {
		String fullyQualifiedTitleId = bookDefinition.getFullyQualifiedTitleId();
		String groupName = bookDefinition.getGroupName();
		String subGroupHeading = bookDefinition.getSubGroupHeading();
		String groupId = getGroupId(bookDefinition);
		GroupDefinition lastGroupDefinition = getLastGroup(groupId);
		
		String majorVersionStr = bookVersion;
		if (StringUtils.contains(bookVersion, '.')) {
			majorVersionStr = StringUtils.substringBefore(bookVersion, ".");
		}
		// check errors in book definition compared to previous group
		validate(bookDefinition, lastGroupDefinition, majorVersionStr);

		GroupDefinition groupDefinition = createNewGroupDefinition(groupName, subGroupHeading, fullyQualifiedTitleId, majorVersionStr,
				splitTitles);
		if (lastGroupDefinition != null && StringUtils.isNotBlank(subGroupHeading)) {
			Set<String> existingTitles = new HashSet<String>();
			SubGroupInfo firstSubGroupInfo = groupDefinition.getSubGroupInfoList().get(0);

			for(String title: firstSubGroupInfo.getTitles()) {
				existingTitles.add(title);
			}
			
			// Add titles from previous group if it had subgroups
			if(lastGroupDefinition.subgroupExists()) {
				for(SubGroupInfo previousSubGroupInfo: lastGroupDefinition.getSubGroupInfoList()) {
					String previousSubgroupHeading = previousSubGroupInfo.getHeading();
					if(subGroupHeading.equalsIgnoreCase(previousSubgroupHeading)) {
						// Subgroup heading matches, add titles to currently created subgroup
						copyTitlesInSubgroup(previousSubGroupInfo, firstSubGroupInfo, existingTitles, fullyQualifiedTitleId,
								majorVersionStr);
					} else {
						// Create new subgroups to add to group
						SubGroupInfo newSubGroupInfo = new SubGroupInfo();
						newSubGroupInfo.setHeading(previousSubGroupInfo.getHeading());
						copyTitlesInSubgroup(previousSubGroupInfo, newSubGroupInfo, existingTitles, fullyQualifiedTitleId,
								majorVersionStr);
						// Only add subgroup if title(s) exists
						if(newSubGroupInfo.getTitles().size() > 0) {
							groupDefinition.addSubGroupInfo(newSubGroupInfo);
						}
					}
				}
			}
		} 
		
		// set group version
		if (lastGroupDefinition != null) {
			if(lastGroupDefinition.getStatus().equalsIgnoreCase(GroupDefinition.REVIEW_STATUS)){
				groupDefinition.setGroupVersion(lastGroupDefinition.getGroupVersion());
			}
			else{
				groupDefinition.setGroupVersion(lastGroupDefinition.getGroupVersion() + 1);
			}
		} else {
			groupDefinition.setGroupVersion(1L);
		}
		groupDefinition.setGroupId(groupId);
		groupDefinition.setType("standard");
		return groupDefinition;
	}
	
	private void validate(BookDefinition book, GroupDefinition previousGroup, String majorVersionStr) throws ProviewException {
		String currentSubgroupName = book.getSubGroupHeading();
		String previousHeadTitle = previousGroup != null ? previousGroup.getHeadTitle(): null;
		Boolean majorVersionChange = false;
		Integer majorVersion = null;
		if (StringUtils.contains(majorVersionStr, 'v')) {
			majorVersion = Integer.valueOf(StringUtils.substringAfter(majorVersionStr, "v"));
		}
		
		if(isTitleWithVersion(previousHeadTitle)){
			String versionOnPreviousHeadTtile = StringUtils.substringAfterLast(previousHeadTitle, "/");
			if(!majorVersionStr.equalsIgnoreCase(versionOnPreviousHeadTtile)){
				majorVersionChange = true;
			}
		}
		
		if (StringUtils.isBlank(book.getGroupName())) {
			throw new ProviewException(CoreConstants.EMPTY_GROUP_ERROR_MESSAGE);
		}
		
		if(book.isSplitBook() && StringUtils.isBlank(currentSubgroupName)) {
			throw new ProviewException("Subgroup name cannot be empty");
		}
		
		if(majorVersion > 1 && StringUtils.isNotBlank(currentSubgroupName) && (previousGroup == null || 
				(previousGroup != null && StringUtils.isBlank(previousGroup.getFirstSubgroupHeading())))) {
			throw new ProviewException(CoreConstants.SUBGROUP_ERROR_MESSAGE);
		}
		
		if(previousGroup != null) {
			List<SubGroupInfo> previousSubgroups = previousGroup.getSubGroupInfoList();
			for(int i=0; i < previousSubgroups.size(); i++) {
				SubGroupInfo subGroupInfo = previousSubgroups.get(i);
				String previousSubgroupHeading = subGroupInfo.getHeading();
				
				if(StringUtils.isNotBlank(currentSubgroupName) ) {
					//Every major version should have a subgroup heading changed for split books
					if(i == 0 && majorVersionChange && book.isSplitBook() && currentSubgroupName.equalsIgnoreCase(previousSubgroupHeading)) {
						throw new ProviewException(CoreConstants.SUBGROUP_SPLIT_ERROR_MESSAGE);
					}
					// Check for duplicate subgroup heading starting from index 1 and up.
					// First subgroup (index 0) can have the same subgroup heading
					if(i > 0 && currentSubgroupName.equalsIgnoreCase(previousSubgroupHeading)) {
						throw new ProviewException(CoreConstants.DUPLICATE_SUBGROUP_ERROR_MESSAGE);
					}
				}
			}
		}
	}
	
	private void copyTitlesInSubgroup(SubGroupInfo previousSubGroupInfo, SubGroupInfo currentSubGroupInfo,
			Set<String> currentTitles, String fullyQualifiedTitleId, String majorVersionStr) {
		// Subgroup heading matches, add titles to currently created subgroup
		for(String title: previousSubGroupInfo.getTitles()) {
			if(!currentTitles.contains(title)) {
				if(StringUtils.endsWithIgnoreCase(title, majorVersionStr) &&
						StringUtils.startsWithIgnoreCase(title, fullyQualifiedTitleId)) {
					// skip adding titles from previous group if it contains the same title id and major version
					// Accounts for cases where previous minor version book had more parts compared to current minor version.
					// The additional parts should not be added because number of split books changed with the minor update.
				} else {
					// Add titles that are not already in subgroup
					currentSubGroupInfo.addTitle(title);
				}
			}
		}
	}
	
	private GroupDefinition createNewGroupDefinition(String groupName, String subGroupHeading,
			String fullyQualifiedTitleId, String majorVersion, List<String> splitTitles) throws Exception{
		GroupDefinition groupDefinition = new GroupDefinition();
		groupDefinition.setName(groupName);
		
		List<SubGroupInfo> subGroupInfoList = new ArrayList<SubGroupInfo>();
		SubGroupInfo subGroupInfo = new SubGroupInfo();		
		List<String> titleList = new ArrayList<String>();
		
		if(StringUtils.isNotBlank(subGroupHeading)) {
			groupDefinition.setHeadTitle(fullyQualifiedTitleId + "/" + majorVersion);
			subGroupInfo.setHeading(subGroupHeading);
			
			//For split books
			if(splitTitles != null && !splitTitles.isEmpty()){
				for (String splitTitleId : splitTitles) {
					titleList.add(splitTitleId + "/" + majorVersion);
				}
			} else {
				titleList.add(fullyQualifiedTitleId+ "/" + majorVersion);
			}
			
		} else{
			groupDefinition.setHeadTitle(fullyQualifiedTitleId);
			titleList.add(fullyQualifiedTitleId);
		}
		
		subGroupInfo.setTitles(titleList);
		subGroupInfoList.add(subGroupInfo);
		groupDefinition.setSubGroupInfoList(subGroupInfoList);
		return groupDefinition;
	}
	
	public void removeAllPreviousGroups(BookDefinition bookDefinition) throws Exception {
		List<GroupDefinition> GroupDefinition = getGroups(bookDefinition);
		
		if(GroupDefinition != null){
			for (GroupDefinition group : GroupDefinition) {
				proviewClient.removeGroup(group.getGroupId(), group.getProviewGroupVersionString());
				TimeUnit.SECONDS.sleep(2);
				proviewClient.deleteGroup(group.getGroupId(), group.getProviewGroupVersionString());			
			}
		}

	}
	
	/**
     * Get list of ProView titles that belong in the group for given book definition.
     * Note: adds pilot titles for Analytical titles based on fully qualified title id contains _waspilot
     */
	public Map<String, ProviewTitleInfo> getProViewTitlesForGroup(BookDefinition bookDef) throws Exception {
		Set<SplitNodeInfo> splitNodeInfos = bookDef.getSplitNodes();
		
		// Get fully qualified title IDs of all split titles
		Set<String> splitTitles = new HashSet<String>();
		if(splitNodeInfos != null && splitNodeInfos.size() > 0) {
			for(SplitNodeInfo splitNodeInfo: splitNodeInfos) {
				splitTitles.add(splitNodeInfo.getSplitBookTitle());
			}
		}
		// Add current fully qualified title Id
		splitTitles.add(bookDef.getFullyQualifiedTitleId());
		List<ProviewTitleInfo> proviewTitleInfos = new ArrayList<>();
		for(String title: splitTitles) {
			List<ProviewTitleInfo> proviewTitleInfo = getMajorVersionProviewTitles(title);
			proviewTitleInfos.addAll(proviewTitleInfo);
		}
		// sort split/single books before adding pilot books
		Collections.sort(proviewTitleInfos);
		
		// account for Analytical pilot books which do not have book definition
		if(bookDef.getFullyQualifiedTitleId().contains("_waspilot")) {
			String pilotTitleId = bookDef.getFullyQualifiedTitleId().replace("_waspilot", "");
			List<ProviewTitleInfo> proviewTitleInfo = getMajorVersionProviewTitles(pilotTitleId);
			proviewTitleInfos.addAll(proviewTitleInfo);
		}
		
		Map<String, ProviewTitleInfo> proviewTitleMap = new LinkedHashMap<>();
		for(ProviewTitleInfo info: proviewTitleInfos) {
			String key = info.getTitleId() + "/v" + info.getMajorVersion();
			proviewTitleMap.put(key, info);
		}
		return proviewTitleMap;
	}
	
	public List<ProviewTitleInfo> getMajorVersionProviewTitles(String titleId) throws ProviewException {
		ProviewTitleContainer container = proviewClient.getProviewTitleContainer(titleId);
		if(container != null) {
			return container.getAllMajorVersions();
		}
		return new ArrayList<ProviewTitleInfo>();
	}
	
	@Required
	public void setProviewClient(ProviewClient proviewClient) {
		this.proviewClient = proviewClient;
	}	

}
