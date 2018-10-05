package com.thomsonreuters.uscl.ereader.smoketest.service;

import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.URI;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.annotation.Resource;
import javax.jms.Connection;

import com.ibm.mq.jms.MQQueueConnectionFactory;
import com.thomsonreuters.uscl.ereader.common.EBookApps;
import com.thomsonreuters.uscl.ereader.common.EBookApps.AppEnv;
import com.thomsonreuters.uscl.ereader.core.CoreConstants.NovusEnvironment;
import com.thomsonreuters.uscl.ereader.core.service.MiscConfigSyncService;
import com.thomsonreuters.uscl.ereader.sap.service.SapService;
import com.thomsonreuters.uscl.ereader.smoketest.dao.SmokeTestDao;
import com.thomsonreuters.uscl.ereader.smoketest.domain.SmokeTest;
import com.thomsonreuters.uscl.ereader.util.EmailNotification;
import org.apache.commons.dbcp.BasicDataSource;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.transaction.annotation.Transactional;

import com.westgroup.novus.productapi.Novus;

/**
 * Service that returns Server statuses
 *
 */
public class SmokeTestServiceImpl implements SmokeTestService {
    private static Logger LOG = LogManager.getLogger(SmokeTestServiceImpl.class);

    private static final String NOVUS_CLIENT_TEST_DOC_ID = "I41077696b67411d9947c9ea867b7826a";
    public static final File APPSERVER_TOMCAT_DIR = new File("/appserver/tomcat");
    private static final int TIME_OUT = 3000; // In milliseconds

    @Resource(name = "smokeTestDao")
    private SmokeTestDao dao;
    @Resource(name = "dataSource")
    private BasicDataSource basicDataSource;
    @Resource
    private JmsTemplate jmsTemplate;
    @Value("${xpp.quality.ftp.server}")
    private String deltaTextftpServerPath;
    @Value("${xpp.quality.webservice}")
    private String deltaTextApiUrl;
    @Resource
    private SapService sapService;
    @Resource
    private MiscConfigSyncService miscConfigSyncService;
    @Value("${image.vertical.context.url}")
    private String imageVertical;

    @Override
    public Map<String, List<SmokeTest>> getServerStatuses() {
        final Map<String, List<SmokeTest>> serverStatuses = new HashMap<>();
        serverStatuses.put("ci", getEnvServerStatuses(EBookApps::ciServers));
        serverStatuses.put("test", getEnvServerStatuses(EBookApps::testServers));
        serverStatuses.put("qa", getEnvServerStatuses(EBookApps::qedServers));
        serverStatuses.put("prod", getEnvServerStatuses(EBookApps::prodServers));
        return serverStatuses;
    }

    private List<SmokeTest> getEnvServerStatuses(final Supplier<List<String>> serversSupplier) {
        return serversSupplier.get().stream()
            .map(this::getServerStatus)
            .collect(Collectors.toList());
    }

    @Override
    public Map<String, List<SmokeTest>> getApplicationStatuses() {
        final Map<String, List<SmokeTest>> applicationStatuses = new HashMap<>();
        applicationStatuses.put("ciApps", getEnvApplicationStatuses(EBookApps::ci));
        applicationStatuses.put("testApps", getEnvApplicationStatuses(EBookApps::test));
        applicationStatuses.put("qaApps", getEnvApplicationStatuses(EBookApps::qed));
        applicationStatuses.put("prodApps", getEnvApplicationStatuses(EBookApps::prod));
        return applicationStatuses;
    }

    private List<SmokeTest> getEnvApplicationStatuses(final Function<EBookApps, AppEnv> environmentFunction) {
        final List<SmokeTest> appStatuses = new ArrayList<>();
        for (final EBookApps app : EBookApps.values()) {
            final EBookApps.AppEnv appEnv = environmentFunction.apply(app);

            appEnv.servers().stream()
                .map(server -> String.format("http://%s:%s/%s/", server, appEnv.port(), app.contextPath()))
                .map(url -> getApplicationStatus(app.appName(), url))
                .forEach(appStatuses::add);

            Optional.of(appEnv.webUrl())
                .filter(StringUtils::isNotBlank)
                .map(webUrl -> String.format("%s/%s/", webUrl, app.contextPath()))
                .map(url -> getApplicationStatus(app.appName(), url))
                .ifPresent(appStatuses::add);
        }
        return appStatuses;
    }

    @Override
    public Map<String, SmokeTest> getDatabaseServerStatuses() {
        final Map<String, SmokeTest> databaseServerStatuses = new HashMap<>();
        databaseServerStatuses.put("lowerEnvDatabase", getServerStatus("c540wfyctdbqf.int.thomsonreuters.com"));
        databaseServerStatuses.put("prodDatabase", getServerStatus("c279znzctdbpf.int.thomsonreuters.com"));
        return databaseServerStatuses;
    }

    @Override
    @Transactional
    public List<SmokeTest> getExternalSystemsStatuses() {
        final List<SmokeTest> externalSystemsStatuses = new ArrayList<>();

        final URI imageVerticalUri = URI.create(imageVertical);
        externalSystemsStatuses.add(getApplicationStatus(
            "Image Vertical", String.format("http://%s/image/v1/StatusCheck", imageVerticalUri.getAuthority())));

        final InetAddress proviewHost = miscConfigSyncService.getProviewHost();
        externalSystemsStatuses.add(getApplicationStatus(
            "ProView", String.format("http://%s/v1/statuscheck", proviewHost.getHostName())));

        externalSystemsStatuses.add(sapService.checkSapStatus());
        externalSystemsStatuses.add(testMQConnection());
        externalSystemsStatuses.add(testDatabaseConnection());
        externalSystemsStatuses.addAll(testNovusAvailability());
        externalSystemsStatuses.add(testSMTPStatus());
        externalSystemsStatuses.addAll(testDeltaTextServices());
        return externalSystemsStatuses;
    }

    private SmokeTest getApplicationStatus(final String appName, final String url) {
        final SmokeTest serverStatus = new SmokeTest();
        serverStatus.setName(appName);
        serverStatus.setAddress(url);
        try {
            final HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(TIME_OUT);
            connection.setReadTimeout(TIME_OUT);
            serverStatus.setIsRunning(connection.getResponseCode() == HttpURLConnection.HTTP_OK);
        } catch (final Exception e) {
            LOG.error(e.getMessage(), e);
            serverStatus.setIsRunning(false);
        }

        return serverStatus;
    }

    private SmokeTest testMQConnection() {
        final MQQueueConnectionFactory factory = (MQQueueConnectionFactory) jmsTemplate.getConnectionFactory();
        final SmokeTest status = new SmokeTest();
        status.setName("XPP MQ Connection");
        status.setAddress(String.join(":", factory.getHostName(), Integer.toString(factory.getPort())));
        try (Connection connection = factory.createConnection()) {
            status.setIsRunning(true);
        } catch (final Exception e) {
            LOG.error(e.getMessage(), e);
        }
        return status;
    }

    private SmokeTest testDatabaseConnection() {
        final SmokeTest status = new SmokeTest();
        status.setName("Database Connection");
        status.setAddress(
            String.format(
                "numActive=%d, numIdle=%d, maxActive=%d, maxWait=%d",
                basicDataSource.getNumActive(),
                basicDataSource.getNumIdle(),
                basicDataSource.getMaxActive(),
                basicDataSource.getMaxWait()));
        status.setIsRunning(dao.testConnection());

        return status;
    }

    private List<SmokeTest> testNovusAvailability() {
        return Stream.of(NovusEnvironment.values())
            .map(this::testNovusConnection)
            .collect(Collectors.toList());
    }

    private SmokeTest testNovusConnection(final NovusEnvironment environment) {
        final SmokeTest smokeTest = new SmokeTest();
        smokeTest.setName("Novus");
        smokeTest.setAddress(String.join(StringUtils.SPACE, environment.toString(), "environment"));
        try {
            createNovus(environment)
                .getFind()
                .getDocument(null, NOVUS_CLIENT_TEST_DOC_ID)
                .getMetaData();
            smokeTest.setIsRunning(true);
        } catch (final Exception e) {
            LOG.error(e.getMessage(), e);
        }
        return smokeTest;
    }

    private Novus createNovus(final NovusEnvironment environment) {
        final Novus novus = new Novus();
        novus.setQueueCriteria(null, environment.toString());
        novus.setResponseTimeout(30000);
        novus.useLatestPit();
        return novus;
    }

    private SmokeTest testSMTPStatus() {
        final SmokeTest smokeTest = getServerStatus(EmailNotification.HOST);
        smokeTest.setName("SMTP");
        smokeTest.setAddress(EmailNotification.HOST);
        return smokeTest;
    }

    private List<SmokeTest> testDeltaTextServices() {
        final String deltaTextWebHost = StringUtils.substringBetween(deltaTextApiUrl, "http://", ":");
        final SmokeTest deltaTextWebsmokeTest = getServerStatus(deltaTextWebHost);
        deltaTextWebsmokeTest.setName("DeltaText Web");
        deltaTextWebsmokeTest.setAddress(deltaTextWebHost);

        final SmokeTest deltaTextFtpsmokeTest = getServerStatus(deltaTextftpServerPath);
        deltaTextFtpsmokeTest.setName("DeltaText FTP");
        deltaTextFtpsmokeTest.setAddress(deltaTextftpServerPath);

        return Arrays.asList(deltaTextWebsmokeTest, deltaTextFtpsmokeTest);
    }

    private SmokeTest getServerStatus(final String serverName) {
        final SmokeTest serverStatus = new SmokeTest();
        try {
            final InetAddress address = InetAddress.getByName(serverName);
            serverStatus.setName(address.getHostName());
            serverStatus.setAddress(address.getHostAddress());
            serverStatus.setIsRunning(address.isReachable(TIME_OUT));
        } catch (final UnknownHostException e) {
            LOG.error(e.getMessage(), e);
            serverStatus.setIsRunning(false);
        } catch (final IOException e) {
            LOG.error(e.getMessage(), e);
            serverStatus.setIsRunning(false);
        }

        return serverStatus;
    }

    @Override
    public List<String> getRunningApplications() {
        List<String> appNames = null;

        try {
            final AppFilenameFilter filter = new AppFilenameFilter();
            if (APPSERVER_TOMCAT_DIR.exists()) {
                appNames = new ArrayList<>(Arrays.asList(APPSERVER_TOMCAT_DIR.list(filter)));
            }
        } catch (final Exception e) {
            LOG.error(e.getMessage(), e);
        }

        return appNames;
    }
}
