package com.thomsonreuters.uscl.ereader.core.service;

import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.thomsonreuters.uscl.ereader.core.job.domain.MiscConfig;

public class MiscConfigSyncServiceImpl implements MiscConfigSyncService {
	//private static Logger log = Logger.getLogger(MiscConfigSyncServiceImpl.class);

	@Override
	public void syncMiscConfig(MiscConfig config) throws Exception {
		// Set the configured logging levels
		setLogLevel(config);
	}
	
	private void setLogLevel(MiscConfig config) {
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
}
