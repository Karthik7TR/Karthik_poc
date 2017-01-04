/*
 * Copyright 2016: Thomson Reuters Global Resources. All Rights Reserved.
 * Proprietary and Confidential information of TRGR. Disclosure, Use or
 * Reproduction without the written authorization of TRGR is prohibited
 */
package com.thomsonreuters.uscl.ereader.deliver.service;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import com.thomsonreuters.uscl.ereader.deliver.exception.ProviewException;

/**
 * Implementors of this interface are responsible for interacting with ProView
 * and returning any relevant information (success, failure, response messages,
 * etc) to the caller.
 * 
 * <a href="https://thehub.thomsonreuters.com/docs/DOC-63763">ProView Publishing REST API</a>
 * 
 * @author <a href="mailto:christopher.schwartz@thomsonreuters.com">Chris
 *         Schwartz</a> u0081674
 * 
 */
public interface ProviewClient {

	public void setProviewHostname(String hostname) throws UnknownHostException;

	public void setProviewHost(InetAddress host);

	/* proview group */
	public String getAllProviewGroups() throws ProviewException;

	public String getProviewGroupById(final String groupId) throws ProviewException;

	public String getProviewGroupInfo(final String groupId, final String groupVersion) throws ProviewException;

	public String createGroup(String groupId, String groupVersion, String requestBody) throws ProviewException, UnsupportedEncodingException;

	public String promoteGroup(final String groupId, final String groupVersion) throws ProviewException;

	public String removeGroup(final String groupId, final String groupVersion) throws ProviewException;

	public String deleteGroup(final String groupId, final String groupVersion) throws ProviewException;

	/* proview list */
	public String getAllPublishedTitles() throws ProviewException;

	public String getSinglePublishedTitle(String fullyQualifiedTitleId) throws ProviewException;

	public String getSingleTitleInfoByVersion(String fullyQualifiedTitleId, String version) throws ProviewException;

	public String publishTitle(final String fullyQualifiedTitleId, final String versionNumber, final File eBook) throws ProviewException;

	public String promoteTitle(final String fullyQualifiedTitleId, final String eBookVersionNumber) throws ProviewException;

	public String removeTitle(final String fullyQualifiedTitleId, final String eBookVersionNumber) throws ProviewException;

	public String deleteTitle(final String fullyQualifiedTitleId, final String eBookVersionNumber) throws ProviewException;

	// public String getStatusByVersion(String fullyQualifiedTitleId, String version) throws Exception;

	// public String getPublishingStatus(final String fullyQualifiedTitleId) throws ProviewException;
}
