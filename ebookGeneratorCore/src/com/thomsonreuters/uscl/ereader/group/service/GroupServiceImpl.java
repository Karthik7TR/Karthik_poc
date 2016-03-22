package com.thomsonreuters.uscl.ereader.group.service;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.thomsonreuters.uscl.ereader.GroupDefinition;
import com.thomsonreuters.uscl.ereader.GroupDefinition.SubGroupInfo;
import com.thomsonreuters.uscl.ereader.core.book.domain.BookDefinition;
import com.thomsonreuters.uscl.ereader.deliver.exception.ProviewException;
import com.thomsonreuters.uscl.ereader.deliver.exception.ProviewRuntimeException;
import com.thomsonreuters.uscl.ereader.deliver.service.ProviewClient;
import com.thomsonreuters.uscl.ereader.util.XMLXpathEvaluator;

public class GroupServiceImpl implements GroupService {
	
	private static final Logger LOG = Logger.getLogger(GroupServiceImpl.class);
	private ProviewClient proviewClient;
	// retry parameters
    private int baseRetryInterval = 30000; // in ms    
	private int maxNumberOfRetries = 3;
    // used to compute a multiplier for successive retries
    private int retryIntervalMultiplierBase = 5;
    // hard limit on the computed interval
    private int maxRetryIntervalLimit = 900 * 1000; // 15 minutes
    public static final String VERSION_NUMBER_PREFIX = "v";

	
	
	public Long getLastGroupVerionFromProviewResponse(String response) throws Exception {
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
				throw new ProviewException("Cannot have a subgroup with no subgroups for previous versions");
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
	
	
	public GroupDefinition getGroupDefinitionForSplitBooks(String groupInfoXML, String majorVersion,
			String newGroupName, String newSubGroupHeading, String fullyQualifiedTitleId, List<String> newSubGroupTitleList) throws Exception{
		GroupDefinition groupDefinition =  new GroupDefinition();
		boolean createGroup = false;
		boolean versionChange = false;

		XMLXpathEvaluator extractor = new XMLXpathEvaluator(groupInfoXML);
		String groupName = extractor.evaluate("group/name");

		if (!groupName.equalsIgnoreCase(newGroupName)) {
			createGroup = true;
		}

		// HEAD TITLE
		Node node = extractor.evaluateNode("group/headtitle");
		String headTitle = node.getTextContent();
		

		if (!StringUtils.substringAfterLast(headTitle, "/").equalsIgnoreCase(majorVersion)) {			
		   //Change in major version so group needs to be created			
			versionChange = true;
			createGroup = true;
		}
		
		NodeList subGroups = extractor.evaluateNodeList("group/members/subgroup");

		List<SubGroupInfo> subGroupInfoList = new ArrayList<SubGroupInfo>();

		if (subGroups != null && subGroups.getLength() > 0) {

			for (int i = 0; i < subGroups.getLength(); i++) {
				SubGroupInfo subGroupInfo = new SubGroupInfo();
				Node subGroup = subGroups.item(i);
				
				NamedNodeMap attributesMap = subGroup.getAttributes();
				if (attributesMap != null && attributesMap.getLength() > 0) {
					for (int j = 0; j < attributesMap.getLength(); j++) {
						Node attributeNode = attributesMap.item(j);
						String attributeName = attributeNode.getNodeName();
						if (attributeName.equalsIgnoreCase("heading")) {
							String oldSubgroupHeading = attributeNode.getTextContent();
							if(i==0){
								
								//If version changed but no subgroup name change then this should be an error.
								//We have a validation at generation step to change subgroup name with major versions
								if(versionChange )
								{
										if(oldSubgroupHeading.equalsIgnoreCase(newSubGroupHeading)){
											throw new ProviewException("Subgroupname should be changed for every major version");
										}
										else{
											createGroup = true;
											//Add new subgroup
											subGroupInfo = getSubGroupInfo(majorVersion, newSubGroupHeading, newSubGroupTitleList);											
											subGroupInfoList.add(subGroupInfo);
											//Add old subgroup
											subGroupInfo = getSubgroupInfo(oldSubgroupHeading,subGroup);
											subGroupInfoList.add(subGroupInfo);
										}
								}
								//No change in version but change in subGroupname (it could be a typo and user fixed it)
								//Then change the subgroup heading								
								else if(!versionChange && !oldSubgroupHeading.equalsIgnoreCase(newSubGroupHeading)){
									createGroup = true;
									//Add the details with new split books
									subGroupInfo = getSubGroupInfo(majorVersion, newSubGroupHeading, newSubGroupTitleList);
									subGroupInfoList.add(subGroupInfo);
								}	
								//No change in version. Version could be minor and split size may vary
								else if(!versionChange && oldSubgroupHeading.equalsIgnoreCase(newSubGroupHeading)){
									SubGroupInfo subgroupInfo = getSubgroupInfo(oldSubgroupHeading,subGroup);
									if(subgroupInfo.getTitles().size() != newSubGroupTitleList.size()){
										createGroup = true;
										//Add details with new splits
										//Todo: Add subgroups in sorted order
										subGroupInfo = getSubGroupInfo(majorVersion, newSubGroupHeading, newSubGroupTitleList);
										subGroupInfoList.add(subGroupInfo);
									}
									else{
										//Add old subgroup
										subGroupInfo = getSubgroupInfo(oldSubgroupHeading,subGroup);
										subGroupInfoList.add(subGroupInfo);
									}
								}
								
							}
							else{
								//Add all the previous subgroups
								subGroupInfo = getSubgroupInfo(oldSubgroupHeading,subGroup);
								subGroupInfoList.add(subGroupInfo);
							}
						}
					}
				}
				
			}
		}
		else{
			throw new ProviewException("No subgroups found from previous versions.Create a group with subgroup");
		}
		
		if (createGroup){
			groupDefinition.setName(newGroupName);
			groupDefinition.setHeadTitle(fullyQualifiedTitleId+"/"+majorVersion);			
			groupDefinition.setSubGroupInfoList(subGroupInfoList);
			return groupDefinition;
		}
		
		LOG.debug("New version of group is not needed for title "+fullyQualifiedTitleId);
		return null;
	}
	
	
	public GroupDefinition getGroupDefinitionForSingleBooks(String groupInfoXML, String majorVersion, String newGroupName,
			String newSubGroupHeading, String fullyQualifiedTitleId) throws Exception {

		boolean createGroup = false;
		GroupDefinition groupDefinition =  new GroupDefinition();
		groupDefinition.setType("standard");
		boolean versionChange = false;

		XMLXpathEvaluator extractor = new XMLXpathEvaluator(groupInfoXML);

		String groupName = extractor.evaluate("group/name");

		if (!groupName.equalsIgnoreCase(newGroupName)) {
			createGroup = true;
		}

		// HEAD TITLE
		Node node = extractor.evaluateNode("group/headtitle");		
		String groupHeadTitle = node.getTextContent();
		String versionOnHeadTitle = StringUtils.substringAfterLast(groupHeadTitle, "/v");
		
		// The headtitle will not have a version at the end, if all the versions
		// of the title are grouped together
		if (StringUtils.isEmpty(versionOnHeadTitle) ){
			if (createGroup) {				
				// A new group will be created if there is change in group name
				// and no subgroups
				groupDefinition = buildGroupDefinition(newGroupName,newSubGroupHeading,fullyQualifiedTitleId,majorVersion,null);
				return groupDefinition;
			}
			return null;
		}

		if (!StringUtils.isEmpty(versionOnHeadTitle)
				&& !versionOnHeadTitle.equalsIgnoreCase(StringUtils.substringAfterLast(majorVersion, "v"))) {
			versionChange = true;
		}

		
		NodeList subGroups = extractor.evaluateNodeList("group/members/subgroup") ;
				
		List<SubGroupInfo> subGroupInfoList = new ArrayList<SubGroupInfo>();

		// If it gets here then there must be a subgroup. Get the first
		// subgroup
		// String oldSubHeading = null;
		if (subGroups != null && subGroups.getLength() > 0) {
			for (int i = 0; i < subGroups.getLength(); i++) {

				Node subGroup = subGroups.item(i);
				NamedNodeMap attributesMap = subGroup.getAttributes();
				if (attributesMap != null && attributesMap.getLength() > 0) {
					for (int j = 0; j < attributesMap.getLength(); j++) {
						Node attributeNode = attributesMap.item(j);
						String attributeName = attributeNode.getNodeName();
						if (attributeName.equalsIgnoreCase("heading")) {
							SubGroupInfo subGroupInfo = new SubGroupInfo();
							String oldSubHeading = attributeNode.getTextContent();
							if (i == 0) {
								if(StringUtils.isEmpty(newSubGroupHeading)){
									groupDefinition = buildGroupDefinition(newGroupName,newSubGroupHeading,fullyQualifiedTitleId,majorVersion, null);
									return groupDefinition;
								}
								else if (versionChange && !oldSubHeading.equalsIgnoreCase(newSubGroupHeading)) {
									createGroup = true;
									//HeadTitle will change for version change and a new subgroup will be created
									groupDefinition.setHeadTitle(fullyQualifiedTitleId + "/" + majorVersion);
									subGroupInfo.setHeading(newSubGroupHeading);
									List<String> titleList = new ArrayList<String>();
									titleList.add(fullyQualifiedTitleId + "/" + majorVersion);
									subGroupInfo.setTitles(titleList);
									subGroupInfoList.add(subGroupInfo);
									
									//Add the existing too
									subGroupInfoList.add(getSubgroupInfo(oldSubHeading,subGroup));
									
								} else if (!versionChange && !oldSubHeading.equalsIgnoreCase(newSubGroupHeading)) {	
									createGroup = true;
									//Add existing subgroup with new subgroup heading
									subGroupInfoList.add(getSubgroupInfo(newSubGroupHeading,subGroup));

								} else if (versionChange && oldSubHeading.equalsIgnoreCase(newSubGroupHeading)) {
									createGroup = true;
									//Add the new version on top for the existing subgroup
									groupDefinition.setHeadTitle(fullyQualifiedTitleId + "/" + majorVersion);
									subGroupInfo.setHeading(oldSubHeading);
									List<String> titleList = new ArrayList<String>();
									titleList.add(fullyQualifiedTitleId + "/" + majorVersion);

									NodeList titles = subGroup.getChildNodes();
									if (titles.getLength() > 0) {
										for (int count = 0; count < titles.getLength(); count++) {
											String title = titles.item(count).getTextContent();
											titleList.add(title);
										}
									}
									subGroupInfo.setTitles(titleList);
									subGroupInfoList.add(subGroupInfo);
								}
								else{									
									subGroupInfoList.add(getSubgroupInfo(oldSubHeading,subGroup));
								}
							} else {
								subGroupInfoList.add(getSubgroupInfo(oldSubHeading,subGroup));
							}
						}
					}
				}
			}
		}

		if (createGroup) {			
				groupDefinition.setName(newGroupName);
			if(groupDefinition.getHeadTitle() == null){
				groupDefinition.setHeadTitle(groupHeadTitle);
			}
			groupDefinition.setSubGroupInfoList(subGroupInfoList);
			return groupDefinition;
		}
		LOG.debug("New version of group is not needed for title "+fullyQualifiedTitleId);
		return null;
	}
	
	protected SubGroupInfo getSubgroupInfo(String subGroupHeading, Node subGroup){
		SubGroupInfo subGroupInfo = new SubGroupInfo();
		if(subGroupHeading != null){
			subGroupInfo.setHeading(subGroupHeading);
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
		return subGroupInfo;
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
					int computedRetryInterval = computeRetryInterval(retryCount);

					LOG.warn("Retriable status received: waiting " + computedRetryInterval + "ms (retryCount: "
							+ retryCount +")");

					retryRequest = true;
					retryCount++;

					try {
						Thread.sleep(computedRetryInterval);
					} catch (InterruptedException e) {
						LOG.error("InterruptedException during HTTP retry", e);
					};
				}else {
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
	
	
	/**
     * Compute an interval that grows somewhat randomly with each retry attempt.
     * 
     * @param retryCount
     * @return
     */
    protected int computeRetryInterval(int retryCount) {
        int randomnessMultiplier = (int) Math.pow(retryIntervalMultiplierBase, Math.max(0, retryCount - 1));
        int randomnessInterval = (int) ((Math.random() - 0.5) * 2 * getBaseRetryInterval() * randomnessMultiplier);
        int multiplier = (int) Math.pow(retryIntervalMultiplierBase, retryCount);
        int interval = Math.max(getBaseRetryInterval(), (getBaseRetryInterval() * multiplier) + randomnessInterval);
        return Math.min(interval, maxRetryIntervalLimit);
    }
    
    public String getGroupInfoByVersion(String groupId, Long groupVersion) throws ProviewException {
		String response = null;
		do {
			try {
				response = proviewClient.getProviewGroupInfo(groupId, VERSION_NUMBER_PREFIX
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
			return getLastGroupVerionFromProviewResponse(response);
		} catch (ProviewRuntimeException ex) {
			String errorMsg = ex.getMessage();
			LOG.debug(errorMsg);
			if (errorMsg.startsWith("404") && errorMsg.contains("No such groups exist")){
				return groupVersion;
			}
		}
		return groupVersion;
	}
    
     
    /**
     * Group will be created based on user input. splitTitles will be null if book is not a splitbook
     */
	public Long generateGroupForEbook(BookDefinition bookDefinition, String bookVersion, List<String> splitTitles)
			throws Exception {
		Long groupVersion = new Long(0);

		String groupName = bookDefinition.getGroupName();

		if (StringUtils.isEmpty(groupName)) {
			throw new ProviewException("Group Name cannot be empty");
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

			if (bookDefinition.isSplitBook()) {
				groupDefinition = getGroupDefinitionForSplitBooks(proviewResponse, majorVersion, groupName,
						subGroupHeading, fullyQualifiedTitleId, splitTitles);
			} else {
				groupDefinition = getGroupDefinitionForSingleBooks(proviewResponse, majorVersion, groupName,
						subGroupHeading, fullyQualifiedTitleId);
			}
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
			groupDefinition.setGroupVersion(VERSION_NUMBER_PREFIX + String.valueOf(groupVersion));
			createGroup(groupDefinition);
		}
		return groupVersion;
	}
    
    public int getMaxNumberOfRetries() {
        return this.maxNumberOfRetries;
    }	
	
	public int getBaseRetryInterval() {
		return baseRetryInterval;
	}

	public void setBaseRetryInterval(int baseRetryInterval) {
		this.baseRetryInterval = baseRetryInterval;
	}
	
	@Required
	public void setProviewClient(ProviewClient proviewClient) {
		this.proviewClient = proviewClient;
	}	

}
