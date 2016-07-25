/*
 * Copyright 2016: Thomson Reuters Global Resources. All Rights Reserved.
 * Proprietary and Confidential information of TRGR. Disclosure, Use or
 * Reproduction without the written authorization of TRGR is prohibited
 */
package com.thomsonreuters.uscl.ereader.gather.services;

import javax.annotation.PostConstruct;

import com.thomsonreuters.uscl.ereader.core.service.AppConfigLoader;

/**
 * Perform initial load of dynamic application configurations.
 */
public class GathererAppConfigLoader implements AppConfigLoader  {
	//private static Logger log = LogManager.getLogger(GathererAppConfigLoader.class);
	
	@PostConstruct
	public void loadApplicationConfiguration() throws Exception {
		//log.debug(">>>");
	}
}
