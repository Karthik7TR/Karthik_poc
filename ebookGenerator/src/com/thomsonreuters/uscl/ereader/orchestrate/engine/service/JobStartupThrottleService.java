package com.thomsonreuters.uscl.ereader.orchestrate.engine.service;

import com.thomsonreuters.uscl.ereader.core.job.domain.JobThrottleConfig;

/**
 * @author Mahendra Survase (u0105927)
 *
 */
public interface JobStartupThrottleService {
    boolean checkIfnewJobCanbeLaunched();

    /**
     * Allows for a new hot configuration to be assigned.
     */
    void setJobThrottleConfig(JobThrottleConfig config);
}
