package com.thomsonreuters.uscl.ereader.core.job.service;

import com.thomsonreuters.uscl.ereader.core.job.domain.JobThrottleConfig;
import com.thomsonreuters.uscl.ereader.core.job.domain.MiscConfig;

public interface AppConfigService
{
    /**
     * Read throttle configuration from the database.
     */
    JobThrottleConfig loadJobThrottleConfig();

    /**
     * Read miscellaneous configuration from the database.
     */
    MiscConfig loadMiscConfig();

    /**
     * Return the value for the specified key from the APP_PARAMETER table.
     * @param key the lookup primary key
     * @return the corresponding value
     */
    String getConfigValue(String key);

    void saveJobThrottleConfig(JobThrottleConfig config);

    void saveMiscConfig(MiscConfig config);
}
