package com.thomsonreuters.uscl.ereader.core.service;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.StringTokenizer;

import com.thomsonreuters.uscl.ereader.core.CoreConstants;
import com.thomsonreuters.uscl.ereader.core.job.domain.JobThrottleConfig;
import com.thomsonreuters.uscl.ereader.core.job.domain.MiscConfig;
import com.thomsonreuters.uscl.ereader.core.job.domain.SimpleRestServiceResponse;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.web.client.RestTemplate;

public class GeneratorRestClientImpl implements GeneratorRestClient {
    //private static final Logger log = LogManager.getLogger(GeneratorRestServiceClientImpl.class);

    private static final String GENERATOR_REST_STOP_JOB_URL_PATTERN = "{context}/service/stop/job/{jobExecutionId}";
    private static final String GENERATOR_REST_RESTART_JOB_URL_PATTERN =
        "{context}/service/restart/job/{jobExecutionId}";
    private static final String GENERATOR_REST_GET_STEP_NAMES_PATTERN = "{context}/service/get/step/names";

    private static final String GENERATOR_GET_JOB_THROTTLE_CONFIG =
        "{context}/" + CoreConstants.URI_GET_JOB_THROTTLE_CONFIG;
    private static final String GENERATOR_GET_MISC_CONFIG = "{context}/" + CoreConstants.URI_GET_MISC_CONFIG;

    /** Used to invoke the REST  job stop and restart operations on the ebookGenerator. */
    private RestTemplate restTemplate;
    /** The root web application context URL for the ebook generator. */
    private URL generatorContextUrl;

    @Override
    public SimpleRestServiceResponse restartJob(final long jobExecutionId) {
        final SimpleRestServiceResponse response = restTemplate.getForObject(
            GENERATOR_REST_RESTART_JOB_URL_PATTERN,
            SimpleRestServiceResponse.class,
            generatorContextUrl.toString(),
            jobExecutionId);
        return response;
    }

    @Override
    public SimpleRestServiceResponse stopJob(final long jobExecutionId) {
        final SimpleRestServiceResponse response = restTemplate.getForObject(
            GENERATOR_REST_STOP_JOB_URL_PATTERN,
            SimpleRestServiceResponse.class,
            generatorContextUrl.toString(),
            jobExecutionId);
        return response;
    }

    @Override
    public JobThrottleConfig getJobThrottleConfig() {
        final JobThrottleConfig config = restTemplate
            .getForObject(GENERATOR_GET_JOB_THROTTLE_CONFIG, JobThrottleConfig.class, generatorContextUrl.toString());
        return config;
    }

    @Override
    public MiscConfig getMiscConfig() {
        final MiscConfig config =
            restTemplate.getForObject(GENERATOR_GET_MISC_CONFIG, MiscConfig.class, generatorContextUrl.toString());
        return config;
    }

    @Override
    public List<String> getStepNames() {
        final String csvStepNames = restTemplate
            .getForObject(GENERATOR_REST_GET_STEP_NAMES_PATTERN, String.class, generatorContextUrl.toString());
        final List<String> stepNames = new ArrayList<>();
        if (csvStepNames != null) {
            final StringTokenizer tokenizer = new StringTokenizer(csvStepNames, ",");
            while (tokenizer.hasMoreTokens()) {
                final String stepName = tokenizer.nextToken();
                stepNames.add(stepName);
            }
        }
        Collections.sort(stepNames);
        return stepNames;
    }

    @Override
    public URL getGeneratorContextUrl() {
        return generatorContextUrl;
    }

    @Required
    public void setRestTemplate(final RestTemplate template) {
        restTemplate = template;
    }

    @Required
    public void setGeneratorContextUrl(final URL url) {
        generatorContextUrl = url;
    }
}
