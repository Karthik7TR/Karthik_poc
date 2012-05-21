package com.thomsonreuters.uscl.ereader.gather.services;

import javax.annotation.PostConstruct;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;

import com.thomsonreuters.uscl.ereader.core.job.domain.MiscConfig;
import com.thomsonreuters.uscl.ereader.core.service.AppConfigLoader;
import com.thomsonreuters.uscl.ereader.core.service.GeneratorRestClient;
import com.thomsonreuters.uscl.ereader.core.service.MiscConfigSyncService;

/**
 * Perform initial load of dynamic application configurations.
 */
public class GathererAppConfigLoader implements AppConfigLoader  {
	private static Logger log = Logger.getLogger(GathererAppConfigLoader.class);
	private GeneratorRestClient generatorRestClient;
	private MiscConfigSyncService miscConfigSyncService;
	
	public GathererAppConfigLoader() {
		log.debug(">>>");
	}
	
	@PostConstruct
	@Override
	public void loadApplicationConfiguration() throws Exception {
		log.debug(">>>");
		try {
// NOTE: This will cause a Tomcat boot hang if the generator and gatherer are running on the same Tomcat instance!!!
			MiscConfig config = generatorRestClient.getMiscConfig();
			miscConfigSyncService.syncMiscConfig(config);
		} catch (Exception e) {
			log.error(String.format("Unable to fetch/sync the MiscConfig from %s - %s",
					  generatorRestClient.getGeneratorContextUrl().toString(), e.getMessage()));
		}
	}

	@Required
	public void setMiscConfigSyncService(MiscConfigSyncService service) {
		this.miscConfigSyncService = service;
	}
	@Required
	public void setGeneratorRestClient(GeneratorRestClient client) {
		this.generatorRestClient = client;
	}
}
