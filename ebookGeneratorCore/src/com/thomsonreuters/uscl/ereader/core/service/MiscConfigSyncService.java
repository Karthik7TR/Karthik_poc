package com.thomsonreuters.uscl.ereader.core.service;

import java.net.InetAddress;

import com.thomsonreuters.uscl.ereader.core.CoreConstants.NovusEnvironment;
import com.thomsonreuters.uscl.ereader.core.job.domain.MiscConfig;

public interface MiscConfigSyncService
{
    /**
     * Update the log4j loggers with the specified new log levels.  The configuration is stored in the APP_PARAMETER table.
     * @throws Exception on any error
     */
    void sync(MiscConfig config) throws Exception;

    MiscConfig getMiscConfig();

    InetAddress getProviewHost();

    NovusEnvironment getNovusEnvironment();
}
