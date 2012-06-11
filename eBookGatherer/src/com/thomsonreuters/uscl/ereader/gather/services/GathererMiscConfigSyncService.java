package com.thomsonreuters.uscl.ereader.gather.services;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;

import com.thomsonreuters.uscl.ereader.core.CoreConstants.NovusEnvironment;
import com.thomsonreuters.uscl.ereader.core.job.domain.MiscConfig;
import com.thomsonreuters.uscl.ereader.core.service.AbstractMiscConfigSyncService;
import com.thomsonreuters.uscl.ereader.core.service.MiscConfigSyncService;

/**
 * Gatherer specific assignment of dynamic configuration data.
 * This is currently the Novus Environment name Client|Prod that is set from the Misc Config section
 * of the Manager Administration pages.
 */
public class GathererMiscConfigSyncService extends AbstractMiscConfigSyncService implements MiscConfigSyncService {
	private static Logger log = Logger.getLogger(GathererMiscConfigSyncService.class);

	private NovusFactory novusFactory;
	
	public GathererMiscConfigSyncService() {
		super();
	}
	
	@Override
	public void syncSpecific(MiscConfig config) throws Exception {
		log.info(config);
		NovusEnvironment newNovusEnvironment = config.getNovusEnvironment();
		novusFactory.setNovusEnvironment(newNovusEnvironment);
	}
	
	@Required
	public void setNovusFactory(NovusFactory factory) {
		this.novusFactory = factory;
	}
}
