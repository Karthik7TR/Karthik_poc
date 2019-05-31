package com.thomsonreuters.uscl.ereader.mgr.rest;

import java.net.URL;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.thomsonreuters.uscl.ereader.core.CoreConstants;
import com.thomsonreuters.uscl.ereader.core.job.AvailableJobs;
import com.thomsonreuters.uscl.ereader.core.job.domain.JobThrottleConfig;
import com.thomsonreuters.uscl.ereader.core.job.domain.MiscConfig;
import com.thomsonreuters.uscl.ereader.core.job.domain.SimpleRestServiceResponse;
import com.thomsonreuters.uscl.ereader.core.service.GeneratorRestClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service("generatorRestClient")
public class GeneratorRestClientImpl implements GeneratorRestClient {
    private static final String GENERATOR_REST_STOP_JOB_URL_PATTERN = "{context}/service/stop/job/{jobExecutionId}";
    private static final String GENERATOR_REST_RESTART_JOB_URL_PATTERN =
        "{context}/service/restart/job/{jobExecutionId}";
    private static final String GENERATOR_REST_GET_STEP_NAMES_PATTERN = "{context}/service/get/step/names/{jobName}";

    private static final String GENERATOR_GET_JOB_THROTTLE_CONFIG =
        "{context}/" + CoreConstants.URI_GET_JOB_THROTTLE_CONFIG;
    private static final String GENERATOR_GET_MISC_CONFIG = "{context}/" + CoreConstants.URI_GET_MISC_CONFIG;

    /** Used to invoke the REST  job stop and restart operations on the ebookGenerator. */
    private final RestTemplate restTemplate;
    /** The root web application context URL for the ebook generator. */
    private final URL generatorContextUrl;

    @Autowired
    public GeneratorRestClientImpl(@Qualifier("generatorRestTemplate") final RestTemplate restTemplate,
                                   @Value("${generator.context.url}")final URL generatorContextUrl) {
        this.restTemplate = restTemplate;
        this.generatorContextUrl = generatorContextUrl;
    }

    @Override
    public SimpleRestServiceResponse restartJob(final long jobExecutionId) {
        return restTemplate.getForObject(
            GENERATOR_REST_RESTART_JOB_URL_PATTERN,
            SimpleRestServiceResponse.class,
            generatorContextUrl.toString(),
            jobExecutionId);
    }

    @Override
    public SimpleRestServiceResponse stopJob(final long jobExecutionId) {
        return restTemplate.getForObject(
            GENERATOR_REST_STOP_JOB_URL_PATTERN,
            SimpleRestServiceResponse.class,
            generatorContextUrl.toString(),
            jobExecutionId);
    }

    @Override
    public JobThrottleConfig getJobThrottleConfig() {
        return restTemplate.getForObject(
            GENERATOR_GET_JOB_THROTTLE_CONFIG,
            JobThrottleConfig.class,
            generatorContextUrl.toString());
    }

    @Override
    public MiscConfig getMiscConfig() {
        return restTemplate.getForObject(
            GENERATOR_GET_MISC_CONFIG,
            MiscConfig.class,
            generatorContextUrl.toString());
    }

    @Override
    public Map<String, Collection<String>> getStepNames() {
        return Stream.of(AvailableJobs.values())
            .map(AvailableJobs::getJobName)
            .collect(Collectors.toMap(Function.identity(),
                jobName -> {
                    final String stepNamesString = restTemplate
                        .getForObject(GENERATOR_REST_GET_STEP_NAMES_PATTERN, String.class, generatorContextUrl.toString(), jobName);
                    return Arrays.asList(stepNamesString.split(","));
                }));
    }

    @Override
    public URL getGeneratorContextUrl() {
        return generatorContextUrl;
    }
}
