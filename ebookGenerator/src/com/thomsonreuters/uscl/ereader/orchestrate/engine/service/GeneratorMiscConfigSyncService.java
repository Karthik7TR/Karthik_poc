package com.thomsonreuters.uscl.ereader.orchestrate.engine.service;

import java.net.UnknownHostException;

import com.thomsonreuters.uscl.ereader.core.job.domain.MiscConfig;
import com.thomsonreuters.uscl.ereader.core.service.AbstractMiscConfigSyncService;
import com.thomsonreuters.uscl.ereader.core.service.MiscConfigSyncService;
import com.thomsonreuters.uscl.ereader.deliver.rest.CloseableAuthenticationHttpClientFactory;
import com.thomsonreuters.uscl.ereader.deliver.service.ProviewClient;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.web.client.RestTemplate;

public class GeneratorMiscConfigSyncService extends AbstractMiscConfigSyncService implements MiscConfigSyncService {
    private static Logger log = LogManager.getLogger(GeneratorMiscConfigSyncService.class);

    private CloseableAuthenticationHttpClientFactory httpClientFactory;
    private ProviewClient proviewClient;
    private RestTemplate proviewRestTemplate;

    public GeneratorMiscConfigSyncService() {
        super();
    }

    @Override
    public void syncSpecific(final MiscConfig config) throws Exception {
        log.info(config);
        syncProviewHost(config);
    }

    private void syncProviewHost(final MiscConfig config) throws UnknownHostException {
        super.syncProviewHost(config, httpClientFactory, proviewClient, proviewRestTemplate);
    }

    @Required
    public void setHttpClientFactory(final CloseableAuthenticationHttpClientFactory httpClientFactory) {
        this.httpClientFactory = httpClientFactory;
    }

    @Required
    public void setProviewClient(final ProviewClient proviewClient) {
        this.proviewClient = proviewClient;
    }

    @Required
    public void setProviewRestTemplate(final RestTemplate proviewRestTemplate) {
        this.proviewRestTemplate = proviewRestTemplate;
    }
}
