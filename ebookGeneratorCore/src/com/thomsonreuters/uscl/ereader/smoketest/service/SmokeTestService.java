package com.thomsonreuters.uscl.ereader.smoketest.service;

import java.util.List;

import com.thomsonreuters.uscl.ereader.smoketest.domain.SmokeTest;

/**
 * Service that returns Server statuses
 *
 */
public interface SmokeTestService {
    List<SmokeTest> getCIServerStatuses();

    List<SmokeTest> getCIApplicationStatuses();

    List<SmokeTest> getTestServerStatuses();

    List<SmokeTest> getTestApplicationStatuses();

    List<SmokeTest> getQAServerStatuses();

    List<SmokeTest> getQAApplicationStatuses();

    List<SmokeTest> getLowerEnvDatabaseServerStatuses();

    List<SmokeTest> getProdServerStatuses();

    List<SmokeTest> getProdApplicationStatuses();

    List<SmokeTest> getProdDatabaseServerStatuses();

    List<String> getRunningApplications();

    SmokeTest getApplicationStatus(String appName, String url);

    SmokeTest testConnection();

    SmokeTest testMQConnection();
}
