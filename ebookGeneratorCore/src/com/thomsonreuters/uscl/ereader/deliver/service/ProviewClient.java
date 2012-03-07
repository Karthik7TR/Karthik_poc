/*
* Copyright 2011: Thomson Reuters Global Resources. All Rights Reserved.
* Proprietary and Confidential information of TRGR. Disclosure, Use or
* Reproduction without the written authorization of TRGR is prohibited
*/
package com.thomsonreuters.uscl.ereader.deliver.service;

import java.io.File;

import com.thomsonreuters.uscl.ereader.deliver.exception.ProviewException;

/**
 * Implementors of this interface are responsible for interacting 
 * with ProView and returning any relevant information (success, failure, response messages, etc) to the caller.
 * @author <a href="mailto:christopher.schwartz@thomsonreuters.com">Chris Schwartz</a> u0081674
 *
 */
public interface ProviewClient {
	
	public String publishTitle(final String fullyQualifiedTitleId, final String versionNumber, final File eBook) throws ProviewException;

	public String getAllPublishedTitles() throws ProviewException;
	
	public String getPublishingStatus(final String fullyQualifiedTitleId) throws ProviewException;
	
	public ProviewTitleInfo getLatestProviewTitleInfo(final String fullyQualifiedTitleId) throws ProviewException;
}
