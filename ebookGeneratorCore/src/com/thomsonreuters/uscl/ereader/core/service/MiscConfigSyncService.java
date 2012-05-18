package com.thomsonreuters.uscl.ereader.core.service;

import com.thomsonreuters.uscl.ereader.core.job.domain.MiscConfig;

public interface MiscConfigSyncService {
	
	/**
	 * Update the log4j loggers with the specified new log levels.  The configuration is stored in the APP_PARAMETER table.
	 * @throws Exception on any error
	 */
	public void syncMiscConfig(MiscConfig config) throws Exception;

}
