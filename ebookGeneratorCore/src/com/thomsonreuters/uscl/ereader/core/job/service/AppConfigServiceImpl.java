package com.thomsonreuters.uscl.ereader.core.job.service;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.transaction.annotation.Transactional;

import com.thomsonreuters.uscl.ereader.core.job.dao.AppParameterDao;
import com.thomsonreuters.uscl.ereader.core.job.domain.AppConfig;
import com.thomsonreuters.uscl.ereader.core.job.domain.AppConfig.Key;
import com.thomsonreuters.uscl.ereader.core.job.domain.AppParameter;
import com.thomsonreuters.uscl.ereader.core.job.domain.JobThrottleConfig;
import com.thomsonreuters.uscl.ereader.core.job.domain.LoggingConfig;

public class AppConfigServiceImpl implements AppConfigService {
	//private static final Logger log = Logger.getLogger(AppConfigServiceImpl.class);
	private static final int DEFAULT_SIZE = 2;
	private AppParameterDao dao;
	
	@Override
	@Transactional(readOnly=true)
	public JobThrottleConfig getJobThrottleConfig() {
		return getAppConfig();
	}
	@Override
	@Transactional(readOnly=true)
	public LoggingConfig getLoggingConfig() {
		return getAppConfig();
	}
	@Override
	@Transactional(readOnly=true)
	public String getConfigValue(AppConfig.Key key) {
		AppParameter param = (AppParameter) dao.findByPrimaryKey(key.toString());
		return (param != null) ? param.getValue() : null;
	}

	@Override
	@Transactional(readOnly=true)
	public AppConfig getAppConfig() {
		String coreThreadPoolSizeString = getConfigValue(Key.coreThreadPoolSize);
		int coreThreadPoolSize = (StringUtils.isNotBlank(coreThreadPoolSizeString)) ? Integer.valueOf(coreThreadPoolSizeString) : DEFAULT_SIZE;
		
		String stepThrottleEnabledString = getConfigValue(Key.stepThrottleEnabled);
		boolean stepThrottleEnabled = (StringUtils.isNotBlank(stepThrottleEnabledString)) ? Boolean.valueOf(stepThrottleEnabledString) : false;
		
		String throttleStepName = getConfigValue(Key.throttleStepName);
		
		String throtttleStepMaxJobsString = getConfigValue(Key.throtttleStepMaxJobs);
		int throtttleStepMaxJobs = (StringUtils.isNotBlank(throtttleStepMaxJobsString)) ? Integer.valueOf(throtttleStepMaxJobsString) : DEFAULT_SIZE;

		Level appLogLevel = fetchLogLevel(Key.appLogLevel, Level.INFO);
		Level rootLogLevel = fetchLogLevel(Key.rootLogLevel, Level.ERROR);
		
		AppConfig config = new AppConfig(appLogLevel, rootLogLevel, coreThreadPoolSize, stepThrottleEnabled, throttleStepName, throtttleStepMaxJobs);
		return config;
	}
	
	private Level fetchLogLevel(Key key, Level defaultLogLevel) {
		String logLevelString = getConfigValue(key);
		Level logLevel = (StringUtils.isNotBlank(logLevelString)) ? Level.toLevel(logLevelString) : defaultLogLevel;
		return logLevel;
	}
	
	@Override
	@Transactional
	public void saveJobThrottleConfig(JobThrottleConfig config) {
		List<AppParameter> params = createJobThrottleConfigAppParameterList(config);
		for (AppParameter param : params) {
			dao.save(param);
		}
	}

	@Override
	@Transactional
	public void saveLoggingConfig(LoggingConfig config) {
		AppParameter param = new AppParameter(AppConfig.Key.appLogLevel.toString(), config.getAppLogLevel());
		dao.save(param);
		param = new AppParameter(AppConfig.Key.rootLogLevel.toString(), config.getRootLogLevel());
		dao.save(param);
	}
	
	@Override
	public void setLogLevel(LoggingConfig config) {
		setAppLogLevel(config.getAppLogLevel());
		setRootLogLevel(config.getRootLogLevel());
	}

	//private static final String LOG_MESG_TEMPLATE = "New \"%s\" log level: %s";
	private void setAppLogLevel(Level level) {
		Logger logger = LogManager.getLogger("com.thomsonreuters.uscl.ereader");
		logger.setLevel(level);
		//log.warn(String.format(LOG_MESG_TEMPLATE, logger.getName(), logger.getLevel()));
	}
	private void setRootLogLevel(Level level) {
		Logger logger = LogManager.getRootLogger();
		logger.setLevel(level);
		//log.warn(String.format(LOG_MESG_TEMPLATE, logger.getName(), logger.getLevel()));
	}	
	
//	@Override
//	@Transactional
//	public void deleteAppConfig(AppConfig config) {
//		List<AppParameter> params = createJobThrottleConfigAppParameterList(config);
//		for (AppParameter param : params) {
//			dao.delete(param);
//		}
//	}
	

	private List<AppParameter> createJobThrottleConfigAppParameterList(JobThrottleConfig config) {
		List<AppParameter> parameters = new ArrayList<AppParameter>();
		parameters.add(new AppParameter(AppConfig.Key.coreThreadPoolSize.toString(), config.getCoreThreadPoolSize()));
		parameters.add(new AppParameter(AppConfig.Key.stepThrottleEnabled.toString(), config.isStepThrottleEnabled()));
		parameters.add(new AppParameter(AppConfig.Key.throttleStepName.toString(), config.getThrottleStepName()));
		parameters.add(new AppParameter(AppConfig.Key.throtttleStepMaxJobs.toString(), config.getThrotttleStepMaxJobs()));
		return parameters;
	}
	
	@Required
	public void setAppParameterDao(AppParameterDao dao) {
		this.dao = dao;
	}
}
