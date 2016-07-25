/*
 * Copyright 2016: Thomson Reuters Global Resources. All Rights Reserved.
 * Proprietary and Confidential information of TRGR. Disclosure, Use or
 * Reproduction without the written authorization of TRGR is prohibited
 */
package com.thomsonreuters.uscl.ereader.mgr.web.service;

import javax.annotation.PostConstruct;

 import org.apache.log4j.LogManager; import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;

import com.thomsonreuters.uscl.ereader.core.job.domain.MiscConfig;
import com.thomsonreuters.uscl.ereader.core.job.service.AppConfigService;
import com.thomsonreuters.uscl.ereader.core.service.AppConfigLoader;
import com.thomsonreuters.uscl.ereader.core.service.MiscConfigSyncService;

/**
 * MANAGER: Perform the system startup boot load of the general application configuration.
 * Its properties are then subsequently modifiable via the Manager administration page.
 *
 */
public class ManagerAppConfigLoader implements AppConfigLoader {
	private static Logger log = LogManager.getLogger(ManagerAppConfigLoader.class);
	
	private AppConfigService appConfigService;
	private MiscConfigSyncService miscConfigSyncService;
	
	@PostConstruct
	@Override
	public void loadApplicationConfiguration() throws Exception {
		log.debug(">>>");
		try {
			MiscConfig miscConfig = appConfigService.loadMiscConfig();
			miscConfigSyncService.sync(miscConfig);
		} catch (Exception e) {
			log.error(e);
		}
	}
	
	@Required
	public void setAppConfigService(AppConfigService service) {
		this.appConfigService = service;
	}
	@Required
	public void setMiscConfigSyncService(
			MiscConfigSyncService service) {
		this.miscConfigSyncService = service;
	}
}
