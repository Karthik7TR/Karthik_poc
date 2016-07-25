/*
 * Copyright 2016: Thomson Reuters Global Resources. All Rights Reserved.
 * Proprietary and Confidential information of TRGR. Disclosure, Use or
 * Reproduction without the written authorization of TRGR is prohibited
 */
package com.thomsonreuters.uscl.ereader.orchestrate.engine.service;

import java.util.Collection;

import javax.annotation.PostConstruct;

 import org.apache.log4j.LogManager; import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;

import com.thomsonreuters.uscl.ereader.core.job.domain.JobThrottleConfig;
import com.thomsonreuters.uscl.ereader.core.job.domain.MiscConfig;
import com.thomsonreuters.uscl.ereader.core.job.service.AppConfigService;
import com.thomsonreuters.uscl.ereader.core.outage.domain.PlannedOutage;
import com.thomsonreuters.uscl.ereader.core.outage.service.OutageProcessor;
import com.thomsonreuters.uscl.ereader.core.outage.service.OutageService;
import com.thomsonreuters.uscl.ereader.core.service.AppConfigLoader;
import com.thomsonreuters.uscl.ereader.core.service.JobThrottleConfigSyncService;
import com.thomsonreuters.uscl.ereader.core.service.MiscConfigSyncService;

/**
 * Perform initial load of dynamic application configurations.
 */
public class GeneratorAppConfigLoader implements AppConfigLoader  {
	private static Logger log = LogManager.getLogger(GeneratorAppConfigLoader.class);
	private AppConfigService appConfigService;
	private MiscConfigSyncService miscConfigSyncService;
	private JobThrottleConfigSyncService jobThrottleConfigSyncService;
	private OutageService outageService;
	private OutageProcessor outageProcessor;
	
	@PostConstruct
	@Override
	public void loadApplicationConfiguration() throws Exception {
		log.debug(">>>");
		try {
			MiscConfig miscConfig = appConfigService.loadMiscConfig();
			JobThrottleConfig jobThrottleConfig = appConfigService.loadJobThrottleConfig();
			miscConfigSyncService.sync(miscConfig);
			jobThrottleConfigSyncService.syncJobThrottleConfig(jobThrottleConfig);
			loadPlannedOutages();
		} catch (Exception e) {
			log.error("Error loading application configuration", e);
		}
	}
	
	/**
	 * Perform initial load of all the planned outages.
	 */
	public void loadPlannedOutages() {
		Collection<PlannedOutage> allOutages = outageService.getAllActiveAndScheduledPlannedOutages();
		for (PlannedOutage outage : allOutages) {
			outageProcessor.addPlannedOutageToContainer(outage);
		}
		log.debug(String.format("Loaded %d planned outage(s) from PLANNED_OUTAGE table", allOutages.size()));
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
	@Required
	public void setOutageService(OutageService service) {
		this.outageService = service;
	}
	@Required
	public void setOutageProcessor(OutageProcessor processor) {
		this.outageProcessor = processor;
	}
}
