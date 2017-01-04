/*
 * Copyright 2016: Thomson Reuters Global Resources. All Rights Reserved.
 * Proprietary and Confidential information of TRGR. Disclosure, Use or
 * Reproduction without the written authorization of TRGR is prohibited
 */
package com.thomsonreuters.uscl.ereader.deliver.service;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Map;

import com.thomsonreuters.uscl.ereader.deliver.exception.ProviewException;
import com.thomsonreuters.uscl.ereader.deliver.service.ProviewGroup.GroupDetails;

/**
 * Implementors of this interface are responsible for interacting with ProView and returning any relevant information (success,
 * failure, response messages, etc) to the caller.
 * 
 * @author <a href="mailto:zack.farrell@thomsonreuters.com">Zack Farrell</a> uc209819
 * 
 */
public interface ProviewHandler {

	/* ProView group */
	public Map<String, ProviewGroupContainer> getAllProviewGroupInfo() throws ProviewException;

	public ProviewGroupContainer getProviewGroupContainerById(final String groupId) throws ProviewException;
	
	public List<GroupDefinition> getGroupDefinitionsById(final String groupId) throws ProviewException;
	
	public GroupDefinition getGroupDefinitionByVersion(final String groupId, final long groupVersion) throws ProviewException;

	public List<ProviewGroup> getAllLatestProviewGroupInfo() throws ProviewException;

	public List<ProviewGroup> getAllLatestProviewGroupInfo(final Map<String, ProviewGroupContainer> groupMap) throws ProviewException;
	
	public String createGroup(final GroupDefinition groupDefinition) throws ProviewException, UnsupportedEncodingException;

	public String promoteGroup(final String groupId, final String groupVersion) throws ProviewException;

	public String removeGroup(final String groupId, final String groupVersion) throws ProviewException;

	public String deleteGroup(final String groupId, final String groupVersion) throws ProviewException;
	
	/* ProView Title */
	public Map<String, ProviewTitleContainer> getAllProviewTitleInfo() throws ProviewException;

	public ProviewTitleContainer getProviewTitleContainer(final String fullyQualifiedTitleId) throws ProviewException;

	public List<ProviewTitleInfo> getAllLatestProviewTitleInfo() throws ProviewException;

	public List<ProviewTitleInfo> getAllLatestProviewTitleInfo(final Map<String, ProviewTitleContainer> titleMap) throws ProviewException;

	public ProviewTitleInfo getLatestProviewTitleInfo(final String fullyQualifiedTitleId) throws ProviewException;
	
	// public ProviewTitleInfo getProviewTitleInfoByVersion(String fullyQualifiedTitleId, String version) throws ProviewException;

	public List<GroupDetails> getSingleTitleGroupDetails(final String fullyQualifiedTitleId) throws ProviewException;
	
	public boolean isTitleInProview(final String fullyQualifiedTitleId) throws ProviewException;

	public boolean hasTitleIdBeenPublished(final String fullyQualifiedTitleId) throws ProviewException;
	
	public String publishTitle(final String fullyQualifiedTitleId, final String versionNumber, final File eBook) throws ProviewException;

	public String promoteTitle(final String fullyQualifiedTitleId, final String eBookVersionNumber) throws ProviewException;

	public String removeTitle(final String fullyQualifiedTitleId, final String eBookVersionNumber) throws ProviewException;

	public boolean deleteTitle(final String fullyQualifiedTitleId, final String eBookVersionNumber) throws ProviewException;

}
