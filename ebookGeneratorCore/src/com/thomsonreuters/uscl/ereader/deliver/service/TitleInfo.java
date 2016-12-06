/*
 * Copyright 2016: Thomson Reuters Global Resources. All Rights Reserved.
 * Proprietary and Confidential information of TRGR. Disclosure, Use or
 * Reproduction without the written authorization of TRGR is prohibited
 */
package com.thomsonreuters.uscl.ereader.deliver.service;

public interface TitleInfo {

	public String getTitle();

	public String getTitleId();

	public Integer getTotalNumberOfVersions();

	public String getVersion();

	public Integer getMajorVersion();

	public Integer getMinorVersion();

	public String getPublisher();

	public String getLastupdate();

	public String getStatus();

}
