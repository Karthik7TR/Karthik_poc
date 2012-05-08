/*
* Copyright 2012: Thomson Reuters Global Resources. All Rights Reserved.
* Proprietary and Confidential information of TRGR. Disclosure, Use or
* Reproduction without the written authorization of TRGR is prohibited
*/
package com.thomsonreuters.uscl.ereader.smoketest.service;

import java.util.List;

import com.thomsonreuters.uscl.ereader.smoketest.domain.SmokeTest;


/**
 * Service that returns Server statuses
 * 
 */
public interface SmokeTestService 
{
	public List<SmokeTest> getCIServerStatuses();
	
	public List<SmokeTest> getCIApplicationStatuses();
	
	public List<SmokeTest> getTestServerStatuses();
	
	public List<SmokeTest> getTestApplicationStatuses();
	
	public List<SmokeTest> getQAServerStatuses();
	
	public List<SmokeTest> getQAApplicationStatuses();
	
	public List<SmokeTest> getLowerEnvDatabaseServerStatuses();
	
	public List<SmokeTest> getProdServerStatuses();
	
	public List<SmokeTest> getProdApplicationStatuses();
	
	public List<SmokeTest> getProdDatabaseServerStatuses();

	public List<String> getRunningApplications();
	
	public SmokeTest getApplicationStatus(String appName, String url);
	
	public SmokeTest testConnection();
}
