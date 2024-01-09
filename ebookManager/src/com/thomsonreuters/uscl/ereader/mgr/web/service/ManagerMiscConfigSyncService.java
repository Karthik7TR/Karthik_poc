package com.thomsonreuters.uscl.ereader.mgr.web.service;

import com.thomsonreuters.uscl.ereader.core.job.domain.MiscConfig;
import com.thomsonreuters.uscl.ereader.core.service.AbstractMiscConfigSyncService;
import com.thomsonreuters.uscl.ereader.core.service.MiscConfigSyncService;
import com.thomsonreuters.uscl.ereader.deliver.rest.CloseableAuthenticationHttpClientFactory;
import com.thomsonreuters.uscl.ereader.deliver.service.ProviewClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service("miscConfigSyncService")
@Slf4j
public class ManagerMiscConfigSyncService extends AbstractMiscConfigSyncService implements MiscConfigSyncService {
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
        log.info(config.toString());
        super.syncProviewHost(config, httpClientFactory, proviewClient, proviewRestTemplate);
    }
}
