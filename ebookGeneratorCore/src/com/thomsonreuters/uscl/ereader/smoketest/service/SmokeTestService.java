package com.thomsonreuters.uscl.ereader.smoketest.service;

import java.util.List;
import java.util.Map;

import com.thomsonreuters.uscl.ereader.smoketest.domain.SmokeTest;

/**
 * Service that returns Server statuses
 *
 */
public interface SmokeTestService {
    Map<String, List<SmokeTest>> getServerStatuses();

    Map<String, List<SmokeTest>> getApplicationStatuses();

    Map<String, SmokeTest> getDatabaseServerStatuses();

    List<SmokeTest> getExternalSystemsStatuses();

    Map<String, List<String>> getRunningApplications();
}
