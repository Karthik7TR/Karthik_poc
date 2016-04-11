package com.thomsonreuters.uscl.ereader.group.service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.thomsonreuters.uscl.ereader.GroupDefinition;
import com.thomsonreuters.uscl.ereader.GroupDefinition.SubGroupInfo;
import com.thomsonreuters.uscl.ereader.core.CoreConstants;
import com.thomsonreuters.uscl.ereader.core.book.domain.BookDefinition;
import com.thomsonreuters.uscl.ereader.deliver.exception.ProviewException;
import com.thomsonreuters.uscl.ereader.deliver.exception.ProviewRuntimeException;
import com.thomsonreuters.uscl.ereader.deliver.service.ProviewClient;
import com.thomsonreuters.uscl.ereader.util.XMLXpathEvaluator;

public class GroupServiceImpl implements GroupService {
	
	private static final Logger LOG = Logger.getLogger(GroupServiceImpl.class);
	private ProviewClient proviewClient;  
	private int maxNumberOfRetries = 3;
	
	public Long getLastGroupVerionFromProviewResponse(String response, List<String> groupVersions) throws Exception {
		Long groupVersion = null;
		XMLXpathEvaluator extractor = new XMLXpathEvaluator(response);
		NodeList groups = extractor.evaluateNodeList("groups/group");

		for (int i = 0; i < groups.getLength(); i++) {
			Node group = groups.item(i);
			NamedNodeMap attributesMap = group.getAttributes();
			if (attributesMap != null && attributesMap.getLength() > 0) {
				for (int j = 0; j < attributesMap.getLength(); j++) {
					Node attributeNode = attributesMap.item(j);
					String attributeName = attributeNode.getNodeName();
					if (attributeName.equalsIgnoreCase("version")) {
						Long version = Long
								.valueOf(StringUtils.substringAfterLast(attributeNode.getTextContent(), "v"));
						groupVersions.add(attributeNode.getTextContent());
						if (groupVersion == null || groupVersion < version) {
							groupVersion = Long.valueOf(version);
						}
					}
				}
			}

		}
		return groupVersion;

	}
	
	public GroupDefinition buildGroupDefinition(String groupName, String subGroupHeading,
			String fullyQualifiedTitleId, String majorVersion, List<String> splitTitles) throws Exception{
		GroupDefinition groupDefinition = new GroupDefinition();
		groupDefinition.setName(groupName);
		
		List<SubGroupInfo> subGroupInfoList = new ArrayList<SubGroupInfo>();
		SubGroupInfo subGroupInfo = new SubGroupInfo();		
		List<String> titleList = new ArrayList<String>();
		
		//For split books
		if(splitTitles != null && !splitTitles.isEmpty()){
			groupDefinition.setHeadTitle(fullyQualifiedTitleId + "/" + majorVersion);
			subGroupInfoList.add(getSubGroupInfo(majorVersion,subGroupHeading,splitTitles));
			groupDefinition.setSubGroupInfoList(subGroupInfoList);
			return groupDefinition;
		}
		//For single books
		//If no subgroup provided then 
		if (!StringUtils.isEmpty(subGroupHeading) ){
			groupDefinition.setHeadTitle(fullyQualifiedTitleId + "/" + majorVersion);
			subGroupInfo.setHeading(subGroupHeading);
			String number = StringUtils.substringAfter(majorVersion, "v");
			if(Integer.parseInt(number) > 1)
			{
				throw new ProviewException(CoreConstants.SUBGROUP_ERROR_MESSAGE);
			}
			else{
				titleList.add(fullyQualifiedTitleId+ "/" + majorVersion);
			}
			
		}
		else{
			groupDefinition.setHeadTitle(fullyQualifiedTitleId);
			titleList.add(fullyQualifiedTitleId);
		}
		subGroupInfo.setTitles(titleList);
		subGroupInfoList.add(subGroupInfo);
		groupDefinition.setSubGroupInfoList(subGroupInfoList);
		return groupDefinition;
	}
	
	
	protected List<SubGroupInfo> getPreviousSubgroups(NodeList subGroups, int index){
		List<SubGroupInfo> subGroupInfoList = new ArrayList<SubGroupInfo>();
		if(subGroups.getLength() < index){
			return subGroupInfoList;
		}
		for (int i = index; i < subGroups.getLength(); i++) {
			SubGroupInfo subGroupInfo = new SubGroupInfo();
			Node subGroup = subGroups.item(i);
			Node headingNodeAttr = subGroup.getAttributes().getNamedItem("heading");
			String heading = headingNodeAttr != null ? headingNodeAttr.getNodeValue() : null;
			if(!StringUtils.isEmpty(heading)){
				subGroupInfo.setHeading(heading);
			}
			NodeList titles = subGroup.getChildNodes();
			List<String> titleList = new ArrayList<String>();
			if (titles.getLength() > 0) {
				for (int count = 0; count < titles.getLength(); count++) {
					String title = titles.item(count).getTextContent();
					titleList.add(title);
				}
			}
			subGroupInfo.setTitles(titleList);
			subGroupInfoList.add(subGroupInfo);
		}
		
		return subGroupInfoList;
	}
	
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
		if(!StringUtils.isEmpty(contentType)){
			buffer.append(contentType+"_");
		}		
		buffer.append(StringUtils.substringAfterLast(bookDefinition.getFullyQualifiedTitleId(), "/"));		
		return buffer.toString();
	}	
	
	public SubGroupInfo getSubGroupInfo(String majorVersion, String subGroupHeading,
			List<String> splitTitles) throws Exception{

		SubGroupInfo subGroupInfo = new SubGroupInfo();
		
		if(StringUtils.isEmpty(subGroupHeading)){
			throw new ProviewException("Subgroup name cannot be empty");
		}
		
		subGroupInfo.setHeading(subGroupHeading);
		List<String> titleList = new ArrayList<String>();
		for (String splitTitleId : splitTitles) {
			titleList.add(splitTitleId + "/" + majorVersion);
		}

		subGroupInfo.setTitles(titleList);

		return subGroupInfo;
	}
	
	/**
	 * Send Group definition to Proview to create a group
	 */
	public void createGroup(GroupDefinition groupDefinition) throws ProviewException {
		boolean retryRequest = true;

		//Most of the books should finish in two minutes
		try{
		TimeUnit.MINUTES.sleep(2);
		} catch (InterruptedException e) {
			LOG.error("InterruptedException during HTTP retry", e);
		};
		
		int retryCount = 0;
		String errorMsg = "";
		do {
			try {
				proviewClient.createGroup(groupDefinition);
				retryRequest = false;
			} catch (ProviewRuntimeException ex) {
				errorMsg = ex.getMessage();
				if (errorMsg.startsWith("400") && errorMsg.contains("This Title does not exist")){
					// retry a retriable request					

					LOG.warn("Retriable status received: waiting " + 15 + "minutes (retryCount: "
							+ retryCount +")");

					retryRequest = true;
					retryCount++;

					try {
						TimeUnit.MINUTES.sleep(15);
					} catch (InterruptedException e) {
						LOG.error("InterruptedException during HTTP retry", e);
					};
				}
				else if (errorMsg.startsWith("400")
						&& (errorMsg.contains("GroupId already exists with same version") || 
								errorMsg.contains("Version Should be greater"))) {
					retryRequest = true;
					retryCount++;
					Long groupVersion = groupDefinition.getGroupVersion();
					groupDefinition.setGroupVersion(groupVersion + 1);
				}
				else {
					throw new ProviewRuntimeException(errorMsg);
				}
			}
		} while (retryRequest && retryCount < getMaxNumberOfRetries());
		if (retryRequest && retryCount == getMaxNumberOfRetries()) {
			throw new ProviewRuntimeException(
					"Tried 3 times to create group and not succeeded. Proview might be down "
					+ "or still in the process of loading parts of the book. Please try again later. ");
		}

	}
	
    
    public String getGroupInfoByVersion(String groupId, Long groupVersion) throws ProviewException {
		String response = null;
		do {
			try {
				response = proviewClient.getProviewGroupInfo(groupId, GroupDefinition.VERSION_NUMBER_PREFIX
						+ groupVersion.toString());
				return response;
			} catch (ProviewRuntimeException ex) {
				if (ex.getMessage().startsWith("400") && ex.toString().contains("No such group id and version exist")) {
					// go down the version by one if the current version is
					// deleted in Proview
					groupVersion = groupVersion - 1;
				} else {
					throw new ProviewRuntimeException(ex.getMessage());
				}
			}
		} while (groupVersion > 0);
		return response;
	}
    
    public Long getLastGroupVersionById(String groupId) throws Exception{
		Long groupVersion = null;
		try {
			String response = proviewClient.getProviewGroupById(groupId);
			List<String> groupVersions = new ArrayList<String> ();
			return getLastGroupVerionFromProviewResponse(response, groupVersions);
		} catch (ProviewRuntimeException ex) {
			String errorMsg = ex.getMessage();
			LOG.debug(errorMsg);
			if (errorMsg.startsWith("404") && errorMsg.contains("No such groups exist")){
				return groupVersion;
			}
			else{
				throw new Exception(ex);
			}
		}		
	}
    
	public boolean validateResponse(BookDefinition bookDefinition, String proviewResponse, String majorVersion) throws Exception {
		XMLXpathEvaluator extractor = new XMLXpathEvaluator(proviewResponse);
		String subGroup = extractor.evaluate("group/members/subgroup/@heading");
		String  headTtile = extractor.evaluateNode("group/headtitle").getTextContent();	
		String  versionOnHeadTtile = StringUtils.substringAfterLast(headTtile, "/v");
		
		boolean versionChange = false;
		
		if(!StringUtils.isEmpty(versionOnHeadTtile) && !majorVersion.equalsIgnoreCase("v"+versionOnHeadTtile)){
			versionChange = true;
		}
		
		// No subgroups but book definition requires subgroup
		if (StringUtils.isEmpty(subGroup) && !StringUtils.isEmpty(bookDefinition.getSubGroupHeading())) {
			throw new ProviewException(CoreConstants.SUBGROUP_ERROR_MESSAGE);
		}
		if (!StringUtils.isEmpty(bookDefinition.getSubGroupHeading())) {
			NodeList subGroups = extractor.evaluateNodeList("group/members/subgroup");
			for (int i = 0; i < subGroups.getLength(); i++) {
				Node item = subGroups.item(i);
				Node headingNodeAttr = item.getAttributes().getNamedItem("heading");
				String heading = headingNodeAttr != null ? headingNodeAttr.getNodeValue() : null;
				
				if (!StringUtils.isEmpty(heading) && bookDefinition.getSubGroupHeading().equalsIgnoreCase(heading)) {
					//Every major version should have a subgroup heading changed for split books not necessary for single boooks
					if(versionChange && i==0 && bookDefinition.isSplitBook()){
					throw new ProviewException(CoreConstants.SUBGROUP_SPLIT_ERROR_MESSAGE);
					}
					//Previous subgroups should not have same subgroupName
					else if(i==1){
						throw new ProviewException(CoreConstants.DUPLICATE_SUBGROUP_ERROR_MESSAGE);
					}
				}			
				
			}
			
		}
		
		return versionChange;
	}
	
	public GroupDefinition getGroupDefinitionforAllBooks(String proviewResponse, String majorVersion, String newGroupName,
			String newSubGroupHeading, String fullyQualifiedTitleId, List<String> splitTitles, boolean versionChange, boolean isSplitBook) throws Exception {
		boolean createGroup = false;
		GroupDefinition groupDefinition = null;
		XMLXpathEvaluator extractor = new XMLXpathEvaluator(proviewResponse);
		String groupName = extractor.evaluate("group/name");
		//check to see if this gives first in the list
		String subGroupHeading = extractor.evaluate("group/members/subgroup/@heading");
		
		NodeList subgroups = extractor.evaluateNodeList("group/members/subgroup");
		NodeList firstSubGroupTitles = subgroups.item(0).getChildNodes();
		boolean isFirstSubgroupSplitBook = true;
		
		if (firstSubGroupTitles.getLength() > 0) {
			List<String> versionList = new ArrayList<String>();
			for (int count = 0; count < firstSubGroupTitles.getLength(); count++) {
				if(firstSubGroupTitles.getLength() == 1){
					isFirstSubgroupSplitBook = false;
					break;
				}
				String title = firstSubGroupTitles.item(count).getTextContent();
				title = StringUtils.substringAfterLast(title, "/v");
				if(StringUtils.isEmpty(title)){
					isFirstSubgroupSplitBook = false;
					break;
				}
				else{
					if(!versionList.isEmpty() && !versionList.contains(title)){
						isFirstSubgroupSplitBook = false;
						break;
					}
					versionList.add(title);
				}
				
			}
		}

		if (!groupName.equalsIgnoreCase(newGroupName)) {
			createGroup = true;
		}
		else if(StringUtils.isEmpty(subGroupHeading) && !StringUtils.isEmpty(newSubGroupHeading) ) {
			createGroup = true;
		}
		else if(!StringUtils.isEmpty(subGroupHeading) && !subGroupHeading.equalsIgnoreCase(newSubGroupHeading)){
				createGroup = true;			
		}
		else if (!StringUtils.isEmpty(subGroupHeading) && subGroupHeading.equalsIgnoreCase(newSubGroupHeading)
				&& versionChange) {

			createGroup = true;
		}
		else if (isSplitBook){	
			//Change in the number of splits
			if(firstSubGroupTitles.getLength() != splitTitles.size()  || versionChange){
				createGroup = true;
			}	
			//Change from single to split
			else if (!isFirstSubgroupSplitBook){
				createGroup = true;
			}
		}
		//From split title to Single title
		else if(isFirstSubgroupSplitBook){
			createGroup = true;
		}
		
		if(createGroup){
			if(StringUtils.isEmpty(newSubGroupHeading)){
				//build group for single book for all versions
				groupDefinition = buildGroupDefinition(newGroupName, newSubGroupHeading, fullyQualifiedTitleId, majorVersion,
						splitTitles);
				return groupDefinition;
			}
			
			else {
					groupDefinition = new GroupDefinition();
					groupDefinition.setName(newGroupName);
					int index = 1;
					NodeList subGroups = extractor.evaluateNodeList("group/members/subgroup");
				    groupDefinition.setHeadTitle(fullyQualifiedTitleId + "/" + majorVersion);
					SubGroupInfo subGroupInfo = new SubGroupInfo();
					subGroupInfo.setHeading(newSubGroupHeading);
					List<SubGroupInfo> subGroupInfoList = new ArrayList<SubGroupInfo>();
					List<String> titleList = new ArrayList<String>();
					
					if(splitTitles != null && !splitTitles.isEmpty()){
						for (String splitTitleId : splitTitles) {
							titleList.add(splitTitleId + "/" + majorVersion);
						}						
					}
					else{
						titleList.add(fullyQualifiedTitleId + "/" + majorVersion);
					}
					if(!versionChange && !isFirstSubgroupSplitBook){
						for (int count = 0; count < firstSubGroupTitles.getLength(); count++) {
							String title = firstSubGroupTitles.item(count).getTextContent();
							title = StringUtils.substringAfterLast(title, "/v");
							if(!StringUtils.isEmpty(title) && !majorVersion.equalsIgnoreCase("v"+title)){
								titleList.add(firstSubGroupTitles.item(count).getTextContent());
							}
						}
					}
					
					//For single titles we allow different versions to same subgroup
					if(versionChange && subGroupHeading.equalsIgnoreCase(newSubGroupHeading)){						
							if( subGroups.getLength() >0){
								NodeList titles = subGroups.item(0).getChildNodes();
								if (titles.getLength() > 0) {
									for (int count = 0; count < titles.getLength(); count++) {
										String title = titles.item(count).getTextContent();
										titleList.add(title);
									}
								}
							}
					}
					else if (versionChange){
						index = 0;
					}
					subGroupInfo.setTitles(titleList);
					subGroupInfoList.add(subGroupInfo);
										
					//Add all previous subgroups
					subGroupInfoList.addAll(getPreviousSubgroups(subGroups,index));						
					groupDefinition.setSubGroupInfoList(subGroupInfoList);
					
					return groupDefinition;
			}
		}
		

		return groupDefinition;
	}
     
    /**
     * Group will be created based on user input. splitTitles will be null if book is not a splitbook
     */
	public GroupDefinition createGroupDefinition(BookDefinition bookDefinition, String bookVersion, List<String> splitTitles)
			throws Exception {
		Long groupVersion = new Long(0);

		String groupName = bookDefinition.getGroupName();

		if (StringUtils.isEmpty(groupName)) {
			throw new ProviewException(CoreConstants.EMPTY_GROUP_ERROR_MESSAGE);
		}

		String majorVersion = bookVersion;
		if (StringUtils.contains(bookVersion, '.')) {
			majorVersion = StringUtils.substringBefore(bookVersion, ".");
		}
		String fullyQualifiedTitleId = bookDefinition.getFullyQualifiedTitleId();

		String groupId = getGroupId(bookDefinition);
		String subGroupHeading = bookDefinition.getSubGroupHeading();
		Long lastGroupVersion = getLastGroupVersionById(groupId);
		GroupDefinition groupDefinition = null;

		if (lastGroupVersion != null) {
			String proviewResponse = getGroupInfoByVersion(groupId, lastGroupVersion);
			
			boolean versionChange = false;
			if(!majorVersion.equalsIgnoreCase("v1")){
				versionChange = validateResponse(bookDefinition,proviewResponse,majorVersion);
			}
			groupDefinition = getGroupDefinitionforAllBooks(proviewResponse, majorVersion, groupName,
					subGroupHeading, fullyQualifiedTitleId, splitTitles, versionChange, bookDefinition.isSplitBook());			
			groupVersion = lastGroupVersion;
			// Increment the group version if the group exists.
			if (groupDefinition != null) {
				groupVersion = lastGroupVersion + 1;
			}

		} else {
			// Group version will be 1 if no group exists
			//splitTitles is null for single books.
			groupVersion = groupVersion + 1;
			groupDefinition = buildGroupDefinition(groupName, subGroupHeading, fullyQualifiedTitleId, majorVersion,
					splitTitles);
		}

		if (groupDefinition != null) {
			groupDefinition.setGroupId(groupId);
			groupDefinition.setType("standard");
			groupDefinition.setGroupVersion(groupVersion);
		}
		return groupDefinition;
	}
	
	public GroupDefinition getLastGroupDefinition(BookDefinition bookDefinition) throws Exception {
		GroupDefinition groupDefinition = null;
		String groupId = getGroupId(bookDefinition);
		Long lastGroupVersion = getLastGroupVersionById(groupId);
		if (lastGroupVersion != null) {
			String proviewResponse = getGroupInfoByVersion(groupId, lastGroupVersion);
			ProViewGroupsParser parser = new ProViewGroupsParser();
			List<GroupDefinition> groups = parser.parse(proviewResponse);
			if(groups.size() == 1) {
				groupDefinition = groups.get(0);
			}
		}
		return groupDefinition;
	}
	
	public void removeAllPreviousGroups(BookDefinition bookDefinition) throws Exception {
		String groupId = getGroupId(bookDefinition);
		List<String> groupVersions = new ArrayList<String>();
		try {
			String response = proviewClient.getProviewGroupById(groupId);
			getLastGroupVerionFromProviewResponse(response, groupVersions);
		} catch (ProviewRuntimeException ex) {
			String errorMsg = ex.getMessage();
			LOG.debug(errorMsg);
			if (errorMsg.startsWith("404") && errorMsg.contains("No such groups exist")) {
				LOG.debug("Group does not exist. Exception can be ignored");
			} else {
				throw new Exception(ex);
			}
		}

		for (String groupVersion : groupVersions) {
			proviewClient.removeGroup(groupId, groupVersion);
			TimeUnit.SECONDS.sleep(2);
			proviewClient.deleteGroup(groupId, groupVersion);			
		}

	}
    
    public int getMaxNumberOfRetries() {
        return this.maxNumberOfRetries;
    }	
	
	
	@Required
	public void setProviewClient(ProviewClient proviewClient) {
		this.proviewClient = proviewClient;
	}	

}
