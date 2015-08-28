/*
 * Copyright 2011: Thomson Reuters Global Resources. All Rights Reserved.
 * Proprietary and Confidential information of TRGR. Disclosure, Use or
 * Reproduction without the written authorization of TRGR is prohibited
 */
package com.thomsonreuters.uscl.ereader.deliver.service;

import java.io.File;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Map;

import com.thomsonreuters.uscl.ereader.GroupDefinition;
import com.thomsonreuters.uscl.ereader.deliver.exception.ProviewException;

/**
 * Implementors of this interface are responsible for interacting with ProView
 * and returning any relevant information (success, failure, response messages,
 * etc) to the caller.
 * 
 * @author <a href="mailto:christopher.schwartz@thomsonreuters.com">Chris
 *         Schwartz</a> u0081674
 * 
 */
public interface ProviewClient {

	public void setProviewHostname(String hostname) throws UnknownHostException;
	public void setProviewHost(InetAddress host);
	
	public String publishTitle(final String fullyQualifiedTitleId,
			final String versionNumber, final File eBook)
			throws ProviewException;
	
	public String createGroup(final GroupDefinition groupDefinition)
			throws ProviewException;
	
	public String updateGroupStatus(final GroupDefinition groupDefinition)
			throws ProviewException;
	
	public String deleteGroup(final GroupDefinition groupDefinition)
			throws ProviewException;
	
	public String getProviewGroupInfo(final String groupId, final String groupVersion)
			throws ProviewException;

	public String getAllPublishedTitles() throws ProviewException;

//	public String getPublishingStatus(final String fullyQualifiedTitleId)
//			throws ProviewException;

	public ProviewTitleInfo getLatestProviewTitleInfo(
			final String fullyQualifiedTitleId) throws ProviewException;

	public Map<String, ProviewTitleContainer> getAllProviewTitleInfo()
			throws ProviewException;

	public boolean hasTitleIdBeenPublished(final String fullyQualifiedTitleId)
			throws ProviewException;

	public ArrayList<ProviewTitleInfo> getAllLatestProviewTitleInfo()
			throws ProviewException;

	public String removeTitle(final String fullyQualifiedTitleId,
			final String eBookVersionNumber) throws ProviewException;

	public String deleteTitle(final String fullyQualifiedTitleId,
			final String eBookVersionNumber) throws ProviewException;

	public String promoteTitle(final String fullyQualifiedTitleId,
			final String eBookVersionNumber) throws ProviewException;

	public ProviewTitleContainer getProviewTitleContainer(
			final String fullyQualifiedTitleId) throws ProviewException;

	public ArrayList<ProviewTitleInfo> getAllLatestProviewTitleInfo(
			Map<String, ProviewTitleContainer> titleMap)
			throws ProviewException;
}
