package com.thomsonreuters.uscl.ereader.gather.services;

import javax.annotation.PostConstruct;

import com.thomsonreuters.uscl.ereader.core.service.AppConfigLoader;

/**
 * Perform initial load of dynamic application configurations.
 */
public class GathererAppConfigLoader implements AppConfigLoader
{
    //private static Logger log = LogManager.getLogger(GathererAppConfigLoader.class);

    @Override
    @PostConstruct
    public void loadApplicationConfiguration() throws Exception
    {
        //log.debug(">>>");
    }
}
