package com.thomsonreuters.uscl.ereader.orchestrate.engine.service;

import java.util.Collection;

import javax.annotation.PostConstruct;

import com.thomsonreuters.uscl.ereader.core.job.domain.JobThrottleConfig;
import com.thomsonreuters.uscl.ereader.core.job.domain.MiscConfig;
import com.thomsonreuters.uscl.ereader.core.job.service.AppConfigService;
import com.thomsonreuters.uscl.ereader.core.outage.domain.PlannedOutage;
import com.thomsonreuters.uscl.ereader.core.outage.service.OutageProcessor;
import com.thomsonreuters.uscl.ereader.core.outage.service.OutageService;
import com.thomsonreuters.uscl.ereader.core.service.AppConfigLoader;
import com.thomsonreuters.uscl.ereader.core.service.JobThrottleConfigSyncService;
import com.thomsonreuters.uscl.ereader.core.service.MiscConfigSyncService;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;

/**
 * Perform initial load of dynamic application configurations.
 */
public class GeneratorAppConfigLoader implements AppConfigLoader
{
    private static Logger log = LogManager.getLogger(GeneratorAppConfigLoader.class);
    private AppConfigService appConfigService;
    private MiscConfigSyncService miscConfigSyncService;
    private JobThrottleConfigSyncService jobThrottleConfigSyncService;
    private OutageService outageService;
    private OutageProcessor outageProcessor;

    @PostConstruct
    @Override
    public void loadApplicationConfiguration() throws Exception
    {
        log.debug(">>>");
        try
        {
            final MiscConfig miscConfig = appConfigService.loadMiscConfig();
            final JobThrottleConfig jobThrottleConfig = appConfigService.loadJobThrottleConfig();
            miscConfigSyncService.sync(miscConfig);
            jobThrottleConfigSyncService.syncJobThrottleConfig(jobThrottleConfig);
            loadPlannedOutages();
        }
        catch (final Exception e)
        {
            log.error("Error loading application configuration", e);
        }
    }

    /**
     * Perform initial load of all the planned outages.
     */
    public void loadPlannedOutages()
    {
        final Collection<PlannedOutage> allOutages = outageService.getAllActiveAndScheduledPlannedOutages();
        for (final PlannedOutage outage : allOutages)
        {
            outageProcessor.addPlannedOutageToContainer(outage);
        }
        log.debug(String.format("Loaded %d planned outage(s) from PLANNED_OUTAGE table", allOutages.size()));
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

    @Required
    public void setJobThrottleConfigSyncService(final JobThrottleConfigSyncService service)
    {
        jobThrottleConfigSyncService = service;
    }

    @Required
    public void setOutageService(final OutageService service)
    {
        outageService = service;
    }

    @Required
    public void setOutageProcessor(final OutageProcessor processor)
    {
        outageProcessor = processor;
    }
}
