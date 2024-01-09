package com.thomsonreuters.uscl.ereader.core.service;

import java.net.URL;
import java.util.Collection;
import java.util.Map;

import com.thomsonreuters.uscl.ereader.core.job.domain.JobThrottleConfig;
import com.thomsonreuters.uscl.ereader.core.job.domain.MiscConfig;
import com.thomsonreuters.uscl.ereader.core.job.domain.SimpleRestServiceResponse;

/**
 * REST service operations offered by the generator web application.
 *
 */
public interface GeneratorRestClient {
    SimpleRestServiceResponse restartJob(long jobExecutionId);

    SimpleRestServiceResponse stopJob(long jobExecutionId);

    Map<String, Collection<String>> getStepNames();

    JobThrottleConfig getJobThrottleConfig();

    MiscConfig getMiscConfig();

    URL getGeneratorContextUrl();
}
