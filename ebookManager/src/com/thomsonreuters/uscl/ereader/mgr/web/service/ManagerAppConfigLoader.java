package com.thomsonreuters.uscl.ereader.mgr.web.service;

import javax.annotation.PostConstruct;

import org.apache.log4j.Logger;

import com.thomsonreuters.uscl.ereader.core.job.domain.AppConfig;
import com.thomsonreuters.uscl.ereader.core.job.domain.LoggingConfig;
import com.thomsonreuters.uscl.ereader.core.job.service.AppConfigService;

/**
 * MANAGER: Perform the system startup boot load of the general application configuration.
 * Its properties are then subsequently modifiable via the Manager administration page.
 *
 */
public class ManagerAppConfigLoader {
	private static Logger log = Logger.getLogger(ManagerAppConfigLoader.class);
	/** The configuration store in-memory in the Spring app context.  Should mirror the table configuration. */
	private AppConfig appConfig;
	private AppConfigService appConfigService;
	
	public ManagerAppConfigLoader(AppConfig appConfig, AppConfigService service) {
		this.appConfig = appConfig;
		this.appConfigService = service;
	}
	
	@PostConstruct
	public void loadApplicationConfiguration() throws Exception {
		AppConfig configurationReadFromDatabase = appConfigService.getAppConfig();
		log.info("Successfully loaded: " + configurationReadFromDatabase);
		
		this.appConfig.copy(configurationReadFromDatabase);
		LoggingConfig loggingConfig = (LoggingConfig) appConfig;
		
		// Set the configured logging levels
		appConfigService.setLogLevel(loggingConfig);
	}
}
