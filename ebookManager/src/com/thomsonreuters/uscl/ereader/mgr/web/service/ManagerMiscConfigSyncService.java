package com.thomsonreuters.uscl.ereader.mgr.web.service;

import com.thomsonreuters.uscl.ereader.core.job.domain.MiscConfig;
import com.thomsonreuters.uscl.ereader.core.service.AbstractMiscConfigSyncService;
import com.thomsonreuters.uscl.ereader.core.service.MiscConfigSyncService;
import com.thomsonreuters.uscl.ereader.deliver.rest.CloseableAuthenticationHttpClientFactory;
import com.thomsonreuters.uscl.ereader.deliver.service.ProviewClient;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.web.client.RestTemplate;

public class ManagerMiscConfigSyncService extends AbstractMiscConfigSyncService implements MiscConfigSyncService {
    private static Logger log = LogManager.getLogger(ManagerMiscConfigSyncService.class);

    private CloseableAuthenticationHttpClientFactory httpClientFactory;
    private ProviewClient proviewClient;
    private RestTemplate proviewRestTemplate;

    @Override
    public void syncSpecific(final MiscConfig config) throws Exception {
        log.info(config);
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
