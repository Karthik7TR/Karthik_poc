package com.thomsonreuters.uscl.ereader.smoketest.service;

import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.annotation.Resource;

import com.thomsonreuters.uscl.ereader.smoketest.dao.SmokeTestDao;
import com.thomsonreuters.uscl.ereader.smoketest.domain.SmokeTest;
import org.apache.commons.dbcp.BasicDataSource;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service that returns Server statuses
 *
 */
public class SmokeTestServiceImpl implements SmokeTestService {
    private static final String GENERATOR = "Generator";
    private static final String MANAGER = "Manager";
    private static final String GATHERER = "Gatherer";
    public static final File APPSERVER_TOMCAT_DIR = new File("/appserver/tomcat");

    private static Logger LOG = LogManager.getLogger(SmokeTestServiceImpl.class);

    private SmokeTestDao dao;
    @Resource(name = "dataSource")
    private BasicDataSource basicDataSource;

    private static final int TIME_OUT = 3000; // In milliseconds

    private static final String[] qedManagerServers =
        {"c045ydwctasqf.int.thomsonreuters.com", "c088jzvctasqf.int.thomsonreuters.com"};
    private static final String[] qedGeneratorServers = {
        "c311ppwctasqf.int.thomsonreuters.com",
        "c531tqhctasqf.int.thomsonreuters.com",
        "c931dsectasqf.int.thomsonreuters.com",
        "c959hcyctasqf.int.thomsonreuters.com"};

    private static final String[] prodManagerServers =
        {"c250ektctaspf.int.thomsonreuters.com", "c312kjfctaspf.int.thomsonreuters.com"};
    private static final String[] prodGeneratorServers = {
        "c378nnactaspf.int.thomsonreuters.com",
        "c634tcpctaspf.int.thomsonreuters.com",
        "c730rejctaspf.int.thomsonreuters.com",
        "c747kpbctaspf.int.thomsonreuters.com"};

    @Override
    public List<SmokeTest> getCIServerStatuses() {
        final List<SmokeTest> statuses = new ArrayList<>();

        statuses.add(getServerStatus("c708pfmctasdf.int.thomsonreuters.com"));

        return statuses;
    }

    @Override
    public List<SmokeTest> getCIApplicationStatuses() {
        final List<SmokeTest> statuses = new ArrayList<>();

        statuses.add(getApplicationStatus(MANAGER, "http://c708pfmctasdf.int.thomsonreuters.com:9007/ebookManager"));
        statuses.add(getApplicationStatus(MANAGER, "http://ebookmanager-ci.int.thomsonreuters.com/ebookManager"));
        statuses
            .add(getApplicationStatus(GENERATOR, "http://c708pfmctasdf.int.thomsonreuters.com:9002/ebookGenerator"));
        statuses.add(getApplicationStatus(GATHERER, "http://c708pfmctasdf.int.thomsonreuters.com:9001/ebookGatherer"));

        return statuses;
    }

    @Override
    public List<SmokeTest> getTestServerStatuses() {
        final List<SmokeTest> statuses = new ArrayList<>();

        statuses.add(getServerStatus("c281ffzctastf.int.thomsonreuters.com"));
        statuses.add(getServerStatus("c273pevctastf.int.thomsonreuters.com"));

        return statuses;
    }

    @Override
    public List<SmokeTest> getTestApplicationStatuses() {
        final List<SmokeTest> statuses = new ArrayList<>();

        statuses.add(getApplicationStatus(MANAGER, "http://c281ffzctastf.int.thomsonreuters.com:9003/ebookManager"));
        statuses.add(getApplicationStatus(MANAGER, "http://ebookmanager-demo.int.thomsonreuters.com/ebookManager"));
        statuses
            .add(getApplicationStatus(GENERATOR, "http://c273pevctastf.int.thomsonreuters.com:9002/ebookGenerator"));
        statuses.add(getApplicationStatus(GATHERER, "http://c273pevctastf.int.thomsonreuters.com:9001/ebookGatherer"));

        return statuses;
    }

    @Override
    public List<SmokeTest> getQAServerStatuses() {
        final List<SmokeTest> statuses = new ArrayList<>();

        // List of eBook Manager Servers
        for (final String server : qedManagerServers) {
            statuses.add(getServerStatus(server));
        }

        // List of eBook Generator Servers
        for (final String server : qedGeneratorServers) {
            statuses.add(getServerStatus(server));
        }

        return statuses;
    }

    @Override
    public List<SmokeTest> getQAApplicationStatuses() {
        final List<SmokeTest> statuses = new ArrayList<>();

        // List of eBook Manager Servers
        for (final String server : qedManagerServers) {
            statuses.add(getApplicationStatus(MANAGER, String.format("http://%s:9001/ebookManager", server)));
        }

        // List of eBook Generator Servers
        for (final String server : qedGeneratorServers) {
            statuses.add(getApplicationStatus(GATHERER, String.format("http://%s:9001/ebookGatherer", server)));
            statuses.add(getApplicationStatus(GENERATOR, String.format("http://%s:9002/ebookGenerator", server)));
        }

        statuses.add(getApplicationStatus(MANAGER, "http://ebookmanager-qed.int.thomsonreuters.com/ebookManager"));
        statuses.add(getApplicationStatus(GATHERER, "http://ebookgatherer-qed.int.thomsonreuters.com/ebookGatherer"));
        statuses
            .add(getApplicationStatus(GENERATOR, "http://ebookgenerator-qed.int.thomsonreuters.com/ebookGenerator"));

        return statuses;
    }

    @Override
    public List<SmokeTest> getLowerEnvDatabaseServerStatuses() {
        final List<SmokeTest> statuses = new ArrayList<>();

        // Lower environment Database servers
        statuses.add(getServerStatus("c540wfyctdbqf.int.thomsonreuters.com"));

        return statuses;
    }

    @Override
    public List<SmokeTest> getProdServerStatuses() {
        final List<SmokeTest> statuses = new ArrayList<>();

        // List of eBook Manager Servers
        for (final String server : prodManagerServers) {
            statuses.add(getServerStatus(server));
        }

        // List of eBook Generator Servers
        for (final String server : prodGeneratorServers) {
            statuses.add(getServerStatus(server));
        }

        return statuses;
    }

    @Override
    public List<SmokeTest> getProdApplicationStatuses() {
        final List<SmokeTest> statuses = new ArrayList<>();

        // List of eBook Manager Servers
        for (final String server : prodManagerServers) {
            statuses.add(getApplicationStatus(MANAGER, String.format("http://%s:9001/ebookManager", server)));
        }

        // List of eBook Generator Servers
        for (final String server : prodGeneratorServers) {
            statuses.add(getApplicationStatus(GATHERER, String.format("http://%s:9001/ebookGatherer", server)));
            statuses.add(getApplicationStatus(GENERATOR, String.format("http://%s:9002/ebookGenerator", server)));
        }

        statuses.add(getApplicationStatus(MANAGER, "http://ebookmanager.int.thomsonreuters.com/ebookManager"));
        statuses.add(getApplicationStatus(GATHERER, "http://ebookgatherer.int.thomsonreuters.com/ebookGatherer"));
        statuses.add(getApplicationStatus(GENERATOR, "http://ebookgenerator.int.thomsonreuters.com/ebookGenerator"));

        return statuses;
    }

    @Override
    public List<SmokeTest> getProdDatabaseServerStatuses() {
        final List<SmokeTest> statuses = new ArrayList<>();

        // Prod Database servers
        statuses.add(getServerStatus("c279znzctdbpf.int.thomsonreuters.com"));

        return statuses;
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

    @Override
    public SmokeTest getApplicationStatus(final String appName, final String url) {
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

    @Override
    @Transactional(readOnly = true)
    public SmokeTest testConnection() {
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

    @Required
    public void setSmokeTestDao(final SmokeTestDao dao) {
        this.dao = dao;
    }
}
