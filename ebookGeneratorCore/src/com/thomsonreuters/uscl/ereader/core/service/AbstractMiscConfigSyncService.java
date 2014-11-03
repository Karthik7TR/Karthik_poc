package com.thomsonreuters.uscl.ereader.core.service;

import java.net.InetAddress;
import java.net.UnknownHostException;

import org.apache.http.client.HttpClient;
import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import com.thomsonreuters.uscl.ereader.core.CoreConstants.NovusEnvironment;
import com.thomsonreuters.uscl.ereader.core.job.domain.MiscConfig;
import com.thomsonreuters.uscl.ereader.deliver.rest.CloseableAuthenticationHttpClientFactory;
import com.thomsonreuters.uscl.ereader.deliver.service.ProviewClient;

/**
 * Perform the work to apply a "on-the-fly" configuration change to the API sub-system that it is applicable.
 * Currently this means changing the log4j logger levels and storing the Novus Environment (Client|Prod).
 *
 */
public abstract class AbstractMiscConfigSyncService implements MiscConfigSyncService  {
	//private static Logger log = Logger.getLogger(AbstractMiscConfigSyncService.class);
	
	private MiscConfig miscConfig;
	
	public AbstractMiscConfigSyncService() {
		super();
		this.miscConfig = new MiscConfig();
	}
	
	public abstract void syncSpecific(MiscConfig config) throws Exception;
	
	public void sync(MiscConfig config) throws Exception {
		miscConfig.copy(config);	// Make a copy of the values maintained within this service
		syncLogLevels(config);  // Common operation for all web apps
		syncSpecific(config);	// Do app specific operations
	}

	@Override
	public MiscConfig getMiscConfig() {
		return miscConfig;
	}
	
	@Override
	public InetAddress getProviewHost() {
		return miscConfig.getProviewHost();
	}
	@Override
	public NovusEnvironment getNovusEnvironment() {
		return miscConfig.getNovusEnvironment();
	}
	
	private void syncLogLevels(MiscConfig config) throws Exception {
		// Set the configured logging levels
		setAppLogLevel(config.getAppLogLevel());
		setRootLogLevel(config.getRootLogLevel());
	}

	private void setAppLogLevel(Level level) {
		Logger logger = LogManager.getLogger("com.thomsonreuters.uscl.ereader");
		logger.setLevel(level);
	}
	private void setRootLogLevel(Level level) {
		Logger logger = LogManager.getRootLogger();
		logger.setLevel(level);
	}

	/**
	 * Assign the new Proview Host into all components that make use of it in communicating with
	 * the Proview service provider.
	 * @param config  Contains the new proview host name
	 */
	public static void syncProviewHost(MiscConfig config,
			 						CloseableAuthenticationHttpClientFactory httpClientFactory,
			 						ProviewClient proviewClient,
			 						RestTemplate proviewRestTemplate) throws UnknownHostException {
		// Set a new proview host for authentication and
		InetAddress host = config.getProviewHost();
		proviewClient.setProviewHostname(config.getProviewHostname());
		httpClientFactory.setHost(host);
		HttpClient httpClient = httpClientFactory.getCloseableAuthenticationHttpClient();
		HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory(httpClient);
		requestFactory.setBufferRequestBody(false);
		proviewRestTemplate.setRequestFactory(requestFactory);
	}
}
