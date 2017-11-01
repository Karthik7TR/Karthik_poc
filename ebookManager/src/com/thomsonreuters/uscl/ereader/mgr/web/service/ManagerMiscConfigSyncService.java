package com.thomsonreuters.uscl.ereader.mgr.web.service;

import com.thomsonreuters.uscl.ereader.core.job.domain.MiscConfig;
import com.thomsonreuters.uscl.ereader.core.service.AbstractMiscConfigSyncService;
import com.thomsonreuters.uscl.ereader.core.service.MiscConfigSyncService;
import com.thomsonreuters.uscl.ereader.deliver.rest.CloseableAuthenticationHttpClientFactory;
import com.thomsonreuters.uscl.ereader.deliver.service.ProviewClient;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service("miscConfigSyncService")
public class ManagerMiscConfigSyncService extends AbstractMiscConfigSyncService implements MiscConfigSyncService {
    private static Logger LOG = LogManager.getLogger(ManagerMiscConfigSyncService.class);

    private final CloseableAuthenticationHttpClientFactory httpClientFactory;
    private final ProviewClient proviewClient;
    private final RestTemplate proviewRestTemplate;

    @Autowired
    public ManagerMiscConfigSyncService(final CloseableAuthenticationHttpClientFactory httpClientFactory,
                                        final ProviewClient proviewClient,
                                        @Qualifier("proviewRestTemplate")final RestTemplate proviewRestTemplate) {
        this.httpClientFactory = httpClientFactory;
        this.proviewClient = proviewClient;
        this.proviewRestTemplate = proviewRestTemplate;
    }

    @Override
    public void syncSpecific(final MiscConfig config) throws Exception {
        LOG.info(config);
        super.syncProviewHost(config, httpClientFactory, proviewClient, proviewRestTemplate);
    }
}
