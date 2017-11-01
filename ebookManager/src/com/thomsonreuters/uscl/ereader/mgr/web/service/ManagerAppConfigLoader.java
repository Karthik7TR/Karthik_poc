package com.thomsonreuters.uscl.ereader.mgr.web.service;

import javax.annotation.PostConstruct;

import com.thomsonreuters.uscl.ereader.core.job.domain.MiscConfig;
import com.thomsonreuters.uscl.ereader.core.job.service.AppConfigService;
import com.thomsonreuters.uscl.ereader.core.service.AppConfigLoader;
import com.thomsonreuters.uscl.ereader.core.service.MiscConfigSyncService;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * MANAGER: Perform the system startup boot load of the general application configuration.
 * Its properties are then subsequently modifiable via the Manager administration page.
 *
 */
@Component("managerAppConfigLoader")
public class ManagerAppConfigLoader implements AppConfigLoader {
    private static Logger LOG = LogManager.getLogger(ManagerAppConfigLoader.class);

    private final AppConfigService appConfigService;
    private final MiscConfigSyncService miscConfigSyncService;

    @Autowired
    public ManagerAppConfigLoader(final AppConfigService appConfigService,
                                  final MiscConfigSyncService miscConfigSyncService) {
        this.appConfigService = appConfigService;
        this.miscConfigSyncService = miscConfigSyncService;
    }

    @PostConstruct
    @Override
    public void loadApplicationConfiguration() throws Exception {
        LOG.debug(">>>");
        try {
            final MiscConfig miscConfig = appConfigService.loadMiscConfig();
            miscConfigSyncService.sync(miscConfig);
        } catch (final Exception e) {
            LOG.error(e);
        }
    }
}
