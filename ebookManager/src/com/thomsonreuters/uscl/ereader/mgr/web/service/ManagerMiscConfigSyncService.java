package com.thomsonreuters.uscl.ereader.mgr.web.service;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.web.client.RestTemplate;

import com.thomsonreuters.uscl.ereader.core.job.domain.MiscConfig;
import com.thomsonreuters.uscl.ereader.core.service.AbstractMiscConfigSyncService;
import com.thomsonreuters.uscl.ereader.core.service.MiscConfigSyncService;
import com.thomsonreuters.uscl.ereader.deliver.rest.BasicAuthenticationHttpClientFactory;
import com.thomsonreuters.uscl.ereader.deliver.service.ProviewClient;

public class ManagerMiscConfigSyncService extends AbstractMiscConfigSyncService implements MiscConfigSyncService {
	private static Logger log = Logger.getLogger(ManagerMiscConfigSyncService.class);
	
	private BasicAuthenticationHttpClientFactory httpClientFactory;
	private ProviewClient proviewClient;
	private RestTemplate proviewRestTemplate;
	
	@Override
	public void syncSpecific(MiscConfig config) throws Exception {
		log.info(config);
		super.syncProviewHost(config, httpClientFactory, proviewClient, proviewRestTemplate);
	}
	
	@Required
	public void setHttpClientFactory(
			BasicAuthenticationHttpClientFactory httpClientFactory) {
		this.httpClientFactory = httpClientFactory;
	}
	@Required
	public void setProviewClient(ProviewClient proviewClient) {
		this.proviewClient = proviewClient;
	}
	@Required
	public void setProviewRestTemplate(RestTemplate proviewRestTemplate) {
		this.proviewRestTemplate = proviewRestTemplate;
	}
}