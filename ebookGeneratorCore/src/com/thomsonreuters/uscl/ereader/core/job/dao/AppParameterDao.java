/*
 * Copyright 2012: Thomson Reuters Global Resources. All Rights Reserved.
 * Proprietary and Confidential information of TRGR. Disclosure, Use or
 * Reproduction without the written authorization of TRGR is prohibited
 */

package com.thomsonreuters.uscl.ereader.core.job.dao;

import com.thomsonreuters.uscl.ereader.core.job.domain.AppParameter;


public interface AppParameterDao {

	public AppParameter findByPrimaryKey(String key);
	
	public void save(AppParameter param);
	
	public void delete(AppParameter param);
}
