package com.thomsonreuters.uscl.ereader.orchestrate.engine.service;

import java.util.Collection;

import javax.annotation.PostConstruct;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;

import com.thomsonreuters.uscl.ereader.core.job.domain.JobThrottleConfig;
import com.thomsonreuters.uscl.ereader.core.job.domain.MiscConfig;
import com.thomsonreuters.uscl.ereader.core.job.service.AppConfigService;
import com.thomsonreuters.uscl.ereader.core.outage.domain.PlannedOutage;
import com.thomsonreuters.uscl.ereader.core.outage.service.OutageService;
import com.thomsonreuters.uscl.ereader.core.service.AppConfigLoader;
import com.thomsonreuters.uscl.ereader.core.service.JobThrottleConfigSyncService;
import com.thomsonreuters.uscl.ereader.core.service.MiscConfigSyncService;
import com.thomsonreuters.uscl.ereader.orchestrate.engine.domain.PlannedOutageContainer;

/**
 * Perform initial load of dynamic application configurations.
 */
public class GeneratorAppConfigLoader implements AppConfigLoader  {
	private static Logger log = Logger.getLogger(GeneratorAppConfigLoader.class);
	private AppConfigService appConfigService;
	private MiscConfigSyncService miscConfigSyncService;
	private JobThrottleConfigSyncService jobThrottleConfigSyncService;
	private OutageService outageService;
	private PlannedOutageContainer plannedOutages;
	
	@PostConstruct
	@Override
	public void loadApplicationConfiguration() throws Exception {
		log.debug(">>>");
		try {
			MiscConfig miscConfig = appConfigService.loadMiscConfig();
			JobThrottleConfig jobThrottleConfig = appConfigService.loadJobThrottleConfig();
			miscConfigSyncService.syncMiscConfig(miscConfig);
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
		log.debug(String.format("Loaded %d planned outage(s) from PLANNED_OUTAGE table", allOutages.size()));		
		this.plannedOutages.saveAll(allOutages);
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
	public void setPlannedOutages(PlannedOutageContainer container) {
		plannedOutages = container;
	}
}
