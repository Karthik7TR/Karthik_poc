package com.thomsonreuters.uscl.ereader.core.service;

import com.thomsonreuters.uscl.ereader.core.job.domain.JobThrottleConfig;

public interface JobThrottleConfigSyncService
{
    /**
     * Update the in-memory state of this applications job throttling configuration with that in the database.
     * @throws Exception on any error
     */
    void syncJobThrottleConfig(JobThrottleConfig config) throws Exception;
}
