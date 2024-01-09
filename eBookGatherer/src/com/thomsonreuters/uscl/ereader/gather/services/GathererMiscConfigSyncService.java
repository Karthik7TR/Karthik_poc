package com.thomsonreuters.uscl.ereader.gather.services;

import com.thomsonreuters.uscl.ereader.core.CoreConstants.NovusEnvironment;
import com.thomsonreuters.uscl.ereader.core.job.domain.MiscConfig;
import com.thomsonreuters.uscl.ereader.core.service.AbstractMiscConfigSyncService;
import com.thomsonreuters.uscl.ereader.core.service.MiscConfigSyncService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Required;

/**
 * Gatherer specific assignment of dynamic configuration data.
 * This is currently the Novus Environment name Client|Prod that is set from the Misc Config section
 * of the Manager Administration pages.
 */
@Slf4j
public class GathererMiscConfigSyncService extends AbstractMiscConfigSyncService implements MiscConfigSyncService {

    private NovusFactory novusFactory;

    public GathererMiscConfigSyncService() {
        super();
    }

    @Override
    public void syncSpecific(final MiscConfig config) throws Exception {
        log.info(config.toString());
        final NovusEnvironment newNovusEnvironment = config.getNovusEnvironment();
        novusFactory.setNovusEnvironment(newNovusEnvironment);
    }

    @Required
    public void setNovusFactory(final NovusFactory factory) {
        novusFactory = factory;
    }
}
