package com.thomsonreuters.uscl.ereader.orchestrate.engine.service;

import javax.annotation.PostConstruct;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;

import com.thomsonreuters.uscl.ereader.core.job.domain.JobThrottleConfig;
import com.thomsonreuters.uscl.ereader.core.job.domain.MiscConfig;
import com.thomsonreuters.uscl.ereader.core.job.service.AppConfigService;
import com.thomsonreuters.uscl.ereader.core.service.AppConfigLoader;
import com.thomsonreuters.uscl.ereader.core.service.JobThrottleConfigSyncService;
import com.thomsonreuters.uscl.ereader.core.service.MiscConfigSyncService;

/**
 * Perform initial load of dynamic application configurations.
 */
public class GeneratorAppConfigLoader implements AppConfigLoader  {
	private static Logger log = Logger.getLogger(GeneratorAppConfigLoader.class);
	private AppConfigService appConfigService;
	private MiscConfigSyncService miscConfigSyncService;
	private JobThrottleConfigSyncService jobThrottleConfigSyncService;
	
	@PostConstruct
	@Override
	public void loadApplicationConfiguration() throws Exception {
		log.debug(">>>");
		try {
			MiscConfig miscConfig = appConfigService.loadMiscConfig();
			JobThrottleConfig jobThrottleConfig = appConfigService.loadJobThrottleConfig();
			miscConfigSyncService.syncMiscConfig(miscConfig);
			jobThrottleConfigSyncService.syncJobThrottleConfig(jobThrottleConfig);
		} catch (Exception e) {
			log.error(e);
		}
	}
	@Required
	public void setAppConfigService(AppConfigService service) {
		this.appConfigService = service;
	}
	@Required
	public void setMiscConfigSyncService(MiscConfigSyncService service) {
		this.miscConfigSyncService = service;
	}
	@Required
	public void setJobThrottleConfigSyncService(JobThrottleConfigSyncService service) {
		this.jobThrottleConfigSyncService = service;
	}
}
