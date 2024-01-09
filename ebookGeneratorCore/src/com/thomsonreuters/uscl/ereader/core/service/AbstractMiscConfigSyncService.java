package com.thomsonreuters.uscl.ereader.core.service;

import java.net.InetAddress;
import java.net.UnknownHostException;

import com.thomsonreuters.uscl.ereader.core.CoreConstants.NovusEnvironment;
import com.thomsonreuters.uscl.ereader.core.job.domain.MiscConfig;
import com.thomsonreuters.uscl.ereader.deliver.rest.CloseableAuthenticationHttpClientFactory;
import com.thomsonreuters.uscl.ereader.deliver.service.ProviewClient;
import org.apache.http.client.HttpClient;
import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

/**
 * Perform the work to apply a "on-the-fly" configuration change to the API sub-system that it is applicable.
 * Currently this means changing the logback logger levels and storing the Novus Environment (Client|Prod).
 *
 */
public abstract class AbstractMiscConfigSyncService implements MiscConfigSyncService {

    private MiscConfig miscConfig;

    public AbstractMiscConfigSyncService() {
        super();
        miscConfig = new MiscConfig();
    }

    public abstract void syncSpecific(MiscConfig config) throws Exception;

    @Override
    public void sync(final MiscConfig config) throws Exception {
        miscConfig.copy(config); // Make a copy of the values maintained within this service
        syncLogLevels(config); // Common operation for all web apps
        syncSpecific(config); // Do app specific operations
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

    private void syncLogLevels(final MiscConfig config) {
        // Set the configured logging levels
        setAppLogLevel(config.getAppLogLevel());
        setRootLogLevel(config.getRootLogLevel());
    }

    private void setAppLogLevel(final Level level) {
        final Logger logger = (Logger) LoggerFactory.getLogger("com.thomsonreuters.uscl.ereader");
        logger.setLevel(level);
    }

    private void setRootLogLevel(final Level level) {
        final Logger logger = (Logger) LoggerFactory.getLogger(org.slf4j.Logger.ROOT_LOGGER_NAME);
        logger.setLevel(level);
    }

    /**
     * Assign the new Proview Host into all components that make use of it in communicating with
     * the Proview service provider.
     * @param config  Contains the new proview host name
     */
    public static void syncProviewHost(
        final MiscConfig config,
        final CloseableAuthenticationHttpClientFactory httpClientFactory,
        final ProviewClient proviewClient,
        final RestTemplate proviewRestTemplate) throws UnknownHostException {
        // Set a new proview host for authentication and
        final InetAddress host = config.getProviewHost();
        proviewClient.setProviewHostname(config.getProviewHostname());
        httpClientFactory.setHost(host);
        final HttpClient httpClient = httpClientFactory.getCloseableAuthenticationHttpClient();
        final HttpComponentsClientHttpRequestFactory requestFactory =
            new HttpComponentsClientHttpRequestFactory(httpClient);
        requestFactory.setBufferRequestBody(false);
        proviewRestTemplate.setRequestFactory(requestFactory);
    }
}
