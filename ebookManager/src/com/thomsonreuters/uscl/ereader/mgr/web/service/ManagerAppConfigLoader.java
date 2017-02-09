package com.thomsonreuters.uscl.ereader.mgr.web.service;

import javax.annotation.PostConstruct;

import com.thomsonreuters.uscl.ereader.core.job.domain.MiscConfig;
import com.thomsonreuters.uscl.ereader.core.job.service.AppConfigService;
import com.thomsonreuters.uscl.ereader.core.service.AppConfigLoader;
import com.thomsonreuters.uscl.ereader.core.service.MiscConfigSyncService;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;

/**
 * MANAGER: Perform the system startup boot load of the general application configuration.
 * Its properties are then subsequently modifiable via the Manager administration page.
 *
 */
public class ManagerAppConfigLoader implements AppConfigLoader
{
    private static Logger log = LogManager.getLogger(ManagerAppConfigLoader.class);

    private AppConfigService appConfigService;
    private MiscConfigSyncService miscConfigSyncService;

    @PostConstruct
    @Override
    public void loadApplicationConfiguration() throws Exception
    {
        log.debug(">>>");
        try
        {
            final MiscConfig miscConfig = appConfigService.loadMiscConfig();
            miscConfigSyncService.sync(miscConfig);
        }
        catch (final Exception e)
        {
            log.error(e);
        }
    }

    @Required
    public void setAppConfigService(final AppConfigService service)
    {
        appConfigService = service;
    }

    @Required
    public void setMiscConfigSyncService(final MiscConfigSyncService service)
    {
        miscConfigSyncService = service;
    }
}
